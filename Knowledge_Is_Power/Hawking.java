import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.List;
/**
 * Write a description of class Hawking here.
 * 
 * @author Danlu 
 * @version v0.1
 */
public class Hawking extends Player
{
    /**
     * Act - do whatever the Hawking wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    private int move_speed = 30;
    
    public void act() 
    {
       /* update move */
       
       switch (move_state){
           case "wasd": wasd_move(); break;
           case "push": push(push_x, push_y, push_speed); break;
           default: break;
        }
       
       /* ability */
       base_attack();
       
      if(Greenfoot.isKeyDown("1")){
            blackhole();
      }
      
      if(Greenfoot.isKeyDown("2")){
            move_fast();
      }
      
      if(Greenfoot.isKeyDown("3")){
            explored();
      }
     
      
      /* game over condition */
      dead();

     }  
     
    /* movement control using key 2 */
    public void move_fast()
    {
        int update_x = getX();
        int update_y = getY();

        MouseInfo mouse = Greenfoot.getMouseInfo();
       
        turnTowards(mouse.getX(),mouse.getY());
      
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(getRotation())));
        update_y = (int)(getY() + move_speed*sin(toRadians(getRotation())));

        /* move */
        setLocation(update_x,update_y);
        
        /* observer notify */
        for (Enermy enermy: getWorld().getObjects(Enermy.class)){
            enermy.update(update_x,update_y);
        }
    }
    
   public void blackhole() 
   {
     int update_x = getX();
     int update_y = getY();
     List<Enermy> nearEnermy = getObjectsInRange(300, Enermy.class);
     for(Enermy enermy: nearEnermy)
     {
         enermy.setLocation(update_x, update_y);
         enermy.dead();
     }
   
   }
   
   public void explored()
   {
     int update_x = getX();
     int update_y = getY();
    for (Enermy enermy: getWorld().getObjects(Enermy.class)){
        enermy.setLocation(update_x, update_y);
        enermy.damage(getX(),getY(),1, "bullet");
        
    }

   }
    
 }
