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
    private GifImage originGif = new GifImage("thunder_chain.gif");
    private GifImage transGif = new GifImage("thunder_chain_black.gif");
    private int sizeX = 1000;
    private int sizeY = 50;
    private int chainCount = 5;
    private int shockedTime = 2000;
    private boolean fade = false;
    private int transVal = 255;
    private int currentX;
    private int currentY;
    private GreenfootSound chain_sound = new GreenfootSound("thunder_medium.wav");
    private GreenfootSound tower_sound = new GreenfootSound("thunder_short.wav");
    public ThunderChain(int currentX, int currentY, int turnX, int turnY, int sizeX, int damage){
        this.sizeX = sizeX;
        this.damage = damage;
        this.currentX = currentX;
        this.currentY = currentY;
        fade = true;
        gifAnimator();
        chain_sound.setVolume(70);
        tower_sound.setVolume(70);
    }
    
    public ThunderChain(int currentX, int currentY, int turnX, int turnY, int sizeX, int damage, int chainCount, int shockedTime){
        this(currentX, currentY, turnX, turnY, sizeX, damage);
        this.chainCount = chainCount;
        this.shockedTime = shockedTime;
    }
    
    public void act() 
    {
        if (!freeze_state){
            int searchRange;
            if(chainCount == 5 && sizeX > 500){
                searchRange = sizeX/2;
            }
            else{
                searchRange = 300;
            }
            boolean shock = true;
            if(chainCount == 1){
                shock = false;
                tower_sound.play();
            } else{
                chain_sound.play();
            }
            if(chainCount > 0){
                Enermy enermy = getNearestEnermy(searchRange, false, shock);
                if(enermy != null && enermy.getWorld() != null){
                    changeAnimation(currentX, currentY, enermy.getX(), enermy.getY());
                    // add shocked buff and effect
                    ShockedBuff buff = new ShockedBuff(this, shockedTime, 0);
                    if(enermy.hasBuff(BuffType.Shocked)){
                        enermy.updateBuff(BuffType.Shocked);
                    }else{
                        ShockedDecorator decorator = new ShockedDecorator(enermy);
                        getWorld().addObject(decorator, enermy.getX(), enermy.getY());
                        buff.setDecorator(decorator);
                        enermy.addBuff(buff);
                    }
                    
                    enermy.damage(getX(), getY(), damage, "bullet");
                    freeze_state = true;
                    Enermy nextEnermy = getNearestEnermy(300, true, true);
                    if(nextEnermy != null && --chainCount!= 0){
                        shockedTime -= 400;
                        int ex = nextEnermy.getX(), ey = nextEnermy.getY();
                        int sx = enermy.getX(), sy = enermy.getY();
                        int centerX = (sx+ex)/2;
                        int centerY = (sy+ey)/2;
                        int width = (int)Math.hypot(sx-ex, sy-ey);
                        if (width < 10) width = 10;
                        getWorld().addObject(new ThunderChain(sx, sy, ex, ey, width, damage, chainCount, shockedTime), centerX, centerY);
                    }
                }
            }
       }
        gifAnimator();
        dead();
    }
    public void gifAnimator(){
        sizeY = (sizeX-100)/20 + 20;
        if(sizeY > 50) sizeY = 50;
        GreenfootImage image;
        if(chainCount <= 1){
            image = transGif.getCurrentImage();
        }else{
            image = originGif.getCurrentImage();
        }
        if(sizeX < 10) sizeX = 10;
        image.scale(sizeX, sizeY);
        setImage(image);
    }
    public void changeAnimation(int startX, int startY, int endX, int endY){
        int centerX = (startX + endX)/2;
        int centerY = (startY + endY)/2;
        setLocation(centerX, centerY);
        turnTowards(endX, endY);
        
        sizeX = (int)Math.hypot(startX - endX, startY - endY);
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
    public double getDistance(Actor actor) {
        return Math.hypot(actor.getX() - getX(), actor.getY() - getY());
    }
    public Enermy getNearestEnermy(int distance, boolean circle, boolean shock) {
        List<Enermy> nearEnermys;
        if(circle){
            nearEnermys = getObjectsInRange(distance, Enermy.class);
        }else{
            nearEnermys = getNeighbours(distance, false, Enermy.class);
        }

        Enermy nearestEnermy = null;
        double nearestDistance = distance;
        for (int i = 0; i < nearEnermys.size(); i++) {
           if(shock && nearEnermys.get(i).hasBuff(BuffType.Shocked)){
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
