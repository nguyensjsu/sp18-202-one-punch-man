import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Decorator here.
 * a subclass decorator showing HP
 * @author Karas
 * @version 0.1.4
 */
public class HpDecorator extends Decorator
{
    /* hp */
    protected HasHp actor;
    protected int max_hp;
    protected int current_hp;
    protected int offset_x;
    protected int offset_y;
    
    /* constructor */
    public HpDecorator(HasHp a, int current,int max, int offx, int offy, int X, int Y){
        super(X,Y,0,0);
        actor = a;
        current_hp = current;
        max_hp = max;
        offset_x = offx;
        offset_y = offy;
    }
    
    /* override */
    public void draw(){
        if(actor.interface_getWorld() != null){
            /* draw under actor */
            setLocation(actor.interface_getX() + offset_x, actor.interface_getY() + offset_y);
            current_hp = actor.get_hp();
            
            /* draw remain hp */
            int remain_x = (int)(size_x * current_hp / max_hp);
            if (current_hp != 0){
                if(remain_x <= 0){
                    remain_x = 1;
                }
                GreenfootImage hp = new GreenfootImage(remain_x,size_y);
                hp.setColor(Color.GREEN);
                hp.fill();
                setImage(hp);
            }
            else go_die = true;
        }
        else go_die = true;
    }
    
    public Actor getActor(){
        return (Actor)actor;
    }
}
