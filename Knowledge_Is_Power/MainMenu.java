import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MainMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainMenu extends NonBattle
{
    public MainMenu()
    {
        super();    
    }
    
    public void prepare(){
        GreenfootImage image = new GreenfootImage(1600,900);
        image.setColor(new Color(0,0,0));
        image.scale(1600, 900);
        image.fill();
        setBackground(image);    
        
        addObject(new Decorator(1000,300,"theme.png"),800,300);
        addObject(new Decorator(450,75,new GreenfootImage("Start new game",450,Color.LIGHT_GRAY,Color.BLACK)),800,600);
        addObject(new Decorator(100,75,new GreenfootImage("Exit",100,Color.LIGHT_GRAY,Color.BLACK)),800,700);
    }
    
    public void act(){
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null && Greenfoot.mousePressed(null)) {
            if (mouse.getX()>=575 && mouse.getX()<=1025 && mouse.getY()>=563 && mouse.getY()<=637) Greenfoot.setWorld(new TestStageWorld());
            else if (mouse.getX()>=750 && mouse.getX()<=850 && mouse.getY()>=663 && mouse.getY()<=737) System.exit(0);
        }
    }
}
