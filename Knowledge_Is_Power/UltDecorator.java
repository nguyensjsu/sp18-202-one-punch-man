import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class UIFrameDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UltDecorator extends UIPictureDecorator
{
    protected int duration_timer;
    
    public UltDecorator(int X, int Y, int s, int r, String pic_name, int d){
        super(X,Y,s,r,pic_name);
        duration_timer = d;
    }
    /*
    public UltDecorator(int X, int Y, int s, int r, String pic_name, int d{
        super(X,Y,s,r,pic_name);
        duration_timer = d;
        tar_x = tar_X;
        tar_y = tar_Y;
    }
    */

    public void move(){
        int update_x;
        int update_y;
        setRotation(rotation);
        
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(rotation)));
        update_y = (int)(getY() + move_speed*sin(toRadians(rotation)));
        
        /* move */
        setLocation(update_x,update_y);
        if (move_speed < 0) move_speed++;
    }
    
    public void timer(){
        if (duration_timer != 0) duration_timer--;
        else go_die = true;
    }
    
}
