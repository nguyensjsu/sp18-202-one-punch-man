import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class UIHpDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UIHpDecorator extends HpDecorator
{
    protected int loc_x;
    protected int loc_y;
    
    public UIHpDecorator(HasHp a, int current, int max, int X, int Y, int loc_X, int loc_Y){
        super(a, current, max, 0, 0, X, Y);
        loc_x = loc_X;
        loc_y = loc_Y;
    }
    
    public void draw(){
        if(actor.interface_getWorld() != null){
            current_hp = actor.get_hp();
            
            /* draw remain hp */
            int remain_x = (int)(size_x * current_hp / max_hp);
            if (current_hp != 0){
                if(remain_x <= 0){
                    remain_x = 1;       //not good, need to modify
                }
                GreenfootImage hp = new GreenfootImage(remain_x,size_y);
                if (current_hp > 0.6*max_hp) hp.setColor(Color.GREEN);
                else if (current_hp > 0.2*max_hp) hp.setColor(Color.YELLOW);
                else hp.setColor(Color.RED);
                hp.fill();
                setImage(hp);
            }
            else go_die = true;
            
            setLocation(loc_x + (size_x * current_hp / max_hp / 2) - 200,loc_y);
        }
        else go_die = true;
    }
}
