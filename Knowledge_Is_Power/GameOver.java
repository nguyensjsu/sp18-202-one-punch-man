import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class GameOver here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GameOver extends NonBattle
{
    public GameOver()
    {
        super();
        if (BaseWorld.BGM.isPlaying()){
            BaseWorld.BGM.stop();
        }
            
        GreenfootImage background = new GreenfootImage("background.jpg");
        background.scale(1600, 900);
        setBackground(background);
    }
    
    public void prepare(){
        //addObject(new Decorator(1000,300,"theme.png"),800,300);
        addObject(new Decorator(900,150,new GreenfootImage("YOU ARE DEAD",600,Color.RED,Color.WHITE)),800,300);
        addObject(new Decorator(250,50,new GreenfootImage("back to menu",300,Color.RED,Color.WHITE)),800,600);
    }
    
    public void act(){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null && Greenfoot.mousePressed(null)) {
            if (mouse.getX()>=675 && mouse.getX()<=925 && mouse.getY()>=575 && mouse.getY()<=625) Greenfoot.setWorld(new MainMenu());
        }
    }
}
