import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class UfoEnermy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UfoEnermy extends Enermy
{
    /**
     * Act - do whatever the UfoEnermy wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    public UfoEnermy(){
        super(80,80,"stop","stop");    //default size 50*50
        attack_speed = 30;
        MAX_HP = 500;
        hp = 50;

        GreenfootImage image = new GreenfootImage("ufo2.png");
        image.scale(size_x, size_y);
        setImage(image);
        
    }
   
     public void act(){   
        if (!freeze_state){
            /* select move strategy */
           
            move_around();
            attack();
            /* collision avoidance */
            collision_avoidance();
            
            /* timer */
            timer();
            
        }

        if(!fade){
            // refresh buff state
            buffRefresh();
        }
        /* remove condition */
        dead();
    }
    
    public void move_around() {
    
        move(4);
        if(Greenfoot.getRandomNumber(100) < 10) {
            turn(Greenfoot.getRandomNumber(90) - 45); 
        }
        if(getX() <=5 || getX() >= getWorld().getWidth() -5) {
            turn(180);
        }
        if(getY() <= 5 || getY() >= getWorld().getHeight() -5) {
            turn(180);
        }
    
    }
   
    public void attack(){
        if (attack_timer == 0){
            setRotation(90);
            getWorld().addObject(new UfoBullet(getRotation(), 50, 50, 20),getX(),getY());
            attack_timer = attack_speed;
        }
    }  
    
    
}
