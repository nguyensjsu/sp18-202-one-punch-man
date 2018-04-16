import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ShockedBuff here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ShockedBuff extends BuffState
{
    public ShockedBuff(Actor actor, int timeout, int damage){
        super(actor, new ShockedDecorator(), timeout, damage, BuffType.Shocked, "freeze");
        super.BuffMessage = "Shocked by Thunder Chain Attack";
    }
}
