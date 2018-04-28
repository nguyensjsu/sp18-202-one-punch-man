import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class screenBoom here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class screenBoom extends Bullet
{
    protected String move_state = "normal";     //normal, freeze
    /* bullet stat */
    protected int size_x;
    protected int size_y;
    protected int boom_timer = 35;
    protected boolean through = false;
    protected boolean hit = false;
    protected int damage = 20;
    /* bullet direction */
    protected int fire_rotation;
    protected GifImage boom = new GifImage("boom1.gif");
   
    /* constructor */
    public screenBoom(){  
        this(100,100,20);
    }
    
    public screenBoom(int sizeX, int sizeY, int d){  
        size_x = sizeX;
        size_y = sizeY;
        damage = d;
        GreenfootImage image = boom.getCurrentImage();
        image.setTransparency(220);
        setImage(image);
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
        if(boom_timer == 0) dead();
    }    
    
    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    
    public void set_move_state(String s){
        move_state = s;
    }
    
    public void enable(){
        GreenfootImage image = boom.getCurrentImage();
        image.scale(size_x, size_y);
        image.setTransparency(220);
        setImage(image);

    }
    
    public void timer(){
        if(boom_timer != 0) boom_timer--;
    }
    
    public void dead(){
        getWorld().removeObject(this);
    }   
}
