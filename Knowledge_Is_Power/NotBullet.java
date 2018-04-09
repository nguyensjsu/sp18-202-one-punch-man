/**
 * Write a description of class NotBullet here.
 * a interface for non bullet actor
 * @author Karas
 * @version v0.1.3
 */
public interface NotBullet
{
    public void damage(int source_x, int source_y, int damage_num, String type);
    public String get_damage_state();
    public void dead();
    public int interface_getX();
    public int interface_getY();
}
