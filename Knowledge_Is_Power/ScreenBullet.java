import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ScreenBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScreenBullet extends Bullet
{
    
    private int timer = 10;
    public ScreenBullet(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public ScreenBullet(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
        GreenfootImage image = new GreenfootImage("screen.png");
        image.scale(sizeX, sizeY);
        setImage(image);
    }
    
   
    /* override */
    public void dead(){
        /* delete if hit enermy and type is not through */
        if (getOneIntersectingObject(Enermy.class) != null && !through){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),damage, "bullet");
                break;
            }
            getWorld().addObject(new screenBoom(),getX(),getY());
            getWorld().removeObject(this);
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
       
}
