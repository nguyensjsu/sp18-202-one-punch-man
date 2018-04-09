import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class ThunderSector here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ThunderSector extends Bullet
{
    private int hand_distance = 50;
    public ThunderSector(int r, int sizeX, int sizeY, int d){
        fire_rotation = r;
        size_x = sizeX;
        size_y = sizeY;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void act() 
    {
        // Add your action code here.
    }
    
    public void updateLocation(int x, int y){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null){
            turnTowards(mouse.getX(),mouse.getY());
        }
        x += hand_distance*cos(toRadians(getRotation()));
        y += hand_distance*sin(toRadians(getRotation()));
        setLocation(x, y);
    }
    
}
