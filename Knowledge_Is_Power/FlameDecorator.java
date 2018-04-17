import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class FlameDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FlameDecorator extends Decorator
{
    private GifImage originGif = new GifImage("flame_hit.gif");
    private Actor bindActor;
    public FlameDecorator(Actor bindActor){
        this(bindActor, 95, 95);
    }
    public FlameDecorator(Actor bindActor, int width, int height){
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
