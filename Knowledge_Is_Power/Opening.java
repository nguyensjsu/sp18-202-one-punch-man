import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Opening here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Opening extends NonBattle
{
    protected int start_timer = 0;
    
    public Opening()
    {
        super();
    }
    
    public void prepare(){
        addObject(new ScreenChange("black to trans"),800,450);
        addObject(new Decorator(1200,800,"logo.png"),800,450);
    }
    
     public void act(){
        if (start_timer == 5 * 60){
            addObject(new ScreenChange("trans to black"),800,450);
        }
        
        if (start_timer == 8 * 60){
            Greenfoot.setWorld(new MainMenu());
        }
         
        timer();
    }
    
    public void timer(){
        start_timer++;
    }
}
