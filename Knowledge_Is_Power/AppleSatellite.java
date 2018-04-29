import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;
/**
 * Write a description of class AppleSatellite here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AppleSatellite extends Bullet
{
    protected String move_state = "normal";     //normal, freeze
    /* bullet stat */
    protected int size_x;
    protected int size_y;
    protected int radius = 150;
    protected int appleSatellite_timer = 120;
    protected boolean through = false;
    protected int damage = 10;
    protected boolean firstTime = true;
    protected int x=0;
    protected int y=0;


    public AppleSatellite(){
        this(40,40,3);        //default size 30*30, not through, 3 damage
    }


    public AppleSatellite(int sizeX, int sizeY, int d){
        size_x = sizeX;
        size_y = sizeY;
        damage = d;

        GreenfootImage image = new GreenfootImage("apple1.png");
        image.scale(size_x, size_y);
        setImage(image);
    }

    public void act(){
        if (move_state != "freeze"&&!getWorld().getObjects(Newton.class).isEmpty()){
            Newton newton = (Newton)getWorld().getObjects(Newton.class).get(0);
            if(firstTime){
                firstTime = false;
                x = newton.getX();
                y = newton.getY();
            } else if(x==newton.getX()&&y==newton.getY()){

            } else {
                setLocation(getX()+newton.getX()-x,getY()+newton.getY()-y);
                x = newton.getX();
                y = newton.getY();

            }
            move();
            /* timer */
            timer();
        }

        /* damage condition */
        if (getOneIntersectingObject(Enermy.class) != null ){
              for (Enermy e: this.getIntersectingObjects(Enermy.class)){
                   e.damage(getX(),getY(),damage, "bullet");
                   break;
              }
        }

        /* remove enemy bullet*/
        if (getOneIntersectingObject(EnermyBullet.class) != null ){
              for (EnermyBullet eb: this.getIntersectingObjects(EnermyBullet.class)){
                   getWorld().removeObject(eb);
                   break;
              }
        }

        /* remove condition */
        if(appleSatellite_timer == 0) dead();
    }

    /* no use, just for interface */
    public int interface_getX(){return getX();}
    public int interface_getY(){return getY();}

    public void set_move_state(String s){
        move_state = s;
    }

    public void move(){
            //double angle = atan2(getY()-newton.getY(),getX()-newton.getX());
            //double a = getY()-newton.getY();
            //double b = getX()-newton.getX();
            //double c = sqrt(pow(getX()-newton.getX(),2)+pow(getY()-newton.getY(),2));
            //int x = (int) (newton.getX()+ radius*b/c);
            //int y = (int) (newton.getY()+ radius*a/c);
            turn(8);
            move(25);;
    }

    public void timer(){
        if(appleSatellite_timer != 0) appleSatellite_timer--;
    }

    public void dead(){
            getWorld().removeObject(this);
    }
}
