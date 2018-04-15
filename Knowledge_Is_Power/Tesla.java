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
    private int sector_timer = 0;
    private AttackStrategy currentAttack;
    private SectorAttack sectorAttack;
    private ChainAttack chainAttack;
    private ExplodeAttack explodeAttack;
    public Tesla(){
        this(60, 60);
    }
    public Tesla(int x, int y){
        size_x = x;
        size_y = y;
        super.originGif = new GifImage("Tesla_head.gif");
        /* Avator */
        GreenfootImage image = originGif.getCurrentImage();
        image.scale(x, y);
        setImage(super.originGif.getCurrentImage());
        /* Attack */
        sectorAttack = new SectorAttack();
        chainAttack = new ChainAttack(this);
        explodeAttack = new ExplodeAttack();
        currentAttack = chainAttack;
    }
    
    public void act() 
    {
       if (move_state != "freeze"){
           switch (move_state){
               case "wasd": wasd_move(); break;
               case "push": push(push_x, push_y, push_speed); break;
               default: break;
            }
           /* invincible flash */
           invincible_flash(player_image,trans_image);
           
           /* timer */
           timer();
           
           currentAttack.attack();
       }
       
       /* game over condition */
       dead();
    }
    
    public void changeAttack(AttackStrategy s){
        currentAttack.exit();
        currentAttack = s;
    }
    
    public class ChainAttack implements AttackStrategy{
        private int cooldown = 0;
        private int timer = 30;
        private int chainDamage = 5;
        private Actor current;
        public ChainAttack(Actor actor){
            current = actor;
        }
        
        public void attack(){
            if(timer == 0){
                MouseInfo mouse = Greenfoot.getMouseInfo();
                if (mouse != null && Greenfoot.mousePressed(null)){
                    int centerX = (mouse.getX() + getX())/2;
                    int centerY = (mouse.getY() + getY())/2;
                    int width = (int)Math.hypot(mouse.getX() - getX(), mouse.getY() - getY());
                    getWorld().addObject(new ThunderChain(current, mouse.getX(), mouse.getY(), width, chainDamage), centerX, centerY);
                    timer = cooldown;
                }
            }
            else{
                timer--;
            }
        }
        public void exit(){

        }
        public int getCoolDown(){
            return timer;
        }
    }
    
    public class SectorAttack implements AttackStrategy{
        private String sector_state = "idle";
        private ThunderSector mySector;
        private int sectorDamage = 1;
        
        public void attack(){
            MouseInfo mouse = Greenfoot.getMouseInfo();
            switch (sector_state){
                case "idle" : 
                    if (mouse != null && Greenfoot.mousePressed(null)){
                        mySector = new ThunderSector(getRotation(),getX(),getY(),sectorDamage);
                        getWorld().addObject(mySector,getX(),getY());
                        sector_state = "active";
                    }
                    break;
                case "active" : 
                    mySector.updateLocation(getX(),getY(),getRotation());
                    if(Greenfoot.mouseClicked(null) ){
                        getWorld().removeObject(mySector);
                        sector_state = "idle";
                    }
                    break;
            }
        }
        public void exit(){
            getWorld().removeObject(mySector);
            sector_state = "idle";
        }
        public int getCoolDown(){
            return 0;
        }
    }
 
    public class ExplodeAttack implements AttackStrategy{
        private int timer = 0;
        private int cooldown = 500;
        private int explodeDamage = 9999;
        
        public void attack(){
            if(timer == 0){
                MouseInfo mouse = Greenfoot.getMouseInfo();
                if (mouse != null && Greenfoot.mousePressed(null)){
                    getWorld().addObject(new ThunderExplode(getRotation(), explodeDamage),getX(),getY());
                    timer = cooldown;
                }
            }else{
                timer--;
            }
        }
        public void exit(){
            
        }
        public int getCoolDown(){
            return timer;
        }
    }
}
