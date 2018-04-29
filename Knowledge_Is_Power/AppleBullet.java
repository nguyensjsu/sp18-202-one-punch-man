import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class AppleBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class AppleBullet extends Bullet
{
    public AppleBullet(int r){
        this(r,20,20,12);        //default size 20*20, not through,12 damage
    }
    
    public AppleBullet(int r, int sizeX, int sizeY, int d){
        fire_rotation = r;
        size_x = sizeX;
        size_y = sizeY;
        damage = d;
        
        GreenfootImage image = new GreenfootImage("apple1.png");
        image.scale(size_x, size_y);
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
            getWorld().removeObject(this);
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
}
