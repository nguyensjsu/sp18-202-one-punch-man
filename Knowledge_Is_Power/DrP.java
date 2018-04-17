import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class DrP here.
 * 
 * @author Karas 
 * @version v0.1.5
 */
public class DrP extends Player
{
    protected String player_image = "man.png";
    
    /* Dr.P state*/
    protected boolean decorator_pattern_state = false;
    protected boolean factory_method_pattern_state = false;
    
    /* timer */
    protected int ult_animation_timer = 0;
    protected int skill_one_duration_timer = 0;
    protected int skill_one_cd_timer = 0;
    protected int skill_two_duration_timer = 0;
    protected int skill_two_cd_timer = 0;
    
    public DrP(){
        this(50,50);    //default size 50*50
    }
    
    public DrP(int x, int y){
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
           invincible_flash(player_image,trans_image);
           
           /* timer */
           timer();
           additional_timer();
       }
       

       /* animation timer */
       animation_timer();
       
       /* game over condition */
       dead();
    }    
    
    public void bullet_attack(){
        if (attack_timer == 0){
            getWorld().addObject(new DrPBaseAttack(getRotation(), 20, 20, bullet_damage),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    public void skill_one(){
        if (skill_one_cd_timer == 0){
            if(Greenfoot.isKeyDown("1")){
                decorator_pattern_state = true;
                getWorld().addObject(new DecoratorPatternDecorator(this,180,150),getX(),getY());
                
                skill_one_duration_timer = 180;
                skill_one_cd_timer = 360;
            }
        }
    }
    
    public void skill_two(){
        if (skill_two_cd_timer == 0){
            if(Greenfoot.isKeyDown("2")){
                factory_method_pattern_state = true;
                getWorld().addObject(new FactoryMethodPatternDecorator(this,180,300),getX(),getY());
                
                skill_two_duration_timer = 300;
                skill_two_cd_timer = 600;
            }
        }
    }
    
    public void ult(){
        if(Greenfoot.isKeyDown("3")){
            BaseWorld.getInstance().freeze_all(true);
            ult_animation_timer = 180;
            for (Enermy enermy: getWorld().getObjects(Enermy.class)){
                getWorld().addObject(new DrPPaperDecorator(enermy.getX(),enermy.getY(),10,0),getX(),getY());
            }
        }
    }
    
    public void additional_timer(){
        if (skill_one_duration_timer == 1){decorator_pattern_state = false;}
        if (skill_one_duration_timer != 0){skill_one_duration_timer--;}
        
        if (skill_two_duration_timer == 1){factory_method_pattern_state = false;}
        if (skill_two_duration_timer != 0){skill_one_duration_timer--;}
        
        if (skill_one_cd_timer != 0){skill_one_cd_timer--;}
        if (skill_two_cd_timer != 0){skill_two_cd_timer--;}
    }
    
    public void animation_timer(){
        /* ult effect */
        if (ult_animation_timer == 1){
            BaseWorld.getInstance().freeze_all(false);
            for (Enermy enermy: getWorld().getObjects(Enermy.class)){
                enermy.damage(getY(), getX(), 200, "bullet");
            }
        }
        /* timer */
        if (ult_animation_timer != 0){ult_animation_timer--;}
        
    }
}
