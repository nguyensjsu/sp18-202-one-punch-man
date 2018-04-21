import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import javax.swing.JOptionPane;

/**
 * Write a description of class Darwin here.
 * 
 * @author Yifan  
 * @version v1.0
 */
public class Darwin extends Player
{
    private String state;
    
    private GreenfootImage humanState= new GreenfootImage("darwin.png");
    private GreenfootImage monkeyState = new GreenfootImage("monkey.png");
    private GreenfootImage apemanState= new GreenfootImage("apeman.png");
    
    protected String player_image = "darwin.png";
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
           
           ult();
           
           /* timer */
           timer();
       }
       
       /* game over condition */
       dead();
    }   
    
    /* bullet style attack (aim by mouse)*/
    public void bullet_attack(){
        if (attack_timer == 0){
            if (getState()=="monkey"){
                getWorld().addObject(new MonkeyBullet(getRotation(), 20, 20, bullet_damage),getX(),getY());
            }
            else if (getState()=="apeman"){
                getWorld().addObject(new ApemanBullet(getRotation(), 20, 20, bullet_damage),getX(),getY());
            }
            else {
                getWorld().addObject(new DarwinBullet(20, 20),getX(),getY());
            }
                
            attack_timer = attack_speed;
        }
    }
    
    public void timer(){   
       if (attack_timer != 0) attack_timer--;
       if (push_timer != 0) push_timer--;
       if (invincible_timer != 0) invincible_timer--;
    }
    
    //transform player states: default-man,1-monkey,2-apeman
    public void transform(){
       if (Greenfoot.isKeyDown("1")) {
            setImage(monkeyState);
            state="monkey";
            player_image = "monkey.png";
        }
        else if (Greenfoot.isKeyDown("2")) {
            setImage(apemanState);
            state="apeman";
            player_image = "apeman.png";
        }

    }
    
    public void ult(){
        if (Greenfoot.isKeyDown("3")){
            String inputValue = JOptionPane.showInputDialog("YOU HAVE THE CONTROL:");
            
            if (inputValue.equals("sudo rm -rf /")) {
                getWorld().removeObjects(getWorld().getObjects(TestEnermy.class));
            }
        }   
    }
    
    public String getState(){  
        return state;
    }
    
    

}


