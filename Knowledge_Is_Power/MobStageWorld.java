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
    private int a = 0;
    private int threshold = 80; //increase to make the time until a ufo enermy created 
    
    public MobStageWorld(){
        super();
        player.hp = player.MAX_HP;      //recover hp at the start of the stage
    }

    public void prepare(){
        BaseWorld.BGM = new GreenfootSound("Endeavors.mp3");
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
        AlienEnermy[] alienEnermy = new AlienEnermy[10];
        for(int i = 0; i < alienEnermy.length/2; i++) {
            alienEnermy[i] = new AlienEnermy(50,50,"chase","bullet");
            int ex = Greenfoot.getRandomNumber(getWidth());
            int ey = Greenfoot.getRandomNumber(getHeight() - 300);
            addObject(alienEnermy[i], ex, ey);
            HpDecorator alienEnermy_hp = new HpDecorator(alienEnermy[i],alienEnermy[i].hp,alienEnermy[i].MAX_HP,0,alienEnermy[i].size_x-10,alienEnermy[i].size_x,10);
            addObject(alienEnermy_hp, 0, 0);
        }
        
        for(int i = alienEnermy.length/2; i <  alienEnermy.length; i++) {
            alienEnermy[i] = new AlienEnermy(50,50,"wander","stop");
            int ex = Greenfoot.getRandomNumber(getWidth());
            //int ey = Greenfoot.getRandomNumber(getHeight() - 300);
            addObject(alienEnermy[i], ex, 0);
            HpDecorator alienEnermy_hp = new HpDecorator(alienEnermy[i],alienEnermy[i].hp,alienEnermy[i].MAX_HP,0,alienEnermy[i].size_x-10,alienEnermy[i].size_x,10);
            addObject(alienEnermy_hp, 0, 0);
        }
        

    }
    
    public void RandomGenerateUFO() {
        int z = Greenfoot.getRandomNumber(10);
        int ex = Greenfoot.getRandomNumber(getWidth());
        
 
        if(z == 1) //delete or comment out this line to make it always add after (threshold) game ticks
        {   UfoEnermy ufoEnermy = new UfoEnermy(80,80,"wander","bullet");
            addObject(ufoEnermy,ex,50);
            HpDecorator ufoEnermy_hp = new HpDecorator(ufoEnermy,ufoEnermy.hp,ufoEnermy.MAX_HP,0,ufoEnermy.size_x-10,ufoEnermy.size_x,10);
            addObject(ufoEnermy_hp, 0, 0);
        }
    }
    
    public void act(){
        a++;
        if (a == threshold) {
            RandomGenerateUFO();
            a = 0;
        }
        
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
