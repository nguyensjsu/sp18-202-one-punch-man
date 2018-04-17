import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DrP_base_attack here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DrPBaseAttack extends PlayerBullet
{    
    public DrPBaseAttack(int r){
        this(r,20,20,10);        //default size 20*20, not through, 10 damage
    }
    
    public DrPBaseAttack(int r, int sizeX, int sizeY, int d){
        super(r,sizeX,sizeY,d);
    }

}
