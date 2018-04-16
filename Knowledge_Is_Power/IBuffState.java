import greenfoot.*;
/**
 * Write a description of class BuffState here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface IBuffState 
{
    public Actor getSource();
    public int buffDamage();
    public String buffMove();
    public void update();
    public void die();
    public boolean isDead();
    public BuffType getType();
}
