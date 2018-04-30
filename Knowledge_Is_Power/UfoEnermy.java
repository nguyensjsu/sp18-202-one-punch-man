import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
import java.util.Random;

/**
 * Write a description of class UfoEnermy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class UfoEnermy extends Enermy
{
    /**
     * Act - do whatever the UfoEnermy wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
     protected int move_cd = 0;
     protected int turn_cd = 0;
     protected int rotation = 90;
     protected int move_speed = 2;
     protected int attack_timer = 100;
     protected int attack_speed = 50;
     protected int num; // 0-7, 8 directions to move

    public UfoEnermy(){
        super(54,80,"stop","stop");    //default size 50*50
        MAX_HP = 120;
        hp = 120;

        GreenfootImage image = new GreenfootImage("ufo2.png");
        image.scale(size_x, size_y);
        setImage(image);

    }

     public void act(){
        if (!freeze_state){
            /* select move strategy */

            move_around();
            attack();
            /* collision avoidance */
            collision_avoidance();

            /* timer */
            timer();

        }
        if(!fade){
            // refresh buff state
            buffRefresh();
        }
        /* remove condition */
        dead();
    }

    public void move_around() {

        // move(4);
        // if(Greenfoot.getRandomNumber(100) < 10) {
        //     turn(Greenfoot.getRandomNumber(90) - 45);
        // }
        // if(getX() <=5 || getX() >= getWorld().getWidth() -5) {
        //     turn(180);
        // }
        // if(getY() <= 5 || getY() >= getWorld().getHeight() -5) {
        //     turn(180);
        // }
        Player player = (Player) getWorld().getObjects(Player.class).get(0);
        turnTowards(player.getX(),player.getY());
        if(getX() <= 5 && getY() <= 5){
            setLocation(getX()+20, getY()+20);
            rotation = 45;
            //move_cd = 0;
        } else if(getX() >= (getWorld().getWidth()-5) && getY() <= 5){
            setLocation(getX()-20, getY()+20);
            rotation = 135;
            //move_cd = 0;
        } else if(getX() <= 5) {
            setLocation(getX()+20, getY());
            rotation = 0;
            //move_cd = 0;
        } else if(getY() <= 5) {
            setLocation(getX(), getY()+20);
            rotation = 90;
            //move_cd = 0;
        } else if (getX() >= (getWorld().getWidth()-5)){
            setLocation(getX()-20, getY());
            rotation = 180;
            //move_cd = 0;
        } else if (getY() >= (getWorld().getHeight()-400)){
            setLocation(getX(), getY()-20);
            rotation = 270;
            //move_cd = 0;
        } else {
            if (move_cd == 0){
                Random rand = new Random();
                num = rand.nextInt(8);
                move_cd = 20;
            }
            switch(num){
              case 0:
                setLocation(getX()+3, getY());
                break;
              case 1:
                setLocation(getX()+2, getY()+2);
                break;
              case 2:
                setLocation(getX(), getY()+3);
                break;
              case 3:
                setLocation(getX()-2, getY()+2);
                break;
              case 4:
                setLocation(getX()-3, getY());
                break;
              case 5:
                setLocation(getX()-2, getY()-2);
                break;
              case 6:
                setLocation(getX(), getY()-3);
                break;
              case 7:
                setLocation(getX()+2, getY()-2);
                break;
              default:
                break;
            }
            // int update_x = (int)(getX() + move_speed*cos(toRadians(rotation)));
            // int update_y = (int)(getY() + move_speed*sin(toRadians(rotation)));
            // setLocation(update_x,update_y);
            // if (move_cd == 0){
            //     Random rand = new Random();
            //     rotation = rand.nextInt(360);
            //     move_cd = 20;
            // }
        }
        //Player player = (Player) getWorld().getObjects(Player.class).get(0);
        //turnTowards(player.getX(),player.getY());

        // if (move_cd == 0){
        //   rotation = Greenfoot.getRandomNumber(180);
        //   turn(rotation);
        //   move_cd = 40;
        // }
        // move(4);
        // if(turn_cd == 0){
        //     if(getX() <=10 || getX() >= getWorld().getWidth() -10 || getY() <= 20 || getY() >= getWorld().getHeight() -600) {
        //         turn(180);
        //         move_cd ++;
        //         turn_cd ++;
        //     }
        // }


    }

    public void attack(){
        if (attack_timer == 0){
            //Player player = (Player)getWorld().getObjects(Player.class).get(0);
            getWorld().addObject(new UfoBullet(getRotation(),50, 17, 20),getX(),getY());
            attack_timer = attack_speed;
        }
    }

    public void timer(){
        if (wander_timer != 0) wander_timer--;
        if (attack_timer != 0) attack_timer--;
        if (push_timer != 0) push_timer--;
        if (move_cd != 0) move_cd--;
        if (turn_cd != 0) turn_cd--;
    }


}
