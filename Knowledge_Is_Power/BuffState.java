import greenfoot.*;
import java.util.*;
/**
 * Write a description of class ShockedBuff here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BuffState implements IBuffState
{
    protected Actor source = null;
    // buff appearance
    protected Decorator decorator = null;
    protected String BuffMessage;
    protected double damage = 0.0;
    protected boolean damping = false;
    protected double dampingRate = 0.9;
    // control move state
    protected String moveState = "";
    protected SimpleTimer lifeTimer = new SimpleTimer();
    // buff life time
    protected int timeout = 1000;
    // buff type
    protected BuffType type;
    
    public BuffState(Actor source, int timeout, int damage, BuffType type, String moveState){
        this.source = source;
        this.damage = (double)damage;
        this.timeout = timeout;
        this.moveState = moveState;
        this.type = type;
        lifeTimer.mark();
    }
    public void setDecorator(Decorator decorator){
        this.decorator = decorator;
    }
    public Actor getSource(){
        return source;
    }
    public int buffDamage(){
        if(damping && damage > 0){
            damage *= dampingRate;
        }
        return (int)damage;
    }
    public String buffMove(){
        return moveState;
    }
    public void display(int x, int y, int r){
        decorator.setLocation(x, y);
        decorator.setRotation(r);
    }
    public boolean isDead(){
        return lifeTimer.millisElapsed() > timeout;
    }
    public void die(){
        if(decorator!=null){
            decorator.setDead();
        }
    }
    public void update(){
        lifeTimer.mark();
    }
    public BuffType getType(){
        return type;
    }
    public String toString(){
        return BuffMessage;
    }
}
