import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class AppleRain here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class AppleRain extends Bullet
{
    protected String move_state = "normal";     //normal, freeze
    /* bullet stat */
    //protected int fire_rotation = 180;
    protected int size_x;
    protected int size_y;
    protected int appleRain_timer = 360;
    protected boolean through = false;
    protected int damage = 20;
    protected int heal = 20;
    protected int x=0;
    protected int y=0;

    
    public AppleRain(){
        this(50,50,20);        //default size 30*30, not through, 2 damage
    }
    
    
    public AppleRain( int sizeX, int sizeY, int d){

        size_x = sizeX;
        size_y = sizeY;
        damage = d;
        
        //setRotation(fire_rotation);
        GreenfootImage image = new GreenfootImage("apple1.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void act(){
        if (move_state != "freeze"){

            move();
            /* timer */
            timer();
        }
        
        /* damage condition */

        
        /* remove condition */
        dead();
    }    
    
    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    
    public void set_move_state(String s){
        move_state = s;
    }
    
    public void move(){
            turn(6);
            setLocation(getX(), getY()+5);
    }
    
    public void timer(){

    }
    
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
        else if (!getWorld().getObjects(Newton.class).isEmpty() && getOneIntersectingObject(Newton.class)!= null){
            for (Newton n: this.getIntersectingObjects(Newton.class)){
                n.damage(getX(),getY(),heal,"heal");
                break;
            }
            getWorld().removeObject(this);
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().addObject(new Boom(),getX(),getY());
            getWorld().removeObject(this);
        }
    }
}
