import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
/**
 * Write a description of class GifSkill here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Prism extends Bullet
{
    
    protected String move_state = "normal";     //normal, freeze
    /* bullet stat */
    protected int size_x;
    protected int size_y;
    protected int prism_timer = 27;
    protected boolean through = false;
    protected boolean hit = false;
    protected int damage = 12;
    /* bullet direction */
    protected int fire_rotation;
    protected GifImage myprism = new GifImage("myprism.gif");
   
    /* constructor */
    public Prism(int r,int d){
        this.fire_rotation = r;
        this.damage = d;
        GreenfootImage image = myprism.getCurrentImage();
        image.setTransparency(180);
        setImage(image);
        this.setRotation(r);
    }
    
    /* method */
    public void act(){
        if (move_state != "freeze"){
            enable();

            /* timer */
            timer();
        }
        /* damage condition */
       if(hit == false){
           if (getOneIntersectingObject(Enermy.class) != null ){
                for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                   e.damage(getX(),getY(),damage, "bullet");
                   break;
                }
           }
           hit = true;
       }
        
        /* remove condition */
        if(prism_timer == 0) dead();
    }    
    
    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    
    public void set_move_state(String s){
        move_state = s;
    }
    
    public void enable(){
        GreenfootImage image = myprism.getCurrentImage();
        if (!getWorld().getObjects(Newton.class).isEmpty())
        {
            Newton newton = (Newton)getWorld().getObjects(Newton.class).get(0);
            int x = (int) (newton.getX()+ 450*cos(toRadians(getRotation())));
            int y = (int) (newton.getY()+ 450*sin(toRadians(getRotation())));
            setLocation(x,y);
            image.setTransparency(180);
            setImage(image);
        }
    }
    
    public void timer(){
        if(prism_timer != 0) prism_timer--;
    }
    
    public void dead(){
        getWorld().removeObject(this);
    }
}
