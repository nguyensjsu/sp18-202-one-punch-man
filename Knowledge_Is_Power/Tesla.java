import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Tesla here.
 * Tesla Special Operation
 * @author Zhiye Chen 
 * @version v0.1
 */
public class Tesla extends Player
{
    private GifImage gif = new GifImage("Tesla_head.gif");

    public Tesla(int x, int y){
        for (GreenfootImage image : gif.getImages())
        {
            image.scale(x, y);
        }
        setImage(gif.getCurrentImage());
    }
    
    public void act() 
    {
        
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
           setImage(gif.getCurrentImage());
        }
       
       if (invincible_timer == 0) damage_state = "normal";
       
       /* timer */
       if (attack_timer != 0) attack_timer--;
       if (pull_timer != 0) pull_timer--;
       if (invincible_timer != 0) invincible_timer--;
    }   
}
