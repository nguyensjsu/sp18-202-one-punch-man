import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class FactoryMethodPatternDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FactoryMethodPatternDecorator extends Decorator
{
    protected int rotation = 0;
    protected int duration;
    protected int radius;
    protected DrP player;
    protected String factory_state = "melee";   //melee or range
    
    public FactoryMethodPatternDecorator(DrP Player, int Duration, int Radius){
        super(Radius,Radius,0,0);
        player = Player;
        duration = Duration;
        radius = Radius;
    }
    
    /* override */
    public void draw(){
        GreenfootImage image = new GreenfootImage("blue circle.png");
        image.scale(radius+100,radius+100);
        setImage(image);
    }
    
    public void move(){
        setLocation(player.getX(),player.getY());
    }
    
    public void update(){
        GreenfootImage image;
        
        rotation++;
        setRotation(rotation);
        
        /* choosing state */
        if (getObjectsInRange(radius+200, Enermy.class).size() == 0){factory_state = "range";}
        else {factory_state = "melee";}
        
        if (factory_state == "range"){
            draw();
            for(DrPSuperAttack bullet: getObjectsInRange(radius-60-15, DrPSuperAttack.class)){
                /* chase blue bullet */
                bullet.chase_state = true;
                image = new GreenfootImage("DrPPaperBlue.png");
                image.scale(bullet.size_x, bullet.size_y);
                bullet.setImage(image);
            }
        }
        else{
            image = new GreenfootImage("yellow circle.jpg");
            image.scale(radius+100,radius+100);
            setImage(image);
            for(DrPSuperAttack bullet: getObjectsInRange(radius-60-15, DrPSuperAttack.class)){
                /* none chase yellow bullet */
                bullet.chase_state = false;
                image = new GreenfootImage("DrPPaperYellow.png");
                image.scale(bullet.size_x, bullet.size_y);
                bullet.setImage(image);
                
                /* AOE 16 times*/
                DrPSuperAttack new_bullet;
                
                for(int i=0;i<16;i++){
                    int angle = 360/16;
                    int x = (int)(sin(toRadians(i*angle))*(radius-20-15));
                    int y = (int)(cos(toRadians(i*angle))*(radius-20-15));
                    
                    new_bullet = new DrPSuperAttack(i*angle+90,bullet.size_x,bullet.size_y,bullet.damage,bullet.chase_state);
                    image = new GreenfootImage("DrPPaperYellow.png");
                    image.scale(bullet.size_x, bullet.size_y);
                    new_bullet.setImage(image);
                    new_bullet.life_timer = 30;
                    new_bullet.damage_type = "push";
                    
                    getWorld().addObject(new_bullet,getX()-x,getY()+y);
                }
                getWorld().removeObject(bullet);
            }
        }
    }
    
    public void timer(){
        if (duration != 0){
            duration--;
        }
        else go_die = true;
    }
}
