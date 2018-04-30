import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class UfoBullet here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class UfoBullet extends EnermyBullet
{
    /**
     * Act - do whatever the UfoBullet wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    protected double move_speed = 7.0;
    public UfoBullet(int r) {
        super(r,50,50,20); 
    }

    public UfoBullet(int r, int sizeX, int sizeY, int d) {
        super(r,sizeX,sizeY,d);
        GreenfootImage image = new GreenfootImage("rocket.png");
        image.scale(sizeX, sizeY);
        setImage(image);
    }

    public void act(){
        if (!freeze_state){
            move();

            /* timer */
            timer();
        }

        /* remove condition */
        dead();
    }

    /* override */
    public void dead(){
        /* delete if hit player and type is not through */
        if (getOneIntersectingObject(Player.class) != null){
            for (Player p: this.getIntersectingObjects(Player.class)){
                p.damage(getX(),getY(),damage, "bullet");
                break;
            }
            if (!through){
                getWorld().removeObject(this);
            }
        }
        /* delete if hit world edge */
        else if (this.isAtEdge()){
            getWorld().removeObject(this);
        }
    }
}
