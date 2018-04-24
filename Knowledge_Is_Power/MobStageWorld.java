import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MobStageWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MobStageWorld extends BaseWorld
{
    protected int die_count = 0;
    protected boolean exit_shown = false;
    
    public MobStageWorld(){
        super();
        player.hp = player.MAX_HP;      //recover hp at the start of the stage
        /*
        mobMusic.playLoop();
        mobMusic.setVolume(15);
        */
    }

    public void prepare(){
        /* create player */
        addObject(player, 800, 850);
        /* create player UI */
        playerUICreate();
        /* create boss */
        enermyCreate();
    }
    
    public void enermyCreate(){
    }
    
    public void exitCreate(){
        /* create enter arrow */
        addObject(new Decorator(100,100,"board.jpg"),800,125);
        /* create next stage */
        addObject(new Decorator(200,50,"board.jpg"),800,25);
    }
    
    public void act(){
        if (die_count == 0){    //total enermy in this stage
            if (!exit_shown){
                exitCreate();
                exit_shown = true;
            }
            /* enter exit to boss stage */
            if (player.getX()>700 && player.getX()<900 && player.getY()<50){
                Greenfoot.setWorld(new BossStageWorld());
            }
        }
    }
}
