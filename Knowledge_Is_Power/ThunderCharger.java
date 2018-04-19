import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ThunderCharger here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ThunderCharger extends Bullet
{
     private GifImage originGif = new GifImage("thunder_charge.gif");
    private int sizeX = 1000;
    private int sizeY = 50;
    private int chargeSpeed = 1;
    private TeslaCar teslaCar;
    private TeslaTower teslaTower;
    private SimpleTimer charge_timer = new SimpleTimer();
    private int transVal = 255;
    private boolean fade = false;
    
    public ThunderCharger(TeslaTower source, TeslaCar target){
        teslaTower= source;
        teslaCar = target;
        changeAnimation();
    }
    public void act(){
        if(!fade){
            if(checkValid()){
                if(charge_timer.millisElapsed() > 100){
                    teslaCar.charge(chargeSpeed);
                    charge_timer.mark();
                }
                changeAnimation();
            }else{
                teslaCar.setCharged(false);
                fade = true;
            }
        }else{
            dead();
        }
    }
    public boolean checkValid(){
        return teslaCar!=null&&teslaCar.getWorld()!=null&&teslaTower!=null&&teslaTower.getWorld()!=null&&getDistance(teslaTower,teslaCar)<300&&!teslaCar.checkFullHP();
    }
    public double getDistance(Actor actor1, Actor actor2) {
        return Math.hypot(actor1.getX() - actor2.getX(), actor1.getY() - actor2.getY());
    }
    public void changeAnimation(){
        int sx = teslaTower.getX(), sy = teslaTower.getY();
        int ex = teslaCar.getX(), ey = teslaCar.getY();
        int centerX = (sx+ex)/2;
        int centerY = (sy+ey)/2;
        int sizeX = (int)Math.hypot(sx-ex, sy-ey);
        if(sizeX < 10)sizeX = 10;
        sizeY = (sizeX-100)/20 + 10;
        if(sizeY > 20) sizeY = 20;
        setLocation(centerX, centerY);
        turnTowards(ex, ey);
        GreenfootImage image = originGif.getCurrentImage();
        image.scale(sizeX, sizeY);
        setImage(image);
    }
    
    public void dead(){
        if(fade){
            transVal-=10;
        }
        if(transVal <=0){
            getWorld().removeObject(this);
        }
        else{
            getImage().setTransparency(transVal);
        }
    }
}
