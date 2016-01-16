package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.Robot;
import java.util.Vector;

import static battlecode.common.Direction.*;

/**
 * Created by ple15 on 15/01/16.
 */
public class Scout {

    private static RobotController rc;
    private static Robot robot;

    public RobotInfo[] seenTeammates;
    public RobotInfo[] seenOpponents;
    public RobotInfo[] seenZombies;

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
        sense_map();
        moveInRandomVector();


    }

    /*Redherring strategy
    * find enemies
    * attempt to draw them into the enemy's base*/
    public void runScoutStrategy2() throws GameActionException{
        sense_map();
        if(isAttractingZombies()){

            if(seenOpponents != null && seenOpponents.length > 0){
                robot.targetMoveLoc = averageOpponentLoc();
                robot.move();
            }else if(seenTeammates != null && seenTeammates.length > 0){
                moveAwayFromTeam();
            }else{
                moveAwayFromArchon();
            }

        }else{
            if(seenZombies != null && seenZombies.length > 0){
                robot.targetMoveLoc = averageZombieLoc();
                robot.nav.move();
            }else{
                moveAwayFromArchon();
            }
        }


    }

    /**************************************END STRATEGY METHODS**************************************************/


    /*************************************CLASS SPECIFIC BEHAVIOR************************************************/
    private void sense_map() throws GameActionException {
        //Sense map
        robot.fight.spotEnemies();
        spotTeammates();
        spotOpponent();
        spotZombies();
        //Update knowledge
        updateArchonLocations();
        updateEnemyArchonLocations();
        updateZombieDenLocations();
    }

    private void updateArchonLocations(){
        for(RobotInfo i:seenTeammates){
            if(i.type == RobotType.ARCHON){
                if(!archonLocations.contains(i)){
                    archonLocations.add(i);
                }
            }
        }
    }

    private void updateEnemyArchonLocations(){
        for(RobotInfo i:seenOpponents){
            if(i.type == RobotType.ARCHON){
                if(!enemyArchonLocations.contains(i)){
                    enemyArchonLocations.add(i);
                }
            }
        }
    }

    private void updateZombieDenLocations(){
        for(RobotInfo i:seenZombies){
            if(i.type == RobotType.ZOMBIEDEN){
                if(!zombieDenLocations.contains(i)){
                    zombieDenLocations.add(i);
                }
            }
        }

    }

    public MapLocation averageArchonLoc() throws GameActionException{
        if(archonLocations.size() > 0 && archonLocations != null){
            int x = 0;
            int y = 0;
            for(RobotInfo i: archonLocations){
                x+=i.location.x;
                y+=i.location.y;
            }
            Math.round(x/=archonLocations.size());
            Math.round(y/=archonLocations.size());
            return new MapLocation(x,y);
        }else{
            return rc.getLocation();
        }
    }

    public void moveAwayFromArchon()throws GameActionException{
        if(rc.isCoreReady()){
            if(archonLocations != null && archonLocations.size() > 0){
                robot.targetMoveLoc = averageArchonLoc();
                robot.targetMoveLoc = new MapLocation((2 * rc.getLocation().x) - robot.targetMoveLoc.x, (2 * rc.getLocation().y)
                        - robot.targetMoveLoc.y);

                robot.nav.move();
            }

        }
    }

    public boolean isAttractingZombies() throws GameActionException {

        if(seenZombies != null && seenZombies.length>0){

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

    /***********************************TO MERGE INTO BEHAVIOR MODULES*******************************************/

    /**********NAV BEHAVIOR*********/

    private void moveInRandomVector() throws GameActionException {
        if(currentVector == null){
            currentVector = Direction.values()[robot.rand.nextInt(8)];
        }
        if(rc.isCoreReady()){
            if(rc.canMove(currentVector)){
                rc.move(currentVector);
            }else{
                robot.nav.randMove();
            }
        }
    }

    public void moveAwayFromTeam()throws GameActionException{
        if(rc.isCoreReady()){
            if(seenTeammates != null && seenTeammates.length > 0){
                robot.targetMoveLoc = averageTeammateLoc();
                robot.targetMoveLoc = new MapLocation((2 * rc.getLocation().x) - robot.targetMoveLoc.x, (2 * rc.getLocation().y)
                        - robot.targetMoveLoc.y);

                robot.nav.moveToTarget(robot.targetMoveLoc);
            }

        }
    }

    /**********FIGHT BEHAVIOR*********/

    public void spotTeammates() throws GameActionException{
        seenTeammates = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam());
    }

    public void spotOpponent() throws GameActionException{
        seenOpponents = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,rc.getTeam().opponent());
    }

    public void spotZombies() throws GameActionException{
        seenZombies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared,Team.ZOMBIE);
    }

    public MapLocation averageTeammateLoc() throws GameActionException{

        if(seenTeammates.length == 0 || seenTeammates == null){
            return rc.getLocation();
        }else{
            int x = 0;
            int y = 0;
            for(RobotInfo i:seenTeammates){
                x+=i.location.x;
                y+=i.location.y;
            }
            Math.round(x/=seenTeammates.length);
            Math.round(y/=seenTeammates.length);
            return new MapLocation(x,y);
        }
    }

    public MapLocation averageOpponentLoc() throws GameActionException{

        if(seenOpponents.length == 0 || seenOpponents == null){
            return rc.getLocation();
        }else{
            int x = 0;
            int y = 0;
            for(RobotInfo i:seenOpponents){
                x+=i.location.x;
                y+=i.location.y;
            }
            Math.round(x/=seenOpponents.length);
            Math.round(y/=seenOpponents.length);
            return new MapLocation(x,y);
        }
    }

    public MapLocation averageZombieLoc() throws GameActionException{

        if(seenZombies.length == 0 || seenZombies == null){
            return rc.getLocation();
        }else{
            int x = 0;
            int y = 0;
            for(RobotInfo i:seenZombies){
                x+=i.location.x;
                y+=i.location.y;
            }
            Math.round(x/=seenZombies.length);
            Math.round(y/=seenZombies.length);
            return new MapLocation(x,y);
        }
    }




}
