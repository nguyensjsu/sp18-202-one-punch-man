import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BossStageWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BossStageWorld extends BaseWorld
{   
    protected boolean exit_shown = false;
    
    public BossStageWorld(){
        super();
        player.hp = player.MAX_HP;      //recover hp at the start of the stage
    }

    public void prepare(){
        /* create player */
        addObject(player, 800, 850);
        /* create player UI */
        playerUICreate();
        /* create boss */
        enermyCreate();
        /* create boss bgm */
        //BaseWorld.BGM = new GreenfootSound("boss_fight.mp3");
        BaseWorld.BGM.playLoop();
        BaseWorld.BGM.setVolume(20);
        /* create boss UI */
        enermyUIcreate();
    }
    
    public void enermyCreate(){
        addObject(new BossEnermy(),800,200);
    }
    
    public void enermyUIcreate(){
        /* boss head pic*/
        addObject(new UIPictureDecorator(150,150,0,0,"white.png"),150,100);
        addObject(new UIPictureDecorator(150,150,0,0,"boss.png"),150,100);

        /* boss hp bar */
        BossEnermy boss = getObjects(BossEnermy.class).get(0);
        UIHpDecorator uihp = new UIHpDecorator(boss,boss.hp,boss.MAX_HP,1200,27,450,50);
        addObject(uihp,0 ,0 );
        
        /* UI frame */
        /* pic */
        makeSquare(150,150,150,100,20,UI_frame_pic);
        /* hp bar */
        addObject(new UIFrameDecorator(1200+20,10,0,0,UI_frame_pic),850,50+18);
        addObject(new UIFrameDecorator(1200+20,10,0,0,UI_frame_pic),850,50-18);
        addObject(new UIFrameDecorator(10,25+20,0,0,UI_frame_pic),850+605,50);
        addObject(new UIFrameDecorator(10,25+20,0,0,UI_frame_pic),850-605,50);
    }
    
    public void act(){
        if (getObjects(BossEnermy.class).size() == 0){
            if (!exit_shown){
                exitCreate();
                exit_shown = true;
            }
            /* enter exit to win */
            if (player.getX()>700 && player.getX()<900 && player.getY()<50){
                BaseWorld.BGM.stop();
                Greenfoot.setWorld(new Win());
            }
        }
    }
}
