import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
/**
 * Write a description of class UfoBullet here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class UfoBullet extends EnermyBullet
{
    /**
     * Act - do whatever the UfoBullet wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    protected double move_speed = 7.0;
    protected boolean first_time = true;
    protected int dx;
    protected int dy;
    protected double d;
    public UfoBullet(int r) {
        super(r,50,50,20); 
    }
    
    public UfoBullet(int sizeX,int sizeY, int d){
        super(sizeX,sizeY,d);
        GreenfootImage image = new GreenfootImage("rocket.png");
        image.scale(sizeX, sizeY);
        setImage(image);
    }

    public UfoBullet(int r, int sizeX, int sizeY, int d) {
        super(r,sizeX,sizeY,d);
        GreenfootImage image = new GreenfootImage("rocket.png");
        image.scale(sizeX, sizeY);
        setImage(image);
    }

    public void act(){
        if (!freeze_state){
            move();

            /* timer */
            timer();
        }

        /* remove condition */
        dead();
    }
    
    public void move(){
        if (first_time){
            Player player = (Player)getWorld().getObjects(Player.class).get(0);
            dx = player.getX()-getX();
            dy = player.getY()-getY();
            d = sqrt(dx*dx+dy*dy);
            first_time = false;
        }
        
        
        /* update */
        int update_x = (int)(getX() + move_speed*dx/d);
        int update_y = (int)(getY() + move_speed*dy/d);
        
        /* move */
        setLocation(update_x,update_y);
        setRotation(fire_rotation);
    }

    /* override */
    public void dead(){
        /* delete if hit player and type is not through */
        if (getOneIntersectingObject(Player.class) != null){
            for (Player p: this.getIntersectingObjects(Player.class)){
                p.damage(getX(),getY(),damage, "bullet");
                break;
            }
            if (!through){
                getWorld().removeObject(this);
            }
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
}
