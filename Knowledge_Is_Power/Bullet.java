import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Bullet here.
 * a base Bullet class
 * @author Karas 
 * @version v0.1.4
 */
public class Bullet extends Actor implements FreezeObj
{
    /* bullet state */
    protected boolean freeze_state = false;
    /* bullet stat */
    protected String bullet_image;
    protected int size_x;
    protected int size_y;
    protected double move_speed = 10.0;
    protected boolean through = false;
    protected int damage = 20;
    protected String damage_type = "bullet";
    /* bullet direction */
    protected int fire_rotation;
   
    /* constructor */
    public Bullet(){
    }
    
    public Bullet(int r){
        this(r,20,20,20);        //default size 20*20, not through, 20 damage
    }
    
    public Bullet(int r, int sizeX, int sizeY, int d){
        fire_rotation = r;
        size_x = sizeX;
        size_y = sizeY;
        damage = d;
        
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    /* method */
    public void act(){
        if (!freeze_state){
            move();

            /* timer */
            timer();
        }
        
        /* remove condition */
        dead();
    }    
    
    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    
    public void set_freeze_state(boolean b){freeze_state = b;}
    
    public void move(){
        int update_x;
        int update_y;
        
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(fire_rotation)));
        update_y = (int)(getY() + move_speed*sin(toRadians(fire_rotation)));
        
        /* move */
        setLocation(update_x,update_y);
    }
    
    public void timer(){
    
    }
    
    public void dead(){
        /* delete if hit world edge */
        if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
}
