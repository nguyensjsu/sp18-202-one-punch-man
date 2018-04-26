import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class YinYangBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class YinYangBullet extends EnermyBullet
{
    public YinYangBullet(int r,String img_name){
        super(r,20,20,5);        //default size 20*20, not through, 5 damage
        move_speed = 8;
        
        GreenfootImage image = new GreenfootImage(img_name);
        image.scale(size_x, size_y);
        setImage(image);
    }
}
