package victorious_secret.Strategy;

import battlecode.common.*;
import org.junit.Test;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;

import static org.junit.Assert.*;

/**
 * Created by rm1715 on 01/03/16.
 */
public class DefendTest {

    @Test
    public void testTurtle() throws Exception {
        RobotController rc = new RobotController();
        Dummy dummy_robot = new Dummy(rc);
        rc.setLocation(new MapLocation(100,100));
        Defend def = new Defend(rc,dummy_robot);
        rc.setCoreReady(true);

        //-------- TURRET ---------
        rc.setType(RobotType.TURRET);

        // Nothing to attack, no signals,
        //Set the nearby robots
        RobotInfo[] robotInfo = new RobotInfo[4];
        robotInfo[0] = new RobotInfo(0,null, RobotType.TURRET,new MapLocation(100,100),0,0,0,50,100,0,0);
        robotInfo[1] = new RobotInfo(1,null, RobotType.TURRET,new MapLocation(101,101),0,0,0,50,100,0,0);
        robotInfo[2] = new RobotInfo(2,null, RobotType.ARCHON,new MapLocation(102,102),0,0,0,30,100,0,0);
        robotInfo[3] = new RobotInfo(3,null, RobotType.TURRET,new MapLocation(103,103),0,0,0,40,100,0,0);
        rc.setNearbyRobots(robotInfo);
        //Nothing to attack
        rc.setHostileRobots(new RobotInfo[0]);
        //Some signals from the Scout
        int ATTACK_X = 15151515;
        int ATTACK_Y = 14141414;
        Signal[] signals = new Signal[2];
        signals[0] = new Signal(null,0,Team.A,ATTACK_X,100);
        signals[1] = new Signal(null,0,Team.A,ATTACK_Y,101);
        rc.setSignals(signals);
        rc.setWeaponReady(true);
        def.turtle();
        assertFalse(rc.hasPacked());
        assertTrue(rc.hasAttacked());

        //Not on the chessboard
        robotInfo[2] = new RobotInfo(2,null, RobotType.ARCHON,new MapLocation(101,102),0,0,0,30,100,0,0);
        rc.setNearbyRobots(robotInfo);
        def.turtle();
        assertTrue(rc.hasPacked());

        //-------- ARCHON ---------

        rc = new RobotController();
        dummy_robot = new Dummy(rc);
        rc.setLocation(new MapLocation(200,200));
        def = new Defend(rc,dummy_robot);
        rc.setType(RobotType.ARCHON);
        rc.setRound(0);
        rc.setCanMove(Direction.values());
        rc.setCoreReady(true);
        rc.setNearbyRobots(robotInfo);

        //Initial archons on the map
        MapLocation initialArchons[] = new MapLocation[3];
        initialArchons[0] = new MapLocation(100,100);
        initialArchons[1] = new MapLocation(200,200);
        initialArchons[2] = new MapLocation(300,300);
        MapLocation initialEnemies[] = new MapLocation[3];
        initialEnemies[0] = new MapLocation(400,400);
        initialEnemies[1] = new MapLocation(500,500);
        initialEnemies[2] = new MapLocation(600,600);
        rc.setInitialEnemyArchonsLoc(initialEnemies);
        rc.setInitialFriendlyArchonsLoc(initialArchons);

        //Not leader
        def.turtle();
        assertTrue(rc.hasMoved());

        //Leader so it should repair the units
        rc.setLocation(new MapLocation(100,100));
        def.turtle();
        assertTrue(rc.hasRepaired());

        //-------- SOLDIER ---------
        rc = new RobotController();
        dummy_robot = new Dummy(rc);
        rc.setLocation(new MapLocation(200,200));
        def = new Defend(rc,dummy_robot);
        rc.setType(RobotType.SOLDIER);
        rc.setCanMove(new Direction[0]);
        rc.setCoreReady(true);
        rc.setNearbyRobots(robotInfo);
        rc.setRubble(1000);

        //No enemies, clearing rubble
        rc.setHostileRobots(new RobotInfo[0]);
        def.turtle();
        assertTrue(rc.hasCleared());

        //No rubble, circle archon
        rc.setCanMove(Direction.values());
        rc.setRubble(0);
        def.turtle();
        assertTrue(rc.hasMoved());

        //-------- TTM ---------
        rc = new RobotController();
        dummy_robot = new Dummy(rc);
        rc.setLocation(new MapLocation(200,200));
        def = new Defend(rc,dummy_robot);
        rc.setType(RobotType.TTM);
        rc.setCanMove(new Direction[0]);
        rc.setCoreReady(true);
        rc.setNearbyRobots(robotInfo);
        rc.setOccupiedLocations(new MapLocation[0]);

        def.turtle();




    }

    @Test
    public void testTryToMove() throws Exception {
        // Put a robot next to an empty space and try to move there
        RobotController rc = new RobotController();
        Dummy dummy_robot = new Dummy(rc);
        rc.setType(RobotType.SOLDIER);
        rc.setCoreReady(true);
        rc.setLocation(new MapLocation(100,100));
        rc.setCanMove(new Direction[0]);
        rc.setRubble(10000);
        Defend def = new Defend(rc,dummy_robot);
        def.tryToMove(Direction.EAST);
        assertTrue(rc.hasCleared());
    }

    @Test
    public void testOnChessBoard() throws Exception {
        MapLocation center = new MapLocation(100,100);
        MapLocation NE = new MapLocation(101,101);
        MapLocation SE = new MapLocation(101,99);
        MapLocation SW = new MapLocation(99,99);
        MapLocation NW = new MapLocation(99,101);

        assertTrue(Defend.onChessBoard(center,NE));
        assertTrue(Defend.onChessBoard(center,SE));
        assertTrue(Defend.onChessBoard(center,SW));
        assertTrue(Defend.onChessBoard(center,NW));
        assertFalse(Defend.onChessBoard(center,new MapLocation(105,108)));
    }

    @Test
    public void testCountUnits() throws Exception {
        RobotInfo[] robotInfo = new RobotInfo[10];
        robotInfo[0] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[1] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[2] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[3] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[4] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[5] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[6] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[7] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[8] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[9] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);

        assertEquals(Defend.countUnits(robotInfo,RobotType.ARCHON),6);
        assertEquals(Defend.countUnits(robotInfo,RobotType.SOLDIER),3);
        assertEquals(Defend.countUnits(robotInfo,RobotType.TURRET),1);
    }

    @Test
    public void testGetFurthestArchonFromEnemyLocation() throws Exception{
        MapLocation initialArchons[] = new MapLocation[3];
        initialArchons[0] = new MapLocation(100,100);
        initialArchons[1] = new MapLocation(200,200);
        initialArchons[2] = new MapLocation(300,300);
        MapLocation averageEnemyLoc = new MapLocation(600,600);

        MapLocation leaderLoc = Defend.getFurthestArchonFromEnemyLocation(initialArchons,averageEnemyLoc);
        assertEquals(leaderLoc.x,100);
        assertEquals(leaderLoc.y,100);
    }

    @Test
    public void testFindBetterChessBoardPosition() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.TURRET);

        MapLocation center = new MapLocation(100,100);
        MapLocation NE = new MapLocation(101,101);
        MapLocation SE = new MapLocation(101,99);
        MapLocation SW = new MapLocation(99,99);
        MapLocation NW = new MapLocation(99,101);
        MapLocation NN = new MapLocation(100,102);
        MapLocation SS = new MapLocation(100,98);
        MapLocation EE = new MapLocation(102,100);
        MapLocation WW = new MapLocation(98,100);
        MapLocation NWW = new MapLocation(98,102);
        MapLocation NEE = new MapLocation(102,102);
        MapLocation SWW = new MapLocation(98,98);
        MapLocation SEE = new MapLocation(102,98);

        MapLocation[] occupied = {center,NE,SE,SW,NN,NW,SS,EE,NWW,NEE,SWW,SEE};
        rc.setOccupiedLocations(occupied);

        rc.setLocation(center);
        Defend def = new Defend(rc, new Robot() {
            @Override
            public void move() throws GameActionException {

            }
        });
        MapLocation loc = def.findBetterChessBoardPosition(center);
        assertEquals(loc.x,WW.x);
        assertEquals(loc.y,WW.y);

//        MapLocation[] occupiedV2 = {center,NE,SE,SW,NN,NW,SS,EE,NWW,NEE,SWW,SEE,WW};
//        rc.setOccupiedLocations(occupiedV2);
//        MapLocation locV2 = def.findBetterChessBoardPosition(center);
//        assertNull(locV2);
    }

    @Test
    public void testGet_archon_index() throws Exception {
        RobotInfo[] robotInfo = new RobotInfo[10];
        robotInfo[0] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[1] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[2] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[3] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[4] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[5] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[6] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[7] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[8] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[9] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);

        int index = Defend.get_archon_index(robotInfo);
        assertEquals(index,2);
    }


    @Test
    public void testLookForEnemies() throws Exception{
        RobotController rc = new RobotController();
        rc.setLocation(new MapLocation(100,100));
        Defend def = new Defend(rc, new Robot() {
            @Override
            public void move() throws GameActionException {

            }
        });
        rc.setWeaponReady(true);

        //For a turret
        rc.setType(RobotType.TURRET);

        //create the list of enemies
        RobotInfo[] robotInfo = new RobotInfo[10];
        robotInfo[0] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[1] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[2] = new RobotInfo(0,null, RobotType.BIGZOMBIE,null,0,0,0,0,0,0,0);
        robotInfo[3] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        robotInfo[4] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[5] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[6] = new RobotInfo(0,null, RobotType.SOLDIER,null,0,0,0,0,0,0,0);
        robotInfo[7] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[8] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        robotInfo[9] = new RobotInfo(0,null, RobotType.ARCHON,null,0,0,0,0,0,0,0);
        rc.setHostileRobots(robotInfo);

        assertTrue(def.lookForEnemies());

        //For a soldier
        rc.setType(RobotType.SOLDIER);
        assertTrue(def.lookForEnemies());

        //Without a bigzombie
        robotInfo[2] = new RobotInfo(0,null, RobotType.TURRET,null,0,0,0,0,0,0,0);
        rc.setHostileRobots(robotInfo);
        assertTrue(def.lookForEnemies());

        //If there is no enemies to kill
        rc.setHostileRobots(new RobotInfo[0]);
        assertFalse(def.lookForEnemies());

        //If the weapon is not ready
        rc.setWeaponReady(false);
        assertFalse(def.lookForEnemies());


    }

    @Test
    public void testFindWeakest() throws Exception{
        RobotInfo[] robotInfo = new RobotInfo[10];
        robotInfo[0] = new RobotInfo(0,null, RobotType.TURRET,new MapLocation(100,100),0,0,0,50,100,0,0);
        robotInfo[1] = new RobotInfo(1,null, RobotType.TURRET,new MapLocation(101,101),0,0,0,50,100,0,0);
        robotInfo[2] = new RobotInfo(2,null, RobotType.ARCHON,new MapLocation(102,102),0,0,0,30,100,0,0);
        robotInfo[3] = new RobotInfo(3,null, RobotType.TURRET,new MapLocation(103,103),0,0,0,40,100,0,0);
        robotInfo[4] = new RobotInfo(4,null, RobotType.SOLDIER,new MapLocation(104,104),0,0,0,50,100,0,0);
        robotInfo[5] = new RobotInfo(5,null, RobotType.SOLDIER,new MapLocation(105,105),0,0,0,60,100,0,0);
        robotInfo[6] = new RobotInfo(6,null, RobotType.SOLDIER,new MapLocation(106,106),0,0,0,70,100,0,0);
        robotInfo[7] = new RobotInfo(7,null, RobotType.ARCHON,new MapLocation(107,107),0,0,0,80,100,0,0);
        robotInfo[8] = new RobotInfo(8,null, RobotType.ARCHON,new MapLocation(108,108),0,0,0,90,100,0,0);
        robotInfo[9] = new RobotInfo(9,null, RobotType.ARCHON,new MapLocation(109,109),0,0,0,100,100,0,0);

        MapLocation where = Defend.findWeakest(robotInfo);
        assertEquals(where.x,103);
        assertEquals(where.y,103);
    }

    @Test
    public void testGet_out_corner() throws Exception{
        RobotController rc = new RobotController();
        rc.setLocation(new MapLocation(100,100));
        Dummy dummy_robot = new Dummy(rc);
        rc.setCoreReady(true);

        //Generate 5 directions (in a corner)
        Direction possible[] = new Direction[5];
        Direction all[] = Direction.values();
        for(int i = 0;i<5;i++){
            possible[i] = all[i];
        }

        rc.setCanMove(possible);
        Defend def = new Defend(rc,dummy_robot);
        assertTrue(def.get_out_corner());

        //Not in a corner
        rc.setCanMove(new Direction[0]);
        assertFalse(def.get_out_corner());



    }

    @Test
    public void testReceiveTargetLocation() throws Exception{
        int ATTACK_X = 15151515;
        int ATTACK_Y = 14141414;
        RobotController rc = new RobotController();
        Dummy dummy_bot = new Dummy(rc);
        rc.setLocation(new MapLocation(100,100));
        Defend def = new Defend(rc,dummy_bot);

        Signal[] signals = new Signal[4];
        signals[0] = new Signal(null,0,Team.A,ATTACK_X,100);
        signals[1] = new Signal(null,0,Team.A,ATTACK_Y,101);
        signals[2] = new Signal(null,0,Team.B,ATTACK_Y,101);
        signals[3] = new Signal(null,0,Team.A);
        rc.setSignals(signals);
        assertTrue(def.receiveTargetLocation());
    }

    @Test
    public void testBuildUnits() throws Exception{
        RobotController rc = new RobotController();
        Dummy dummy_bot = new Dummy(rc);
        rc.setLocation(new MapLocation(100,100));
        rc.setType(RobotType.ARCHON);
        Defend def = new Defend(rc,dummy_bot);

        // Enough parts and soldiers < 5
        RobotInfo[] robotInfo = new RobotInfo[10];
        robotInfo[0] = new RobotInfo(0,null, RobotType.TURRET,new MapLocation(100,100),0,0,0,50,100,0,0);
        robotInfo[1] = new RobotInfo(1,null, RobotType.TURRET,new MapLocation(101,101),0,0,0,50,100,0,0);
        robotInfo[2] = new RobotInfo(2,null, RobotType.SOLDIER,new MapLocation(102,102),0,0,0,30,100,0,0);
        robotInfo[3] = new RobotInfo(3,null, RobotType.TURRET,new MapLocation(103,103),0,0,0,40,100,0,0);
        robotInfo[4] = new RobotInfo(4,null, RobotType.SOLDIER,new MapLocation(104,104),0,0,0,50,100,0,0);
        robotInfo[5] = new RobotInfo(5,null, RobotType.SOLDIER,new MapLocation(105,105),0,0,0,60,100,0,0);
        robotInfo[6] = new RobotInfo(6,null, RobotType.SOLDIER,new MapLocation(106,106),0,0,0,70,100,0,0);
        robotInfo[7] = new RobotInfo(7,null, RobotType.ARCHON,new MapLocation(107,107),0,0,0,80,100,0,0);
        robotInfo[8] = new RobotInfo(8,null, RobotType.ARCHON,new MapLocation(108,108),0,0,0,90,100,0,0);
        robotInfo[9] = new RobotInfo(9,null, RobotType.ARCHON,new MapLocation(109,109),0,0,0,100,100,0,0);
        rc.setNearbyRobots(robotInfo);
        rc.setParts(1000);
        assertTrue(def.buildUnits());

        //Build a scout when turretNumber % 7 == 0
        robotInfo[0] = new RobotInfo(0,null, RobotType.GUARD,new MapLocation(100,100),0,0,0,50,100,0,0);
        robotInfo[1] = new RobotInfo(1,null, RobotType.GUARD,new MapLocation(101,101),0,0,0,50,100,0,0);
        robotInfo[2] = new RobotInfo(2,null, RobotType.SOLDIER,new MapLocation(102,102),0,0,0,30,100,0,0);
        robotInfo[3] = new RobotInfo(3,null, RobotType.SOLDIER,new MapLocation(103,103),0,0,0,40,100,0,0);
        robotInfo[4] = new RobotInfo(4,null, RobotType.SOLDIER,new MapLocation(104,104),0,0,0,50,100,0,0);
        robotInfo[5] = new RobotInfo(5,null, RobotType.SOLDIER,new MapLocation(105,105),0,0,0,60,100,0,0);
        robotInfo[6] = new RobotInfo(6,null, RobotType.SOLDIER,new MapLocation(106,106),0,0,0,70,100,0,0);
        robotInfo[7] = new RobotInfo(7,null, RobotType.ARCHON,new MapLocation(107,107),0,0,0,80,100,0,0);
        robotInfo[8] = new RobotInfo(8,null, RobotType.ARCHON,new MapLocation(108,108),0,0,0,90,100,0,0);
        robotInfo[9] = new RobotInfo(9,null, RobotType.ARCHON,new MapLocation(109,109),0,0,0,100,100,0,0);
        rc.setNearbyRobots(robotInfo);
        assertTrue(def.buildUnits());

        //Otherwise build a turret
        robotInfo[0] = new RobotInfo(0,null, RobotType.GUARD,new MapLocation(100,100),0,0,0,50,100,0,0);
        robotInfo[1] = new RobotInfo(1,null, RobotType.TURRET,new MapLocation(101,101),0,0,0,50,100,0,0);
        robotInfo[2] = new RobotInfo(2,null, RobotType.SOLDIER,new MapLocation(102,102),0,0,0,30,100,0,0);
        robotInfo[3] = new RobotInfo(3,null, RobotType.SOLDIER,new MapLocation(103,103),0,0,0,40,100,0,0);
        robotInfo[4] = new RobotInfo(4,null, RobotType.SOLDIER,new MapLocation(104,104),0,0,0,50,100,0,0);
        robotInfo[5] = new RobotInfo(5,null, RobotType.SOLDIER,new MapLocation(105,105),0,0,0,60,100,0,0);
        robotInfo[6] = new RobotInfo(6,null, RobotType.SOLDIER,new MapLocation(106,106),0,0,0,70,100,0,0);
        robotInfo[7] = new RobotInfo(7,null, RobotType.ARCHON,new MapLocation(107,107),0,0,0,80,100,0,0);
        robotInfo[8] = new RobotInfo(8,null, RobotType.ARCHON,new MapLocation(108,108),0,0,0,90,100,0,0);
        robotInfo[9] = new RobotInfo(9,null, RobotType.ARCHON,new MapLocation(109,109),0,0,0,100,100,0,0);
        rc.setNearbyRobots(robotInfo);
        assertTrue(def.buildUnits());

        // Not enough team parts
        rc.setParts(0);
        assertFalse(def.buildUnits());


    }

}