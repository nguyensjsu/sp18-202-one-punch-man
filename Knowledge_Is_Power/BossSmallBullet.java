import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class EnermyTestBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BossSmallBullet extends EnermyBullet
{
    public BossSmallBullet(double s,int r,String img_name){
        super(r,20,20,5);        //default size 30*30, not through, 5 damage
        move_speed = s;
        
        GreenfootImage image = new GreenfootImage(img_name);
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public BossSmallBullet(double s,int x, int y, String img_name){
        super(0,20,20,5); //default size 30*30, not through, 5 damage
        move_speed = s;
        turnTowards(x,y);
        fire_rotation = getRotation();
        
        GreenfootImage image = new GreenfootImage(img_name);
        image.scale(size_x, size_y);
        setImage(image);
    }
}
