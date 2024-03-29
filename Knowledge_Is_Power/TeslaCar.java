import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import static java.lang.Math.*;
/**
 * Write a description of class TeslaCar here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TeslaCar extends Player
{
    private int sizeX = 90;
    private int sizeY = 50;
    private int transVal = 255;
    private boolean fade = false;
    private int damage;
    private int acceleration = 2;
    private int velocity = 1;
    private int friction_factor = 1;
    private int maxVelocity = 12;
    private int prev_rotation = 0;
    private int drift_state = 0;
    private Actor currentChase;
    private SimpleTimer moveTimer = new SimpleTimer();
    private SimpleTimer driftEndTimer = new SimpleTimer();
    private boolean isCharged;
    private final int LOCAL_INVINCIBLE_TIME = 10;
    private GreenfootSound running_sound = new GreenfootSound("tesla_car_running.mp3");
    private int running_volume = 0;
    private int attack_volume = 90;
    private GreenfootSound brake_sound = new GreenfootSound("tesla_car_brake.wav");
    private int brake_volume = 90;
    private List<NotBullet> intersect_list = new ArrayList<>();
    public TeslaCar(int damage){
        this.damage = damage;
        GreenfootImage image = new GreenfootImage("tesla_car.png");
        image.scale(sizeX, sizeY);
        setImage(image);
    }
    public void act() 
    {
        if (!freeze_state){
            wasd_move();
            attack();
            if ((invincible_timer == 0) && damage_state.equals("invincible")) damage_state = "normal";
            /* timer */
            timer();
        }
        dead();
    }
    public void wasd_move(){
        if(abs(velocity)>0){
            int turn_degree = 1;
            if(Math.abs(velocity)>9){
                turn_degree = 2;
            }
            if(drift_state == 1){
                turn_degree += 2;
            }
            if(Greenfoot.isKeyDown("a")){
                turn(-turn_degree);
            }
            if(Greenfoot.isKeyDown("d")){
                turn(turn_degree);
            }
        }
        
        if(moveTimer.millisElapsed() > 100){
            if(Greenfoot.isKeyDown("w") && drift_state==0){
                accelerate(true);
            }
            if(Greenfoot.isKeyDown("s") && drift_state==0){
                accelerate(false);
            }
            if(velocity > 0){
                velocity -= friction_factor;
            }
            else if(velocity < 0){
                velocity += friction_factor;
            }
            moveTimer.mark();
        }
        
        switch(drift_state){
            case 0:
                move(velocity);
                if(Greenfoot.isKeyDown("space")){
                    prev_rotation = getRotation();
                    drift_state = 1;
                    brake_sound.setVolume(brake_volume);
                    brake_sound.play();
                }
                break;
            case 1:
                int update_x = getX()+(int)(velocity*cos(toRadians(prev_rotation)));
                int update_y = getY()+(int)(velocity*sin(toRadians(prev_rotation)));
                setLocation(update_x, update_y);
                if(!Greenfoot.isKeyDown("space") && abs(velocity) <= 1){
                    drift_state = 0;
                }
                break;
        }
        running_volume = (int)((double)abs(velocity)/(double)maxVelocity * 30.0 + 65);
        if(abs(velocity) < 1){
            running_sound.stop();
        }
        running_sound.setVolume(running_volume);
        running_sound.play();
        /* observer notify */
        for (Enermy enermy: getWorld().getObjects(Enermy.class)){
            enermy.update(getX(),getY());
        }
    }
    public void accelerate(boolean speedup){
        if(velocity < maxVelocity && speedup){
            velocity += acceleration;
        }else if(velocity > -maxVelocity && !speedup){
            velocity -= acceleration;
        }
    }
    public void attack(){
        for (NotBullet intersect_obj : this.getIntersectingObjects(NotBullet.class)){
            /* melee attack player */
            if(intersect_obj instanceof Enermy){
                if(abs(velocity)>3){
                    intersect_obj.damage(getX(),getY(),damage,"push");
                    if(!intersect_list.contains(intersect_obj)){
                        intersect_list.add(intersect_obj);
                        GreenfootSound new_attack_sound = new GreenfootSound("tesla_car_attack.wav");
                        new_attack_sound.setVolume(attack_volume);
                        new_attack_sound.play();
                    }
                }
            }
        }
        for (NotBullet intersect_obj : new ArrayList<NotBullet>(intersect_list)){
            if(!intersects((Actor)intersect_obj))
                intersect_list.remove(intersect_obj);
        }
    }
    public void damage(int source_x,int source_y, int damage_num, String type){
        if (damage_state != "invincible"){
            switch (type){
                case "push":
                    /* bounce away */
                    move_state = "push";
                    int dx = source_x - getX();
                    int dy = source_y - getY();
                    push_x = getX() - (int)(100*dx/sqrt(dx*dx+dy*dy));
                    push_y = getY() - (int)(100*dy/sqrt(dx*dx+dy*dy));
                    push_speed = 10;
                    push_timer = 20;
                    /* take damage */
                    hp -= 1;
                    /* invincible time */
                    invincible_timer = LOCAL_INVINCIBLE_TIME;
                    damage_state = "invincible";
                    break;
                    
                case "bullet":
                    /* take damage */
                    hp -= 1;
                    /* invincible time */
                    invincible_timer = LOCAL_INVINCIBLE_TIME;
                    damage_state = "invincible";
                    break;
                    
                default: break;
            }
        }
    }
    public void dead(){
        if(hp <= 0){
            freeze_state = true;
            fade = true;
        }
        if(fade){
            if(running_volume > 0){
                running_volume -= 5;
            }else{
                running_volume = 0;
            }
            running_sound.setVolume(running_volume);
            transVal-=10;
        }
        if(transVal <= 0){
            running_sound.stop();
            getWorld().removeObject(this);
        }
        else{
            getImage().setTransparency(transVal);
        }
    }
    public void exit(){
        fade = true;
        freeze_state = true;
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
    public void charge(int hp){
        if(this.hp < MAX_HP ){
            this.hp += hp;
        }
        if(this.hp > MAX_HP){
            this.hp = MAX_HP;
        }
    }
    public boolean checkCharged(){
        return isCharged;
    }
    public void setCharged(boolean flag){
        isCharged = flag;
    }
    public boolean checkFullHP(){
        return hp == MAX_HP;
    }
}
