import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class UIDecorator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UIPictureDecorator extends Decorator
{
    protected GreenfootImage UIimage;
    
    public UIPictureDecorator(int X, int Y, int s, int r, String pic_name){
        super(X,Y,s,r);
        if (pic_name != "no pic"){
            UIimage = new GreenfootImage(pic_name);
            UIimage.scale(size_x,size_y);
            setImage(UIimage);
        }
    }
    
    public UIPictureDecorator(int X, int Y, int s, int r, String pic_name, String gif_type){
        super(X,Y,s,r);
        
        gif = new GifImage(pic_name);
        GreenfootImage image = gif.getCurrentImage();
        image.scale(size_x, size_y);
        setImage(image);
    }
}
