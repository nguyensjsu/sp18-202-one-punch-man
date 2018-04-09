import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.*;
/**
 * Write a description of class ThunderSector here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ThunderSector extends Bullet
{
    private int hand_distance = 60;
    private SimpleTimer damage_timer = new SimpleTimer();
    public ThunderSector(int r, int d){
        this(r,200,200,d);
    }
    
    public ThunderSector(int r, int sizeX, int sizeY, int d){
        damage = 1;
        fire_rotation = r;
        size_x = sizeX;
        size_y = sizeY;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);
        damage_timer.mark();
    }
    
    public void act() 
    {
        if(damage_timer.millisElapsed() > 100){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),damage, "bullet");
            }
            damage_timer.mark();
        }
    }
    
    public void updateLocation(int x, int y, int r){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null){
            turnTowards(mouse.getX(),mouse.getY());
        }
        x += hand_distance*cos(toRadians(r));
        y += hand_distance*sin(toRadians(r));
        setLocation(x, y);
    }
}
