import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BigBangDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BigBangDecorator extends Decorator
{
    private int sizeX;
    private int sizeY;
    private int damage;
    private int lifeTime = 2000;
    private int attackTime = 500;
    private SimpleTimer lifeTimer = new SimpleTimer();
    private SimpleTimer attackTimer = new SimpleTimer();
    
    
    public BigBangDecorator(int sizeX, int sizeY, int d){
        
        this.size_x = sizeX;
        this.size_y = sizeY;
        this.damage = d;
        rotation = 0;
        GreenfootImage image = new GreenfootImage("bigblackhole.png");
        image.scale(900, 900);
        setImage(image);

        lifeTimer.mark();
    }
    
  
    public void act() 
    {
        if (!freeze_state){
            
            setRotation(rotation);
            rotation+=2;
            
            if(attackTimer.millisElapsed() > attackTime){
                
                for (Enermy enermy: getWorld().getObjects(Enermy.class)){
                    enermy.setLocation(getX(), getY());
                    enermy.damage(getX(),getY(),damage, "pull");
                
                }
         
                attackTimer.mark();
            }
        }
        
        dead();
    }
    
    
    public void dead(){
        if(lifeTimer.millisElapsed() > lifeTime){
             getWorld().removeObject(this);
        }
    
 
    }
}
