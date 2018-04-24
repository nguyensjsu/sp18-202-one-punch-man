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
    protected boolean freeze_state;
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
        setRotation(r);
    }
    
    public Decorator(int X, int Y, String pic_name){
        this(X,Y,0,0);
        setRotation(0);
        
        GreenfootImage image = new GreenfootImage(pic_name);
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public Decorator(int X, int Y, GreenfootImage img){
        this(X,Y,0,0);
        setRotation(0);
        
        img.scale(size_x, size_y);
        setImage(img);
    }
    
    /* method */
    public void act() 
    {
        if(!freeze_state){
            draw();
            move();
            update();

            /* timer */
            timer();
        }

        /* remove condition */
        dead();
    }
    
    public void draw(){}
    
    public void move(){
        int update_x;
        int update_y;
        setRotation(rotation);
        
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(rotation)));
        update_y = (int)(getY() + move_speed*sin(toRadians(rotation)));
        
        /* move */
        setLocation(update_x,update_y);
    }
    
    public void update(){}

    public void timer(){}
    
    public void setDead(){
        go_die = true;
    }
    
    public void dead(){
        if (go_die){
            getWorld().removeObject(this);
        }
    }
    public void setFreeze(){
        freeze_state = true;
    }
    public void resetFreeze(){
        freeze_state = false;
    }
}
