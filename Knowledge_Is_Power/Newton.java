import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.Random;

/**
 * Write a description of class Newton here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Newton extends Player
{
    //protected GifImage gif = new GifImage("obj_arrow.gif");
    /* player state */
    protected String move_state = "wasd";       //wasd, push, freeze
    protected String damage_state = "normal";       //normal, invincible

    /* player stat */
    protected String player_image = "Newton.png";
    protected String trans_image = "red-draught.png";
    protected GifImage myprism = new GifImage("myprism.gif");
    protected int size_x;
    protected int size_y;
    protected int move_speed = 5;
    protected int bullet_damage = 12;
    protected int attack_speed = 30;  //2 per sec
    protected int radius = 150;
    protected int attack_timer = 0;
    protected int prism_cd_timer = 0;
    protected int appleSatellite_cd_timer = 0;
    protected int appleRain_cd_timer = 0;
    protected int appleRain_duration = 0;
    protected int appleRain_duration_helper = 0;

    /* push stat */
    protected int push_x;
    protected int push_y;
    protected int push_rotation;
    protected int push_speed = 0;
    protected int push_timer = 0;

    /* damage stat */
    protected final int INVINCIBLE_TIME = 60;
    protected int invincible_timer = 0;

    /* constructor */
    public Newton(){
        this(50,50);    //default size 50*50
    }

    public Newton(int x, int y){
        size_x = x;
        size_y = y;
        GreenfootImage image = new GreenfootImage("Newton.png");
        image.scale(size_x, size_y);
        setImage(image);
    }

    /* method */
    public void act(){
       //setImage(gif.getCurrentImage());
       /* update move */
       if (!freeze_state){
           switch (move_state){
               case "wasd": wasd_move(); break;
               case "push": push(push_x, push_y, push_speed); break;
               default: break;
            }

           /* ability */
           base_attack();
           skill_1();
           skill_2();
           skill_3();
           ult_animation();
           /* invincible flash */
           invincible_flash(player_image,trans_image);

           /* timer */
           timer();
       }
       animation_timer();
       /* game over condition */
       dead();
    }

    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}
    public World interface_getWorld(){return getWorld();}

    public int get_hp(){return hp;}
    public String get_damage_state(){return damage_state;}
    public void set_move_state(String s){move_state = s;}

    /* Newton's skills */
    /* AppleSatellite */
    public void skill_1(){
         if(Greenfoot.isKeyDown("1")){
            if (appleSatellite_cd_timer == 0){
                //getWorld().addObject(new Prism(40),getX(),getY());
                AppleSatellite appleSatellite = new AppleSatellite();
                //prism.setRotation(getRotation());
                int x = (int) getX();
                int y = (int) (getY() - radius);
                getWorld().addObject(appleSatellite,x,y);
                appleSatellite_cd_timer = 360;

                getWorld().addObject(new UICDDecorator(this,100,100,1200,775,360),0,0);
            }
        }
    }

    /* Prism */
    public void skill_2(){
        if(Greenfoot.isKeyDown("2")){
            if (prism_cd_timer == 0){
                //getWorld().addObject(new Prism(40),getX(),getY());
                Prism prism = new Prism(getRotation(),12);
                //prism.setRotation(getRotation());
                int x = (int) (getX()+ 450*cos(toRadians(getRotation())));
                int y = (int) (getY()+ 450*sin(toRadians(getRotation())));
                getWorld().addObject(prism,x,y);
                prism_cd_timer = 60;

                getWorld().addObject(new UICDDecorator(this,100,100,1350,775,60),0,0);
            }
        }
    }

     /* AppleRain */
    public void skill_3(){
        if (ult_trigger ){
            appleRain_duration = 300;
            appleRain_cd_timer = 1200;
            getWorld().addObject(new UICDDecorator(this,100,100,1500,775,1200),1500,775);

        if(appleRain_duration != 0 && appleRain_duration_helper == 0){
            Random rand = new Random();
            int x = rand.nextInt(1580) + 10;
            getWorld().addObject(new AppleRain(),x,10);
            appleRain_duration_helper = 15;
        }
        if (appleRain_duration == 0)ult_trigger = false;
    }
    }
    
    public void ult_animation(){
        if (appleRain_cd_timer == 0){
            if(Greenfoot.isKeyDown("3")){
                ult_cutscence("Newton_swag.jpg","Newton ult.png");
                appleRain_cd_timer = 10000;
                ((BaseWorld)getWorld()).freeze_all(true);
            }
        }
    }
    
    public void animation_timer(){
        if (ult_cutscence_timer !=0) ult_cutscence_timer--;
        if (ult_cutscence_timer ==1) {ult_trigger = true;((BaseWorld)getWorld()).freeze_all(false);}
    }

    /* movement control using WASD */
    public void wasd_move(){
        int update_x = getX();
        int update_y = getY();

        /* WASD move */
        if(Greenfoot.isKeyDown("w")){
            update_y = getY() - move_speed;
        }
        if(Greenfoot.isKeyDown("s")){
            update_y = getY() + move_speed;
        }
        if(Greenfoot.isKeyDown("a")){
            update_x = getX() - move_speed;
        }
        if(Greenfoot.isKeyDown("d")){
            update_x = getX() + move_speed;
        }

        /* turn and move */
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null){
            turnTowards(mouse.getX(),mouse.getY());
        }
        setLocation(update_x,update_y);

        /* observer notify */
        for (Enermy enermy: getWorld().getObjects(Enermy.class)){
            enermy.update(update_x,update_y);
        }
    }

    public void push(int target_x, int target_y, int speed){
        int update_x = getX();
        int update_y = getY();

        /* push to target */
        if (abs(getX() - target_x) > 5|| abs(getY() - target_y) > 5){
            double dx = target_x - getX();
            double dy = target_y - getY();
            double scale_x = dx / sqrt(dx*dx+dy*dy);
            double scale_y = dy / sqrt(dx*dx+dy*dy);
            update_x = getX() + (int)(speed*scale_x);
            update_y = getY() + (int)(speed*scale_y);
        }

        /* turn and move */
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null){
            turnTowards(mouse.getX(),mouse.getY());
        }
        setLocation(update_x,update_y);

        /* smooth end */
        if(push_timer < 10)
            push_speed--;

        /* back to wasd */
        if (push_timer == 0){
            move_state = "wasd";
        }
    }

    /* base attack */
    public void base_attack(){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        /* bullet attack, fire by mouse */
        if (mouse != null && Greenfoot.mousePressed(null)){
            bullet_attack();
        }
    }

    /* bullet style attack (aim by mouse)*/
    public void bullet_attack(){
        if (attack_timer == 0){
            getWorld().addObject(new AppleBullet(getRotation(), 20, 20, bullet_damage),getX(),getY());
            attack_timer = attack_speed;
        }
    }

    public void damage(int source_x,int source_y, int damage_num, String type){
        if ((damage_state) != "invincible"){
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
                    hp -= damage_num;
                    /* invincible time */
                    invincible_timer = INVINCIBLE_TIME;
                    damage_state = "invincible";
                    break;

                case "bullet":
                    /* take damage */
                    hp -= damage_num;
                    /* invincible time */
                    invincible_timer = INVINCIBLE_TIME;
                    damage_state = "invincible";
                    break;

                case "heal":
                    if ((hp += damage_num) > MAX_HP)
                        hp = MAX_HP;
                    break;

                default: break;
            }
        }
    }

    public void invincible_flash(String origin, String trans){
       if (invincible_timer % 20 >= 10){
           GreenfootImage image = new GreenfootImage(trans);
           image.scale(size_x, size_y);
           setImage(image);
       }
       else{
           GreenfootImage image = new GreenfootImage(origin);
           image.scale(size_x, size_y);
           setImage(image);
        }

       if ((invincible_timer == 0) && damage_state == "invincible") damage_state = "normal";
    }

    public void timer(){
       if (attack_timer != 0) attack_timer--;
       if (push_timer != 0) push_timer--;
       if (invincible_timer != 0) invincible_timer--;
       if (prism_cd_timer != 0) prism_cd_timer--;
       if(appleSatellite_cd_timer !=0) appleSatellite_cd_timer--;
       if (appleRain_cd_timer !=0) appleRain_cd_timer--;
       if (appleRain_duration !=0) appleRain_duration--;
       if (appleRain_duration_helper !=0) appleRain_duration_helper--;
    }


}
