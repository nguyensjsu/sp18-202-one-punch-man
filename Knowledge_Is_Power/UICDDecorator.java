import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CDDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UICDDecorator extends UIPictureDecorator
{
    protected Player player;
    protected int remain_cd;
    protected int max_cd;
    protected int loc_x;
    protected int loc_y;
    
    public UICDDecorator(Player p, int X, int Y, int loc_X, int loc_Y,int max_CD){
        super(X,Y,0,0,"no pic");
        player = p;
        remain_cd = max_CD;
        max_cd = max_CD;
        loc_x = loc_X;
        loc_y = loc_Y;
        
        UIimage = new GreenfootImage(size_x,size_y);
        UIimage.setColor(new Color(0,0,0,200));
        UIimage.fill();
        UIimage.scale(size_x,size_y);
        setImage(UIimage);
    }
    
    public void update(){
        if ((size_y*remain_cd/max_cd) != 0){
            UIimage.scale(size_x,(size_y*remain_cd/max_cd));
            setLocation(loc_x, loc_y - (size_y*remain_cd/max_cd/2)+ 50 );
            setImage(UIimage);
        }
    }

    public void timer(){
        if (!player.freeze_state){
            if (remain_cd != 0) remain_cd--;
            else go_die = true;
        }
    }
}
