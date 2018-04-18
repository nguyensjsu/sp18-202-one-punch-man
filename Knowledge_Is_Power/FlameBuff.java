import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * @author Zhiye Chen
 * @version (a version number or a date)
 */
public class FlameBuff extends BuffState 
{
    public FlameBuff(Actor actor, int timeout, int damage){
        super(actor, timeout, damage, BuffType.Burning, "");
        super.BuffMessage = "Burned by Flame Attack";
        super.damping = true;
    }
}
