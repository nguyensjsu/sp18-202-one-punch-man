import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DarwinBullet2 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DarwinBullet2 extends Bullet
{
    private GreenfootImage apemanBullet= new GreenfootImage("state2.png");
    private GreenfootImage humanBullet= new GreenfootImage("state3.png");
    
    protected boolean through = true;
    
    public DarwinBullet2(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public DarwinBullet2(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
        setImage(apemanBullet);
    }
    
     public void act(){
        //transform();
        if (move_state != "freeze"){
            move();

            /* timer */
            timer();
        }
        
        /* remove condition */
        dead();
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

