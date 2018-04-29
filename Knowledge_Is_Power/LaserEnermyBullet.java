import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Laser here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LaserEnermyBullet extends EnermyBullet
{
    public LaserEnermyBullet(int r, int s, int x, int y){
        super(r,x,y,5);
        move_speed = s;
        setRotation(r);
        through = true;
        
        GreenfootImage image = new GreenfootImage("laser.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
}
