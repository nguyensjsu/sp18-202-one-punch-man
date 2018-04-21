import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Exit here.
 * 
 * @author Yifan  
 * @version v1.0
 */
public class Exit extends Actor
{
    /**
     * Act - do whatever the Exit wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
       if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(new EndScreen());
        }
    }    
}
