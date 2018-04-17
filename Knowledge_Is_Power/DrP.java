import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DrP here.
 * 
 * @author Karas 
 * @version v0.1.5
 */
public class DrP extends Player
{
    protected String player_image = "man.png";
    protected int ult_animation_timer = 0;
    
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
           skill_one();
           ult();
           
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
    
    public void bullet_attack(){
        if (attack_timer == 0){
            getWorld().addObject(new DrPBaseAttack(getRotation(), 20, 20, bullet_damage),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    public void skill_one(){}
    
    public void skill_two(){}
    
    public void ult(){
        if(Greenfoot.isKeyDown("3")){
            BaseWorld.getInstance().freeze_all(true);
            ult_animation_timer = 180;
        }
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
