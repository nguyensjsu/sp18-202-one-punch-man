import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Darwin here.
 * 
 * @author Yifan  
 * @version v1.0
 */
public class Darwin extends Player
{
    //left:monkey, up: apeman, down: human
    private String state;
    
    private GreenfootImage monkeyState = new GreenfootImage("state1.png");
    private GreenfootImage apemanState= new GreenfootImage("state2.png");
    private GreenfootImage humanState= new GreenfootImage("state3.png");
    
    protected String player_image = "state3.png";
    protected String trans_image = "red-draught.png";
    
    /* constructor */
    public Darwin(){
        this(50,50);    //default size 50*50
    }
    
    public Darwin(int x, int y){
        size_x = x;
        size_y = y;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(humanState);
    }
    
    /**
     * Act - do whatever the Darwin wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    /* method */
    public void act(){
       transform();
       //setImage(gif.getCurrentImage());      
       /* update move */
       if (move_state != "freeze"){
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
       
       /* game over condition */
       dead();
    }   
    
    /* bullet style attack (aim by mouse)*/
    //YL: create customized bullet under bullet then change the method
    public void bullet_attack(){
        if (attack_timer == 0){
            if (getState()=="monkey"){
                getWorld().addObject(new DarwinBullet1(getRotation(), 20, 20, bullet_damage),getX(),getY());
            }
            else if (getState()=="apeman"){
                getWorld().addObject(new DarwinBullet2(getRotation(), 20, 20, bullet_damage),getX(),getY());
            }
            else {
                getWorld().addObject(new DarwinBullet3(getRotation(), 20, 20, bullet_damage),getX(),getY());
            }
                
            attack_timer = attack_speed;
        }
    }
    
    
    
    //YL: make changes to meet the customized needs
    public void timer(){   
       if (attack_timer != 0) attack_timer--;
       if (push_timer != 0) push_timer--;
       if (invincible_timer != 0) invincible_timer--;
    }
    
    //YL: transform player states: 1-monkey, 2-apeman, 3-human
    public void transform(){
       if (Greenfoot.isKeyDown("1")) {
            setImage(monkeyState);
            state="monkey";
        }
        else if (Greenfoot.isKeyDown("2")) {
            setImage(apemanState);
            state="apeman";
        }

    }
    
    public String getState(){  
        return state;
    }
    
    

}


