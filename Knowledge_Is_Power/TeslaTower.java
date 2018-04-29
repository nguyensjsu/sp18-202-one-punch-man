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
    private int sizeX = 72;
    private int sizeY = 116;
    private int transVal = 255;
    private int damage;
    private int attack_range = 300;
    private int lifeTime = 100000;
    private int attackTime = 500;
    private SimpleTimer lifeTimer = new SimpleTimer();
    private SimpleTimer attackTimer = new SimpleTimer();
    private GreenfootImage tower_image = new GreenfootImage("tesla_tower.png");
    private GreenfootImage broken_image = new GreenfootImage("tesla_tower_broken.png");
    public TeslaTower(int damage){
        this.damage = damage;
        this.hp = 60;
        tower_image.scale(sizeX, sizeY);
        setImage(tower_image);
        lifeTimer.mark();
    }
    public void act() 
    {
        if (!freeze_state){
            if(attackTimer.millisElapsed() > attackTime){
                Enermy enermy = getNearestEnermy(attack_range);
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
            List<TeslaCar> cars = getObjectsInRange(attack_range, TeslaCar.class);
            if(cars.size() != 0){
                charge(cars.get(0));
            }
            if ((invincible_timer == 0) && damage_state == "invincible") damage_state = "normal";
        }
        timer();
        dead();
    }
    public void charge(TeslaCar car){
        if(!car.checkCharged() && !car.checkFullHP())
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
        if(lifeTimer.millisElapsed() > lifeTime || hp <= 0){
            broken_image.scale(sizeX, sizeY);
            setImage(broken_image);
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
