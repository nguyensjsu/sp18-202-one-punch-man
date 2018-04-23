import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Huge here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class HugeEnermyBullet extends EnermyBullet
{
    public HugeEnermyBullet(int r, int s){
        super(r,200,200,10);
        move_speed = s;
        
        GreenfootImage image = new GreenfootImage("gold-ball.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
}
