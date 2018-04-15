import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Decorator here.
 * a base decorator class
 * @author Karas
 * @version 0.1.4
 */
public class Decorator extends Actor
{
    /* decorator stat */
    protected int size_x;
    protected int size_y;
    protected int move_speed = 0;
    protected int rotation = 0;
    /* remove flag */
    protected boolean go_die = false;
    
    /* constructor */
    public Decorator(){
        this(1,1,0,0);
    }
    
    public Decorator(int X, int Y, int s, int r){
        size_x = X;
        size_y = Y;
        move_speed = s;
        rotation = r;
    }
    
    /* method */
    public void act() 
    {
        draw();
        move();
        
        /* remove condition */
        dead();
    }
    
    public void draw(){}
    
    public void move(){
        int update_x;
        int update_y;
        
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(rotation)));
        update_y = (int)(getY() + move_speed*sin(toRadians(rotation)));
        
        /* move */
        setLocation(update_x,update_y);
    }
    
    public void dead(){
        if (go_die){
            getWorld().removeObject(this);
        }
    }
}