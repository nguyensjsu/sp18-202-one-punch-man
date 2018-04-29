import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class DrPPaperDecorator here.
 * Dr.P's ult paper animation
 * @author Karas
 * @version v1.0.6
 */
public class DrPPaperDecorator extends Decorator
{   
    protected int target_x;
    protected int target_y;
    protected int finish_timer = 180;   //2 sec animation before kill all
    protected DialogDecorator dialog = null;
    
    public DrPPaperDecorator(int targetX, int targetY, int speed, int rotation){
        super(30,30,speed,rotation);
        target_x = targetX;
        target_y = targetY;
        
        GreenfootImage image = new GreenfootImage("DrPUltIcon.png");
        image.scale(30,30);
        setImage(image);
    }
    
     /* method */
    public void act() 
    {
        move();
        
        if (finish_timer != 0){finish_timer--;}
        if (finish_timer == 0){go_die = true;}
        /* remove condition */
        dead();
    }
    
    public void move()
    {
        /* if not reach target */
        if ((abs(target_x - getX()) > 5) || (abs(target_y - getY()) > 5)){
            int update_x;
            int update_y;
            
            turnTowards(target_x,target_y);
            
            /* update */
            update_x = (int)(getX() + move_speed*cos(toRadians(getRotation())));
            update_y = (int)(getY() + move_speed*sin(toRadians(getRotation())));
    
            /* move */
            setLocation(update_x,update_y);
        }
        /* reached target */
        else
        {
            if (dialog == null){
                dialog = new DialogDecorator(finish_timer);
                getWorld().addObject(dialog, getX()-50,getY()-100);
                setRotation(0);
            }
        }
    }
}
