import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Enermy here.
 * a base enermy class
 * @author Karas
 * @version v0.1.3
 */
public class Enermy extends Actor implements NotBullet
{
    /*enermy state */
    private String attack_state = "stop";
    private String move_state = "stop";
    private String damage_state = "normal";
    
    /* enermy stat */
    private int size_x;
    private int size_y;
    private int move_speed = 3;
    private int hp = 100;
    private int attack_speed = 60;  //1 per sec
    private int attack_timer = 0;
    
    /* latest player location */
    private int player_x;
    private int player_y;
    
    /* random timer for wander direction */
    private int wander_timer = (int)(40*random());  //give differences between different enermy obj
    private int wander_x;
    private int wander_y;
    
    public Enermy(){
        this(50,50,"stop","stop");    //default size 50*50
    }
    
    public Enermy(int sizeX, int sizeY, String move, String attack){
        size_x = sizeX;
        size_y = sizeY;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);
        move_state = move;
        attack_state = attack;
    }
    
    public void act() 
    {  
        /* select move strategy */

        switch (move_state){
            case "wander": wander(); break;
            case "chase": chase(); break;      
            default: break;
        }
        
        /* select attack strategy */
        switch (attack_state){
            case "bullet": bullet_attack(); break;
            default: break;
        }
        
        /* collision avoidance */
        collision_avoidance();
        
        /* timer */
        if (wander_timer != 0) wander_timer--;
        if (attack_timer != 0) attack_timer--;
    }
    
    /* no use, just for interface */
    public int NB_getX(){return getX();}
    public int NB_getY(){return getY();}
    
    public void set_move_state(String s){
        move_state = s;
    }
    
    public void set_attack_state(String s){
        attack_state = s;
    }
    
    /* observer update */
    public void update(int x, int y){
        player_x = x;
        player_y = y;
    }
    
    public void chase(){
        int update_x;
        int update_y;
        
        turnTowards(player_x,player_y);
        
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(getRotation())));
        update_y = (int)(getY() + move_speed*sin(toRadians(getRotation())));

        /* move */
        setLocation(update_x,update_y);
        
        /* collision avoidance */
        collision_avoidance();
    }
    
    public void wander(){
        int update_x;
        int update_y;
        
        /* calculate direction to a random point for every 80-120 frames (~2sec) */
        if (wander_timer == 0){
            wander_x = (int)(1600*random()) - getX();
            wander_y = (int)(900*random()) - getY();
            wander_timer = (int)(120-40*random());
        }
        else if (wander_timer > 40){
            turnTowards(wander_x,wander_y);
            
            update_x = (int)(getX() + move_speed*cos(toRadians(getRotation())));
            update_y = (int)(getY() + move_speed*sin(toRadians(getRotation())));

            setLocation(update_x,update_y);
        }
    }
    
    /* bullet style attack ,aim player */
    public void bullet_attack(){
        if (attack_timer == 0){ 
            getWorld().addObject(new Bullet(getRotation()),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    public void collision_avoidance(){
        for (NotBullet collision_obj: this.getIntersectingObjects(NotBullet.class)){
            /* avoid x collision */
            if(this.getX() <= collision_obj.NB_getX()){
                setLocation(getX()-move_speed,getY());
            }
            else{
                setLocation(getX()+move_speed,getY());
            }
            /* avoid y collision */
            if(this.getY() <= collision_obj.NB_getY()){
                setLocation(getX(),getY()-move_speed);
            }
            else{
                setLocation(getX(),getY()+move_speed);
            }
            
            if(collision_obj.getClass() == Player.class){
                if (collision_obj.get_damage_state() != "invincible"){
                    collision_obj.damage(getX(),getY(),10,"melee");
                }
            }
        }
    }
    
     public void damage(int source_x, int source_y, int damage_num, String type){
    
    };
    
     public String get_damage_state(){
        return damage_state;
    }
    
    public void dead(){
    
    };
}
