import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class ThunderExplode extends Bullet
{
    private GifImage originGif = new GifImage("thunder_explode.gif");
    private int sizeX = 200;
    private int sizeY = 121;
    private int prevRotation;
    private int transVal = 255;
    private boolean fade = false;
    private GreenfootSound explode_sound = new GreenfootSound("thunder_explode.wav");
    public ThunderExplode(int r, int d){
        fire_rotation = r;
        damage = d;
        move_speed = 1;
        gifAnimator();
        explode_sound.setVolume(70);
    }
    public void act() 
    {
        if (!freeze_state){
            chase();
            // if touch
            List<Enermy> touchingEnermys = getObjectsInRange(150, Enermy.class);
            if(touchingEnermys.size() != 0){
                List<Enermy> enermys = getObjectsInRange(200, Enermy.class);
                for (int i=0;i<enermys.size();i++){
                    // explode
                    enermys.get(i).damage(getX(),getY(),damage,"bullet");
                    explode_sound.play();
                    if(i==enermys.size()-1){
                        fade = true;
                        freeze_state = true;
                    }
                }
            }
        }
        gifAnimator();
        dead();
    }
    public void dead(){
        if(fade){
            transVal-=5;
        }
        if(transVal <=0 || this.isAtEdge()){
            getWorld().removeObject(this);
        }
        else{
            getImage().setTransparency(transVal);
        }
    }
    public void chase(){
        Enermy chaseEnermy = getNearestEnermy(300);
        if(chaseEnermy != null){
            turnTowards(chaseEnermy.getX(), chaseEnermy.getY());
            fire_rotation = getRotation();
        }else{
            turn(fire_rotation);
        }
        move((int)(move_speed));
        setRotation(0);
    }
    public void gifAnimator(){
        GreenfootImage image = originGif.getCurrentImage();
        image.scale(sizeX, sizeY);
        setImage(originGif.getCurrentImage());
    }
    public double getDistance(Actor actor) {
        return Math.hypot(actor.getX() - getX(), actor.getY() - getY());
    }
    public Enermy getNearestEnermy(int range) {
        List<Enermy> nearEnermys = getObjectsInRange(range, Enermy.class);
        Enermy nearestEnermy = null;
        double nearestDistance = range;
        for (int i = 0; i < nearEnermys.size(); i++) {
            double distance = getDistance(nearEnermys.get(i));
            if (distance < nearestDistance) {
                nearestEnermy = nearEnermys.get(i);
                nearestDistance = distance;
            }
        }
        return nearestEnermy;
    }
}
