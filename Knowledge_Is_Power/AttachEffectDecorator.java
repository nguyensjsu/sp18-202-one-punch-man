import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class AttachEffectDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class AttachEffectDecorator extends Decorator
{
    protected GifImage originGif;
    protected Actor bindActor;
    protected int lifetime = 0;
    protected SimpleTimer lifeTimer = new SimpleTimer();
    public void act() 
    {
        if(lifeTimer.millisElapsed() > lifetime){
            setDead();
        }
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
    
    public void update(){
        lifeTimer.mark();
    }
}
