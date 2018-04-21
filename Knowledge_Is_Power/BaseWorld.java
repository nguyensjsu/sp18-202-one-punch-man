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
    protected String player_pic;
    protected String skill_one_pic;
    protected String skill_two_pic;
    protected String ult_pic;

    public BaseWorld()
    {
        // Create a new world with 1600x900 cells with a cell size of 1x1 pixels.
        super(1600, 900, 1);
        prepare();

    }

    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    public void prepare()
    {
        /* setting actor order*/
        setActOrder(/* effect under actor */
                    BlackHoleDecorator.class,
                    DecoratorPatternDecorator.class,
                    FactoryMethodPatternDecorator.class,
                    /* enermy */
                    Enermy.class,
                    /* player */
                    Player.class,
                    /* effect over actor */
                    HpDecorator.class,
                    Bullet.class,
                    FlameDecorator.class,
                    ShockedDecorator.class,
                    DrPPaperDecorator.class,
                    /* UI picture*/
                    UIPictureDecorator.class,
                    UICDDecorator.class,
                    UIHpDecorator.class,
                    /* UI frame */
                    UIFrameDecorator.class,
                    /* ult cutscence */
                    UltDecorator.class
        );

        /* create player */
        playerCreate();
        /* create enermy */
        enermyCreate();
        /* create player UI */
        playerUICreate();

    }

    public void playerCreate(){
        Player player = new DrP();  //size 50*50
        //Player player = new Hawking(); //for test
        //Player player = new Tesla();
        addObject(player, 800, 700);
        //HpDecorator player_hp = new HpDecorator(player,player.hp,player.MAX_HP,0,player.size_x-10,player.size_x,10);   // hp 100-100, offset(0,40), size 50*10
        //addObject(player_hp, 10000, 10000);

        /* player ui */
        player_pic = "board.jpg";
        skill_one_pic = "board.jpg";
        skill_two_pic = "board.jpg";
        ult_pic = "board.jpg";
    }

    public void enermyCreate(){
        /* create 3 chasing enermies that shots bullets */
        for (int i=0; i<3; i++){
            Enermy enermy = new TestEnermy(50,50,"chase","bullet");
            addObject(enermy, (int)(800*random()+400), (int)(400*random())+100);
            HpDecorator enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
            addObject(enermy_hp, 10000, 10000);
        }

        /* create 1 wandering enermy that does not attack */
        Enermy enermy = new TestEnermy(50,50,"wander","stop");
        addObject(enermy, (int)(800*random()+400), (int)(400*random())+100);
        HpDecorator enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 10000, 10000);
    }

    public void playerUICreate(){
        /* player head pic*/
        addObject(new UIPictureDecorator(150,150,0,0,player_pic),150,750);

        /* player hp bar */
        Player player = getObjects(Player.class).get(0);
        UIHpDecorator uihp = new UIHpDecorator(player,player.hp,player.MAX_HP,400,27,450,800);
        addObject(uihp,0 ,0 );

        /* skill 123 pic */
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
        makeSquare(150,150,150,750,20,"bluerock.jpg");
        /* hp bar */
        addObject(new UIFrameDecorator(400+20,10,0,0,"bluerock.jpg"),450,800+18);
        addObject(new UIFrameDecorator(400+20,10,0,0,"bluerock.jpg"),450,800-18);
        addObject(new UIFrameDecorator(10,25+20,0,0,"bluerock.jpg"),450+205,800);
        addObject(new UIFrameDecorator(10,25+20,0,0,"bluerock.jpg"),450-205,800);
        /* skill pic */
        makeSquare(100,100,1200,775,10,"bluerock.jpg");
        makeSquare(100,100,1350,775,10,"bluerock.jpg");
        makeSquare(100,100,1500,775,10,"bluerock.jpg");

    }

    public void makeSquare(int size_X, int size_Y, int loc_X, int loc_Y, int thick, String frame_pic)
    {
        addObject(new UIFrameDecorator(size_X+2*thick,thick,0,0,frame_pic),loc_X,loc_Y + ((size_Y + thick)/2));
        addObject(new UIFrameDecorator(size_X+2*thick,thick,0,0,frame_pic),loc_X,loc_Y - ((size_Y + thick)/2));
        addObject(new UIFrameDecorator(thick,size_Y+2*thick,0,0,frame_pic),loc_X + ((size_X + thick)/2),loc_Y);
        addObject(new UIFrameDecorator(thick,size_Y+2*thick,0,0,frame_pic),loc_X - ((size_X + thick)/2),loc_Y);
    }

    public void freeze_all(boolean t){
        for (FreezeObj freeze_obj: getObjects(FreezeObj.class)){
            freeze_obj.set_freeze_state(t);
        }
    }
}
