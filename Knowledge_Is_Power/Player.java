import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Obj here.
 * A base player class
 * @author Karas
 * @version v0.1.3
 */
public class Player extends Actor implements NotBullet
{
    //private GifImage gif = new GifImage("obj_arrow.gif");
    /* player state */
    private String move_state = "wasd";
    private String damage_state = "normal";
    
    /* player stat */
    private int size_x;
    private int size_y;
    private int move_speed = 5;
    private final int MAX_HP = 100;
    private int hp = MAX_HP;
    private int attack_speed = 30;  //2 per sec
    private int attack_timer = 0;
    
    /* pull stat */
    private int pull_x;
    private int pull_y;
    private int pull_rotation;
    private int pull_speed = 0;
    private int pull_timer = 0;
    
    /* damage stat */
    private final int INVINCIBLE_TIME = 60;
    private int invincible_timer = 0;
    
    public Player(){
        this(50,50);    //default size 50*50
    }
    
    public Player(int x, int y){
        size_x = x;
        size_y = y;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void act() 
    {
       //setImage(gif.getCurrentImage());
       
       switch (move_state){
           case "wasd": wasd_move(); break;
           case "pull": pull(pull_x, pull_y, pull_speed); break;
           default: break;
        }
       base_attack();
       
       /* invincible flash */
       if (invincible_timer % 20 >= 10){
           setImage(new GreenfootImage("red-draught.png"));
       }
       else{
           setImage(new GreenfootImage("lobster.png"));
        }
       
       if (invincible_timer == 0) damage_state = "normal";
       
       /* timer */
       if (attack_timer != 0) attack_timer--;
       if (pull_timer != 0) pull_timer--;
       if (invincible_timer != 0) invincible_timer--;
    }    

    /* no use, just for interface */
    public int NB_getX(){return getX();}
    public int NB_getY(){return getY();}
    
    /* movement control using WASD */
    public void wasd_move(){
        int update_x = getX();
        int update_y = getY();
        
        /* WASD move */
        if(Greenfoot.isKeyDown("w")){
            update_y = getY() - move_speed;
        }
        if(Greenfoot.isKeyDown("s")){
            update_y = getY() + move_speed;
        }
        if(Greenfoot.isKeyDown("a")){
            update_x = getX() - move_speed;
        }
        if(Greenfoot.isKeyDown("d")){
            update_x = getX() + move_speed;
        }
        
        /* turn and move */
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null){
            turnTowards(mouse.getX(),mouse.getY());
        }
        setLocation(update_x,update_y);
        
        /* observer notify */
        for (Enermy enermy: getWorld().getObjects(Enermy.class)){
            enermy.update(update_x,update_y);
        }
    }

    public void pull(int target_x, int target_y, int speed){
        int update_x = getX();
        int update_y = getY();
        
        /* pull to target */
        if (abs(getX() - target_x) > 5|| abs(getY() - target_y) > 5){
            double dx = target_x - getX();
            double dy = target_y - getY();
            double scale_x = dx / sqrt(dx*dx+dy*dy);
            double scale_y = dy / sqrt(dx*dx+dy*dy);
            update_x = getX() + (int)(speed*scale_x);
            update_y = getY() + (int)(speed*scale_y);
        }
        
        /* turn and move */
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null){
            turnTowards(mouse.getX(),mouse.getY());
        }
        setLocation(update_x,update_y);
        
        /* smooth end */
        if(pull_timer < 10)
            pull_speed--;
        
        /* back to wasd */
        if (pull_timer == 0){
            move_state = "wasd";
        }
    }
    
    /* base attack */
    public void base_attack(){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        /* bullet attack, fire by mouse */
        if (mouse != null && Greenfoot.mousePressed(null)){
            bullet_attack();
        }
    }
    
    /* bullet style attack (aim by mouse)*/
    public void bullet_attack(){
        if (attack_timer == 0){
            getWorld().addObject(new Bullet(getRotation()),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    public void damage(int source_x,int source_y, int damage_num, String type){
        if (type == "melee"){
            /* bounce away */
            move_state = "pull";
            int dx = source_x - getX();
            int dy = source_y - getY();
            pull_x = getX() - (int)(100*dx/sqrt(dx*dx+dy*dy));
            pull_y = getY() - (int)(100*dy/sqrt(dx*dx+dy*dy));
            pull_speed = 10;
            pull_timer = 20;
            /* take damage */
            hp -= damage_num;
            /* invincible time */
            invincible_timer = INVINCIBLE_TIME;
            damage_state = "invincible";
        }
    }
    
    public String get_damage_state(){
        return damage_state;
    }
    
    public void dead(){
    
    }
}
