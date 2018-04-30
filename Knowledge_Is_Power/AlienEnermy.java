import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class AlienEnermy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AlienEnermy extends Enermy
{


    public AlienEnermy(){
        this(50,50,"stop","stop");    //default size 50*50
    }

    public AlienEnermy(int sizeX, int sizeY, String move, String attack){
        super(sizeX,sizeY,move,attack);
        attack_speed = 80;
        MAX_HP = 80;
        hp = 80;

        prevMoveState = move_state = move;
        attack_state = attack;

        GreenfootImage image = new GreenfootImage("alien.png");
        image.scale(size_x, size_y);
        setImage(image);
    }

   public void act(){
        if (!freeze_state){
            /* select move strategy */

            switch (move_state){
                case "wander": wander(); break;
                case "chase": chase(); break;
                case "push": push(push_x, push_y, push_speed); break;
                default: break;
            }

            /* select attack strategy */
            switch (attack_state){
                case "bullet": bullet_attack(); break;
                default: break;
            }

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

    public void bullet_attack(){
        if (attack_timer == 0){
            setRotation(90);
            getWorld().addObject(new AlienBullet(getRotation(), 20, 20, 20),getX(),getY());
            attack_timer = attack_speed;
        }
    }
}
