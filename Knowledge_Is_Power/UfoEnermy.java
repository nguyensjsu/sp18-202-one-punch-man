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
    
    public UfoEnermy(int sizeX, int sizeY, String move, String attack){
        super(sizeX,sizeY,move,attack);    //default size 50*50
        attack_speed = 30;
        MAX_HP = 500;
        hp = 200;

        GreenfootImage image = new GreenfootImage("ufo2.png");
        image.scale(sizeX, sizeY);
        setImage(image);
        
    }
   
     public void act(){   
        if (!freeze_state){
            /* select move strategy */
            
            switch (move_state){
                case "wander": wander(); break;
                case "chase": chase(); break;
                case "push": push(push_x, push_y, push_speed); break;
                default: break;
            }
            
            /* select attack strategy */
            switch (attack_state){
                case "bullet": bullet_attack(); break;
                default: break;
            }

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
    
    // public void move_around() {
    
        // move(4);
        // if(Greenfoot.getRandomNumber(100) < 10) {
            // turn(Greenfoot.getRandomNumber(90) - 45); 
        // }
        // if(getX() <=5 || getX() >= getWorld().getWidth() -5) {
            // turn(180);
        // }
        // if(getY() <= 5 || getY() >= getWorld().getHeight() -5) {
            // turn(180);
        // }
    
    // }
   
    public void bullet_attack(){
        if (attack_timer == 0){
            setRotation(90);
            getWorld().addObject(new UfoBullet(getRotation(), 50, 50, 20),getX(),getY());
            attack_timer = attack_speed;
        }
    }  
    
    
}
