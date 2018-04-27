import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class TestEnermy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TestEnermy extends Enermy
{
    public TestEnermy(){
        this(50,50,"stop","stop");    //default size 50*50
    }
    
    public TestEnermy(int sizeX, int sizeY, String move, String attack){
        super(sizeX,sizeY,move,attack);
    }
}
