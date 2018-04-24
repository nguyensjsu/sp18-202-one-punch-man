import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SmoothScreenChange here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScreenChange extends Decorator
{
    protected String type;
    protected World next_world;
    protected int trans_value_trans = 0;
    protected int trans_value_black = 255;
    GreenfootImage img = new GreenfootImage(1600,900);
    
    public ScreenChange(String Type){
        super(1600,900,0,0);
        type = Type;
    }
    
    public ScreenChange(String Type, World world){
        super(1600,900,0,0);
        type = Type;
        next_world = world;
    }
    
    public void act() 
    {
        if (type == "black to trans"){
            if (trans_value_black >= 0){
                img.clear();
                img.setColor(new Color(0,0,0,trans_value_black));
                img.scale(1600, 900);
                img.fill();
                setImage(img);
                trans_value_black-=2;
            }
            else getWorld().removeObject(this);
        }
        else if (type == "trans to black"){
            if (trans_value_trans <= 255){
                img.clear();
                img.setColor(new Color(0,0,0,trans_value_trans));
                img.scale(1600, 900);
                img.fill();
                setImage(img);
                trans_value_trans+=2;
            }
        }

    }    
}
