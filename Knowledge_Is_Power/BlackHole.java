import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.List;
/**
 * Write a description of class Blackhole here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BlackHole extends Bullet
{
    private int timer = 10;
    public BlackHole(int r){
        this(r,60,60,10);        //default size 20*20, not through, 10 damage
    }
    
    public BlackHole(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
    }
    
    
    public void act(){
        if (!freeze_state){
            //move();
          
            List<Enermy> nearEnermy = getObjectsInRange(200, Enermy.class);
             for(Enermy enermy: nearEnermy)
             {
                 enermy.setLocation(getX(), getY());
                 enermy.damage(getX(),getY(),0, "bullet");
    
             }
            /* timer */
            timer();
        }
        
        /* remove condition */
        dead();
    } 
    
    public void timer(){
        
    }
    
    public void dead(){
       
            getWorld().removeObject(this);

    }
}
