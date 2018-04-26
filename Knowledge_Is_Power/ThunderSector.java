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
    private int hand_distance = 80;
    private SimpleTimer damage_timer = new SimpleTimer();
    private int sizeX = 200;
    private int sizeY = 200;
    private int transVal = 250;
    private boolean fade_out = true;
    public ThunderSector(int r, int x, int y, int damage){
        super.damage = damage;
        GreenfootImage image = new GreenfootImage("thunder_sector.png");
        image.scale(sizeX, sizeY);
        setImage(image);
        updateLocation(x,y,r);
        damage_timer.mark();
    }
    
    public void act() 
    {
        animator();
        if(damage_timer.millisElapsed() > 100){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),super.damage, "bullet");
            }
            damage_timer.mark();
        }
    }
    
    public void animator(){
        if(fade_out)
            transVal-=10;
        else
            transVal+=10;
        if(transVal <= 80)
            fade_out = false;
        if(transVal >= 240)
            fade_out = true;
        getImage().setTransparency(transVal);
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
