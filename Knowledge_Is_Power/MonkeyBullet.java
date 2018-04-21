import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class MonkeyBullet here.
 * 
 * @author Yifan  
 * @version v1.0
 */

public class MonkeyBullet extends Bullet
{
    private GreenfootImage monkeyBullet= new GreenfootImage("banana.png");
    
    protected boolean through = false;
    
    public MonkeyBullet(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public MonkeyBullet(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
        setImage(monkeyBullet);
    }
    
    /* override */
    public void dead(){
        /* delete if hit enermy and type is not through */
        if (getOneIntersectingObject(Enermy.class) != null && !through){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),damage, "bullet");
                break;
            }
            getWorld().removeObject(this);
        }
        /* if hit enermy and type is through */
        /* ATTENTION: through damage must be very small */
        else if (getOneIntersectingObject(Enermy.class) != null && through){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),damage, "bullet");
                break;
            }
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
        
}   

