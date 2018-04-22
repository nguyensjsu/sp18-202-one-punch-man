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
    protected int ult_cd_timer = 0;
    
    /* constructor */
    public Darwin(){ 
        this(100,100);  
    }
    
    public Darwin(int x, int y){
        size_x = x;
        size_y = y;
        
        ult_cd_timer = 0;
        GreenfootImage image = getImage();
        image.scale(size_x, size_y);
        setImage(humanState);
        
    }
    
    /* method */
    public void act(){
       transform();
            
       /* update move */
       if (!freeze_state){
           switch (move_state){
               case "wasd": wasd_move(); break;
               case "push": push(push_x, push_y, push_speed); break;
               default: break;
            }
              
           /* ability */
           base_attack();
           ult_animation();
           ult();
           
           /* invincible flash */
           invincible_flash(player_image,trans_image);
           
           /* timer */
           timer();
       }
       animation_timer();
       
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
            else if (getState()=="human"){
                getWorld().addObject(new DarwinBullet(getRotation(), 5),getX(),getY());
            }
            else{
                getWorld().addObject(new DarwinBullet(getRotation(), 5),getX(),getY());
            }
                
            attack_timer = attack_speed;
        }
    }
    
    public void timer(){   
       if (attack_timer != 0) attack_timer--;
       if (push_timer != 0) push_timer--;
       if (invincible_timer != 0) invincible_timer--;
       
       if (ult_cd_timer != 0){ult_cd_timer--;}
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
       else if (Greenfoot.isKeyDown("UP")) {
            setImage(apemanState);
            state="human";
            player_image = "darwin.png";
        }
        

    }
   
    public void ult_animation(){
        if (ult_cd_timer == 0){
            if(Greenfoot.isKeyDown("3")){
                /* ult cutscence */
                ult_cutscence("bluej-icon.png","bluej-icon.png");   //player, sentence
                ult_cd_timer = 10000;
                ((BaseWorld)getWorld()).freeze_all(true);
            }
        }
    }
    
    public void ult(){
        if (ult_trigger){
            ult_trigger = false;

            String inputValue = JOptionPane.showInputDialog("YOU HAVE THE CONTROL:");
            
            if (inputValue.equals("sudo rm -rf /")) {
                getWorld().removeObjects(getWorld().getObjects(TestEnermy.class));
                getWorld().removeObjects(getWorld().getObjects(Bullet.class));           
            }
            else{}
            
            
            ult_cd_timer = 1800;
            
            getWorld().addObject(new UICDDecorator(this,100,100,1500,775,1800),0,0);
        }   
    }
    
    public void animation_timer(){
        if (ult_cutscence_timer != 0) ult_cutscence_timer--;
        if (ult_cutscence_timer == 1){ ult_trigger = true;((BaseWorld)getWorld()).freeze_all(false);};
    }
    
    public String getState(){  
        return state;
    }
    
}


