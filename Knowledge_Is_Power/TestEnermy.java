import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class TestEnermy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TestEnermy extends Enermy
{
    public TestEnermy(){
        this(50,50,"stop","stop");    //default size 50*50
    }
    
    public TestEnermy(int sizeX, int sizeY, String move, String attack){
        super(sizeX,sizeY,move,attack);
        attack_speed = 26;
        MAX_HP = 200;
        hp = 200;
        
        GreenfootImage image = new GreenfootImage("man01.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void bullet_attack(){
        if (attack_timer == 0){
            setRotation(90);
            getWorld().addObject(new EnermyTestBullet(getRotation()),getX(),getY());
            attack_timer = attack_speed;
        }
    }
}
