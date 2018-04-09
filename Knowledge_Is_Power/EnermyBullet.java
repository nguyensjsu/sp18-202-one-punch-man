import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Player_Bullet here.
 * a subclass of bullet from enermy only
 * @author Karas
 * @version 0.1.4
 */
public class EnermyBullet extends Bullet
{
    public EnermyBullet(int r){
        this(r,20,20,20);        //default size 20*20, not through, 20 damage
    }
    
    public EnermyBullet(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
    }
    
    /* override */
    public void dead(){
        /* delete if hit player and type is not through */
        if (getOneIntersectingObject(Player.class) != null && !through){
            for (Player p: this.getIntersectingObjects(Player.class)){
                p.damage(getX(),getY(),damage, "bullet"); 
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
