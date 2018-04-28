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
            playerCreate("board.jpg","board.jpg","board.jpg","board.jpg");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f2")){
            playerUIRemove();
            removeObject(player);
            player = new Darwin();
            playerCreate("board.jpg","board.jpg","board.jpg","board.jpg");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f3")){
            playerUIRemove();
            removeObject(player);
            player = new Newton();
            playerCreate("board.jpg","board.jpg","board.jpg","board.jpg");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f4")){
            playerUIRemove();
            removeObject(player);
            player = new Tesla();
            playerCreate("board.jpg","board.jpg","board.jpg","board.jpg");
            /* create player UI */
            playerUICreate();
        }
        if(Greenfoot.isKeyDown("f5")){
            playerUIRemove();
            removeObject(player);
            player = new DrP();
            playerCreate("DrP_head.jpg","yellow circle.jpg","red circle.png","DrPUltIcon.png");
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
