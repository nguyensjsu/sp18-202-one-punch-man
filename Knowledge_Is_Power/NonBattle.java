import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class OP here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NonBattle extends World
{   
    public NonBattle()
    {    
        super(1600, 900, 1);
        
        /* setting actor order*/
        setActOrder(/* effect under actor */
                    Decorator.class,
                    /* ScreenChange*/
                    ScreenChange.class
        );
        
        prepare();
    }
    
    public void prepare(){
        /*
        GreenfootImage image = new GreenfootImage(1600,900);
        image.setColor(new Color(255,255,255));
        image.scale(1600, 900);
        image.fill();
        setBackground(image);
        */
    }
}
