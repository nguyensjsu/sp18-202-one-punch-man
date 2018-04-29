import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class DialogDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DialogDecorator extends Decorator
{
    protected GreenfootImage image;
    protected int finish_timer;
    
    DialogDecorator(int t){
        super(150,150,0,0);
        finish_timer = t;
        
        int i = (int)(4*random()-0.001);
        switch (i){
            case 0: image = new GreenfootImage("question mark.png"); break;
            case 1: image = new GreenfootImage("lay down.png"); break;
            case 2: image = new GreenfootImage("MMP.png"); break;
            case 3: image = new GreenfootImage("smile.png"); break;
            default: break;
        }
        image.scale(size_x,size_y);
        setImage(image);
    }
    
    public void act() 
    {
        if (finish_timer != 0){finish_timer--;}
        if (finish_timer == 0){go_die = true;}
        /* remove condition */
        dead();
    }
}
