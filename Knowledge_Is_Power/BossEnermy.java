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
    
    /* stage four */
    protected int laser_x = 200;
    protected int laser_y = 200;
    protected int laser_x_interval = 150;
    protected int laser_y_interval = 150;
    protected int Huge_trigger = 0;
    protected boolean left_right = true;
    protected int laser_delay_count = 0;
    protected boolean laser_y_start = true;
    
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
            if (current_beat <= 13){
                for(int j=0;j<360;j+=30)    
                    for(int i=0;i<5;i++){
                        getWorld().addObject(new BossSmallBullet(9-i,(int)(30*random())+j+4*i,"red-draught.png"),getX(),getY());
                    }
            } 
            
            attack_timer = attack_speed * 2;
            attack_timer = music_match(attack_timer,2);
            
            current_beat++;
            if(current_beat == 17){   
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
            
            current_beat++;
            if(current_beat == 16){
                getWorld().removeObjects(getWorld().getObjects(Yin.class));
                getWorld().removeObjects(getWorld().getObjects(Yang.class));
                
                current_beat = 0;
                attack_timer = attack_speed * 2 - 4;
                attack_state = "stage four";
            }
        }
    }        

    
    public void stage_four(){
        if (attack_timer == 0){
            if (Huge_trigger == 0){
                turnTowards(player_x,player_y);
                getWorld().addObject(new HugeEnermyBullet(getRotation(),5),getX(),getY());
                setRotation(0);
                
                Huge_trigger = 7;
            }
            else Huge_trigger--;
            
            if (left_right && laser_delay_count == 0){
                getWorld().addObject(new LaserEnermyBullet(90,5,400,20),laser_x,201);
                laser_x += laser_x_interval;
                if (laser_x >= 1600){ 
                    left_right = false; 
                    laser_x = 1400; 
                    laser_delay_count = 7;
                }
            }
            else if (laser_delay_count == 0){
                getWorld().addObject(new LaserEnermyBullet(90,5,400,20),laser_x,201);
                laser_x -= laser_x_interval;
                if (laser_x <= 0){ 
                    left_right = true; 
                    laser_x = 200; 
                    laser_delay_count = 7;
                }
            }
            
            if (left_right && laser_delay_count == 0 && laser_y_start){
                getWorld().addObject(new LaserEnermyBullet(0,8,400,20),201,laser_y);
                laser_y += laser_y_interval;
                if (laser_y >= 900){
                    laser_y = 200;
                    laser_y_start = false;
                }
            }
            else if (laser_delay_count == 0 && laser_y_start)
            {
                getWorld().addObject(new LaserEnermyBullet(180,8,400,20),1399,laser_y);
                laser_y += laser_y_interval;
                if (laser_y >= 900){
                    laser_y = 200;
                    laser_y_start = false;
                }
            }
            
            attack_timer = attack_speed/2;
            attack_timer = music_match(attack_timer,0);
            if (laser_delay_count != 0) laser_delay_count--;
            if (laser_delay_count == 1) laser_y_start = true;
            
            current_beat++;
            if(current_beat == 62){
                laser_x = 200;
                laser_y = 200;
                Huge_trigger = 0;
                left_right = true;
                laser_delay_count = 0;
                laser_y_start = true;
                
                current_beat = 0;
                attack_timer = attack_speed*2;
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
    
     public void dead(){
        if (hp <= 0) {
            fade = true;
            freeze_state = true;
        }
        if(fade){
            transVal-=5;
        }
        if(transVal <=0){
            getWorld().removeObjects(getWorld().getObjects(Yin.class));
            getWorld().removeObjects(getWorld().getObjects(Yang.class));
            getWorld().removeObjects(getWorld().getObjects(EnermyBullet.class));
            
            getWorld().removeObject(this);
            clearBuff();
        }
        else{
            getImage().setTransparency(transVal);
        }
    };
}
