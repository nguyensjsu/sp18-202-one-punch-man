/*
 This file is part of the Greenfoot program. 
 Copyright (C) 2005-2010,2011,2012,2013,2014  Poul Henriksen and Michael Kolling 
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package greenfoot.core;

import greenfoot.Actor;
import greenfoot.ActorVisitor;
import greenfoot.World;
import greenfoot.WorldVisitor;
import greenfoot.event.SimulationEvent;
import greenfoot.event.SimulationListener;
import greenfoot.event.WorldEvent;
import greenfoot.event.WorldListener;
import greenfoot.platforms.SimulationDelegate;
import greenfoot.util.HDTimer;

import java.awt.EventQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

/**
 * The main class of the simulation. It drives the simulation and calls act()
 * on the objects in the world and then paints them.
 * 
 * @author Poul Henriksen
 */
public class Simulation extends Thread
    implements WorldListener
{
    // Most of the fields require synchronized access. Some of them do not because they are only
    // accessed from the simulation thread itself. "repaintLock" protects paintPending and
    // lastRepaintTime.
    
    // All user code should generally be run on the simulation thread. The simulation monitor
    // should not be held while executing user code (though the world lock should be held).
    
    // The following two constants control repainting of the world while the simulation is
    // running. We skip repaints if the simulation is running faster than the MAX_FRAME_RATE.
    // This makes the high speeds run faster, since we avoid repaints that can't be seen
    // anyway. Once requested, the repaint may take some time to occur; if the effective
    // repaint rate falls below MIN_FRAME_RATE, then we temporarily suspend the simulation
    // and wait for the repaint to occur.
    
    /** Repaints will be requested at this rate (at most) */
    private static int MAX_FRAME_RATE = 65;
    /** Simulation will wait for repaints if the repaint rate falls below this */
    private static int MIN_FRAME_RATE = 35;
    
    private WorldHandler worldHandler;
    
    /** Whether the simulation is (to be) paused */
    private boolean paused;

    /** Whether the simulation is enabled (world installed) */
    private volatile boolean enabled;

    /** Whether to run one loop when paused */
    private boolean runOnce;
    
    /** Tasks that are queued to run on the simulation thread */
    private Queue<Runnable> queuedTasks = new LinkedList<Runnable>();

    private EventListenerList listenerList = new EventListenerList();

    /* Various simulation events */
    private SimulationEvent startedEvent;
    private SimulationEvent stoppedEvent;
    private SimulationEvent disabledEvent;
    private SimulationEvent speedChangeEvent;
    private SimulationEvent debuggerPausedEvent;
    private SimulationEvent debuggerResumedEvent;
    
    private static Simulation instance;

    /** for timing the animation */
    public static final int MAX_SIMULATION_SPEED = 100;
    private int speed; // the simulation speed in range (1..100)

    private long lastDelayTime;
    private long delay; // the speed translated into delay (nanoseconds)

    // private long updates; // used for debugging to calculate update rate
    //private long lastUpdate; // used for debugging to calculate update rate
    
    /** Protects "paintPending" and "lastRepaintTime" */
    private Object repaintLock = new Object();
    /** The last time that a repaint of the World was issued. */
    private long lastRepaintTime;
    /** true if a repaint has been issued and not yet processed. */
    private boolean paintPending;
    
    private SimulationDelegate delegate;

    /**
     * Lock to synchronize access to the two fields: delaying and interruptDelay
     */
    private Object interruptLock = new Object();
    /** Whether we are currently delaying between act-loops. */
    private boolean delaying;
    /** Whether a delay between act-loops should be interrupted. */
    private boolean interruptDelay;

    
    /**
     * Used to figure out when we are transitioning from running to paused state and vice versa.
     * Only modify this from the simulation thread.
     */
    private boolean isRunning = false;
    
    /** flag to indicate that we want to abort the simulation and never start it again. */
    private volatile boolean abort;
    
    /**
     * Create new simulation. Leaves the simulation in paused state
     * 
     * @param worldHandler
     *            The handler for the world that is simulated
     */
    private Simulation(SimulationDelegate simulationDelegate)
    {
        this.setName("SimulationThread");
        this.delegate = simulationDelegate;
        startedEvent = new SimulationEvent(this, SimulationEvent.STARTED);
        stoppedEvent = new SimulationEvent(this, SimulationEvent.STOPPED);
        speedChangeEvent = new SimulationEvent(this, SimulationEvent.CHANGED_SPEED);
        disabledEvent = new SimulationEvent(this, SimulationEvent.DISABLED);
        debuggerPausedEvent = new SimulationEvent(this, SimulationEvent.DEBUGGER_PAUSED);
        debuggerResumedEvent = new SimulationEvent(this, SimulationEvent.DEBUGGER_RESUMED);
        setPriority(Thread.MIN_PRIORITY);
        paused = true;
        speed = 50;
        delay = calculateDelay(speed);
        HDTimer.init();
    }
    
    /**
     * Initialize the (singleton) simulation instance.
     * The simulation thread will not actually be started until the WorldHandler
     * is attached.
     */
    public static void initialize(SimulationDelegate simulationDelegate)
    {
        instance = new Simulation(simulationDelegate);
    }

    /**
     * Returns the simulation if it is initialised. If not, it will return null.
     */
    public static Simulation getInstance()
    {
        return instance;
    }

    /**
     * Attach this simulation to the world handler (and vice versa).
     */
    public void attachWorldHandler(WorldHandler worldHandler)
    {
        this.worldHandler = worldHandler;
        worldHandler.addWorldListener(this);
        addSimulationListener(worldHandler);
        start();
    }
    
    // The following methods should run only on the simulation thread itself!

    /**
     * Runs the simulation from the current state.
     */
    @Override
    public void run()
    {
        /* It is important this redirects to another method.
         * The debugger sets a breakpoint on the first line of this method, and if
         * that is a loop (as is the case for the first line of runContent at the time of writing)
         * then it hits the breakpoint every time.  By putting it all in a separate
         * method, we avoid that happening:
         */
        runContent();
    }
    
    private void runContent()
    {
        while (!abort) {
            try {
                maybePause();
                                
                if (worldHandler.hasWorld()) {
                    runOneLoop(worldHandler.getWorld());
                }

                delay();
            }
            catch (ActInterruptedException e) {
                // Someone interrupted the user code. We ignore it and let
                // maybePause() handle whatever needs to be done.
            }
            catch (InterruptedException e) {
                // maybePause was interrupted. Do nothing, will be handled the next time we get to maybePause.
            }
            catch (Throwable t) {
                // If any other exceptions occur, halt the simulation
                synchronized (this) {
                    paused = true;
                }
                t.printStackTrace();
            }
        }

        // The simulations has been aborted. But, we might still have to notify the world.
        synchronized (this) {
            if(isRunning) {
                World world = worldHandler.getWorld();
                if (world != null) {
                    worldStopped(world);
                }
                isRunning = false;
            } 
        }
    }   

    /**
     * Schedule some task to run on the simulation thread. The task will be run with the
     * world write lock held.
     */
    public synchronized void runLater(Runnable r)
    {
        queuedTasks.add(r);
        // If the simulation is paused we must notify so that the wait is triggered and the
        // queued task will run. We check 'paused' as well as 'enabled' since a world may
        // be instantiated via this mechanism.
        if (paused || ! enabled) {
            notify();
        }
    }
    
    public final static String PAUSED = "simulationWait";
    /**
     * A special method recognised by the debugger as indicating that the simulation
     * is pausing.
     */
    private void simulationWait() throws InterruptedException
    {
        this.wait();
    }
    
    public final static String WORLD_STARTED = "worldStarted";
    
    private static void worldStarted(World world)
    {
        world.started();
    }
    
    public final static String WORLD_STOPPED = "worldStopped";
    
    private static void worldStopped(World world)
    {
        world.stopped();
    }
    
    /**
     * Block if the simulation is paused. This will block until the simulation
     * is resumed (is both enabled and unpaused). It should only be called on the
     * simulation thread.
     * 
     * @throws InterruptedException If it couldn't acquire the world lock when
     *             signalling started()/stopped() to the world.
     */
    private void maybePause()
        throws InterruptedException
    {
        while (!abort) {
            runQueuedTasks();

            // Wait loop that waits until such time that at least one simulation
            // loop can be run.

            World world;
            boolean checkStop;
            
            synchronized (this) {
                checkStop = (paused || !enabled) && isRunning;
                world = worldHandler.getWorld();
                
                if (checkStop) {
                    isRunning = false; // if we start again, we'll need to signal it.
                    synchronized (interruptLock) {
                        interruptDelay = false;
                    }
                }
                else if (isRunning) {
                    return; // We're running and don't need to stop
                }
            }
            
            // We are either not running, or running and need to stop.
            
            if (checkStop) {
                try {
                    signalStopping(world);
                }
                catch (InterruptedException ie) {
                    continue;
                }
                    
                synchronized (this) {
                    runOnce = false;

                    if (! paused) {
                        isRunning = enabled; // Never signalled a stop, so don't signal a start
                    }
                }
            }
            
            // We're not running; we may need to resume running.
            
            boolean doResumeRunning;
            
            synchronized (this) {
                doResumeRunning = !paused && enabled && !abort && !isRunning;
                if (! isRunning && ! doResumeRunning && ! runOnce) {
                    // Still paused, so notify listeners, and actually pause
                    if (enabled) {
                        fireSimulationEvent(stoppedEvent);
                    }
                    if (worldHandler != null) {
                        worldHandler.repaint();
                    }
                    
                    if (! queuedTasks.isEmpty()) {
                        continue; // Must run queued tasks before wait
                    }
                    
                    System.gc();
                    try {
                        simulationWait();
                        lastDelayTime = System.nanoTime();
                    }
                    catch (InterruptedException e1) {
                        // Swallow the interrupt
                    }
                    
                    continue; // take it from the top
                }
            }
            
            if (doResumeRunning) {
                resumeRunning();
            }
            
            synchronized (this) {
                if (runOnce || isRunning) {
                    // Run the simulation
                    runOnce = false;
                    return;
                }
            }
        }
    
        runQueuedTasks();
    }
    
    /**
     * Send a started event and notify the world that it is now running.
     * 
     * @throws InterruptedException
     */
    private void resumeRunning() throws InterruptedException
    {
        isRunning = true;
        lastDelayTime = System.nanoTime();
        fireSimulationEvent(startedEvent);
        World world = worldHandler.getWorld();
        if (world != null) {
            // We need to sync to avoid ConcurrentModificationException
            ReentrantReadWriteLock lock = worldHandler.getWorldLock();
            try {
                lock.writeLock().lockInterruptibly();
            }
            catch (InterruptedException ie) {
                isRunning = false; // need to notify again
                throw ie;
            }
                
            try {
                worldStarted(world); // may cause us to pause
            }
            catch (Throwable t) {
                isRunning = false;
                synchronized (interruptLock) {
                    // Clear interrupted status
                    Thread.interrupted();
                    interruptDelay = false;
                }
                setPaused(true);
                t.printStackTrace();
                return;
            }
            finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Tell the world that the simulation is stopping. The world might resume
     * the simulation when this happens.
     */
    private void signalStopping(World world) throws InterruptedException
    {
        // This code will be executed when:
        //  runOnce is over  or
        //  setPaused(true)   or
        //  setEnabled(false)  or
        //  abort() (sometimes, depending on timing)
        if (world != null) {
            // We need to sync to avoid ConcurrentModificationException
            ReentrantReadWriteLock lock = worldHandler.getWorldLock();
            lock.writeLock().lockInterruptibly();
            try {
                worldStopped(world); // may un-pause
            }
            catch (ActInterruptedException aie) {
                synchronized (this) {
                    paused = true;
                }
                throw aie;
            }
            catch (Throwable t) {
                // If any exceptions occur, halt the simulation
                synchronized (this) {
                    paused = true;
                }
                t.printStackTrace();
            }
            finally {
                lock.writeLock().unlock();
            }
        }
    }

    /** This must match the method name below! */
    public static String RUN_QUEUED_TASKS = "runQueuedTasks";
    
    /**
     * Run all tasks that have been schedule to run on the simulation thread.
     * Of course, this should only be called from the simulation thread...
     * (and from an unsynchronized context).
     */
    private void runQueuedTasks()
    {
        Runnable r;
        synchronized (this) {
            r = queuedTasks.poll();
        }
        
        while (r != null) {
            World world = WorldHandler.getInstance().getWorld();
            try {
                ReentrantReadWriteLock lock  = null;
                if (world != null) {
                    lock = worldHandler.getWorldLock();
                    lock.writeLock().lock();
                }
                
                try {
                    // This may run user code, which might throw an exception.
                    r.run();
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
                
                if (world != null) {
                    lock.writeLock().unlock();
                }
            }
            finally {
                
            }
            synchronized (this) {
                r = queuedTasks.poll();
            }
        }
    }
    
    /**
     * Performs one step in the simulation. Calls act() on all actors.
     * 
     * @throws ActInterruptedException  if an act() call was interrupted.
     */
    private void runOneLoop(World world)
    {
        worldHandler.startSequence();

        // We don't want to be interrupted in the middle of an act-loop
        // so we remember the first interrupted exception and throw it
        // when all the actors have acted.
        ActInterruptedException interruptedException = null;
        
        List<? extends Actor> objects = null;

        // We need to sync to avoid ConcurrentModificationException
        try {
            ReentrantReadWriteLock lock = worldHandler.getWorldLock();
            lock.writeLock().lockInterruptibly();
            try {
                try {
                    actWorld(world);
                    if (world != worldHandler.getWorld()) {
                        return; // New world was set
                    }
                }
                catch (ActInterruptedException e) {
                    interruptedException = e;
                }
                // We need to make a copy so that the original collection can be
                // modified by the actors' act() methods.
                objects = new ArrayList<Actor>(WorldVisitor.getObjectsListInActOrder(world));
                for (Actor actor : objects) {
                    if (!enabled) {
                        return;
                    }
                    if (ActorVisitor.getWorld(actor) != null) {
                        try {
                            actActor(actor);
                            if (world != worldHandler.getWorld()) {
                                return; // New world was set
                            }
                        }
                        catch (ActInterruptedException e) {
                            if (interruptedException == null) {
                                interruptedException = e;
                            }
                        }
                    }

                }
                
                worldHandler.getKeyboardManager().clearLatchedKeys();
            }
            finally {
                lock.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            // Interrupted while trying to acquire lock
            throw new ActInterruptedException(e);
        }

        // We were interrupted while running through the act-loop. Throw now.
        if(interruptedException != null) {
            throw interruptedException;
        }
        
        // printUpdateRate(System.nanoTime());

        repaintIfNeeded();
    }
    
    // The actActor, actWorld and newInstance methods exist as a tagging mechanism
    // that allows them to be found easily in the debugger when we
    // are attempting to reach the next call to user code
    
    public static final String ACT_ACTOR = "actActor";
    private static void actActor(Actor actor)
    {
        actor.act();
    }
    
    public static final String ACT_WORLD = "actWorld";
    private static void actWorld(World world)
    {
        world.act();
    }
    
    public static final String NEW_INSTANCE = "newInstance";
    public static Object newInstance(Constructor<?> constructor)
        throws InvocationTargetException, IllegalArgumentException, InstantiationException, IllegalAccessException
    {
        return constructor.newInstance((Object[])null);
    }
    
    /**
     * Repaints the world if needed to obtain the desired frame rate.
     */
    private void repaintIfNeeded()
    {
        long currentTime = System.currentTimeMillis();
        long timeSinceLast = Math.max(1, currentTime - lastRepaintTime);
        
        if ((1000 / timeSinceLast) <= MAX_FRAME_RATE) {
            try {
                synchronized(repaintLock) {
                    
                    // Current frame rate is less than maximum, so we'll at least request
                    // a repaint at this time.
                    if (! paintPending) {
                        lastRepaintTime = currentTime;
                        worldHandler.repaint();
                        paintPending = true;
                    }
                    
                    if ((1000 / timeSinceLast) <= MIN_FRAME_RATE) {
                        // Waiting here makes sure the WorldCanvas gets a chance to
                        // repaint. It also lets the rest of the UI be responsive, even if
                        // we are running at maximum speed, by making sure events on the
                        // event queue are processed.

                        // Schedule a forced repaint, so that we don't deadlock while
                        // waiting for a repaint if something stops the repaint from
                        // occurring (no world for instance).
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run()
                            {
                                forcedRepaint();
                            }
                        });
                        
                        while (paintPending) {
                            repaintLock.wait();
                        }
                    }
                }
            }
            catch (InterruptedException ie) {}
        }
    }
    
    private void forcedRepaint()
    {
        JComponent wcanvas = WorldHandler.getInstance().getWorldCanvas();
        synchronized (repaintLock) {
            if (WorldHandler.getInstance().hasWorld()) {
                wcanvas.paintImmediately(wcanvas.getBounds());
            }
            
            if (paintPending) {
                paintPending = false;
                repaintLock.notify();
            }
        }
    }

    /**
     * Inform the simulation that the world has been repainted successfully.
     */
    public void worldRepainted()
    {
        synchronized (repaintLock) {
            paintPending = false;
            //long response = System.currentTimeMillis() - lastRepaintTime;
            //if (response > 250) {
            //    System.out.println("Repaint response time: " + response);
            //}
            repaintLock.notify();
        }
    }

    /**
     * Debug output to print the rate at which updates are performed
     * (acts/second).
     */
    /*
    private void printUpdateRate(long currentTime)
    {
        //updates++;

        long timeSinceUpdate = currentTime - lastUpdate;
        if (timeSinceUpdate > 3000000000L) {
            lastUpdate = currentTime;
            //updates = 0;
        }
    }
    */

    // Public methods etc.

    /**
     * Run one step of the simulation. Each actor in the world acts once.
     */
    public synchronized void runOnce()
    {
        // Don't call runOneLoop directly as that executes user code
        // and might hang.
        if (enabled) {
            synchronized (interruptLock) {
                interruptDelay = false;
            }
        }
        runOnce = true;
        notifyAll();
    }

    /**
     * Pauses and unpauses the simulation.
     */
    public synchronized void setPaused(boolean b)
    {
        if(paused == b) {
            //Nothing to do for us.
            return;
        }
        paused = b;
        if (enabled) {
            if(!paused) 
            {
                synchronized (interruptLock) {
                    interruptDelay = false;
                }
            }
            
            notifyAll();

            // If we are currently in the delay loop, interrupt it so that
            // the pause takes effect immediately.
            if (paused) {
                interruptDelay();                
            }
        }
    }

    /**
     * Interrupt if we are currently delaying between act-loops or the user is
     * using the Greenfoot.delay() method. This will basically jump to the next
     * act-loop as fast as possible while still executing the rest of actors in
     * the current loop. Used by setPaused() and setSpeed() to interrupt current
     * delays.
     */
    private void interruptDelay()
    {
        synchronized (interruptLock) {
            if (delaying) {
                interrupt();
            }
            else {
                // Called outside the delaying, so make sure it doesn't go into
                // the delay by signalling with this flag
                interruptDelay = true;
            }
        }
    }

    /**
     * Enable or disable the simulation.
     */
    public synchronized void setEnabled(boolean b)
    {
        if (b == enabled) {
            return;
        }

        enabled = b;
        
        if (b) {
            notifyAll();
            // fire a paused event to let listeners know we are
            // enabled again
            if (paused) {
                fireSimulationEvent(stoppedEvent);
            }
        }
        else {
            // Note that a user method might be executing even if paused (i.e. an
            // interactive method invocation by right-clicking an object in the
            // world). We need to interrupt any delay that is currently running
            // and to prevent any future delay, until the simulation is re-enabled.
            interruptDelay();
            if (! paused) {
                paused = true;
            }
            else {
                // We are paused, or at least should be.
                // We don't want interruptDelay set; we're not running, so the
                // only delay we can get is a call to Greenfoot.delay(...), which
                // goes through sleep(...). That will exit early if the simulation
                // is not enabled. If we leave interruptDelay set now, it may affect
                // a future delay.
                synchronized (interruptLock) {
                    interruptDelay = false;
                }
            }
            fireSimulationEvent(disabledEvent);
        }
    }

    private void fireSimulationEvent(SimulationEvent event)
    {
        // Guaranteed to return a non-null array
        Object[] listeners;
        synchronized (listenerList) {
            listeners = listenerList.getListenerList();

            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == SimulationListener.class) {
                    ((SimulationListener) listeners[i + 1]).simulationChanged(event);
                }
            }
        }
    }
    
    /**
     * Notify that the simulation thread has been halted or resumed by the debugger.
     */
    public void notifyThreadStatus(boolean halted)
    {
        if (halted) {
            fireSimulationEvent(debuggerPausedEvent);
        }
        else {
            // resumed
            fireSimulationEvent(debuggerResumedEvent);
        }
    }

    /**
     * Add a simulationListener to listen for changes.
     * 
     * @param l
     *            Listener to add
     */
    public void addSimulationListener(SimulationListener l)
    {
        synchronized (listenerList) {
            listenerList.add(SimulationListener.class, l);
        }
    }

    /**
     * Remove a simulationListener to listen for changes.
     * 
     * @param l
     *            Listener to remove
     */
    public void removeSimulationListener(SimulationListener l)
    {
        synchronized (listenerList) {
            listenerList.remove(SimulationListener.class, l);
        }
    }

    /**
     * Set the speed of the simulation.
     * 
     * @param speed
     *            The speed in the range (0..100)
     */
    public void setSpeed(int speed)
    {
        if (speed < 0) {
            speed = 0;
        }
        else if (speed > MAX_SIMULATION_SPEED) {
            speed = MAX_SIMULATION_SPEED;
        }

        boolean speedChanged;
        synchronized (this) {
            speedChanged = this.speed != speed;
            if (speedChanged) {
                this.speed = speed;
                
                delegate.setSpeed(speed);
                
                this.delay = calculateDelay(speed);

                // If simulation is running we should interrupt any waiting or
                // sleeping that is currently happening.
                
                if(!paused) {
                    synchronized (interruptLock) {
                        if (delaying) {
                            interrupt();
                        }
                    }
                }    
            }
        }
        
        if (speedChanged) {
            fireSimulationEvent(speedChangeEvent);
        }
    }

    /**
     * Returns the delay as a function of the speed.
     * 
     * @return The delay in nanoseconds.
     */
    private long calculateDelay(int speed)
    {
        // Make the speed into a delay
        long rawDelay = MAX_SIMULATION_SPEED - speed;

        long min = 30 * 1000L; // Delay at MAX_SIMULATION_SPEED - 1
        long max = 10000 * 1000L * 1000L; // Delay at slowest speed

        double a = Math.pow(max / (double) min, 1D / (MAX_SIMULATION_SPEED - 1));
        long delay = 0;
        if (rawDelay > 0) {
            delay = (long) (Math.pow(a, rawDelay - 1) * min);
        }
        return delay;
    }

    /**
     * Get the current simulation speed.
     * 
     * @return The speed in the range (1..100)
     */
    public synchronized int getSpeed()
    {
        return speed;
    }

    /**
     * Sleep an amount of time according to the current speed setting for this
     * simulation. This will wait without considering previous waits, as opposed
     * to delay(). It should be called only from the simulation thread, in an
     * unsynchronized context.
     */
    public void sleep(int numCycles)
    {
        World world = worldHandler.getWorld();

        synchronized (this) {
            if (paused && isRunning && !runOnce) {
                // If it should be paused but is still running, it means that we
                // should try to end as quickly as possible and hence should NOT
                // delay.
                // If the user is interactively invoking a method that calls this
                // method, it will not be caught here, which is the correct
                // behaviour. Otherwise the call to sleep() will have no visible
                // effect at all.
                return;
            }
            if (! enabled) {
                // It's possible an interactive method invocation was fired which
                // calls Greenfoot.delay(), but the simulation was disabled (reset)
                // between the invocation and call to Greenfoot.delay(). In this
                // case, we don't want to delay.
                return;
            }
            
            // We need to check the interruptDelay while the simulation lock is
            // still held, in case setEnabled(false) is called just after we release
            // the simulation lock.
            synchronized (interruptLock) {
                if (interruptDelay) {
                    // If interrupted, we just want to return now. We do not
                    // want to abort by throwing an exception, because that will
                    // leave the user code execution in an inconsistent state.
                    return;
                }
                delaying = true;
            }
        }
        
        try {
            worldHandler.repaint();
            for (int i = 0; i < numCycles; i++) {
                if (world != null) {
                    // The WorldCanvas may be trying to synchronize on the world in
                    // order to do a repaint. So, we use wait() here in order
                    // to release the world lock temporarily.
                    HDTimer.wait(delay, worldHandler.getWorldLock());
                }
                else {
                    // shouldn't really happen
                    HDTimer.sleep(delay);
                }
            }
        }
        catch (InterruptedException e) {
            // If interrupted, we just want to return now. We do not
            // want to abort by throwing an exception, because that will
            // leave the user code execution in an inconsistent state.            
        }
        finally {
            synchronized (interruptLock) {
                Thread.interrupted(); // clear interrupt, in case we were interrupted just after the delay
                interruptDelay = false;
                delaying = false;
            }
        }
    }

    /**
     * Cause a delay (wait) according to the current speed setting for this
     * simulation. It will take the time spend in this simulation loop into
     * consideration and only pause the remaining time.
     * 
     * <p>This method is used for controlling the speed of the animation.
     * 
     * <p>The world lock should not be held when this method is called, so
     * that repaints can occur.
     */
    private void delay()
    {
        long currentTime = System.nanoTime();
        long timeElapsed = currentTime - lastDelayTime;
        long actualDelay = Math.max(delay - timeElapsed, 0L);
        
        synchronized (this) {
            synchronized (interruptLock) {
                if(interruptDelay) {
                    // interruptDelay was issued before entering this sync, so interrupt now.
                    interruptDelay = false;
                    if (paused || abort) {
                        lastDelayTime = currentTime;
                        return; // return... without delay
                    }
                }
                delaying = true;
            }
        }

        while (actualDelay > 0) {

            try {
                HDTimer.sleep(actualDelay);
            }
            catch (InterruptedException ie) {
                // We get interrupted either due to a pause, abort, being disabled or
                // a speed change. If it's a speed change, we can continue to delay, up
                // to the new time; otherwise we should finish up now.
                synchronized (this) {
                    if (!enabled || paused || abort) {
                        break;
                    }
                }
            }

            currentTime = System.nanoTime();
            timeElapsed = currentTime - lastDelayTime;
            actualDelay = delay - timeElapsed;
        }

        lastDelayTime = currentTime;
        synchronized (interruptLock) {
            Thread.interrupted(); // clear interrupt, in case we were interrupted just after the delay
            interruptDelay = false;
            delaying = false;
        }
    }

    /**
     * Abort the simulation. It abruptly stops what is running and ends the
     * simulation thread, and it is not possible to start it again.
     */
    public void abort()
    {
        abort = true;
        setEnabled(false);
    }


    // ---------- WorldListener interface -----------

    /**
     * A new world was created - we're ready to go. Enable the simulation
     * functions.
     */
    @Override
    public void worldCreated(WorldEvent e)
    {
        setEnabled(true);
    }

    /**
     * The world was removed - disable the simulation functions.
     */
    @Override
    public void worldRemoved(WorldEvent e)
    {
        setEnabled(false);
    }

    // ----------- End of WorldListener interface -------------

}



/*
 This file is part of the Greenfoot program. 
 Copyright (C) 2005-2009,2010,2011,2012,2013,2014,2015  Poul Henriksen and Michael Kolling 
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package greenfoot;

import greenfoot.util.GraphicsUtilities;
import greenfoot.util.GreenfootUtil;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;


/**
 * An image to be shown on screen. The image may be loaded from an image file
 * and/or drawn by using various drawing methods.
 * 
 * @author Poul Henriksen
 * @version 2.6
 */
public class GreenfootImage
{
    private static final Color DEFAULT_BACKGROUND = new Color(255,255,255,0);
    private static final Color DEFAULT_FOREGROUND = Color.BLACK;
    
    /** The image name and url are primarily used for debugging. */
    private String imageFileName;
    private URL imageUrl;
    
    private BufferedImage image;
    private static MediaTracker tracker;
    
    private Color currentColor = DEFAULT_FOREGROUND;
    private Font currentFont;
    
    /**
     * Copy on write is used for performance reasons. If an image is
     * copyOnWrite, it means that the actual image data might be shared between
     * several GreenfootImage instances. As soon as a copy-on-write GreenfootImage is
     * modified, it is necessary to create a copy of the image, in order not to
     * change the image for the rest of the GreenfootImages sharing this image.
     * This flag is used to keep track of whether it is a shared image that
     * needs to be copied upon write (changes) to the image.
     */
    private boolean copyOnWrite = false;
    
    /**
     * Value from 0 to 255, with 0 being completely transparent and 255 being opaque.
     */
    private int transparency = 255;

    /**
     * Create an image from an image file. Supported file formats are JPEG, GIF
     * and PNG.
     * 
     * <p>The file name may be an absolute path, or a base name for a file located in
     * the project directory.
     * 
     * @param filename Typically the name of a file in the images directory within
     *            the project directory.
     * @throws IllegalArgumentException If the image can not be loaded.
     */
    public GreenfootImage(String filename)
        throws IllegalArgumentException
    {
        GreenfootImage gImage = GreenfootUtil.getCachedImage(filename);
        if (gImage != null)
        {
            createClone(gImage);
        }
        else 
        {
            try{
                loadFile(filename);
            }
            catch(IllegalArgumentException ile){
                GreenfootUtil.addCachedImage(filename, null);
                throw ile;
            }
        }
        //if the image was successfully cached, ensure that the image is copyOnWrite
        boolean success = GreenfootUtil.addCachedImage(filename, new GreenfootImage(this));
        if (success){
            copyOnWrite = true;
        }
    }
       
    /**
     * Create an empty (transparent) image with the specified size.
     * 
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     */
    public GreenfootImage(int width, int height)
    {
        setImage(GraphicsUtilities.createCompatibleTranslucentImage(width, height));
    }

    /**
     * Create a GreenfootImage from another GreenfootImage.
     * 
     * @param image The source image to be copied.
     */
    public GreenfootImage(GreenfootImage image)
        throws IllegalArgumentException
    {
        if (! image.copyOnWrite) {
            setImage(GraphicsUtilities.createCompatibleTranslucentImage(image.getWidth(), image.getHeight()));
            Graphics2D g = getGraphics();
            g.setComposite(AlphaComposite.Src);
            g.drawImage(image.getAwtImage(), 0, 0, null);
            g.dispose();
        }
        else {
            // If the source image is a copy-on-write image, we can easily
            // make this a copy-on-write image as well.
            this.image = image.image;
            copyOnWrite = true;
        }
        copyStates(image, this);
    }
    
    /**
     * Creates an image with the given string drawn as text using the given font size, with the given foreground
     * color on the given background color.  If the string has newline characters, it
     * is split into multiple lines which are drawn horizontally-centred.
     * 
     * @param string the string to be drawn
     * @param size the requested height in pixels of each line of text (the actual height may be different by a pixel or so)
     * @param foreground the color of the text.  Since Greenfoot 2.2.0, passing null will use black.
     * @param background the color of the image behind the text.  Since Greenfoot 2.2.0, passing null with leave the background transparent.
     * @since 2.0.1
     */
    public GreenfootImage(String string, int size, Color foreground, Color background)
    {
        this(string, size, foreground, background, null);
    }
    
    /**
     * Creates an image with the given string drawn as text using the given font size, with the given foreground
     * color on the given background color.  If the string has newline characters, it
     * is split into multiple lines which are drawn horizontally-centred.
     * 
     * @param string the string to be drawn
     * @param size the requested height in pixels of each line of text (the actual height may be different by a pixel or so)
     * @param foreground the color of the text.  Since Greenfoot 2.2.0, passing null will use black.
     * @param background the color of the image behind the text.  Since Greenfoot 2.2.0, passing null with leave the background transparent.
     * @param outline the colour of the outline that will be drawn around the text.  Passing null will draw no outline.
     * @since 2.4.0
     */
    public GreenfootImage(String string, int size, Color foreground, Color background, Color outline)
    {
        String[] lines = GraphicsUtilities.splitLines(string);
        GraphicsUtilities.MultiLineStringDimensions d = GraphicsUtilities.getMultiLineStringDimensions(lines, Font.BOLD, size);
        image = GraphicsUtilities.createCompatibleTranslucentImage(d.getWidth(), d.getHeight());
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(background == null ? new Color(0, 0, 0, 0) : background);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        GraphicsUtilities.drawOutlinedText(g, d, foreground, outline);
        g.dispose();
    }

    //Package-visible:
    GreenfootImage(byte[] imageData)
    {
        try {
            image = GraphicsUtilities.loadCompatibleTranslucentImage(imageData);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load image" + (imageFileName != null ? (" from: " + imageFileName) : ""));
        }
    }  

    private GreenfootImage() { }
    
    /**
     * Create a copy-on-write image based on this image. If the new image is
     * modified, the original image will not be affected.
     * <p>
     * Only use this method if you are sure that the original image will never
     * be modified.
     */
    GreenfootImage getCopyOnWriteClone()
    {
        GreenfootImage clone = new GreenfootImage();
        clone.copyOnWrite = true;
        clone.image = image;
        copyStates(this, clone);
        
        return clone;
    }
    
    /**
     * Creates a copy of the cached image
     * @param cachedImage image to copy
     */
    void createClone(GreenfootImage cachedImage)
    {
        this.copyOnWrite = true;
        this.image = cachedImage.image;
        copyStates(cachedImage, this);
    }
    
    /**
     * Copies the states from the src image to dst image.
     */
    private static void copyStates(GreenfootImage src, GreenfootImage dst)
    {
        dst.imageFileName = src.imageFileName;
        dst.imageUrl = src.imageUrl;
        dst.currentColor = src.currentColor;
        dst.currentFont = src.currentFont;
        dst.transparency = src.transparency;
    }    
    
    private void loadURL(URL imageURL)
        throws IllegalArgumentException
    {
        if (imageURL == null) {
            throw new NullPointerException("Image URL must not be null.");
        }
        try {
            image = GraphicsUtilities.loadCompatibleTranslucentImage(imageURL);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load image from: " + imageFileName);
        }
    }

    /**
     * Tries to find the filename using the classloader. It first searches in
     * 'projectdir/images/', then in the 'projectdir' and last as an absolute
     * filename or URL
     * 
     * @param filename Name of the image file
     * @throws IllegalArgumentException If it could not read the image.
     */
    private void loadFile(String filename)
        throws IllegalArgumentException
    {
        if (filename == null) {
            throw new NullPointerException("Filename must not be null.");
        }
        imageFileName = filename;
        try {
            imageUrl = GreenfootUtil.getURL(filename, "images");
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);           
        }
        loadURL(imageUrl);
    }

    /**
     * Sets the image to the specified AWT image
     * 
     * @param image
     */
    private void setImage(Image image)
        throws IllegalArgumentException
    {
        if (image == null) {
            throw new IllegalArgumentException("Image must not be null.");
        }
        this.image = getBufferedImage(image);
        copyOnWrite = false;
    }


    /**
     * Returns the java.awt.image.BufferedImage that backs this GreenfootImage. Any changes to
     * the returned image will be reflected in the GreenfootImage.
     * 
     * @return The java.awt.image.BufferedImage backing this GreenfootImage
     * @since Greenfoot version 1.0.2
     */
    public BufferedImage getAwtImage()
    {
        ensureWritableImage();
        return image;
    }
    
    /**
     * Remember to call dispose() when no longer using the graphics object.
     */
    private Graphics2D getGraphics()
    {
        if (copyOnWrite) {
        ensureWritableImage();
        }
        Graphics2D graphics = image.createGraphics();
        initGraphics(graphics);
        return graphics;
    }

    /**
     * Initialises the graphics. Should be called whenever we have created a
     * graphics for this image.
     */
    private void initGraphics(Graphics2D graphics)
    {
        if(graphics != null) {
            graphics.setBackground(DEFAULT_BACKGROUND);
            graphics.setColor(currentColor);
            if(currentFont != null) {
                graphics.setFont(currentFont);
            }
        }
    }

    /**
     * Return the width of the image.
     * 
     * @return Width of the image.
     */
    public int getWidth()
    {
        return image.getWidth(null);
    }

    /**
     * Return the height of the image.
     * 
     * @return Height of the image.
     */
    public int getHeight()
    {
        return image.getHeight(null);
    }
    

    /**
     * Rotates this image around the center.
     * 
     * @param degrees The number of degrees the object will rotate for.
     */
    public void rotate(int degrees)
    {
        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(degrees), getWidth()/2., getHeight()/2.);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage newImage = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
        setImage(op.filter(image, newImage));
    }

    /**
     * Scales this image to a new size.
     * 
     * @param width Width of new image
     * @param height Height of new image
     */
    public void scale(int width, int height)
    {
        if (width == image.getWidth() && height == image.getHeight())
            return;
        
        // getScaledInstance is too slow, see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6196792
        // This is adapted from: http://java.sun.com/products/java-media/2D/reference/faqs/index.html#Q_How_do_I_create_a_resized_copy
        BufferedImage scaled = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
        Graphics2D g = scaled.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        setImage(scaled);
    }

    /**
     * Mirrors the image vertically (the top of the image becomes the bottom, and vice versa).
     */
    public void mirrorVertically()
    {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        setImage(op.filter(image, null));
    }

    /**
     * Mirrors the image horizontally (the left of the image becomes the right, and vice versa).
     */
    public void mirrorHorizontally()
    {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        setImage(op.filter(image, null));
    }

    /**
     * Fill the entire image with the current drawing color.
     */
    public void fill()
    {
        Graphics g = getGraphics();
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    /**
     * Draws the given Image onto this image
     * 
     * @param image The image to draw onto this one.
     * @param x x-coordinate for drawing the image.
     * @param y y-coordinate for drawing the image.
     */
    public void drawImage(GreenfootImage image, int x, int y)
    {
        Graphics2D g = getGraphics();
        image.drawImage(g, x, y, null, true);
        g.dispose();
    }

    /**
     * Draws this image onto the given Graphics object.
     * 
     * @param useTransparency Whether the transparency value should be used when
     *            drawing the image.
     */
    void drawImage(Graphics2D g, int x, int y, ImageObserver observer, boolean useTransparency)
    {
        Composite oldComposite = null;
        if(useTransparency) {
            float opacity = getTransparency() / 255f;
            if(opacity < 1) {
                // Don't bother with the composite if completely opaque.
                if(opacity < 0) opacity = 0;
                oldComposite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            }
        }
        
        g.drawImage(image, x, y, observer);

        if(oldComposite != null) {
            g.setComposite(oldComposite);
        }
    }
    
    /**
     * Set the current font. This font will be used for subsequent text operations.
     * 
     * @param f The new Font to be used.
     */
    public void setFont(Font f)
    {
        currentFont = f;
    }
    
    /**
     * Get the current font.
     * 
     * @return The current used font, if none, set it as the Graphics font, then return it.
     */
    public Font getFont()
    {        
        if(currentFont == null) {
            currentFont = getGraphics().getFont();
        }
        return currentFont;
    }

    /**
     * Set the current drawing color. This color will be used for subsequent
     * drawing operations.
     * 
     * @param color The color to be used.
     */
    public void setColor(Color color)
    {
        currentColor = color;
    }

    /**
     * Return the current drawing color.
     * 
     * @return The current color.
     */
    public Color getColor()
    {
        return currentColor;
    }

    /**
     * Return the color at the given pixel.
     * 
     * @throws IndexOutOfBoundsException If the pixel location is not within the
     *             image bounds.
     * @param x The horizontal coordinate of the pixel.
     * @param y The vertical coordinate of the pixel.
     * @return The Color at the specific pixel.
     */
    public Color getColorAt(int x, int y)
    {
        return new Color(getRGBAt(x, y), true); 
    }
    
    /**
     * Sets the given pixel to the given color.
     * 
     * @param x The horizontal coordinate of the pixel.
     * @param y The vertical coordinate of the pixel.
     * @param color The Color to be assigned at the specific pixel.
     */
    public void setColorAt(int x, int y, Color color) {
        setRGBAt(x, y, color.getRGB());
    }

    /**
     * Set the transparency of the image.
     * 
     * @param t A value in the range 0 to 255. 0 is completely transparent
     *            (invisible) and 255 is completely opaque (the default).
     */
    public void setTransparency(int t)
    {
        if (t < 0 || t > 255) {
            throw new IllegalArgumentException("The transparency value has to be in the range 0 to 255. It was: " + t);
        }

        this.transparency = t;
    }

    /**
     * Return the current transparency of the image.
     * 
     * @return A value in the range 0 to 255. 0 is completely transparent
     *         (invisible) and 255 is completely opaque (the default).
     */
    public int getTransparency()
    {
        return transparency;
    }    
    
    private int getRGBAt(int x, int y)
    {
        if (x >= getWidth()) {
            throw new IndexOutOfBoundsException("X is out of bounds. It was: " + x
                    + " and it should have been smaller than: " + getWidth());
        }
        if (y >= getHeight()) {
            throw new IndexOutOfBoundsException("Y is out of bounds. It was: " + y
                    + " and it should have been smaller than: " + getHeight());
        }
        if (x < 0) {
            throw new IndexOutOfBoundsException("X is out of bounds. It was: " + x
                    + " and it should have been at least: 0");
        }
        if (y < 0) {
            throw new IndexOutOfBoundsException("Y is out of bounds. It was: " + y
                    + " and it should have been at least: 0");
        }

        return image.getRGB(x,y);
    }
    
    private void setRGBAt(int x, int y, int rgb)
    {
        if (x >= getWidth()) {
            throw new IndexOutOfBoundsException("X is out of bounds. It was: " + x
                    + " and it should have been smaller than: " + getWidth());
        }
        if (y >= getHeight()) {
            throw new IndexOutOfBoundsException("Y is out of bounds. It was: " + y
                    + " and it should have been smaller than: " + getHeight());
        }
        if (x < 0) {
            throw new IndexOutOfBoundsException("X is out of bounds. It was: " + x
                    + " and it should have been at least: 0");
        }
        if (y < 0) {
            throw new IndexOutOfBoundsException("Y is out of bounds. It was: " + y
                    + " and it should have been at least: 0");
        }

        ensureWritableImage();
        image.setRGB(x,y,rgb);
    }
 
    /**
     * Fill the specified rectangle. The left and right edges of the rectangle
     * are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom
     * edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting
     * rectangle covers an area <code>width</code> pixels wide by
     * <code>height</code> pixels tall. The rectangle is filled using the
     * current color.
     * 
     * @param x the <i>x </i> coordinate of the rectangle to be filled.
     * @param y the <i>y </i> coordinate of the rectangle to be filled.
     * @param width the width of the rectangle to be filled.
     * @param height the height of the rectangle to be filled.
     */
    public void fillRect(int x, int y, int width, int height)
    {
        Graphics2D g = getGraphics();
        g.fillRect(x, y, width, height);
        g.dispose();
    }

    /**
     * Clears the image.
     */
    public void clear()
    {
        Graphics2D g = getGraphics();
        g.clearRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    /**
     * Draw the outline of the specified rectangle. The left and right edges of
     * the rectangle are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width</code>. The top and bottom edges are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>. The rectangle
     * is drawn using the current color.
     * 
     * @param x the <i>x </i> coordinate of the rectangle to be drawn.
     * @param y the <i>y </i> coordinate of the rectangle to be drawn.
     * @param width the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     */
    public void drawRect(int x, int y, int width, int height)
    {
        Graphics2D g = getGraphics();
        g.drawRect(x, y, width, height);
        g.dispose();
    }

    /**
     * Draw the text given by the specified string, using the current font and
     * color. The baseline of the leftmost character is at position ( <i>x
     * </i>,&nbsp; <i>y </i>).
     * 
     * @param string the string to be drawn.
     * @param x the <i>x </i> coordinate.
     * @param y the <i>y </i> coordinate.
     */
    public void drawString(String string, int x, int y)
    {
        Graphics2D g = getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int height = g.getFontMetrics(g.getFont()).getHeight();
        
        String[] lines = GraphicsUtilities.splitLines(string);
        for (int i = 0; i < lines.length; i++) {
            g.drawString(lines[i], x, y + (i * height));
        }
        
        g.dispose();
    }

    /**
     * Draw a shape directly on the image. Shapes are specified by the <a href=
     * "http://java.sun.com/javase/6/docs/api/java/awt/Shape.html">shape
     * interface</a>.
     * @param shape the shape to be drawn.
     */
    public void drawShape(Shape shape)
    {
        Graphics2D g = getGraphics();
        g.draw(shape);
        g.dispose();
    }

    /**
     * Draw a filled shape directly on the image. Shapes are specified by the
     * <a href="http://java.sun.com/javase/6/docs/api/java/awt/Shape.html">shape
     * interface</a>.
     * @param shape the shape to be filled.
     */
    public void fillShape(Shape shape)
    {
        Graphics2D g = getGraphics();
        g.fill(shape);
        g.dispose();
    }

    /**
     * Fill an oval bounded by the specified rectangle with the current drawing
     * color.
     * 
     * @param x the <i>x </i> coordinate of the upper left corner of the oval to
     *            be filled.
     * @param y the <i>y </i> coordinate of the upper left corner of the oval to
     *            be filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     */
    public void fillOval(int x, int y, int width, int height)
    {
        Graphics2D g = getGraphics();
        g.fillOval(x, y, width, height);
        g.dispose();
    }

    /**
     * Draw an oval bounded by the specified rectangle with the current drawing
     * color.
     * 
     * @param x the <i>x </i> coordinate of the upper left corner of the oval to
     *            be drawn.
     * @param y the <i>y </i> coordinate of the upper left corner of the oval to
     *            be drawn.
     * @param width the width of the oval to be drawn.
     * @param height the height of the oval to be drawn.
     */
    public void drawOval(int x, int y, int width, int height)
    {
        Graphics2D g = getGraphics();
        g.drawOval(x, y, width, height);
        g.dispose();
    }

    /**
     * Fill a closed polygon defined by arrays of <i>x </i> and <i>y </i>
     * coordinates.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code> line
     * segments are line segments from
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code>
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 1&nbsp;&le;&nbsp;
     * <i>i </i>&nbsp;&le;&nbsp; <code>nPoints</code>. The figure is
     * automatically closed by drawing a line connecting the final point to the
     * first point, if those points are different.
     * <p>
     * The area inside the polygon is defined using an even-odd fill rule, also
     * known as the alternating rule.
     * 
     * @param xPoints an array of <code>x</code> coordinates.
     * @param yPoints an array of <code>y</code> coordinates.
     * @param nPoints the total number of points.
     */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
    {
        Graphics2D g = getGraphics();
        g.fillPolygon(xPoints, yPoints, nPoints);
        g.dispose();
    }

    /**
     * Draws a closed polygon defined by arrays of <i>x</i> and <i>y</i>
     * coordinates. Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates
     * defines a point.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code> line
     * segments are line segments from
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code>
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
     * The figure is automatically closed by drawing a line connecting the final
     * point to the first point, if those points are different.
     * 
     * @param xPoints an array of <code>x</code> coordinates.
     * @param yPoints an array of <code>y</code> coordinates.
     * @param nPoints the total number of points.
     */
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
    {
        Graphics2D g = getGraphics();
        g.drawPolygon(xPoints, yPoints, nPoints);
        g.dispose();
    }

    /**
     * Draw a line, using the current drawing color, between the points
     * <code>(x1,&nbsp;y1)</code> and <code>(x2,&nbsp;y2)</code>.
     * 
     * @param x1 the first point's <i>x </i> coordinate.
     * @param y1 the first point's <i>y </i> coordinate.
     * @param x2 the second point's <i>x </i> coordinate.
     * @param y2 the second point's <i>y </i> coordinate.
     */
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        Graphics2D g = getGraphics();
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }

    /**
     * Return a text representation of the image.
     */
    public String toString()
    {
        String superString = super.toString();
        if (imageFileName == null) {
            return superString;
        }
        else {
            return "Image file name: " + imageFileName +   "   Image url: " + imageUrl + "  " + superString;
        }
    }
    
    static boolean equal(GreenfootImage image1, GreenfootImage image2)
    {
        if (image1 == null || image2 == null) {
            return image1 == image2;
        }
        else {
            return (image1.image == image2.image || image1.equals(image2));
        }
    }

    /**
     * Ensure we have an image which we are allowed to write to. If we are
     * a copy-on-write image, create a copy of the image (and set up the
     * graphics2d object) before returning.
     */
    private void ensureWritableImage()
    {
        if (copyOnWrite) {
            BufferedImage bImage = GraphicsUtilities.createCompatibleTranslucentImage(image.getWidth(null), image.getHeight(null));
            Graphics2D graphics = bImage.createGraphics();
            initGraphics(graphics);
            graphics.drawImage(image, 0, 0, null);
            image = bImage;
            copyOnWrite = false;
            graphics.dispose();
        }
    }
    
    /**
     * Gets a BufferedImage of the AWT Image that this GreenfootImage
     * represents. We need this for some of the image manipulation methods.
     */
    private static BufferedImage getBufferedImage(Image image)
    {
        if (image instanceof BufferedImage) {}
        else if (image instanceof VolatileImage) {
            image = ((VolatileImage) image).getSnapshot();
            waitForImageLoad(image);
        }
        else {
            waitForImageLoad(image);
            BufferedImage bImage = GraphicsUtilities.createCompatibleTranslucentImage(image.getWidth(null), image.getHeight(null));
            Graphics g = bImage.getGraphics();
            g.drawImage(image, 0, 0, null);
            image = bImage;
        }
        return (BufferedImage) image;
    }
        
    /**
     * Wait until the image is fully loaded and then init the graphics.
     * 
     */
    private static void waitForImageLoad(Image image)
    {
        if (tracker == null) {
            tracker = new MediaTracker(new Component() {});
        }
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
            tracker.removeImage(image);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*
 This file is part of the Greenfoot program. 
 Copyright (C) 2005-2009,2010,2014  Poul Henriksen and Michael Kolling 
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package greenfoot;

import java.util.List;

/**
 * Test object that can easily be configured to having different sizes.
 * 
 * @author Poul Henriksen
 */
public class TestObject extends Actor
{
    /**
     * A test object with an image size of 7x7. Using 7x7 gives a size just less
     * than 10x10 if rotating 45 degrees. This makes it suitable to test a scenario
     * where the gridsize is 10x10 and we do not want objects to extent to more than
     * one cell.
     */
    public TestObject()
    {
        this(7, 7);
    }
    
    public TestObject(int width, int height) {
        GreenfootImage image = new GreenfootImage(width, height);
        setImage(image);
    }

    @SuppressWarnings("unchecked")
    public List getNeighboursP(int distance, boolean diagonal, Class cls)
    {
        return getNeighbours(distance, diagonal, cls);
    }

    @SuppressWarnings("unchecked")
    public List getObjectsInRangeP(int distance, Class cls)
    {
        return getObjectsInRange(distance, cls);
    }

    public boolean intersectsP(Actor other)
    {
        return intersects(other);
    }

    @SuppressWarnings("unchecked")
    public List getIntersectingObjectsP(Class cls)
    {
        return getIntersectingObjects(cls);
    }

    @SuppressWarnings("unchecked")
    public List getObjectsAtP(int dx, int dy, Class cls)
    {
        return getObjectsAtOffset(dx, dy, cls);
    }

    public Actor getOneIntersectingObjectP(Class<? extends Actor> cls)
    {
       return getOneIntersectingObject(cls);
    }

    public Actor getOneObjectAtP(int dx, int dy, Class<? extends Actor> cls)
    {
        return getOneObjectAtOffset(dx, dy, cls);
    }
}
