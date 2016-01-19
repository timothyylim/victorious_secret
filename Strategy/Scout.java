package victorious_secret.Strategy;

import battlecode.common.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import victorious_secret.Robot;

import java.util.Random;
import java.util.Vector;

import static battlecode.common.Direction.*;

/**
 * Created by ple15 on 15/01/16.
 */
public class Scout {

    private static RobotController rc;
    private static Robot robot;
    private static Flee flee;


    public Vector<RobotInfo> archonLocations;
    public Vector<RobotInfo> zombieDenLocations;
    public Vector<RobotInfo> enemyArchonLocations;

    /*Scout Strategy 3 Variables: Turrent information*/
    public MapLocation turretLoc;



    MapLocation[] corners = {new MapLocation(9999,9999),new MapLocation(-9999,9999),new MapLocation(9999,-9999),new MapLocation(-9999,-9999)};



    public Scout(RobotController _rc, Robot _robot) {
        rc = _rc;
        robot = _robot;

        flee = new Flee();
        flee.initialiseFlee(rc);

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

    /*Turret Strategy
    * don't move
    * scan for enemies
    * pass enemy locations to turret*/
    public void runScoutStrategy3() throws GameActionException{
        sense_map();
        turretLoc = findClosestRobot(robot.fight.seenAllies,RobotType.TURRET);
        flee.setTarget(turretLoc);

        if(rc.isCoreReady()){
            broadcastEnemyInTurretBlindSpot();
        }

        if(rc.isCoreReady()){
            rc.move(flee.getNextMove());
        }

    }

    /**************************************END STRATEGY METHODS**************************************************/


    /*************************************CLASS SPECIFIC BEHAVIOR************************************************/
    private void sense_map() throws GameActionException {
        //Sense map
        robot.fight.spotEnemies();
        robot.fight.spotOpponents();
        robot.fight.spotZombies();
        robot.fight.spotAllies();

        //Sense terrain

        //Update knowledge
        updateArchonLocations();
        updateEnemyArchonLocations();
        updateZombieDenLocations();
    }

   private void broadcastEnemyInTurretBlindSpot() throws GameActionException{

       if(robot.fight.seenEnemies != null && robot.fight.seenEnemies.length >0){
           MapLocation loc = enemyInTurretBlindSpot(robot.fight.seenEnemies);

           if(loc != null){
               if(rc.isCoreReady()){
                   rc.broadcastMessageSignal(loc.x,loc.y,rc.getType().sensorRadiusSquared);

               }

           }

       }
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

    /*
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
*/
    /*********************************END CLASS SPECIFIC BEHAVIOR************************************************/

    //Returns the first enemy in the robots array that is within the turret's blindspot
    public MapLocation enemyInTurretBlindSpot(RobotInfo[] robots) {

        if(robots != null && robots.length>0){

            if(turretLoc != null){

                for(RobotInfo i : robots){
                    int dist = i.location.distanceSquaredTo(turretLoc);

                    if(dist < RobotType.TURRET.attackRadiusSquared && dist > RobotType.TURRET.sensorRadiusSquared) {
                        return i.location;

                    }
                }

            }
        }
        return null;
    }

    public MapLocation findClosestRobot(RobotInfo[] robots, RobotType type){
        double minDistance = 9999999;
        RobotInfo closestTarget = null;


        for(RobotInfo i : robots)
        {
            if(i.type == type){
                int sqDist = i.location.distanceSquaredTo(rc.getLocation());
                if(sqDist  < minDistance)
                {
                    minDistance = sqDist;
                    closestTarget = i;
                }
            }
        }
        return closestTarget.location;
    }


}
