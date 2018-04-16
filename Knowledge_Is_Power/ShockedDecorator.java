import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ShockedDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ShockedDecorator extends Decorator
{
    private GifImage originGif = new GifImage("state_shocked.gif");
    public ShockedDecorator(){
        this(75, 75);
    }
    public ShockedDecorator(int width, int height){
        size_x = width;
        size_y = height;
    }
    public void act() 
    {
        gifAnimator();
    }
    public void gifAnimator(){
        GreenfootImage image = originGif.getCurrentImage();
        image.scale(size_x, size_y);
        setImage(originGif.getCurrentImage());
    }
}
