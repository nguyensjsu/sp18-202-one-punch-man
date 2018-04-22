import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ShockedDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ShockedDecorator extends AttachEffectDecorator
{
    public ShockedDecorator(Actor bindActor){
        this(bindActor, 75, 75, 2000);
    }
    public ShockedDecorator(Actor bindActor, int width, int height, int lifetime){
        this.bindActor = bindActor;
        this.lifetime = lifetime;
        size_x = width;
        size_y = height;
        originGif = new GifImage("state_shocked.gif");
        lifeTimer.mark();
        gifAnimator();
    }
}
