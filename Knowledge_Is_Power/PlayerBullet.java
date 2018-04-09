import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Player_Bullet here.
 * a subclass of bullet from player only
 * @author Karas
 * @version 0.1.4
 */
public class PlayerBullet extends Bullet
{
    public PlayerBullet(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public PlayerBullet(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
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
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
}
