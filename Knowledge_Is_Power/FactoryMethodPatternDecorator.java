import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class FactoryMethodPatternDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FactoryMethodPatternDecorator extends Decorator
{
    protected DrP player;
    protected int duration;
    protected int radius;
    
    public FactoryMethodPatternDecorator(DrP Player, int Duration, int Radius){
        super(Radius,Radius,0,0);
        player = Player;
        duration = Duration;
        radius = Radius;
    }
    
    /* override */
    public void draw(){
        setLocation(player.getX(),player.getY());
        
        GreenfootImage image = new  GreenfootImage(radius, radius);
        for (int r=0;r<15;r++){
            image.setColor(Color.BLUE);
            image.drawOval(r, r, radius-2*r, radius-2*r);
           // image.fillOval(r, r, radius-2*r, radius-2*r);
        }
        setImage(image);
    }
    
    public void timer(){
        if (duration != 0){
            duration--;
        }
        else go_die = true;
    }
}
