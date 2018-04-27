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
    }

    public void prepare(){
        BaseWorld.BGM = new GreenfootSound("mob_fight.mp3");
        BaseWorld.BGM.playLoop();
        BaseWorld.BGM.setVolume(20);
        
        /* create player */
        addObject(player, 800, 850);
        /* create player UI */
        playerUICreate();
        /* create boss */
        enermyCreate();
    }
    
    public void enermyCreate(){
    }
    
    public void act(){
        if (die_count == 0){    //total enermy in this stage
            if (!exit_shown){
                exitCreate();
                exit_shown = true;
            }
            /* enter exit to boss stage */
            if (player.getX()>700 && player.getX()<900 && player.getY()<50){
                BaseWorld.BGM.stop();
                Greenfoot.setWorld(new TestStageTwoWorld());
            }
        }
    }
}
