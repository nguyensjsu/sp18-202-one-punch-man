import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Tesla here.
 * Tesla Special Operation
 * @author Zhiye Chen 
 * @version v0.1
 */
public class Tesla extends Player implements ActorAction
{
    private GifImage gif = new GifImage("Tesla_head.gif");
    private int sector_last = 60;
    private int sector_timer = 0;
    private String sector_state = "idle";
    private ThunderSector mySector;
    public Tesla(int x, int y){
        for (GreenfootImage image : gif.getImages())
        {
            image.scale(x, y);
        }
        setImage(gif.getCurrentImage());
        super.attach(this);
    }
    
    public void doAction() 
    {
       sector_attack();
       chain_attack();
       explode_attack();
       /* timer */
       if (sector_timer!=0) sector_timer--;
    }
    
    public void sector_attack(){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        switch (sector_state){
            case "idle" : 
                if (mouse != null && Greenfoot.mousePressed(null)){
                    mySector = new ThunderSector(getRotation(),100,100, 50);
                    getWorld().addObject(mySector,getX(),getY());
                    sector_state = "active";
                }
                break;
            case "active" : 
                mySector.updateLocation(getX(),getY());
                break;
        }
        
    }
    
    public void chain_attack(){
        
    }
    
    public void explode_attack(){
    }
}
