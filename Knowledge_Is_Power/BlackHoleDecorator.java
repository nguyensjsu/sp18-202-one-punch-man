import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.List;

/**
 * Write a description of class BlackHoleDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BlackHoleDecorator extends Decorator
{
    private int sizeX = 100;
    private int sizeY = 100;
    private int damage;
    private int lifeTime = 2000;
    private int attackTime = 500;
    private SimpleTimer lifeTimer = new SimpleTimer();
    private SimpleTimer attackTimer = new SimpleTimer();
    
    
    public BlackHoleDecorator(int sizeX, int sizeY, int d){
        
        this.size_x = sizeX;
        this.size_y = sizeY;
        this.damage = d;
        rotation = 0;
   
        GreenfootImage image = new GreenfootImage("blackhole.png");
        image.scale(400, 400);
        setImage(image);

        lifeTimer.mark();
    }
    
  
    public void act() 
    {
        if (!freeze_state){
            
            setRotation(rotation);
            rotation+=2;
            
            if(attackTimer.millisElapsed() > attackTime){
               
                List<Enermy> nearEnermy = getObjectsInRange(400, Enermy.class);
                 for(Enermy enermy: nearEnermy)
                 {
                     enermy.damage(getX(),getY(),damage, "pull");
                 }

                attackTimer.mark();
            }
        }
        
        dead();
    }
    
    
    public void dead(){
        if(lifeTimer.millisElapsed() > lifeTime){
             getWorld().removeObject(this);
        }
    
 
    }
    
}
