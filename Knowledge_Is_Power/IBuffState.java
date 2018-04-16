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
    public boolean changeMove();
    public String buffMove(String prevMoveState);
    public String getPrevMoveState();
    public void display(int x, int y, int r);
    public boolean isDead();
    public String toString();
}
