import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * Write a description of class TeslaTower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TeslaTower extends Player
{
    private int sizeX = 100;
    private int sizeY = 100;
    private int transVal = 255;
    private int damage;
    private int lifeTime = 100000;
    private int attackTime = 100;
    private SimpleTimer lifeTimer = new SimpleTimer();
    private SimpleTimer attackTimer = new SimpleTimer();
    public TeslaTower(int damage){
        this.damage = damage;
        lifeTimer.mark();
    }
    public void act() 
    {
        if (move_state != "freeze"){
            if(attackTimer.millisElapsed() > attackTime){
                Enermy enermy = getNearestEnermy(400);
                if(enermy != null && enermy.getWorld() != null){
                    int sx = getX(), sy = getY();
                    int ex = enermy.getX(), ey = enermy.getY();
                    int centerX = (sx+ex)/2;
                    int centerY = (sy+ey)/2;
                    int width = (int)Math.hypot(sx-ex, sy-ey);
                    getWorld().addObject(new ThunderChain(sx, sy, ex, ey, width, damage, 1, 2000), centerX, centerY);
                }
                attackTimer.mark();
            }
            List<TeslaCar> cars = getObjectsInRange(300, TeslaCar.class);
            if(cars.size() != 0){
                charge(cars.get(0));
            }
        }
        dead();
    }
    public void charge(TeslaCar car){
        if(!car.checkCharged())
        {
            car.setCharged(true);
            int ex = getX(), ey = getY();
            int sx = car.getX(), sy = car.getY();
            int centerX = (sx+ex)/2;
            int centerY = (sy+ey)/2;
            ThunderCharger thunderCharger = new ThunderCharger(this, car);
            getWorld().addObject(thunderCharger, centerX, centerY);
            thunderCharger.turnTowards(ex, ey);
        }
    }
    public void dead(){
        if(lifeTimer.millisElapsed() > lifeTime){
            transVal-=5;
        }
        if(transVal <= 0){
            getWorld().removeObject(this);
        }
        else{
            getImage().setTransparency(transVal);
        }
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
