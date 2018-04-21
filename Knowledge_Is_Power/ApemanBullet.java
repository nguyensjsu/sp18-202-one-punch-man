import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ApemanBullet here.
 * 
 * @author Yifan  
 * @version v1.0
 */

public class ApemanBullet extends Bullet
{   
    private GifImage apemanBullet = new GifImage("flame_hit.gif");
    private int sizeX = 200;
    private int sizeY = 120;
    
    protected boolean through = true;
    
    public ApemanBullet(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public ApemanBullet(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
        gifAnimator();
    }
    
    public void gifAnimator(){
        GreenfootImage image = apemanBullet.getCurrentImage();
        image.scale(sizeX, sizeY);
        setImage(apemanBullet.getCurrentImage());
    }
    
    /* override */
    public void dead(){
        /* delete if hit enermy and type is not through */
        if (getOneIntersectingObject(Enermy.class) != null && !through){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),damage, "bullet");
                break;
            }
            getWorld().removeObject(this);
        }
        /* if hit enermy and type is through */
        /* ATTENTION: through damage must be very small */
        else if (getOneIntersectingObject(Enermy.class) != null && through){
            for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                e.damage(getX(),getY(),damage, "bullet");
                break;
            }
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
        
}

