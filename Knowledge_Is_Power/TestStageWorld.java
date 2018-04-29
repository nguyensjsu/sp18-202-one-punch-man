import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import static java.lang.Math.*;

/**
 * Write a description of class TestStageWorld here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class TestStageWorld extends BaseWorld
{
    public TestStageWorld(){
        super();
    }

    public void prepare(){
        /* create player */
        player = new Hawking(); //init player
        playerCreate("hawking_face_right.png","hawking_skill1.png","hawking_skill2.png","hawking_skill3.png");
        /* create player UI */
        playerUICreate();
        /* create enermy */
        enermyCreate();
        /* create exit */
        exitCreate();
    }

    public void enermyCreate(){
        /* create 1 stand enermies that shots bullets */
        TestEnermy enermy = new TestEnermy(75,75,"stop","bullet");
        addObject(enermy, 450, 300);
        HpDecorator enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);

        /* create 4 stand enermy that does not attack */
        enermy = new TestEnermy(75,75,"stop","stop");
        addObject(enermy, 1150, 125);
        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);


        enermy = new TestEnermy(50,50,"stop","stop");
        addObject(enermy, 1050, 250);
        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);

        enermy = new TestEnermy(50,50,"stop","stop");
        addObject(enermy, 1350, 250);
        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);

        enermy = new TestEnermy(50,50,"stop","stop");
        addObject(enermy, 1200, 425);

        enermy = new TestEnermy(75,75,"stop","stop");
        addObject(enermy, 1000, 300);
        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);

        enermy = new TestEnermy(75,75,"stop","stop");
        addObject(enermy, 1300, 300);
        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);

        enermy = new TestEnermy(75,75,"stop","stop");
        addObject(enermy, 1150, 475);

        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);
    }

    public void act(){
        /* player selection */
        if(Greenfoot.isKeyDown("f1")){
            playerUIRemove();
            removeObject(player);
            player = new Hawking();
            playerCreate("hawking_face_right.png","hawking_skill1.png","hawking_skill2.png","hawking_skill3.png");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f2")){
            playerUIRemove();
            removeObject(player);
            player = new Darwin();
            playerCreate("darwin.png","evolution_monkey.png","evolution_human.png","programmer.png");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f3")){
            playerUIRemove();
            removeObject(player);
            player = new Newton();
            playerCreate("Newton_icon.png","appleSatellite_cd.jpg","prism_cd.jpg","appleRain_cd.png");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f4")){
            playerUIRemove();
            removeObject(player);
            player = new Tesla();
            playerCreate("tesla_full.gif","thunder_explode_display.jpeg","tesla_tower.png","tesla_car_display.jpg");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f5")){
            playerUIRemove();
            removeObject(player);
            player = new DrP();
            playerCreate("DrP_head.jpg","yellow circle.png","red circle.png","DrPUltIcon.png");
            /* create player UI */
            playerUICreate();
        }

        /* Enermy Refresh */
         if(Greenfoot.isKeyDown("r")){
            removeObjects(getObjects(Enermy.class));
            enermyCreate();
        }

        /* enter exit to mob stage */
        if (player.getX()>700 && player.getX()<900 && player.getY()<50){
            Greenfoot.setWorld(new MobStageWorld());
        }
    }
}
