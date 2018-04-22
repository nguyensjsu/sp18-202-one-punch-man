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
    /* attack interval */
    protected int attack_speed = 26; //26X
    /* timer */
    protected int attack_one_timer = 0; 
    protected int attack_two_timer = 0;
    protected int music_beat = 0;   //1/2
    protected int current_beat = 0; //24X
    
    public BossEnermy(){
        super(200,200,"stop","start");    //default size 200*200
        
        /* starting stat */
        damage_state = "invincible";
        MAX_HP = 3000;
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
                //case "stage three": stage_three(); break;
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
        if (attack_one_timer == 0){
            turnTowards(player_x,player_y);
            for(int i=0;i<9;i++){
                getWorld().addObject(new BossSmallBullet(5,getRotation(),"red-draught.png"),getX()-600+i*150,getY());
            }
            setRotation(0);
            
            current_beat++;
            if(current_beat <= 24){
                attack_one_timer = attack_speed * 2;
                attack_one_timer = music_match(attack_one_timer,2); //2 scale
            }
            else if (current_beat <= 56){
                attack_one_timer = attack_speed;
                attack_one_timer = music_match(attack_one_timer,1); //1 scale
            }
            else{
                current_beat = 0;
                attack_state = "stage two";
            }
        }        
    }
    
    public void stage_two(){
        if (attack_two_timer == 0){
            for(int j=0;j<360;j+=30)    
                for(int i=0;i<5;i++){
                    getWorld().addObject(new BossSmallBullet(9-i,(int)(30*random())+j+4*i,"red-draught.png"),getX(),getY());
                }
                
            attack_two_timer = attack_speed * 2;
            attack_one_timer = music_match(attack_one_timer,2);
            
            current_beat++;
            if(current_beat == 16){   
                current_beat = 0;
                attack_state = "stage three";
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
        if(attack_one_timer != 0) attack_one_timer--;
        if(attack_two_timer != 0) attack_two_timer--;
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
