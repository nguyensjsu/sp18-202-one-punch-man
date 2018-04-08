import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class BaseWorld here.
 * a base world class
 * @author Karas     
 * @version v0.1.3
 */

public class BaseWorld extends World
{  
    public BaseWorld()
    {    
        // Create a new world with 1600x900 cells with a cell size of 1x1 pixels.
        super(1600, 900, 1); 
        prepare();
         
    }
 
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    public void prepare()
    {
        /* create player */
        Player player = new Player(50,50);
        addObject(player,800,400);
        
        /* create 3 enermies */
        Enermy enermy1 = new Enermy(50,50,"wander","stop");
        Enermy enermy2 = new Enermy(50,50,"chase","stop");
        Enermy enermy3 = new Enermy(50,50,"chase","bullet");
        Enermy enermy4 = new Enermy(50,50,"chase","stop");
        Enermy enermy5 = new Enermy(50,50,"chase","stop");
        addObject(enermy1,200,200);
        addObject(enermy2,400,400);
        addObject(enermy3,800,800);
        addObject(enermy4,400,800);
        addObject(enermy5,200,800);
    }
}
