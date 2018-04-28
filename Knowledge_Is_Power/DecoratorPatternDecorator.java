import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DecoratorPatternDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DecoratorPatternDecorator extends Decorator
{
    protected int duration;
    protected int radius;
    
    public DecoratorPatternDecorator(int Duration, int Radius){
        super(Radius,Radius,0,0);
        duration = Duration;
        radius = Radius;
        
        GreenfootImage image = new GreenfootImage("red circle.png");
        image.scale(radius,radius);
        setImage(image);
    }

    public void update(){
        rotation--;
        setRotation(rotation);
        
        for(DrPSuperAttack bullet: getObjectsInRange(radius-60-15, DrPSuperAttack.class)){
            if (bullet.size_x != 60){
                bullet.size_x = 60;
                bullet.size_y = 60;
                bullet.damage = 20;
                GreenfootImage image = bullet.getImage();
                image.scale(bullet.size_x, bullet.size_y);
                bullet.setImage(image);
            }
        }
    }
    
    public void timer(){
        if (duration != 0){
            duration--;
        }
        else go_die = true;
    }
}
