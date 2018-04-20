import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DarwinBullet1 here.
 * 
 * @author Yifan  
 * @version v1.0
 */
public class DarwinBullet1 extends Bullet
{
    /**
     * Act - do whatever the DarwinBullet wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */    
    private GreenfootImage monkeyBullet = new GreenfootImage("state1.png");
    //private GreenfootImage apemanBullet= new GreenfootImage("state2.png");
    //private GreenfootImage humanBullet= new GreenfootImage("state3.png");
    public DarwinBullet1(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public DarwinBullet1(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
        setImage(monkeyBullet);
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
