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
    private int attack_speed = 30;  //2 per sec
    private int attack_timer = 0;
    private int blackhole_duration_time = 0;
    private int blackhole_cd_time = 0;
    private int movefast_duration_time = 0;
    private int movefast_cd_time = 0;
    private int explored_duration_time = 0;
    private int explored_cd_time = 0;
    private int move_speed = 30;
    private String player_image = "man01.png";

    
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
       if (move_state != "freeze"){
           
           switch (move_state){
               case "wasd": wasd_move(); break;
               case "push": push(push_x, push_y, push_speed); break;
               default: break;
            }
          /* invincible flash */
           //invincible_flash(player_image,trans_image);
           base_attack();
           if(Greenfoot.isKeyDown("1")){
               
               if(blackhole_cd_time == 0 && blackhole_duration_time == 0) {
                   blackHoleAttack();
               }
               
               blackHoleTimer();
            }
            else if(Greenfoot.isKeyDown("2")){
                moveFast();
            }
            else if(Greenfoot.isKeyDown("3")){
                explodeAttack();
            }
           timer();
   
       }
       
      /* game over condition */
      dead();

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
            getWorld().addObject(new ScreenBullet(getRotation(), 50, 50, bullet_damage),getX(),getY());
            attack_timer = attack_speed;
        }
    }
     
 /* movement control using key 2 */
    public void moveFast()
    {
        int update_x = getX();
        int update_y = getY();

        MouseInfo mouse = Greenfoot.getMouseInfo();
       if (mouse != null){
           turnTowards(mouse.getX(),mouse.getY());
       }
      
        /* update */
        update_x = (int)(getX() + move_speed*cos(toRadians(getRotation())));
        update_y = (int)(getY() + move_speed*sin(toRadians(getRotation())));

        /* move */
        setLocation(update_x,update_y);
        
        /* observer notify */
        for (Enermy enermy: getWorld().getObjects(Enermy.class)){
            enermy.update(update_x,update_y);
        }
    }
    
  /* movement control using key 1 */  
   public void blackHoleAttack() 
   {
     
     MouseInfo mouse = Greenfoot.getMouseInfo();
     int update_x = (int)(getX() + 200*cos(toRadians(getRotation())));
     int update_y = (int)(getY() + 200*sin(toRadians(getRotation())));
     getWorld().addObject(new BlackHole(getRotation(), 80, 80, 1),update_x, update_y);
     
     
   }
   
   public void explodeAttack()
   {
     MouseInfo mouse = Greenfoot.getMouseInfo();
     int update_x = (int)(getX() + 200*cos(toRadians(getRotation())));
     int update_y = (int)(getY() + 200*sin(toRadians(getRotation())));
     getWorld().addObject(new BlackHole(getRotation(), 80, 80, 1),update_x, update_y);
     for (Enermy enermy: getWorld().getObjects(Enermy.class)){
        enermy.setLocation(update_x, update_y);
        enermy.damage(getX(),getY(),1, "bullet");
        
    }

   }
    

    public void timer(){   
       if (attack_timer != 0) attack_timer--;
       if (push_timer != 0) push_timer--;
       if (invincible_timer != 0) invincible_timer--;
    }
    
    public void blackHoleTimer(){   
       if (blackhole_duration_time != 0) blackhole_duration_time--;
       if (blackhole_cd_time != 0) blackhole_cd_time--;
       
    }
     
}