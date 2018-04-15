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
    private TowerAttack towerAttack;
    private CarAttack carAttack;
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
        chainAttack = new ChainAttack();
        explodeAttack = new ExplodeAttack();
        towerAttack = new TowerAttack();
        carAttack = new CarAttack();
        currentAttack = chainAttack;
    }
    
    public void act() 
    {
       if (move_state != "freeze"){
           waitChanges();
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
           towerAttack.attack();
           carAttack.attack();
       }
       /* game over condition */
       dead();
    }
    
    public void waitChanges(){
        if(Greenfoot.isKeyDown("1")){
            changeAttack(chainAttack);
        }
        else if(Greenfoot.isKeyDown("2")){
            changeAttack(sectorAttack);
        }
        else if(Greenfoot.isKeyDown("3")){
            changeAttack(explodeAttack);
        }
    }
    
    public void changeAttack(AttackStrategy s){
        currentAttack.exit();
        currentAttack = s;
    }
    
    public class ChainAttack implements AttackStrategy{
        private int cooldown = 0;
        private int timer = 30;
        private int chainDamage = 5;
        
        public void attack(){
            if(timer == 0){
                MouseInfo mouse = Greenfoot.getMouseInfo();
                if (mouse != null && Greenfoot.mousePressed(null)){
                    int ex = mouse.getX(), ey = mouse.getY();
                    int sx = getX(), sy = getY();
                    int width = (int)Math.hypot(sx-ex, sy-ey);
                    int centerX = (sx+ex)/2;
                    int centerY = (sy+ey)/2;
                    if(width > 400){
                        Double rate = 400.0/width;
                        centerX = (int)((ex-sx)/2*rate) + sx;
                        centerY = (int)((ey-sy)/2*rate) + sy;
                        width = 400;
                    }
                    ThunderChain thunderChain = new ThunderChain(sx, sy, ex, ey, width, chainDamage);
                    thunderChain.setRotation(getRotation());
                    getWorld().addObject(thunderChain, centerX, centerY);
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
        private int cooldown = 300;
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
    
    public class TowerAttack implements AttackStrategy{
        private int timer = 0;
        private int cooldown = 300;
        private int towerDamage = 5;
        
        public void attack(){
            if(timer == 0){
                if(Greenfoot.isKeyDown("4")){
                    getWorld().addObject(new TeslaTower(towerDamage), getX(), getY());
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
    public class CarAttack implements AttackStrategy{
        private int timer = 0;
        private int cooldown = 300;
        private int carDamage = 10;
        
        public void attack(){
            if(timer == 0){
                if(Greenfoot.isKeyDown("5")){
                    getWorld().addObject(new TeslaCar(carDamage), getX(), getY());
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
