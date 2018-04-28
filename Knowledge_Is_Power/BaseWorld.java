import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class BaseWorld here.
 * a base world class
 * @author Karas
 * @version v0.1.3
 */

public class BaseWorld extends World
{
    protected static Player player;
    protected static String player_pic;
    protected static String skill_one_pic;
    protected static String skill_two_pic;
    protected static String ult_pic;
    
    /* UI pic */
    protected String UI_frame_pic = "frame.png";
    
    /* BGM */
    protected static GreenfootSound BGM;
    protected static GreenfootSound bossBGM = new GreenfootSound("boss_fight.mp3");;
    
    /* Background */
    protected static GreenfootImage background = new GreenfootImage("background.jpg");

    public BaseWorld()
    {
        super(1600, 900, 1);
        
        /* setting actor order*/
        setActOrder(/* effect under actor */
                    Decorator.class,
                    /* enermy */
                    Enermy.class,
                    /* player */
                    Player.class,
                    /* effect over actor */
                    HpDecorator.class,
                    Bullet.class,
                    AttachEffectDecorator.class,
                    DrPPaperDecorator.class,
                    DialogDecorator.class,
                    /* UI picture*/
                    UIPictureDecorator.class,
                    UICDDecorator.class,
                    UIHpDecorator.class,
                    /* UI frame */
                    UIFrameDecorator.class,
                    /* ult cutscence */
                    UltDecorator.class,
                    /* screen change */
                    ScreenChange.class
        );
        
        background.scale(1600, 900);
        setBackground(background);
        
        prepare();
    }

    public void prepare(){}

    public void playerCreate(String pic, String one_pic, String two_pic, String u_pic){
        /* player position */
        addObject(player, 800, 850);

        /* player ui */
        player_pic = pic;
        skill_one_pic = one_pic;
        skill_two_pic = two_pic;
        ult_pic = u_pic;
    }

    public void playerUICreate(){
        /* player head pic*/
        addObject(new UIPictureDecorator(150,150,0,0,"white.png"),150,750);
        addObject(new UIPictureDecorator(150,150,0,0,player_pic),150,750);

        /* player hp bar */
        Player player = getObjects(Player.class).get(0);
        UIHpDecorator uihp = new UIHpDecorator(player,player.hp,player.MAX_HP,400,27,450,800);
        addObject(uihp,0 ,0 );

        /* skill 123 pic */
        addObject(new UIPictureDecorator(100,100,0,0,"white.png"),1200,775);
        addObject(new UIPictureDecorator(100,100,0,0,"white.png"),1350,775);
        addObject(new UIPictureDecorator(100,100,0,0,"white.png"),1500,775);
        addObject(new UIPictureDecorator(100,100,0,0,skill_one_pic),1200,775);
        addObject(new UIPictureDecorator(100,100,0,0,skill_two_pic),1350,775);
        addObject(new UIPictureDecorator(100,100,0,0,ult_pic),1500,775);

        /* skill 123 cooldown */
        /* choose one code to add into your player's skill, right after it press the key and successfully use */
        /*
        getWorld().addObject(new UICDDecorator(this,100,100,1200,775,360),0,0);    //skill 1: cd 360 (only modify last parameter)
        getWorld().addObject(new UICDDecorator(this,100,100,1350,775,600),0,0);    //skill 2: cd 600
        getWorld().addObject(new UICDDecorator(this,100,100,1500,775,1800),0,0);    //skill 3: cd 1800
        */

        /* UI frame */
        /* player pic */
        makeSquare(150,150,150,750,20,UI_frame_pic);
        /* hp bar */
        addObject(new UIFrameDecorator(400+20,10,0,0,UI_frame_pic),450,800+18);
        addObject(new UIFrameDecorator(400+20,10,0,0,UI_frame_pic),450,800-18);
        addObject(new UIFrameDecorator(10,25+20,0,0,UI_frame_pic),450+205,800);
        addObject(new UIFrameDecorator(10,25+20,0,0,UI_frame_pic),450-205,800);
        /* skill pic */
        makeSquare(100,100,1200,775,10,UI_frame_pic);
        makeSquare(100,100,1350,775,10,UI_frame_pic);
        makeSquare(100,100,1500,775,10,UI_frame_pic);

    }

    public void enermyCreate(){}
    
    public void exitCreate(){
        /* create enter arrow */
        addObject(new Decorator(100,100,"enter.png"),800,125);
        /* create next stage */
        addObject(new Decorator(200,50,"portal.png"),800,25);
    }
    
    public void playerUIRemove(){
        removeObjects(getObjects(UIPictureDecorator.class));
    }
    
    public void makeSquare(int size_X, int size_Y, int loc_X, int loc_Y, int thick, String frame_pic)
    {
        addObject(new UIFrameDecorator(size_X+2*thick,thick,0,0,frame_pic),loc_X,loc_Y + ((size_Y + thick)/2));
        addObject(new UIFrameDecorator(size_X+2*thick,thick,0,180,frame_pic),loc_X,loc_Y - ((size_Y + thick)/2));
        addObject(new UIFrameDecorator(size_X+2*thick,thick,0,90,frame_pic),loc_X + ((size_X + thick)/2),loc_Y);
        addObject(new UIFrameDecorator(size_X+2*thick,thick,0,270,frame_pic),loc_X - ((size_X + thick)/2),loc_Y);
        //addObject(new UIFrameDecorator(thick,size_Y+2*thick,0,0,frame_pic),loc_X + ((size_X + thick)/2),loc_Y);
        //addObject(new UIFrameDecorator(thick,size_Y+2*thick,0,0,frame_pic),loc_X - ((size_X + thick)/2),loc_Y);
    }

    public void freeze_all(boolean t){
        for (FreezeObj freeze_obj: getObjects(FreezeObj.class)){
            freeze_obj.set_freeze_state(t);
        }
    }
}
