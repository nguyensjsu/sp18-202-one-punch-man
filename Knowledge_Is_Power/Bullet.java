import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Bullet here.
 * a base Bullet class
 * @author Karas 
 * @version v0.1.3
 */
public class Bullet extends Actor
{
    /* bullet stat */
    private int size_x;
    private int size_y;
    private int move_speed = 10;
    /* bullet direction */
    private int fire_rotation;
   
    /* method */
    public Bullet(int r){
        this(r,20,20);        //default size 20*20
    }
    
    public Bullet(int r, int sizeX, int sizeY){
        fire_rotation = r;
        size_x = sizeX;
        size_y = sizeY;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void act() {
       move();
       dead();
       
       /* timer */
    }    
    
    public void move(){
        /* get current location */
        int update_x;
        int update_y;
        
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(fire_rotation)));
        update_y = (int)(getY() + move_speed*sin(toRadians(fire_rotation)));
        
        /* move */
        setLocation(update_x,update_y);
    }
    
    public void dead(){
        /* delete if hit world edge */
        if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
}
