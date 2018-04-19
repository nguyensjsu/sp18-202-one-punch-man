import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DrP_base_attack here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DrPSuperAttack extends PlayerBullet
{   
    protected boolean chase_state = false;
    protected int life_timer = 6000;
    
    public DrPSuperAttack(int r){
        this(r,20,20,5,false);        //default size 20*20, not through, 5 damage, not chasing
    }
    
    public DrPSuperAttack(int r, int sizeX, int sizeY, int d, boolean b){
        super(r,sizeX,sizeY,d);
        chase_state = b;
    }
    
    public void act(){
        if (!freeze_state){
            if (chase_state) chase_turn();
            move();
            
            /* timer */
            timer();
        }
        
        /* remove condition */
        dead();
    }
    
    public void chase_turn(){
        for (Enermy enermy: getObjectsInRange(300,Enermy.class)){
            turnTowards(enermy.getX(),enermy.getY());
            fire_rotation = getRotation();
            break;
        }
    }
    
    public void timer(){
        life_timer--;
    }
    
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
        /* delete if life runs out */
        else if (life_timer == 0){
            getWorld().removeObject(this);
        }
    }
}
