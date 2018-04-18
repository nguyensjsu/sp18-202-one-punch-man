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
    private Actor bindActor;
    public ShockedDecorator(Actor bindActor){
        this(bindActor, 75, 75);
    }
    public ShockedDecorator(Actor bindActor, int width, int height){
        this.bindActor = bindActor;
        size_x = width;
        size_y = height;
        gifAnimator();
    }
    public void act() 
    {
        if(bindActor != null && bindActor.getWorld() != null){
            setLocation(bindActor.getX(), bindActor.getY());
            setRotation(bindActor.getRotation());
        }
        gifAnimator();
        dead();
    }
    public void gifAnimator(){
        GreenfootImage image = originGif.getCurrentImage();
        image.scale(size_x, size_y);
        setImage(originGif.getCurrentImage());
    }
}
