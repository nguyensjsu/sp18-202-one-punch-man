import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ChargeDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ChargeDecorator extends Decorator
{
    private GifImage originGif = new GifImage("thunder_chain_yellow.gif");
    private int sizeX = 1000;
    private int sizeY = 50;
    private TeslaCar teslaCar;
    private TeslaTower teslaTower;
    public ChargeDecorator(TeslaCar source, TeslaTower target){
        teslaCar= source;
        teslaTower = target;
    }
    
    public void act() 
    {
        // Add your action code here.
    }    
}
