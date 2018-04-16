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
        Player player = new Tesla();  //size 50*50
        addObject(player, 800, 400);
        HpDecorator player_hp = new HpDecorator(player,100,100,0,40,50,10);   // hp 100-100, offset(0,40), size 50*10
        addObject(player_hp, 10000, 10000);
        
        /* create 3 chasing enermies that shots bullets */
        for (int i=0; i<0; i++){
            Enermy enermy = new Enermy(50,50,"chase","bullet");
            addObject(enermy, (int)(1600*random()), (int)(900*random()));
            HpDecorator enermy_hp = new HpDecorator(enermy,20,20,0,40,50,10);   //hp 20-20, offset(0,40), size 50*10
            addObject(enermy_hp, 10000, 10000);
        }
        
        /* create 1 wandering enermy that does not attack */
        Enermy enermy = new Enermy(50,50,"wander","stop");
        addObject(enermy, (int)(1600*random()), (int)(900*random()));
        HpDecorator enermy_hp = new HpDecorator(enermy,20,20,0,40,50,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 10000, 10000);
    }
}
