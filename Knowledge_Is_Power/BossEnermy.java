import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class BossEnermy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BossEnermy extends Enermy
{
    protected final double PI = 3.1415926;
    
    /* stage one */
    protected int angle = 90;
    protected int angle_modifier = 15;
    
    /* stage three */
    protected int position_state = 0;
    
    /* attack interval */
    protected int attack_speed = 26; //26X
    /* timer */
    protected int attack_timer = 0; 
    protected int music_beat = 0;   //1/2
    protected int current_beat = 0; //24X
    
    public BossEnermy(){
        super(200,200,"stop","start");    //default size 200*200
        
        /* starting stat */
        damage_state = "invincible";
        MAX_HP = 2960;
        hp = 50;
        
        GreenfootImage image = new GreenfootImage("man01.png");
        image.scale(size_x, size_y);
        setImage(image);
    }
    
    public void act(){   
        if (!freeze_state){
            /* select move strategy */
            /*
            switch (move_state){
                case "start": start(); break;
                case "stage one": stage_one(); break;
                case "stage two": stage_two(); break;
                case "stage three": stage_three(); break;
                default: break;
            }
            */
           
            /* select attack strategy */

            switch (attack_state){
                case "start": start(); break;
                case "stage one": stage_one(); break;
                case "stage two": stage_two(); break;
                case "stage three": stage_three(); break;
                case "stage four": stage_four(); break;
                default: break;
            }

           
            /* collision avoidance */
            collision_avoidance();
            
            /* timer */
            timer();
            additional_timer();
        }

        /* remove condition */
        dead();
    }
    
    public void start(){
        if (hp < MAX_HP){ hp += 6;}
        else{
            damage_state = "normal";
            attack_state = "stage one";
        }
    }
    
    public void stage_one(){
        if (attack_timer == 0){
            if(current_beat <=52){
                setRotation(angle);
                for(int i=0;i<11;i++){
                    getWorld().addObject(new BossSmallBullet(5/cos(toRadians(angle-90)),getRotation(),"red-draught.png"),50+i*150,100);
                }
                angle += angle_modifier;
                if (angle>=120 || angle<=60) angle_modifier*=-1;
                setRotation(0);
            }
            
            current_beat++;
            if(current_beat <= 24){
                attack_timer = attack_speed * 2;
                attack_timer = music_match(attack_timer,2); //2 scale
            }
            else if (current_beat <= 56){
                attack_timer = attack_speed;
                attack_timer = music_match(attack_timer,1); //1 scale
            }
            else{
                current_beat = 0;
                attack_timer = 0;
                attack_state = "stage two";
            }
        }        
    }
    
    public void stage_two(){
        if (attack_timer == 0){
            for(int j=0;j<360;j+=30)    
                for(int i=0;i<5;i++){
                    getWorld().addObject(new BossSmallBullet(9-i,(int)(30*random())+j+4*i,"red-draught.png"),getX(),getY());
                }
                
            attack_timer = attack_speed * 2;
            attack_timer = music_match(attack_timer,2);
            
            current_beat++;
            if(current_beat == 16){   
                current_beat = 0;
                attack_timer = 0;
                attack_state = "stage three";
            }
        }        
    }
    
    public void stage_three(){
        if (attack_timer == 0){
            getWorld().addObject(new Yin(this),800,450);
            getWorld().addObject(new Yang(this),800,450);

            attack_timer = attack_speed * 2;
            attack_timer = music_match(attack_timer,2);
            
            current_beat++;/*
            if(current_beat == 32){   
                current_beat = 0;
                attack_timer = 0;
                attack_state = "stage four";
            }*/
        }
    }        

    
    public void stage_four(){
        if (attack_timer == 0){
            
            attack_timer = attack_speed * 1;
            attack_timer = music_match(attack_timer,1);
            
            current_beat++;
            if(current_beat == 32){   
                current_beat = 0;
                attack_timer = 0;
                attack_state = "stage two";
            }
        }        
    }
    
    public int music_match(int input, int scale){
        if (music_beat == 2){
            music_beat = 0;
            return input + 1 * scale;
        }
        else{
            music_beat++;
            return input;
        }
    }
    
    public void additional_timer(){
        if(attack_timer != 0) attack_timer--;
    }
    
    /* immune to buff, can not add */
    public void addBuff(IBuffState buff){}

    /* no push state */
    public void damage(int source_x, int source_y, int damage_num, String type){
        if (damage_state != "invincible"){
            switch (type){
                    case "push":
                        /* take damage */
                        hp -= damage_num;
                        break;
                        
                     case "pull":
                        /* take damage */
                        hp -= damage_num;
                        break;
                        
                    case "bullet":
                        /* take damage */
                        hp -= damage_num;
                        break;
                        
                    default: break;
            }
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
}
