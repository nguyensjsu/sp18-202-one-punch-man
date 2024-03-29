import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.*;

/**
 * Write a description of class Obj here.
 * A base player class
 * @author Karas
 * @version v0.1.4
 */
public class Player extends Actor implements NotBullet,FreezeObj,HasHp
{
    protected GifImage originGif;
    protected GifImage transGif;
    /* player state */
    protected boolean freeze_state = false;      //pause
    protected String move_state = "wasd";       //wasd, push
    protected String damage_state = "normal";       //normal, invincible
    
    /* player stat */
    protected String player_image = "lobster.png";
    protected String trans_image = "trans.png";
    protected int size_x;
    protected int size_y;
    protected int move_speed = 5;
    protected final int MAX_HP = 200;
    protected int hp = MAX_HP;
    protected int bullet_damage = 10;
    protected int attack_speed = 30;  //2 per sec
    
    /* push stat */
    protected int push_x;
    protected int push_y;
    protected int push_rotation;
    protected int push_speed = 0;
    
    /* damage stat */
    protected final int INVINCIBLE_TIME = 60;
    
    /* timer */
    protected int attack_timer = 0;
    protected int push_timer = 0;
    protected int invincible_timer = 0;
    protected int ult_cutscence_timer = 0;
    
    /* ult trigger flag */
    protected boolean ult_trigger = false;
    
    /* constructor */
    public Player(){
        this(50,50);    //default size 50*50
    }
    
    public Player(int x, int y){
        size_x = x;
        size_y = y;
    }
    
    
    public void act() 
    {   /* update move */
       if (!freeze_state){
           switch (move_state){
               case "wasd": wasd_move(); break;
               case "push": push(push_x, push_y, push_speed); break;
               default: break;
           }
           
           /* ability */
           base_attack();
           
           /* invincible flash */
           invincible_flash(player_image,trans_image);
           
           /* timer */
           timer();
       }
       
       /* animation timer */
       animation_timer();
       /* game over condition */
       dead();
    }

    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    public World interface_getWorld(){return getWorld();}
    
    public int get_hp(){return hp;}
    public String get_damage_state(){return damage_state;}
    public void set_freeze_state(boolean b){freeze_state = b;}
    public void set_move_state(String s){move_state = s;}
    
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

    public void push(int target_x, int target_y, int speed){
        int update_x = getX();
        int update_y = getY();
        
        /* push to target */
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
        if(push_timer < 10)
            push_speed--;
        
        /* back to wasd */
        if (push_timer == 0){
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
            getWorld().addObject(new PlayerBullet(getRotation(), 20, 20, bullet_damage),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    public void damage(int source_x,int source_y, int damage_num, String type){
        if ((damage_state) != "invincible"){
            switch (type){
                case "push":
                    /* bounce away */
                    move_state = "push";
                    int dx = source_x - getX();
                    int dy = source_y - getY();
                    push_x = getX() - (int)(100*dx/sqrt(dx*dx+dy*dy));
                    push_y = getY() - (int)(100*dy/sqrt(dx*dx+dy*dy));
                    push_speed = 10;
                    push_timer = 20;
                    /* take damage */
                    hp -= damage_num;
                    /* invincible time */
                    invincible_timer = INVINCIBLE_TIME;
                    damage_state = "invincible";
                    break;
                    
                case "bullet":
                    /* take damage */
                    hp -= damage_num;
                    /* invincible time */
                    invincible_timer = INVINCIBLE_TIME;
                    damage_state = "invincible";
                    break;
                    
                default: break;
            }
        }
    }
    
    public void invincible_flash(String origin, String trans){
       GreenfootImage image = new GreenfootImage(origin);
       if (invincible_timer % 20 >= 10){
           if(transGif != null){
               image = transGif.getCurrentImage();
           }
           else{
               image.clear();
           }
       }
       else{
           if(originGif != null){
               image = originGif.getCurrentImage();
           }
           else{
               image = new GreenfootImage(origin);
            }
        }
       image.scale(size_x, size_y);
       setImage(image);
       if ((invincible_timer == 0) && damage_state == "invincible") damage_state = "normal";
    }
    
    public void timer(){   
       if (attack_timer != 0) attack_timer--;
       if (push_timer != 0) push_timer--;
       if (invincible_timer != 0) invincible_timer--;
    }
    
    public void animation_timer(){}
    
    public void ult_cutscence(String player_pic, String sentence_pic){
        /* middle back */
        getWorld().addObject(new UltDecorator(1600,500,0,0,"ultback.gif","gif",120),800,450);
        
        /* player */
        getWorld().addObject(new UltDecorator(500,500,-30,0,player_pic,120),1600,450);
        
        /* sentence */
        getWorld().addObject(new UltDecorator(600,400,-47,0,sentence_pic,120),1600,450);
    
        /* side backs */
        /*
        getWorld().addObject(new UltDecorator(900,400,0,30,"board.jpg",120),1300,50);
        getWorld().addObject(new UltDecorator(2000,300,0,352,"board.jpg",120),1000,900);
        */
        
        /* two lines */
        getWorld().addObject(new UltDecorator(1600,30,0,0,"ultframe.png",120),800,200);
        getWorld().addObject(new UltDecorator(1600,30,0,0,"ultframe.png",120),800,700);
        
        ult_cutscence_timer = 150;
    }
    
    public void dead(){
        if(hp <= 0){
            Greenfoot.setWorld(new GameOver());
        }
    }
    public List<Enermy> getObjectsInRange(int range){
        return getObjectsInRange(range, Enermy.class);
    }
}
