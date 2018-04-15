import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * Write a description of class ThunderChain here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ThunderChain extends Bullet
{
    private int sizeX = 1000;
    private int sizeY = 50;
    private int chainCount = 5;
    private boolean fade = false;
    private int transVal = 255;
    public ThunderChain(int r, int d){
        fire_rotation = r;
        damage = d;
    }
    public ThunderChain(int r, int d, int count){
        fire_rotation = r;
        damage = d;
        chainCount = count;
    }
    
    public void act() 
    {
        if (move_state != "freeze"){
            GreenfootImage image = getImage();
            image.scale(sizeX, sizeY);
            setImage(image);
            // if touch
            if(isTouching(Enermy.class)){
                Enermy enermy = getNearestEnermy();
                if(enermy != null){
                    showAnimation(enermy);
                    fade = true;
                    move_state = "freeze";
                    if(--chainCount!=0){
                        getWorld().addObject(new ThunderChain(enermy.getRotation(), damage, chainCount),enermy.getX(),enermy.getY());
                    }
                }
            }
        }
        
        dead();
    }  
    public void dead(){
        if(fade){
            transVal-=5;
        }
        if(transVal <=0){
            getWorld().removeObject(this);
        }
        else{
            getImage().setTransparency(transVal);
        }
    }
    public void showAnimation(Actor actor){
        turnTowards(actor.getX(), actor.getY());
        sizeX = (int)getDistance(actor);
        GreenfootImage image = getImage();
        image.scale(sizeX, sizeY);
        setImage(image);
    }
    public double getDistance(Actor actor) {
        return Math.hypot(actor.getX() - getX(), actor.getY() - getY());
    }
    public Enermy getNearestEnermy() {
        List<Enermy> nearEnermys = getObjectsInRange(300, Enermy.class);
        Enermy nearestEnermy = null;
        double nearestDistance = 300;
        double distance;
        for (int i = 0; i < nearEnermys.size(); i++) {
            distance = getDistance(nearEnermys.get(i));
            if (distance < nearestDistance) {
                nearestEnermy = nearEnermys.get(i);
                nearestDistance = distance;
            }
        }
        return nearestEnermy;
    }
}
