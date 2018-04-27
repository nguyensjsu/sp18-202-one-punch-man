/*
 This file is part of the Greenfoot program. 
 Copyright (C) 2005-2009,2010,2011,2013,2014,2015  Poul Henriksen and Michael Kolling 
 
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

import greenfoot.collision.ibsp.Rect;
import greenfoot.core.WorldHandler;
import greenfoot.platforms.ActorDelegate;
import greenfoot.util.GreenfootUtil;

import java.util.List;

/**
 * An Actor is an object that exists in the Greenfoot world. 
 * Every Actor has a location in the world, and an appearance (that is:
 * an icon).
 * 
 * <p>An Actor is not normally instantiated, but instead used as a superclass
 * to more specific objects in the world. Every object that is intended to appear
 * in the world must extend Actor. Subclasses can then define their own
 * appearance and behaviour.
 * 
 * <p>One of the most important aspects of this class is the 'act' method. This method
 * is called when the 'Act' or 'Run' buttons are activated in the Greenfoot interface.
 * The method here is empty, and subclasses normally provide their own implementations.
 * 
 * @author Poul Henriksen
 * @version 2.5
 */
public abstract class Actor
{
    /** Error message to display when trying to use methods that requires a world. */
    private static final String NO_WORLD = "An actor is trying to access the world, when no world has been instantiated.";

    /** Error message to display when trying to use methods that requires the actor be in a world. */
    private static final String ACTOR_NOT_IN_WORLD = "Actor not in world. An attempt was made to use the actor's location while it is not in the world. Either it has not yet been inserted, or it has been removed.";

    /** Counter of number of actors constructed, used as a hash value */
    private static int sequenceNumber = 0;

    /**
     * x-coordinate of the object's location in the world. The object is
     * centered around this location.
     */
    int x;

    /**
     * y-coordinate of the object's location in the world. The object is
     * centered around this location.
     */
    int y;

    /**
     * Sequence number of this actor
     */
    private int mySequenceNumber;

    /**
     * The last time objects in the world were painted, where was this object
     * in the sequence?
     */
    private int lastPaintSequenceNumber;

    /** Rotation in degrees (0-359) */
    int rotation = 0;

    /** Reference to the world that this actor is a part of. */
    World world;

    /** The image for this actor. */
    private GreenfootImage image;

    /** Field used to store some extra data in an object. Used by collision checkers. */
    private Object data;

    static GreenfootImage greenfootImage;

    /** Axis-aligned bounding rectangle of the object, in pixels. */
    private Rect boundingRect;
    /** X-coordinates of the rotated bounding rectangle's corners */
    private int[] boundingXs = new int[4];
    /** Y-coordinates of the rotated bounding rectangle's corners */
    private int[] boundingYs = new int[4];

    static {
        //Do this in a 'try' since a failure at this point will crash Greenfoot.
        try {
            greenfootImage = new GreenfootImage(GreenfootUtil.getGreenfootLogoPath());
        }
        catch (Exception e) {
            // Should not happen unless the Greenfoot installation is seriously broken.
            e.printStackTrace();
            System.err.println("Greenfoot installation is broken - reinstalling Greenfoot might help.");
        }
    }

    /**
     * Construct an Actor.
     * The object will have a default image.
     */
    public Actor()
    {
        // Use the class image, if one is defined, as the default image, or the
        // Greenfoot logo image otherwise
        mySequenceNumber = sequenceNumber++;
        GreenfootImage image = getClassImage();
        if (image == null) {
            image = greenfootImage;
        }

        // Make the image a copy of the original to avoid modifications to the
        // original.
        image = image.getCopyOnWriteClone();

        setImage(image);
    }

    /**
     * The act method is called by the greenfoot framework to give actors a
     * chance to perform some action. At each action step in the environment,
     * each object's act method is invoked, in unspecified order.
     * 
     * <p>The default implementation does nothing. This method should be overridden in
     * subclasses to implement an actor's action.
     */
    public void act()
    {
    }

    /**
     * Return the x-coordinate of the actor's current location. The
     * value returned is the horizontal index of the actor's cell in the world.
     * 
     * @return The x-coordinate of the object's current location.
     * @throws IllegalStateException If the actor has not been added into a world.
     */
    public int getX() throws IllegalStateException
    {
        failIfNotInWorld();
        return x;
    }

    /**
     * Return the y-coordinate of the object's current location. The
     * value returned is the vertical index of the actor's cell in the world.
     * 
     * @return The y-coordinate of the actor's current location
     * @throws IllegalStateException If the actor has not been added into a world.
     */
    public int getY()
    {
        failIfNotInWorld();
        return y;
    }

    /**
     * Return the current rotation of this actor. Rotation is expressed as a degree
     * value, range (0..359). Zero degrees is towards the east (right-hand side of
     * the world), and the angle increases clockwise.
     * 
     * @see #setRotation(int)
     * 
     * @return The rotation in degrees.
     */
    public int getRotation()
    {
        return rotation;
    }

    /**
     * Set the rotation of this actor. Rotation is expressed as a degree
     * value, range (0..359). Zero degrees is to the east (right-hand side of the
     * world), and the angle increases clockwise.
     * 
     * @param rotation The rotation in degrees.
     * 
     * @see #turn(int)
     */
    public void setRotation(int rotation)
    {
        // First normalize
        if (rotation >= 360) {
            // Optimize the usual case: rotation has adjusted to a value greater than
            // 360, but is still within the 360 - 720 bound.
            if (rotation < 720) {
                rotation -= 360;
            }
            else {
                rotation = rotation % 360;
            }
        }
        else if (rotation < 0) {
            // Likwise, if less than 0, it's likely that the rotation was reduced by
            // a small amount and so will be >= -360.
            if (rotation >= -360) {
                rotation += 360;
            }
            else {
                rotation = 360 + (rotation % 360);
            }
        }
        
        if (this.rotation != rotation) {
            this.rotation = rotation;
            // Recalculate the bounding rect.
            boundingRect = null;
            // since the rotation have changed, the size probably has too.
            sizeChanged();
        }
    }
    
    /**
     * Turn this actor to face towards a certain location.
     * 
     * @param x  The x-coordinate of the cell to turn towards
     * @param y  The y-coordinate of the cell to turn towards
     */
    public void turnTowards(int x, int y)
    {
        double a = Math.atan2(y - this.y, x - this.x);
        setRotation((int) Math.toDegrees(a));
    }
    
    /**
     * Detect whether the actor has reached the edge of the world.
     * 
     * The actor is at the edge of the world if their position is
     * at, or beyond, the cells at the very edge of the world.  For example,
     * if your world is 640 by 480 pixels, an actor is at the edge if its
     * X position is &lt;= 0 or &gt;= 639, or its Y position is &lt;= 0 or &gt;= 479.
     * 
     * @return True if the actor is at or beyond the edge cell of the world, and false otherwise.
     */
    public boolean isAtEdge()
    {
        failIfNotInWorld();
        // We use <=,>= not == because actors can be outside the world bounds, and 
        // the method should still return true in this case
        return (x <= 0 || y <= 0 || x >= getWorld().getWidth() - 1 || y >= getWorld().getHeight() - 1);
    }

    /**
     * Assign a new location for this actor. This moves the actor to the specified
     * location. The location is specified as the coordinates of a cell in the world.
     * 
     * <p>If this method is overridden it is important to call this method as
     * "super.setLocation(x,y)" from the overriding method, to avoid infinite recursion.
     * 
     * @param x Location index on the x-axis
     * @param y Location index on the y-axis
     * 
     * @see #move(int)
     */
    public void setLocation(int x, int y)
    {
        setLocationDrag(x, y);
    }
    
    /**
     * Move this actor the specified distance in the direction it is
     * currently facing.
     * 
     * <p>The direction can be set using the {@link #setRotation(int)} method.
     * 
     * @param distance  The distance to move (in cell-size units); a negative value
     *                  will move backwards
     * 
     * @see #setLocation(int, int)
     */
    public void move(int distance)
    {
        double radians = Math.toRadians(rotation);

        // We round to the nearest integer, to allow moving one unit at an angle
        // to actually move.
        int dx = (int) Math.round(Math.cos(radians) * distance);
        int dy = (int) Math.round(Math.sin(radians) * distance);
        setLocation(x + dx, y + dy);
    }
    
    /**
     * Turn this actor by the specified amount (in degrees).
     * 
     * @param amount  the number of degrees to turn; positive values turn clockwise
     * 
     * @see #setRotation(int)
     */
    public void turn(int amount)
    {
        setRotation(rotation + amount);
    }
    
    /**
     * The implementation of setLocation.  The main reason for the existence of this method
     * (rather than inlining it into setLocation) is that setLocation can
     * be overridden.  We make sure that setLocationInPixels (used during dragging)
     * always calls this method (setLocationDrag) so that it never calls the 
     * potentially-overridden setLocation method.
     * 
     * <p>setLocation is then called once after the drag, by WorldHandler, so that actors
     * that do override setLocation only see the method called once at the end of the drag
     * (even though the stored location is changing during the drag). 
     */
    private void setLocationDrag(int x, int y)
    {
        // Note this should not call user code - because it is called off the
        // simulation thread. We must access world fields (width, height, cellSize) directly.
        
        if (world != null) {
            int oldX = this.x;
            int oldY = this.y;

            if (world.isBounded()) {
                this.x = limitValue(x, world.width);
                this.y = limitValue(y, world.height);
            }
            else {
                this.x = x;
                this.y = y;
            }

            if (this.x != oldX || this.y != oldY) {
                if (boundingRect != null) {
                    int dx = (this.x - oldX) * world.cellSize;
                    int dy = (this.y - oldY) * world.cellSize;

                    boundingRect.setX(boundingRect.getX() + dx);
                    boundingRect.setY(boundingRect.getY() + dy);

                    for (int i = 0; i < 4; i++) {
                        boundingXs[i] += dx;
                        boundingYs[i] += dy;
                    }
                }
                locationChanged(oldX, oldY);
            }
        }
    }

    /**
     * Limits the value v to be less than limit and large or equal to zero.
     */
    private int limitValue(int v, int limit)
    {
        if (v < 0) {
            v = 0;
        }
        if (limit <= v) {
            v = limit - 1;
        }
        return v;
    }

    /**
     * Return the world that this actor lives in.
     * 
     * @return The world, or null if this actor is not in a world.
     */
    public World getWorld()
    {
        return world;
    }
    
    /**
     * Return the world that this actor lives in, provided that it is
     * an instance of the given "worldClass" class (i.e. that it is an instance
     * of worldClass or one of its subclasses).
     * 
     * @return The world this actor is in, or null if either this actor is not in a world
     * @throws java.lang.ClassCastException If the actor is in a world, but not one that is an instance of worldClass or one of its subclasses
     */
    public <W> W getWorldOfType(Class<W> worldClass)
    {
        // If null, returns null.  If not of right type, already throws ClassCastException for us:
        return worldClass.cast(world);
    }

    /**
     * This method is called by the Greenfoot system when this actor has
     * been inserted into the world. This method can be overridden to implement
     * custom behaviour when the actor is inserted into the world.
     * <p>
     * The default implementation does nothing.
     * 
     * @param world The world the object was added to.
     */
    protected void addedToWorld(@SuppressWarnings("unused") World world)
    {}

    /**
     * Returns the image used to represent this actor. This image can be
     * modified to change the actor's appearance.
     * 
     * @return The object's image.
     */
    public GreenfootImage getImage()
    {
        return image;
    }

    /**
     * Set an image for this actor from an image file. The file may be in
     * jpeg, gif or png format. The file should be located in the project
     * directory.
     * 
     * @param filename The name of the image file.
     * @throws IllegalArgumentException If the image can not be loaded.
     */
    public void setImage(String filename) throws IllegalArgumentException
    {
        setImage(new GreenfootImage(filename));
    }

    /**
     * Set the image for this actor to the specified image.
     * 
     * @see #setImage(String)
     * @param image The image.
     */
    public void setImage(GreenfootImage image)
    {
        if (image == null && this.image == null) {
            return;
        }

        boolean sizeChanged = true;

        if (image != null && this.image != null) {
            if (image.getWidth() == this.image.getWidth() && image.getHeight() == this.image.getHeight()) {
                sizeChanged = false;
            }
        }

        this.image = image;

        if (sizeChanged) {
            boundingRect = null;
            sizeChanged();
        }
    }

    // ==================================
    //
    // PACKAGE PROTECTED METHODS
    //
    // ==================================
    
    /**
     * 
     * Translates the given location into cell-coordinates before setting the
     * location.
     * 
     * Used by the WorldHandler to drag objects.
     * 
     * @param x x-coordinate in pixels
     * @param y y-coordinate in pixels
     */
    void setLocationInPixels(int x, int y)
    {
        int xCell = world.toCellFloor(x);
        int yCell = world.toCellFloor(y);

        if (xCell == this.x && yCell == this.y) {
            return;
        }

        setLocationDrag(xCell, yCell);
    }

    /**
     * Sets the world of this actor.
     * 
     * @param world
     */
    void setWorld(World world)
    {
        this.world = world;
    }

    /**
     * Sets the world, and the initial location. The location is adjusted according to the world's bounding
     * rules. The cached collision checking bounds, if any, are cleared.
     */
    void addToWorld(int x, int y, World world)
    {
        if (world.isBounded()) {
            x = limitValue(x, world.getWidth());
            y = limitValue(y, world.getHeight());
        }
        
        this.x = x;
        this.y = y;
        boundingRect = null;

        this.setWorld(world);
        
        // This call is not necessary, however setLocation may be overridden
        // so it must still be called. (Asteroids scenario relies on setLocation
        // being called when the object is added to the world...)
        this.setLocation(x, y);
    }

    /**
     * Get the axis-aligned bounding rectangle of the object, taking rotation into account.
     * This returns a rectangle which completely covers the rotated actor's area.
     * 
     * @return A rect specified in pixels!
     */
    Rect getBoundingRect() 
    {
        if (boundingRect == null) {
            calcBounds();
        }
        return boundingRect;
    }

    /**
     * Calculates the bounds.
     */
    private void calcBounds()
    {        
        World w = getActiveWorld();
        if(w == null) {
            return;
        }
        int cellSize = w.getCellSize();
        
        if (image == null) {
            int wx = x * cellSize + cellSize / 2;
            int wy = y * cellSize + cellSize / 2;
            boundingRect = new Rect(wx, wy, 0, 0);
            for (int i = 0; i < 4; i++) {
                boundingXs[i] = wx;
                boundingYs[i] = wy;
            }
            return;
        }
        
        if (rotation % 90 == 0) {
            // Special fast calculation when rotated a multiple of 90
            int width = 0;
            int height = 0;
            
            if(rotation % 180 == 0) {
                // Rotated by 180 multiple
                width = image.getWidth();
                height = image.getHeight();
            } else {
                // Swaps width and height since image is rotated by 90 (+/- multiple of 180)
                width = image.getHeight();
                height = image.getWidth();                
            }
            
            int x = cellSize * this.x + (cellSize - width - 1) / 2;
            int y = cellSize * this.y + (cellSize - height - 1) / 2;
            boundingRect = new Rect(x, y, width, height);
            boundingXs[0] = x; boundingYs[0] = y;
            boundingXs[1] = x + width - 1; boundingYs[1] = y;
            boundingXs[2] = boundingXs[1]; boundingYs[2] = y + height - 1;
            boundingXs[3] = x; boundingYs[3] = boundingYs[2];
        }
        else {
            getRotatedCorners(boundingXs, boundingYs, cellSize);
            
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            
            for (int i = 0; i < 4; i++) {
                minX = Math.min(boundingXs[i] - 1, minX);
                maxX = Math.max(boundingXs[i] + 1, maxX);
                minY = Math.min(boundingYs[i] - 1, minY);
                maxY = Math.max(boundingYs[i] + 1, maxY);
            }
            
            // This rect will be bit big to include all pixels that are covered.
            // We lose a bit of precision by using integers and might get
            // collisions that wouldn't be there if using floating point. But
            // making it a big bigger, we will get all the collision that we
            // would get with floating point.
            // For instance, if something has the width 28.2, it might cover 30
            // pixels.
            boundingRect = new Rect(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    }

    /**
     * Set collision-checker-private data for this actor.
     */
    void setData(Object o)
    {
        this.data = o;
    }
    
    /**
     * Get the collision-checker-private data for this actor.
     * @return An Object contains the info about the collision.
     */
    Object getData()
    {
        return data;
    }
    
    /**
     * Translate a cell coordinate into a pixel. This will return the coordinate of the centre of he cell.
     */
    int toPixel(int x)
    {        
        World aWorld = getActiveWorld();
        if(aWorld == null) {
            // Should never happen
            throw new IllegalStateException(NO_WORLD);
        }
        return x * aWorld.getCellSize() +  aWorld.getCellSize()/2;
    }

    
    // ============================
    //
    // Private methods
    //
    // ============================
    
    /**
     * Get the default image for objects of this class. May return null.
     */
    private GreenfootImage getClassImage()
    {
        Class<?> clazz = getClass();
        while (clazz != null) {
            GreenfootImage image = null;
            try {
                image = getImage(clazz);
            }
            catch (Throwable e) {
                // Ignore exception and continue looking for images
            }
            if (image != null) {
                return image;
            }
            clazz = clazz.getSuperclass();
        }

        return greenfootImage;
    }
    

    /**
     * Notify the world that this object's size has changed, if it in fact has changed.
     */
    private void sizeChanged()
    {
        if(world != null) {
            world.updateObjectSize(this);
        }
    }   

    /**
     * Notify the world that this object's location has changed.
     */
    private void locationChanged(int oldX, int oldY)
    {
        if(world != null) {
            world.updateObjectLocation(this, oldX, oldY);
        }
    }
    
    /**
     * Throws an exception if the actor is not in a world.
     * 
     * @throws IllegalStateException If not in world.
     */
    private void failIfNotInWorld()
    {
        if(world == null) {
            throw new IllegalStateException(ACTOR_NOT_IN_WORLD);
        }
    }
    
    /**
     * Calculated the co-ordinates of the bounding rectangle after it is rotated
     * and translated for the actor position, in pixels.
     * 
     * @param xs  The array to hold the four X coordinates
     * @param ys  The array to hold the four Y coordinates
     * @param cellSize  The world cell size
     */
    private void getRotatedCorners(int [] xs, int [] ys, int cellSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        
        xs[0] = -width / 2;
        xs[1] = xs[0] + width - 1;
        xs[2] = xs[1];
        xs[3] = xs[0];
        
        ys[0] = -height / 2;
        ys[1] = ys[0];
        ys[2] = ys[1] + height - 1;
        ys[3] = ys[2];
        
        double rotR = Math.toRadians(rotation);
        double sinR = Math.sin(rotR);
        double cosR = Math.cos(rotR);
        
        double xc = cellSize * x + cellSize / 2.;
        double yc = cellSize * y + cellSize / 2.;
        
        // Do the actual rotation
        for (int i = 0; i < 4; i++) {
            int nx = (int)(xs[i] * cosR - ys[i] * sinR + xc);
            int ny = (int)(ys[i] * cosR + xs[i] * sinR + yc);
            xs[i] = nx;
            ys[i] = ny;
        }
    }

    /**
     * Check whether all of the vertexes in the "other" rotated rectangle are on the
     * outside of any one of the edges in "my" rotated rectangle.
     *  
     * @param myX   The x-coordinates of the corners of "my" rotated rectangle
     * @param myY    The y-coordinates of the corners "my" rotated rectangle
     * @param otherX  The x-coordinates of the corners of the "other" rotated rectangle
     * @param otherY  The y-coordinates of the corners of the "other" rotated rectangle
     * 
     * @return  true if all corners of the "other" rectangle are on the outside of any of
     *          the edges of "my" rectangle.
     */
    private static boolean checkOutside(int [] myX, int [] myY, int [] otherX, int [] otherY)
    {
        vloop:
        for (int v = 0; v < 4; v++) {
            int v1 = (v + 1) & 3; // wrap at 4 back to 0
            int edgeX = myX[v] - myX[v1];
            int edgeY = myY[v] - myY[v1];
            int reX = -edgeY;
            int reY = edgeX;
            
            if (reX == 0 && reY == 0) {
                continue vloop;
            }

            for (int e = 0; e < 4; e++) {
                int scalar = reX * (otherX[e] - myX[v1]) + reY * (otherY[e] - myY[v1]);
                if (scalar < 0) {
                    continue vloop;
                }
            }

            // If we got here, we have an edge with all vertexes from the other rect
            // on the outside:
            return true;
        }

        return false;
    }
    
    // ============================
    //
    // Collision stuff
    //
    // ============================

    /**
     * Check whether this object intersects with another given object.
     * 
     * @param other  The second object to detect the existing of intersection with it.
     * @return True if the object's intersect, false otherwise.
     */
    protected boolean intersects(Actor other)
    {
        if (image == null) {
            if (other.image == null) {
                // No images; the actors can be considered to represent points,
                // and we'll say they intersect if they match exactly.
                return x == other.x && y == other.y;
            }
            
            int cellSize = world.getCellSize();
            
            // We are a point, the other actor is a rect. Rotate our relative
            return other.containsPoint(x * cellSize + cellSize / 2, y * cellSize + cellSize / 2);
        }
        else if (other.image == null) {
            // We are a rectangle, the other is a point
            int cellSize = world.getCellSize();
            return containsPoint(other.x * cellSize + cellSize / 2, other.y * cellSize + cellSize / 2);
        }
        else {
            Rect thisBounds = getBoundingRect();
            Rect otherBounds = other.getBoundingRect();
            if (rotation == 0 && other.rotation == 0) {
                return thisBounds.intersects(otherBounds);
            }
            else {
                // First do a check based only on axis-aligned bounding boxes.
                if (! thisBounds.intersects(otherBounds)) {
                    return false;
                }
                
                int [] myX = boundingXs;
                int [] myY = boundingYs;
                int [] otherX = other.boundingXs;
                int [] otherY = other.boundingYs;
                
                if (checkOutside(myX, myY, otherX, otherY)) {
                    return false;
                }
                if (checkOutside(otherX, otherY, myX, myY)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Return the neighbours to this object within a given distance. This
     * method considers only logical location, ignoring extent of the image.
     * Thus, it is most useful in scenarios where objects are contained in a
     * single cell.
     * <p>
     * 
     * All cells that can be reached in the number of steps given in 'distance'
     * from this object are considered. Steps may be only in the four main
     * directions, or may include diagonal steps, depending on the 'diagonal'
     * parameter. Thus, a distance/diagonal specification of (1,false) will
     * inspect four cells, (1,true) will inspect eight cells.
     * <p>
     * 
     * @param distance Distance (in cells) in which to look for other objects.
     * @param diagonal If true, include diagonal steps.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @return A list of all neighbours found.
     */
    protected <A> List<A> getNeighbours(int distance, boolean diagonal, Class<A> cls)
    {
        failIfNotInWorld();
        // Don't use getWorld() here, as it is overridable
        return world.getNeighbours(this, distance, diagonal, cls);
    }
    
    /**
     * Return all objects that intersect the center of the given location (relative to
     * this object's location). <br>
     * 
     * @return List of objects at the given offset. The list will include this
     *         object, if the offset is zero.
     * @param dx X-coordinate relative to this objects location.
     * @param dy y-coordinate relative to this objects location.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     */
    protected <A> List<A> getObjectsAtOffset(int dx, int dy, Class<A> cls)
    {
        failIfNotInWorld();
        return world.getObjectsAt(x + dx, y + dy, cls);
    }

    /**
     * Return one object that is located at the specified cell (relative to this
     * objects location). Objects found can be restricted to a specific class
     * (and its subclasses) by supplying the 'cls' parameter. If more than one
     * object of the specified class resides at that location, one of them will
     * be chosen and returned.
     * 
     * @param dx X-coordinate relative to this objects location.
     * @param dy y-coordinate relative to this objects location.
     * @param cls Class of objects to look for (passing 'null' will find all objects).
     * @return An object at the given location, or null if none found.
     */
    protected Actor getOneObjectAtOffset(int dx, int dy, Class<?> cls)
    {
        failIfNotInWorld();
        return world.getOneObjectAt(this, x + dx, y + dy, cls);        
    }
    
    /**
     * Return all objects within range 'radius' around this object. 
     * An object is within range if the distance between its centre and this
     * object's centre is less than or equal to 'radius'.
     * 
     * @param radius Radius of the circle (in cells)
     * @param cls Class of objects to look for (passing 'null' will find all objects).
     * @return List of objects of the given class type within the given radius.
     */
    protected <A> List<A> getObjectsInRange(int radius, Class<A> cls)
    {
        failIfNotInWorld();
        List<A> inRange = world.getObjectsInRange(x, y, radius, cls);
        inRange.remove(this);
        return inRange;
    }

    /**
     * Return all the objects that intersect this object. This takes the
     * graphical extent of objects into consideration. <br>
     * 
     * @param cls Class of objects to look for (passing 'null' will find all objects).
     * @return List of objects of the given class type that intersect with the current object.
     */
    protected <A> List<A> getIntersectingObjects(Class<A> cls)
    {
        failIfNotInWorld();
        List<A> l = world.getIntersectingObjects(this, cls);
        l.remove(this);
        return l;
    }
    
    /**
     * Return an object that intersects this object. This takes the
     * graphical extent of objects into consideration. <br>
     * 
     * @param cls Class of objects to look for (passing 'null' will find all objects).
     * @return An object of the given class type that intersects with the current object.
     */
    protected Actor getOneIntersectingObject(Class<?> cls)
    {
        failIfNotInWorld();
        return world.getOneIntersectingObject(this, cls);
    }
    
    /**
     * Checks whether this actor is touching any other objects
     * of the given class.
     * 
     * @param cls Class of objects to look for (passing 'null' will check for all actors).
     * @return True if there is an object of the given class type that intersects with the 
     *            current object, false otherwise.
     */
    protected boolean isTouching(Class<?> cls)
    {
        failIfNotInWorld();
        return getOneIntersectingObject(cls) != null;
    }
    
    /**
     * Removes one object of the given class that this actor is
     * currently touching (if any exist). 
     * 
     * @param cls Class of objects to remove (passing 'null' will remove any actor).
     */
    protected void removeTouching(Class<?> cls)
    {
        failIfNotInWorld();
        // This cast should never fail, because getOneIntersectingObject will only
        // be non-null if cls extends Actor.
        Actor a = (Actor)getOneIntersectingObject(cls);
        if (a != null)
        {
            world.removeObject(a);
        }
    }
    
    /**
     * Checks whether the specified point (specified in pixel co-ordinates) is within the area
     * covered by the (rotated) graphical representation of this actor.
     * 
     * @param px  The (world relative) x pixel co-ordinate
     * @param py  The (world relative) y pixel co-ordinate
     * @return  true if the pixel is within the actor's bounds; false otherwise
     */
    boolean containsPoint(int px, int py)
    {
        failIfNotInWorld();
        if (image == null) {
            return false;
        }

        if (boundingRect == null) {
            calcBounds(); // Make sure bounds are up-to-date
        }
        
        if (rotation == 0 || rotation == 90 || rotation == 270) {
            // We can just check the bounding rectangle
            return (px >= boundingRect.getX() && px < boundingRect.getRight()
                    && py >= boundingRect.getY() && py < boundingRect.getTop());
        }
        
        vloop: for (int v = 0; v < 4; v++) {
            int v1 = (v + 1) & 3; // wrap at 4 back to 0
            int edgeX = boundingXs[v] - boundingXs[v1];
            int edgeY = boundingYs[v] - boundingYs[v1];
            int reX = -edgeY;
            int reY = edgeX;

            if (reX == 0 && reY == 0) {
                continue vloop;
            }

            int scalar = reX * (px - boundingXs[v1]) + reY * (py - boundingYs[v1]);
            if (scalar < 0) {
                continue vloop;
            }

            // If we got here, we have an edge with all vertexes from the other rect
            // on the outside:
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the sequence number of this actor. This can be used as a
     * hash value, which is not overridable by the user.
     */
    final int getSequenceNumber()
    {
        return mySequenceNumber;
    }

    /**
     * Get the sequence number of this actor from the last paint operation.
     * (Returns whatever was set using the setLastPaintSeqNum method).
     */
    final int getLastPaintSeqNum()
    {
        return lastPaintSequenceNumber;
    }
    
    /**
     * Set the sequence number of this actor from the last paint operation.
     */
    final void setLastPaintSeqNum(int num)
    {
        lastPaintSequenceNumber = num;
    }
    
    // ============================================================================
    //  
    // Methods below here are delegated to different objects depending on how
    // the project is run.
    // (From Greenfoot IDE or StandAlone)
    //  
    // ============================================================================

    private static ActorDelegate delegate;
    
    /**
     * Set the object that this actor should delegate method calls to.
     *
     */
    static void setDelegate(ActorDelegate d)
    {
        delegate = d;
    }
    
    static ActorDelegate getDelegate()
    {
        return delegate;
    }
    
    /**
     * Get the default image for objects of this class. May return null.
     */
    GreenfootImage getImage(Class<?> clazz)
    {
        return delegate.getImage(clazz.getName());
    }

    /**
     * Get the active world. This method will return the instantiated world,
     * even if the object is not yet added to a world.
     */
    World getActiveWorld()
    {
        if(world != null) {
            return world;
        }
        WorldHandler handler = WorldHandler.getInstance();
        if (handler != null) {
            return handler.getWorld();
        }
        else {
            return null;
        }
    }
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
 Copyright (C) 2010,2011,2012,2013,2015 Poul Henriksen and Michael Kolling 
 
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
package rmiextension;

import greenfoot.actions.ResetWorldAction;
import greenfoot.core.Simulation;
import greenfoot.core.SimulationDebugMonitor;

import java.awt.EventQueue;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rmiextension.wrappers.RProjectImpl;
import rmiextension.wrappers.WrapperPool;
import bluej.debugger.Debugger;
import bluej.debugger.DebuggerClass;
import bluej.debugger.DebuggerEvent;
import bluej.debugger.DebuggerEvent.BreakpointProperties;
import bluej.debugger.DebuggerField;
import bluej.debugger.DebuggerListener;
import bluej.debugger.DebuggerObject;
import bluej.debugger.DebuggerThread;
import bluej.debugger.SourceLocation;
import bluej.debugmgr.Invoker;
import bluej.extensions.BProject;
import bluej.extensions.ExtensionBridge;
import bluej.extensions.ProjectNotOpenException;
import bluej.pkgmgr.Project;
import bluej.utility.Debug;
import bluej.utility.JavaNames;

/**
 * A class that does several things:
 * 
 * <p>Firstly, it listens for the debugger terminating the Greenfoot VM, and relaunches Greenfoot.
 * 
 * <p>Secondly, it tries to make sure that the debugger never stops the code
 * entirely outside the user's code (i.e. there should always be some user code
 * somewhere in the call stack) 
 * 
 * @author Neil Brown
 */
public class GreenfootDebugHandler implements DebuggerListener
{  
    private static final String SIMULATION_CLASS = Simulation.class.getName();   
    private static final String[] INVOKE_METHODS = {Simulation.ACT_WORLD, Simulation.ACT_ACTOR,
            Simulation.NEW_INSTANCE, Simulation.RUN_QUEUED_TASKS, Simulation.WORLD_STARTED, Simulation.WORLD_STOPPED};
    private static final String SIMULATION_INVOKE_KEY = SIMULATION_CLASS + "INTERNAL";
    
    private static final String PAUSED_METHOD = Simulation.PAUSED;
    private static final String SIMULATION_THREAD_PAUSED_KEY = "SIMULATION_THREAD_PAUSED"; 
        
    private static final String SIMULATION_THREAD_RUN_KEY = "SIMULATION_THREAD_RUN";
    
    private static final String RESET_CLASS = ResetWorldAction.class.getName();
    private static final String RESET_METHOD = ResetWorldAction.RESET_WORLD;
    private static final String RESET_KEY = "RESET_WORLD";
    
    private BProject project;
    private DebuggerThread simulationThread;
    private DebuggerClass simulationClass;
    
    private GreenfootDebugHandler(BProject project)
    {
        this.project = project;
    }
        
    /**
     * This is the publicly-visible way to add a debugger listener for a particular project.    
     */
    static void addDebuggerListener(BProject project)
    {
        try {
            Project proj = Project.getProject(project.getDir());

            // Technically I could collapse the two listeners into one, but they
            // perform orthogonal tasks so it's nicer to keep the code separate:
            GreenfootDebugHandler handler = new GreenfootDebugHandler(project);
            int mstate = proj.getDebugger().addDebuggerListener(handler);
            proj.getDebugger().addDebuggerListener(handler.new GreenfootDebugControlsLink());
            if (mstate == Debugger.IDLE) {
                // The VM may have already started by the time the listener was added. If so,
                // we need to kick off Greenfoot on the other VM here:
                handler.addRunResetBreakpoints(proj.getDebugger());
                ProjectManager.instance().openGreenfoot(project);
            }
        } catch (ProjectNotOpenException ex) {
            Debug.reportError("Project not open when adding debugger listener in Greenfoot", ex);
        }
    }

    private void addRunResetBreakpoints(Debugger debugger)
    {
        try {
            // We have to initialise the class; the IBM JDK otherwise throws an ObjectCollectedException
            // exception, seemingly in error.
            simulationClass = debugger.getClass(SIMULATION_CLASS, true);

            Map<String, String> simulationRunBreakpointProperties = new HashMap<String, String>();
            simulationRunBreakpointProperties.put(SIMULATION_THREAD_RUN_KEY, "TRUE");
            simulationRunBreakpointProperties.put(Debugger.PERSIST_BREAKPOINT_PROPERTY, "TRUE");
            debugger.toggleBreakpoint(simulationClass, "run", true, simulationRunBreakpointProperties);

            Map<String, String> resetBreakpointProperties = new HashMap<String, String>();
            resetBreakpointProperties.put(RESET_KEY, "yes");
            resetBreakpointProperties.put(Debugger.PERSIST_BREAKPOINT_PROPERTY, "TRUE");
            debugger.toggleBreakpoint(RESET_CLASS, RESET_METHOD, true, resetBreakpointProperties);
        }
        catch (ClassNotFoundException cnfe) {
            Debug.reportError("Simulation class could not be located. Possible installation problem.", cnfe);
        }
    }
    
    private boolean isSimulationThread(DebuggerThread dt)
    {
        return dt != null && simulationThread != null && simulationThread.sameThread(dt);
    }
    
    /**
     * An early examination of the debugger event (gets called before processDebuggerEvent)
     * 
     * This method is responsible for checking where the debugger has stopped (if it has),
     * and deciding whether it should be run on for a bit until it reaches user code.
     * 
     * This method does not actually run it on; see the comments on the scheduledTasks field
     * at the top of the class for how it works.
     */
    @Override
    public boolean examineDebuggerEvent(final DebuggerEvent e)
    {
        final Debugger debugger = (Debugger)e.getSource();
        List<SourceLocation> stack = e.getThread().getStack();
        
        if (e.getID() == DebuggerEvent.THREAD_BREAKPOINT
            && e.getThread() != null &&
            e.getBreakpointProperties().get(SIMULATION_THREAD_RUN_KEY) != null) {
            // This is the breakpoint at the very beginning of the simulation thread;
            // record this thread as being the simulation thread and set it running again:
            simulationThread = e.getThread();
            try {
                RProjectImpl rproj = WrapperPool.instance().getWrapper(project);
                rproj.setSimulationThread(simulationThread);
            }
            catch (RemoteException re) {
                Debug.reportError("Unexpected exception getting project wrapper: ", re);
            }
            e.getThread().cont();
            return true;
            
        } else if (e.getID() == DebuggerEvent.THREAD_BREAKPOINT
                && atResetBreakpoint(e.getBreakpointProperties())) {
            // The user has clicked reset,
            // Set the simulation thread going if it's suspended:
            if (simulationThread.isSuspended()) {
                simulationThread.cont();
            }

            EventQueue.invokeLater(new Runnable() {
                public void run()
                {
                    try {
                        ExtensionBridge.clearObjectBench(project);
                    }
                    catch (ProjectNotOpenException e) { }

                    // Run the GUI thread on:
                    e.getThread().cont();
                };
            });
            
            return true;
        } else if (e.isHalt() && isSimulationThread(e.getThread())) {
            if (atPauseBreakpoint(e.getBreakpointProperties())) {
                // They are going to pause; remove all special breakpoints and set them going
                // (so that they actually hit the pause):
                debugger.removeBreakpointsForClass(SIMULATION_CLASS);
                e.getThread().cont();
                return true;
            } else if (insideUserCode(stack)) {
                // They are in an act method, make sure the breakpoints are cleared:
                
                // This method can be safely invoked without needing to talk to the worker thread:
                debugger.removeBreakpointsForClass(SIMULATION_CLASS);
                        
                // If they have just hit the breakpoint and are in InvokeAct itself,
                // step-into the World/Actor:
                if (atInvokeBreakpoint(e.getBreakpointProperties())) {
                    e.getThread().stepInto();
                    return true;
                } else if (inInvokeMethods(stack, 0)) {
                    // Finished calling act() and have stepped out; run to next one:
                    runToInternalBreakpoint(debugger, e.getThread());
                    return true;                    
                } //otherwise they are in their own code
            } else  {
                if (inPauseMethod(stack)) {
                    // They are paused, just set them running again and forget it:
                    e.getThread().cont();
                } else {
                    // They are not in an act() method and not paused; run until they get to an act() method:
                    runToInternalBreakpoint(debugger, e.getThread());
                }
                return true;
            }
        }

        return false;
    }
    
    /**
     * Processes a debugger event.  This is called after examineDebuggerEvent, with a second
     * parameter that effectively corresponds to the return result of examineDebuggerEvent.
     * 
     * <p>Thus, if the parameter is true, we look for a scheduled task to run.
     * 
     * <p>We call threadHalted if necessary.
     */
    @Override
    public void processDebuggerEvent(final DebuggerEvent e, boolean skipUpdate)
    {
        if (e.getNewState() == Debugger.IDLE && e.getOldState() == Debugger.NOTREADY) {
            if (! ProjectManager.checkLaunchFailed()) {
                //It is important to have this code run at a later time.
                //If it runs from this thread, it tries to notify the VM event handler,
                //which is currently calling us and we get a deadlock between the two VMs.
                EventQueue.invokeLater(new Runnable() {
                    public void run()
                    {
                        addRunResetBreakpoints((Debugger) e.getSource());
                        ProjectManager.instance().openGreenfoot(project);
                    }
                });
            }
        }
    }

    /**
     * Runs the debugger on until it hits the special invoke-act breakpoints that occur
     * just before user code might be encountered.  This method doesn't actually check if you're
     * thereabouts already, so it should be only called once you've checked that you actually
     * want to run onwards.
     * 
     * Returns a task that will run them onwards, which can be scheduled as you like
     */
    private void runToInternalBreakpoint(final Debugger debugger, final DebuggerThread thread)
    {
        // Set a break point where we want them to be:
        setSpecialBreakpoints(debugger);

        // Then set them running again:
        thread.cont();
    }
    
    /**
     * Works out if we are currently in a call to the World or Actor act() methods
     * by looking in the call stack for them. Strictly speaking, we might not be
     * truly inside the user code: it might be we are about to enter or have just
     * left the act() method. It is only valid to call this for the simulation
     * thread.
     */
    private static boolean insideUserCode(List<SourceLocation> stack)
    {
        for (int i = 0; i < stack.size();i++) {
            if (inInvokeMethods(stack, i)) {
                return true;
            }
        }
        return false;
    }
   
    /**
     * Works out if the specified frame in the call-stack is in one of the special invoke-act
     * methods that call the World and Actor's act() methods or the method that runs
     * other user code on the simulation thread 
     */
    private static boolean inInvokeMethods(List<SourceLocation> stack, int frame)
    {
        if (frame < stack.size()) {
            String className = stack.get(frame).getClassName();
            if (className.equals(SIMULATION_CLASS)) {
                String methodName = stack.get(frame).getMethodName();
                for (String actMethod : INVOKE_METHODS) {
                    if (actMethod.equals(methodName)) {
                        return true;
                    }
                }
            }
            else if (JavaNames.getBase(className).startsWith(Invoker.SHELLNAME)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Works out if they are at the breakpoint triggered by the user clicking
     * the Reset button.
     */
    private static boolean atResetBreakpoint(BreakpointProperties props)
    {
        return props != null && props.get(RESET_KEY) != null;
    }
    
    /**
     * Works out if they are currently in the Simulation.PAUSED method by looking 
     * for the special breakpoint property
     */
    private static boolean atPauseBreakpoint(BreakpointProperties props)
    {
        return props != null && props.get(SIMULATION_THREAD_PAUSED_KEY) != null;
    }
    
    /**
     * Works out if they are currently paused by looking at the call stack
     * while they are suspended.
     */
    private static boolean inPauseMethod(List<SourceLocation> stack)
    {
        for (SourceLocation loc : stack) {
            if (loc.getClassName().equals(SIMULATION_CLASS) && loc.getMethodName().equals(PAUSED_METHOD)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the given breakpoint is an invoke breakpoint set by 
     * the setSpecialBreakpoints call, below  
     */
    private static boolean atInvokeBreakpoint(BreakpointProperties props)
    {
        return props != null && props.get(SIMULATION_INVOKE_KEY) != null;
    }
    
    /**
     * Sets breakpoints in the special invoke-act methods that call the World and Actor's
     * act() methods, and the method that constructs new objects, and the method called when
     * the simulation will pause.  These breakpoints will thus be encountered immediately before control
     * would descend into the World and Actor's act() methods or other tasks (i.e. potential user code),
     * or if the simulation is going to wait for the user to click the controls (e.g. end of an
     * Act, or because the simulation is now going to be Paused).
     */
    private void setSpecialBreakpoints(final Debugger debugger)
    {
        for (String method : INVOKE_METHODS) {
            String err = debugger.toggleBreakpoint(simulationClass, method, true, Collections.singletonMap(SIMULATION_INVOKE_KEY, "yes"));
            if (err != null) {
                Debug.reportError("Problem setting special breakpoint: " + err);
            }
        }
        
        String err = debugger.toggleBreakpoint(simulationClass, PAUSED_METHOD, true, Collections.singletonMap(SIMULATION_THREAD_PAUSED_KEY, "yes"));
        if (err != null) {
            Debug.reportError("Problem setting special breakpoint: " + err);
        }
    }
    
    /**
     * A second debug listener that only worries about enabling and disabling the
     * Act/Run/Pause buttons according to whether the Simulation thread is currently at
     * a breakpoint.
     */
    private class GreenfootDebugControlsLink implements DebuggerListener
    {
        private LinkedList<String> queuedStateVars = new LinkedList<String>();
        private Object SEND_EVENT = new Object();
        private String CLASS_NAME = SimulationDebugMonitor.class.getName();
        
        private void simplifyEvents()
        {
            // If there is more than one event, it must be made redundant by the latest
            // event:
            while (queuedStateVars.size() > 1) {
                queuedStateVars.removeFirst();
            }
        }
        
        private class SendNextEvent implements Runnable
        {
            private Debugger debugger;
            
            public SendNextEvent(Debugger debugger)
            {
                this.debugger = debugger;
            }

            @Override
            public void run()
            {
                // We hold the monitor until the object has been instantiated, to prevent race hazards:
                synchronized (SEND_EVENT) {
                    String stateVar;
                    synchronized (queuedStateVars) {
                        simplifyEvents();
                        if (queuedStateVars.isEmpty()) {
                            return;
                        }
                        stateVar = queuedStateVars.removeFirst();
                    }
                    try {
                        DebuggerClass simMonClass = debugger.getClass(CLASS_NAME, true);
                        DebuggerObject stateObject = null;
                        for (int i = 0; ; i++) {
                            DebuggerField simMonField = simMonClass.getStaticField(i);
                            if (simMonField.getName().equals(stateVar)) {
                                stateObject = simMonField.getValueObject(null);
                                break;
                            }
                        }
                        debugger.instantiateClass(CLASS_NAME, new String[] {"java.lang.Object"},
                                new DebuggerObject[] {stateObject});
                    } catch (ClassNotFoundException ex) {
                        Debug.reportError("Could not find internal class " + CLASS_NAME, ex);
                    }
                }
            }
        }

        @Override
        public synchronized void processDebuggerEvent(DebuggerEvent e, boolean skipUpdate)
        {
            final String stateVar;
            if (e.isHalt()) {
                if (isSimulationThread(e.getThread())) {
                    stateVar = "NOT_RUNNING";
                }
                else {
                    return;
                }
            }
            else if (e.getID() == DebuggerEvent.THREAD_CONTINUE) {
                if (isSimulationThread(e.getThread())) {
                    stateVar = "RUNNING";
                }
                else {
                    return;
                }
            } else {
                return;
            }
            
            final Debugger debugger = (Debugger) e.getSource();
            
            /* We are on the BlueJ VM, but we need to adjust the state of the buttons
             * on the Greenfoot VM (aka Debug VM).  We use this slight hack of constructing
             * an object on the Greenfoot VM that will do the work for us there.
             * 
             * For a parameter, we pass one of the static objects that the class holds
             * (this was more obviously do-able than passing a boolean constant, fix it if you know how)
             * 
             * We must do this in a new thread because we'll deadlock if we try to directly
             * create the object from a debug handler as we are. 
             */

            synchronized (queuedStateVars) {
                queuedStateVars.addLast(stateVar);
                new Thread (new SendNextEvent(debugger)).start();
            }                
        }
    }
}
