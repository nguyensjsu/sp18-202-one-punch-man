import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Enermy here.
 * a base enermy class
 * @author Karas
 * @version v0.1.4
 */
public class Enermy extends Actor implements NotBullet,FreezeObj,HasHp
{
    /*enermy state */
    protected String attack_state = "stop";     //stop, bullet
    protected String move_state = "stop";       //stop, wander, chase, freeze
    protected String damage_state = "normal";   //normal
    
    /* enermy stat */
    protected String enermy_image;
    protected int size_x;
    protected int size_y;
    protected int move_speed = 3;
    protected final int MAX_HP = 20;
    protected int hp = MAX_HP;
    protected int attack_speed = 60;  //1 per sec
    protected int attack_timer = 0;
    protected int push_damage = 20;     //push attack damage
    
    /* latest player location */
    protected int player_x;
    protected int player_y;
    
    /* random timer for wander direction */
    protected int wander_timer = (int)(40*random());  //give differences between different enermy obj
    protected int wander_x;
    protected int wander_y;
    
    protected SimpleTimer freezeTimer = new SimpleTimer();
    
    /* constructor */
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
    
    /* method */
    public void act(){   
        if (move_state != "freeze"){
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
            timer();
        }
        
        /* remove condition */
        dead();
    }
   
    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    public World interface_getWorld(){return getWorld();}
    
    public int get_hp(){return hp;}
    public void set_move_state(String s){move_state = s;}
    public void set_attack_state(String s){attack_state = s;}
    public String get_damage_state(){return damage_state;}
    
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
            turnTowards(player_x, player_y);
            getWorld().addObject(new EnermyBullet(getRotation()),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    public void collision_avoidance(){
        for (NotBullet collision_obj: this.getIntersectingObjects(NotBullet.class)){
            /* avoid x collision */
            if(this.getX() <= collision_obj.interface_getX()){
                setLocation(getX()-move_speed,getY());
            }
            else{
                setLocation(getX()+move_speed,getY());
            }
            /* avoid y collision */
            if(this.getY() <= collision_obj.interface_getY()){
                setLocation(getX(),getY()-move_speed);
            }
            else{
                setLocation(getX(),getY()+move_speed);
            }
            
            /* melee attack player */
            if(collision_obj.getClass() == Player.class){
                collision_obj.damage(getX(),getY(),push_damage,"push");      //damage = 20
            }
        }
    }
    
    public void damage(int source_x, int source_y, int damage_num, String type){
         if (type == "bullet"){
            /* take damage */
            hp -= damage_num;
        }
    };
    
    public void timer(){
        if (wander_timer != 0) wander_timer--;
        if (attack_timer != 0) attack_timer--;
    }
    
    public void dead(){
        if (hp <= 0) {
            getWorld().removeObject(this);
        }
    };
    public void setNegativeState(String type, int d){
        switch(type){
            case "shocked":
                freezeTimer.mark();
                if(freezeTimer.millisElapsed()>1000){
                    
                }
                break;
        }
    }
}
