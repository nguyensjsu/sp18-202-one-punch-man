import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class FlameDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FlameDecorator extends AttachEffectDecorator
{
    public FlameDecorator(Actor bindActor){
        this(bindActor, 95, 95, 2000);
    }
    public FlameDecorator(Actor bindActor, int width, int height, int lifetime){
        this.bindActor = bindActor;
        this.lifetime = lifetime;
        size_x = width;
        size_y = height;
        originGif = new GifImage("flame_hit.gif");
        lifeTimer.mark();
        gifAnimator();
    }
}
