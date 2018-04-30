import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Random;
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
  private int ufo_timer = 250;
  private int ufo_stopTime = 1000;
  private int alienEnermy_timer = 250;
  private int alienEnermy_stopTime = 1000;

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
    // AlienEnermy[] alienEnermy = new AlienEnermy[10];
    // for(int i = 0; i < alienEnermy.length/2; i++) {
    //     alienEnermy[i] = new AlienEnermy(50,50,"chase","bullet");
    //     int ex = Greenfoot.getRandomNumber(getWidth());
    //     int ey = Greenfoot.getRandomNumber(getHeight() - 300);
    //     addObject(alienEnermy[i], ex, ey);
    // }
    //
    // for(int i = alienEnermy.length/2; i <  alienEnermy.length; i++) {
    //     alienEnermy[i] = new AlienEnermy(50,50,"wander","stop");
    //     int ex = Greenfoot.getRandomNumber(getWidth());
    //     //int ey = Greenfoot.getRandomNumber(getHeight() - 300);
    //     addObject(alienEnermy[i], ex, 0);
    // }

    AlienEnermy enermy = new AlienEnermy(50,50,"chase","bullet");
    addObject(enermy, 100, 400);
    HpDecorator enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);
    addObject(enermy_hp, 0, 0);
    enermy = new AlienEnermy(50,50,"chase","bullet");
    addObject(enermy, 800, 150);
    enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);
    addObject(enermy_hp, 0, 0);
    enermy = new AlienEnermy(50,50,"chase","bullet");
    addObject(enermy, 1500, 400);
    enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);
    addObject(enermy_hp, 0, 0);


    UfoEnermy ufo = new UfoEnermy();
    addObject(ufo,200,55);
    HpDecorator ufo_hp = new HpDecorator(ufo,ufo.hp,ufo.MAX_HP,0,ufo.size_x-10,ufo.size_x,10);
    addObject(ufo_hp, 0, 0);
    ufo = new UfoEnermy();
    addObject(ufo,800,55);
    ufo_hp = new HpDecorator(ufo,ufo.hp,ufo.MAX_HP,0,ufo.size_x-10,ufo.size_x,10);
    addObject(ufo_hp, 0, 0);
    ufo = new UfoEnermy();
    addObject(ufo,1400,55);
    ufo_hp = new HpDecorator(ufo,ufo.hp,ufo.MAX_HP,0,ufo.size_x-10,ufo.size_x,10);
    addObject(ufo_hp, 0, 0);

  }

  public void RandomGenerateAlienEnermy() {
    Random rand = new Random();
    int y = rand.nextInt(400);
    int side = rand.nextInt(2);
    AlienEnermy enermy = new AlienEnermy(50,50,"chase","bullet");
    HpDecorator enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);
    switch(side){
      case 0:
      enermy = new AlienEnermy(50,50,"chase","bullet");
      addObject(enermy, 20, y);
      enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);
      addObject(enermy_hp, 0, 0);
      break;
      case 1:
      enermy = new AlienEnermy(50,50,"chase","bullet");
      addObject(enermy, 1580, y);
      enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);
      addObject(enermy_hp, 0, 0);
      break;
      default:
      break;
    }
  }

  public void RandomGenerateUFO() {
    Random rand = new Random();
    int x = rand.nextInt(getWidth());
    UfoEnermy ufo = new UfoEnermy();
    addObject(ufo,x,50);
    HpDecorator ufo_hp = new HpDecorator(ufo,ufo.hp,ufo.MAX_HP,0,ufo.size_x-10,ufo.size_x,10);
    addObject(ufo_hp, 0, 0);
  }

  public void act(){
    if (ufo_timer == 0 && ufo_stopTime >= 0) {
      RandomGenerateUFO();
      ufo_timer = 250;
    }

    if (alienEnermy_timer == 0 && alienEnermy_stopTime >= 0) {
      RandomGenerateAlienEnermy();
      alienEnermy_timer = 250;
    }

    timer();

    if (getObjects(Enermy.class).isEmpty()){    //total enermy in this stage
        if (!exit_shown){
            exitCreate();
            exit_shown = true;
        }
    /* enter exit to boss stage */
    if (player.getX()>700 && player.getX()<900 && player.getY()<50 && getObjects(Enermy.class).isEmpty()){
      BaseWorld.BGM.stop();
      Greenfoot.setWorld(new TestStageTwoWorld());
    }
  }

}
public void timer(){
    if (ufo_timer != 0) ufo_timer--;
    if (ufo_stopTime != 0) ufo_stopTime--;
    if (alienEnermy_timer != 0) alienEnermy_timer--;
    if (alienEnermy_stopTime != 0) alienEnermy_stopTime--;
  }
}
