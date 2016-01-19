package victorious_secret.Strategy;

<<<<<<< HEAD
=======
import battlecode.common.*;
import victorious_secret.Robot;
import java.util.Vector;

import static battlecode.common.Direction.*;

>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0
/**
 * Created by ple15 on 15/01/16.
 */
public class Scout {
<<<<<<< HEAD
=======

    private static RobotController rc;
    private static Robot robot;


    public Vector<RobotInfo> archonLocations;
    public Vector<RobotInfo> zombieDenLocations;
    public Vector<RobotInfo> enemyArchonLocations;

    private Direction currentVector;

    public Scout(RobotController _rc, Robot _robot) {
        rc = _rc;
        robot = _robot;

        archonLocations = new Vector<RobotInfo>();
        zombieDenLocations = new Vector<RobotInfo>();
        enemyArchonLocations = new Vector<RobotInfo>();
    }

    /********************************************STRATEGY METHODS**********************************************

    /*Basic scouting behavior:
    * pick a random direction to explore
    * collect information about the map
    * go within signaling range of archon
    * broadcast signal*/
    public void runScoutStrategy1() throws GameActionException{



    }

    /*Redherring strategy
    * find enemies
    * attempt to draw them into the enemy's base*/
    public void runScoutStrategy2() throws GameActionException{



    }

    /**************************************END STRATEGY METHODS**************************************************/


    /*************************************CLASS SPECIFIC BEHAVIOR************************************************/
    private void sense_map() throws GameActionException {
        //Sense map
        robot.fight.spotEnemies();
        robot.fight.spotOpponents();
        robot.fight.spotZombies();
        robot.fight.spotAllies();

        //Update knowledge
        updateArchonLocations();
        updateEnemyArchonLocations();
        updateZombieDenLocations();
    }

    private void updateArchonLocations(){
        for(RobotInfo i:robot.fight.seenAllies){
            if(i.type == RobotType.ARCHON){
                if(!archonLocations.contains(i)){
                    archonLocations.add(i);
                }
            }
        }
    }

    private void updateEnemyArchonLocations(){
        for(RobotInfo i:robot.fight.seenOpponents){
            if(i.type == RobotType.ARCHON){
                if(!enemyArchonLocations.contains(i)){
                    enemyArchonLocations.add(i);
                }
            }
        }
    }

    private void updateZombieDenLocations(){
        for(RobotInfo i:robot.fight.seenZombies){
            if(i.type == RobotType.ZOMBIEDEN){
                if(!zombieDenLocations.contains(i)){
                    zombieDenLocations.add(i);
                }
            }
        }
    }

    public void moveAwayFromArchon()throws GameActionException{
        if(rc.isCoreReady()){
            if(archonLocations != null && archonLocations.size() > 0){
                robot.targetMoveLoc = robot.nav.averageLoc((MapLocation[]) archonLocations.toArray());
                robot.targetMoveLoc = new MapLocation((2 * rc.getLocation().x) - robot.targetMoveLoc.x, (2 * rc.getLocation().y)
                        - robot.targetMoveLoc.y);

                robot.nav.move();
            }
        }
    }

    public boolean isAttractingZombies() throws GameActionException {

        if(robot.fight.seenZombies != null && robot.fight.seenZombies.length>0){

            int distance = rc.getLocation().distanceSquaredTo(averageZombieLoc());
            if(distance < 10){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    /*********************************END CLASS SPECIFIC BEHAVIOR************************************************/



    public MapLocation averageTeammateLoc() throws GameActionException{

        return robot.nav.averageLoc(robot.fight.seenAllies);

    }

    public MapLocation averageOpponentLoc() throws GameActionException{

        return robot.nav.averageLoc(robot.fight.seenOpponents);
    }

    public MapLocation averageZombieLoc() throws GameActionException{

        return robot.nav.averageLoc(robot.fight.seenZombies);
    }




>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0
}
