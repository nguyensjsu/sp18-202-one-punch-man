import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class AlienBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class AlienBullet extends EnermyBullet
{
   
    public AlienBullet(int r) {
        super(r,20,20,1); 
    }
    
    public AlienBullet(int r, int sizeX, int sizeY, int d) {
        super(r,sizeX,sizeY,d); 
        GreenfootImage image = new GreenfootImage("ice.png");
        image.scale(size_x, size_y);
        setImage(image);
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
