import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * Write a description of class TeslaCar here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TeslaCar extends Player
{
    private int sizeX = 100;
    private int sizeY = 100;
    private int transVal = 255;
    private boolean fade = false;
    private int damage;
    private int acceleration = 1;
    private int velocity = 1;
    private int maxVelocity = 12;
    private int chaseX;
    private int chaseY;
    private Actor currentChase;
    private SimpleTimer moveTimer = new SimpleTimer();
    private int maxHP = 100;
    
    public TeslaCar(int damage){
        this.damage = damage;
    }
    public void act() 
    {
        if (move_state != "freeze"){
            Enermy enermy = getNearestEnermy(400);
            if(enermy != null && enermy.getWorld() != null){
                currentChase = enermy;
                chaseX = enermy.getX();
                chaseY = enermy.getY();
            }
            
        }
        dead();
    }
    public void accelerate(boolean speedup){
        if(velocity < maxVelocity && speedup){
            velocity += acceleration;
        }else if(velocity > 0 && !speedup){
            velocity -= acceleration;
        }
    }
    public void chase(Actor actor){
        turnTowards(chaseX, chaseY);
        move(velocity);
    }
    public void dead(){
        if(fade){
            transVal-=5;
        }
        if(transVal <= 0){
            getWorld().removeObject(this);
        }
        else{
            getImage().setTransparency(transVal);
        }
    }
    public double getDistance(Actor actor) {
        return Math.hypot(actor.getX() - getX(), actor.getY() - getY());
    }
    public Enermy getNearestEnermy(int range) {
        
        List<Enermy> nearEnermys = getObjectsInRange(range, Enermy.class);
        Enermy nearestEnermy = null;
        double nearestDistance = range;
        for (int i = 0; i < nearEnermys.size(); i++) {
            double distance = getDistance(nearEnermys.get(i));
            if (distance < nearestDistance) {
                nearestEnermy = nearEnermys.get(i);
                nearestDistance = distance;
            }
        }
        return nearestEnermy;
    }
    public void charge(){
        
    }
}
