import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class TestStageTwoWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TestStageTwoWorld extends TestStageWorld
{
    public TestStageTwoWorld()
    {
        super();
        player.hp = player.MAX_HP;
        
        addObject(new ScreenChange("black to trans",300),800,450);
        BaseWorld.BGM = new GreenfootSound("res.mp3");
        BaseWorld.BGM.play();
        BaseWorld.BGM.setVolume(20);
    }
    
    public void prepare(){
        /* create player */
        addObject(player, 800, 850);
        /* create player UI */
        playerUICreate();
        /* create enermy */
        enermyCreate();
        /* create exit */
        exitCreate();
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
            BaseWorld.BGM.stop();
            BaseWorld.BGM = bossBGM;
            Greenfoot.setWorld(new BossStageWorld());
        }
    }
}
