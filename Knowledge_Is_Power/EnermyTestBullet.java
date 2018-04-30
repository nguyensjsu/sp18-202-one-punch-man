import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class EnermyTestBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EnermyTestBullet extends EnermyBullet
{
    public EnermyTestBullet(int r){
        super(r,20,20,1);        //default size 20*20, not through, 1 damage
    
        GreenfootImage image = new GreenfootImage("laser.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
}
