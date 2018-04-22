import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BossStageWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BossStageWorld extends BaseWorld
{   
    public BossStageWorld(){
        super();
        player.hp = player.MAX_HP;      //recover hp at the start of the stage
        bossMusic.playLoop();
        bossMusic.setVolume(15);
    }

    public void prepare(){
        /* create player */
        addObject(player, 800, 850);
        /* create player UI */
        playerUICreate();
        /* create boss */
        enermyCreate();
        /* create boss UI */
        enermyUIcreate();
    }
    
    public void enermyCreate(){
        addObject(new BossEnermy(),800,200);
    }
    
    public void enermyUIcreate(){
        /* boss head pic*/
        addObject(new UIPictureDecorator(150,150,0,0,"board.jpg"),150,100);

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
}
