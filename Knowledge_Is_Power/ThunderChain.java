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
    private int sizeY = 100;
    private int chainCount = 5;
    private int shockedTime = 2000;
    private boolean fade = false;
    private int transVal = 255;
    private int currentX;
    private int currentY;
    
    public ThunderChain(int currentX, int currentY, int turnX, int turnY, int sizeX, int damage){
        turnTowards(turnX, turnY);
        this.sizeX = sizeX;
        this.damage = damage;
        this.currentX = currentX;
        this.currentY = currentY;
        GreenfootImage image = getImage();
        image.scale(sizeX, sizeY);
        setImage(image);
        fade = true;
    }
    
    public ThunderChain(int currentX, int currentY, int turnX, int turnY, int sizeX, int damage, int chainCount, int shockedTime){
        turnTowards(turnX, turnY);
        this.sizeX = sizeX;
        this.damage = damage;
        this.currentX = currentX;
        this.currentY = currentY;
        this.chainCount = chainCount;
        this.shockedTime = shockedTime;
        
        GreenfootImage image = getImage();
        image.scale(sizeX, sizeY);
        setImage(image);
        fade = true;
    }
    
    public void act() 
    {
        if (move_state != "freeze"){
            int searchRange;
            if(chainCount == 5 && sizeX > 500){
                searchRange = sizeX/2;
            }
            else{
                searchRange = 300;
            }
            Enermy enermy = getNearestEnermy(searchRange, false);
            if(enermy != null && enermy.getWorld() != null){
                showAnimation(currentX, currentY, enermy.getX(), enermy.getY());
                enermy.setNegativeState("shocked", 1000, damage);
                move_state = "freeze";
                Enermy nextEnermy = getNearestEnermy(300, true);
                if(nextEnermy != null && --chainCount!= 0){
                    shockedTime -= 400;
                    int centerX = (enermy.getX() + nextEnermy.getX())/2;
                    int centerY = (enermy.getY() + nextEnermy.getY())/2;
                    int width = (int)Math.hypot(enermy.getX() - nextEnermy.getX(), enermy.getY() - nextEnermy.getY());
                    if (width<30) width = 30;
                    getWorld().addObject(new ThunderChain(enermy.getX(), enermy.getY(), nextEnermy.getX(), nextEnermy.getY(), width, damage, chainCount, shockedTime), enermy.getX(), enermy.getY());
                }
            }else{
                GreenfootImage image = getImage();
                image.scale(sizeX, sizeY);
                setImage(image);
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
    public void showAnimation(int startX, int startY, int endX, int endY){
        int centerX = (startX + endX)/2;
        int centerY = (startY + endY)/2;
        setLocation(centerX, centerY);
        turnTowards(endX, endY);
        
        sizeX = (int)Math.hypot(startX - endX, startY - endY);
        sizeY = 50;
        GreenfootImage image = getImage();
        image.scale(sizeX, sizeY);
        setImage(image);
    }
    public double getDistance(Actor actor) {
        return Math.hypot(actor.getX() - getX(), actor.getY() - getY());
    }
    public Enermy getNearestEnermy(int distance, boolean circle) {
        List<Enermy> nearEnermys;
        if(circle){
            nearEnermys = getObjectsInRange(distance, Enermy.class);
        }else{
            nearEnermys = getNeighbours(distance, false, Enermy.class);
        }

        Enermy nearestEnermy = null;
        double nearestDistance = distance;
        for (int i = 0; i < nearEnermys.size(); i++) {
           if(nearEnermys.get(i).isShocked()){
               continue;
           }
           double tmpDistance = getDistance(nearEnermys.get(i));
           if (tmpDistance < nearestDistance) {
                nearestEnermy = nearEnermys.get(i);
                nearestDistance = tmpDistance;
            }
        }
        return nearestEnermy;
    }
}
