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
    private int sector_timer = 0;
    private String sector_state = "idle";
    private ThunderSector mySector;
    public Tesla(int x, int y){
        size_x = x;
        size_y = y;
        super.originGif = new GifImage("Tesla_head.gif");
        for (GreenfootImage image : originGif.getImages())
        {
            image.scale(x, y);
        }
        setImage(super.originGif.getCurrentImage());
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
                    mySector = new ThunderSector(getRotation(), 5);
                    getWorld().addObject(mySector,getX(),getY());
                    sector_state = "active";
                }
                break;
            case "active" : 
                if(Greenfoot.mousePressed(null) || Greenfoot.mouseDragged(null)){
                    mySector.updateLocation(getX(),getY(),getRotation());
                }
                else if(Greenfoot.mouseClicked(null) ){
                    getWorld().removeObject(mySector);
                    sector_state = "idle";
                }
                break;
        }
        
    }
    
    public void chain_attack(){
        
    }
    
    public void explode_attack(){
    }
}
