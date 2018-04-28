import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class Yin here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Yang extends Enermy
{
    protected BossEnermy boss;
    protected double exist_timer = 0;
    
    public Yang(BossEnermy Boss){
        super(50,50,"none","none");
        attack_speed = 26*4;
        MAX_HP = 10000;
        hp = 10000;
        setRotation(180);
        move_speed = 1;
        boss = Boss;
        
        GreenfootImage image = new GreenfootImage("white ball.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void act(){   
        if (!freeze_state){
            /* select move strategy */
            moveTo();
            
            /* select attack strategy */
            bullet_attack();

            /* collision avoidance */
            collision_avoidance();
                
            /* timer */
            timer();
            //additional_timer();
        }
        if(!fade){
            // refresh buff state
            buffRefresh();
        }
        /* remove condition */
        dead();
    }
    
    public void moveTo(){
        int x = (int)((0+35*exist_timer)*cos(exist_timer));
        int y = (int)((0+35*exist_timer)*sin(exist_timer));
        
        setLocation(x+800,y+450);
        exist_timer-=0.03;
    }
    
    public void bullet_attack(){
        if (attack_timer == 0){
            turnTowards(player_x, player_y);
            getWorld().addObject(new YinYangBullet(getRotation(),"white ball.png"),getX(),getY());
            attack_timer = attack_speed;
        }
    }
    
    /* melee hit player */
    public void collision_avoidance(){
        for (NotBullet collision_obj: this.getIntersectingObjects(NotBullet.class)){
            /* melee attack player */
            if(collision_obj instanceof Player){
                collision_obj.damage(getX(),getY(),push_damage,"push");      //damage = 20
            }
        }
    }
    
    public void damage(int source_x, int source_y, int damage_num, String type){
        if (damage_state != "invincible"){
            switch (type){
                    case "push":
                        /* take damage */
                        boss.hp -= (int)(damage_num/2);
                        break;
                        
                     case "pull":
                        /* take damage */
                        boss.hp -= (int)(damage_num/2);
                        break;
                        
                    case "bullet":
                        /* take damage */
                        boss.hp -= (int)(damage_num/2);
                        break;
                        
                    default: break;
            }
        }
    }
    
    public void dead(){
        /* delete if hit world edge */
        if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
        if (hp <= 0) {
            fade = true;
            freeze_state = true;
        }
        if(fade){
            transVal-=5;
        }
        if(transVal <=0){
            getWorld().removeObject(this);
            clearBuff();
        }
        else{
            getImage().setTransparency(transVal);
        }
    }
}