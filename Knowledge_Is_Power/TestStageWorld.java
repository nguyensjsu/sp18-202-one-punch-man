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
        playerCreate("board.jpg","board.jpg","board.jpg","board.jpg");
        /* create player UI */
        playerUICreate();
        /* create enermy */
        enermyCreate();
        /* create exit */
        exitCreate();
    }
    
    public void enermyCreate(){
        /* create 1 stand enermies that shots bullets */
        TestEnermy enermy = new TestEnermy(50,50,"stop","bullet");
        addObject(enermy, 400, 250);
        HpDecorator enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);
    
        /* create 4 stand enermy that does not attack */
        enermy = new TestEnermy(50,50,"stop","stop");
        addObject(enermy, 1200, 75);
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
        enermy_hp = new HpDecorator(enermy,enermy.hp,enermy.MAX_HP,0,enermy.size_x-10,enermy.size_x,10);   //hp 20-20, offset(0,40), size 50*10
        addObject(enermy_hp, 0, 0);
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
            Greenfoot.setWorld(new MobStageWorld());
        }
    }
}
