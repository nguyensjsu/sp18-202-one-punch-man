import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.List;
/**
 * Write a description of class Hawking here.
 * 
 * @author Danlu 
 * @version v0.1
 */
public class Hawking extends Player
{
    /**
     * Act - do whatever the Hawking wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
   
    protected String player_image = "man01.png";
   
    protected int attack_speed = 15;
    protected int bullet_damage = 5;
    
    /* timer */
    protected int skill_one_duration_timer = 0;
    protected int skill_one_cd_timer = 0;
    protected int skill_two_duration_timer = 0;
    protected int skill_two_cd_timer = 0;
    protected int ult_duration_timer = 0;
    protected int ult_cd_timer = 0;
    private int move_speed = 300;

    
    public Hawking() {
        this(50,50);
    }
    
    public Hawking(int x, int y) {
        size_x = x;
        size_y = y;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(image);     
    }
    
    public void act() 
    {
       /* update move */
       if (!freeze_state){
           
           switch (move_state){
               case "wasd": wasd_move(); break;
               case "push": push(push_x, push_y, push_speed); break;
               default: break;
            }
           
            
           

           /* ability */
           base_attack();
           skill_one();
           skill_two();
           ult();
           
           /* invincible flash */
           //invincible_flash(player_image,trans_image);
           
             /* timer */
           timer(); 
           additional_timer();
   
       }
       
      /* animation timer */
      // animation_timer();
       
       /* game over condition */
       dead();

     } 
    
    
    /* bullet style attack (aim by mouse)*/
    public void bullet_attack(){
        if (attack_timer == 0){
            getWorld().addObject(new ScreenBullet(getRotation(), 50, 50, bullet_damage),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    
    public void skill_one(){
        if (skill_one_cd_timer == 0){
            if(Greenfoot.isKeyDown("1")){
              
                //MouseInfo mouse = Greenfoot.getMouseInfo();
                 int update_x = (int)(getX() + 200*cos(toRadians(getRotation())));
                 int update_y = (int)(getY() + 200*sin(toRadians(getRotation())));
                 getWorld().addObject(new BlackHoleDecorator(100,100,bullet_damage),update_x, update_y);
                
              
                skill_one_cd_timer = 360;
            }
        }
    }
    
    public void skill_two(){
        if (skill_two_cd_timer == 0){
            if(Greenfoot.isKeyDown("2")){
               
               MouseInfo mouse = Greenfoot.getMouseInfo();
               if (mouse != null){
                   turnTowards(mouse.getX(),mouse.getY());
               }
              
                /* update */
               int update_x = (int)(getX() + move_speed*cos(toRadians(getRotation())));
               int update_y = (int)(getY() + move_speed*sin(toRadians(getRotation())));
        
                /* move */
                setLocation(update_x,update_y);
                
                /* observer notify */
                for (Enermy enermy: getWorld().getObjects(Enermy.class)){
                    enermy.update(update_x,update_y);
                }
                
                skill_two_cd_timer = 360;
            }
        }
    }
    
    public void ult(){
        if (ult_cd_timer == 0){
            if(Greenfoot.isKeyDown("3")){
                MouseInfo mouse = Greenfoot.getMouseInfo();
                 int update_x = (int)(getX() + 200*cos(toRadians(getRotation())));
                 int update_y = (int)(getY() + 200*sin(toRadians(getRotation())));
                 getWorld().addObject(new BlackHoleDecorator(200, 200, bullet_damage),update_x, update_y);
                 for (Enermy enermy: getWorld().getObjects(Enermy.class)){
                        enermy.setLocation(getX(), getY());
                        enermy.damage(getX(),getY(),bullet_damage, "bullet");
                    
                 }
               
                 
                 ult_cd_timer = 360;
                
                
            }
        }
    }
    
    public void additional_timer(){
    
        if (skill_one_cd_timer != 0){skill_one_cd_timer--;}
        
        if (skill_two_cd_timer != 0){skill_two_cd_timer--;}
        
        if (ult_cd_timer != 0){ult_cd_timer--;}
        
    }
    
   
    
    
    
}