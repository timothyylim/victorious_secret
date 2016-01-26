
package victorious_secret.Strategy;

import battlecode.common.*;

import victorious_secret.Robot;
import victorious_secret.Behaviour.Nav;

import java.util.Vector;

/**
 * Created by ple15 on 15/01/16.
 */
public class Scout {
    private static RobotController rc;
    private static Robot robot;
    private static Flee flee;

    private static int ATTACK_X = 15151515;
    private static int ATTACK_Y = 14141414;


    public Vector<RobotInfo> archonLocations;
    public Vector<RobotInfo> zombieDenLocations;
    public Vector<RobotInfo> enemyArchonLocations;

    /*Scout Strategy 3 Variables: Turrent information*/
    public MapLocation turretLoc;
    
    /*Scout Strategy 5 Variables: Swarm Strategy*/
    double strength_needed = 200;
    boolean has_strength = false;


    MapLocation[] corners = {new MapLocation(9999, 9999), new MapLocation(-9999, 9999), new MapLocation(9999, -9999), new MapLocation(-9999, -9999)};


    public Scout(RobotController _rc, Robot _robot) {
        rc = _rc;
        robot = _robot;

        flee = new Flee();
        flee.initialiseFlee(rc);

        archonLocations = new Vector<RobotInfo>();
        zombieDenLocations = new Vector<RobotInfo>();
        enemyArchonLocations = new Vector<RobotInfo>();
        
        robot.setArchonLocations();

    }

    /********************************************
     * STRATEGY METHODS**********************************************
     * <p>
     * /*Basic scouting behavior:
     * pick a random direction to explore
     * collect information about the map
     * go within signaling range of archon
     * broadcast signal
     */
    public void runScoutStrategy1() throws GameActionException {


    }

    /*Redherring strategy
    * find enemies
    * attempt to draw them into the enemy's base*/
    public void runScoutStrategy2() throws GameActionException {


    }

    /*Turret Strategy
    * don't move
    * scan for enemies
    * pass enemy locations to turret*/
    public void runScoutStrategy3() throws GameActionException {
        sense_map();
        turretLoc = findClosestRobot(robot.fight.seenAllies, RobotType.TURRET);

        if(turretLoc != null){
        	flee.target = turretLoc;
        }else{
        	flee.target = rc.getLocation();
        }
        
        
        if (rc.isCoreReady()) {
            broadcastEnemyInTurretBlindSpot();
        }

        if (rc.isCoreReady()) {
            Direction dir = flee.getNextMove();
            if(rc.canMove(dir)){
                rc.move(dir);
            }
        }

    }

    /*Swarm Assist Strategy:
    * scan for teammates
    * if enough units are close by, explore the map
    * if enemy units are seen call soldiers*/
    public void runScoutStrategy4() throws GameActionException {
        double attackPowerNeeded = 550;
        double attackPowerOfSeenAllies;
        sense_map();
        attackPowerOfSeenAllies = strengthOfRobotsInArray(robot.fight.seenAllies);

        if(attackPowerOfSeenAllies>attackPowerNeeded){
            flee.target = rc.getInitialArchonLocations(rc.getTeam().opponent())[1];
            if (rc.isCoreReady()) {
                Direction dir = flee.getNextMove();
                if(rc.canMove(dir)){
                    rc.move(flee.getNextMove());
                }
            }

        }
        MapLocation loc = rc.getLocation();
        int broadcastRange = rc.getLocation().distanceSquaredTo(rc.getInitialArchonLocations(rc.getTeam())[0]);
        rc.broadcastMessageSignal(loc.x,loc.y,broadcastRange);


    }
    
    /*Swarm Assist Strategy 2: Improvement on runScoutStrategy4()
     * */
    public void runScoutStrategy5() throws GameActionException{
    	sense_map();
    	
    	//If can see friendly archon and doesn't have required strength
    	if(!has_strength){
    		check_strength();
    	}
    	if(has_strength && !can_see_enemy()){
    		move_towards_enemy();
    	}
    	if(can_see_enemy() && !can_see_army()){
    		call_for_help();
    	}if(can_see_enemy() && can_see_army()){
    		call_for_help();
    		//runScoutStrategy3();
    	}
    	
    	
    }
    
    /**********************SCOUT STRATEGY 5 HELPER METHODS***********************/
    private boolean can_see_archon(){
    	if(can_see_robot_type(RobotType.ARCHON, robot.fight.seenEnemies, rc.getTeam())){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean can_see_enemy(){
    	if(robot.fight.seenEnemies != null && robot.fight.seenEnemies.length>0){
    		return true;
    	}else return false;
    }
    
    private boolean can_see_army(){
    	if(robot.fight.seenAllies != null && robot.fight.seenAllies.length>0){
    		return true;
    	}else return false;
    }
    
    private void move_towards_enemy() throws GameActionException{
    	
    	MapLocation loc = robot.fight.findClosestMapLocation(robot.enemyArchonLocations.values(), rc.getLocation());
    	Flee.setTarget(loc);
    	if (rc.isCoreReady()) {
            Direction dir = flee.getNextMove();
            if(rc.canMove(dir)){
                rc.move(flee.getNextMove());
            }
        }
        locationClear(loc);
    }
    
    private void call_for_help() throws GameActionException{
    	MapLocation loc = robot.fight.findLowestHealthEnemy(robot.fight.seenEnemies).location;
    	//MapLocation loc = rc.getLocation();
        int broadcastRange = rc.getLocation().distanceSquaredTo(rc.getInitialArchonLocations(rc.getTeam())[0]);
        rc.broadcastMessageSignal(loc.x,loc.y,broadcastRange);
    }
    
    private boolean locationClear(MapLocation m)
    {
        if(rc.canSense(m) && robot.fight.spotEnemies().length == 0)
        {
            //Then the location is clear, remove the location from our list of target Archon Locations
            robot.removeArchonLocation(m);
            return true;
        }
        return false;
    }
    
   

    /**************************************END STRATEGY METHODS**************************************************/


    /*************************************
     * CLASS SPECIFIC BEHAVIOR
     ************************************************/
    private void sense_map() throws GameActionException {
        //Sense map
        robot.fight.spotEnemies();
        robot.fight.spotOpponents();
        robot.fight.spotZombies();
        robot.fight.spotAllies();
        
        
        robot.updateEnemyArchonLocations(robot.fight.seenEnemies);
        

        //Sense terrain

        //Update knowledge
        updateArchonLocations();
        updateEnemyArchonLocations();
        updateZombieDenLocations();
    }
    


    private void broadcastEnemyInTurretBlindSpot() throws GameActionException {

        if (turretLoc != null && robot.fight.seenEnemies != null && robot.fight.seenEnemies.length > 0) {
            MapLocation loc = enemyInTurretBlindSpot(robot.fight.seenEnemies);

            if (loc != null) {
                if (rc.isCoreReady()) {
                    rc.broadcastMessageSignal(ATTACK_X, loc.x, rc.getType().sensorRadiusSquared);
                    rc.broadcastMessageSignal(ATTACK_Y, loc.y, rc.getType().sensorRadiusSquared);
                }

            }

        }
    }

    private void updateArchonLocations() {
        for (RobotInfo i : robot.fight.seenAllies) {
            if (i.type == RobotType.ARCHON) {
                if (!archonLocations.contains(i)) {
                    archonLocations.add(i);
                }
            }
        }
    }

    private void updateEnemyArchonLocations() {
        for (RobotInfo i : robot.fight.seenOpponents) {
            if (i.type == RobotType.ARCHON) {
                if (!enemyArchonLocations.contains(i)) {
                    enemyArchonLocations.add(i);
                }
            }
        }
    }

    private void updateZombieDenLocations() {
        for (RobotInfo i : robot.fight.seenZombies) {
            if (i.type == RobotType.ZOMBIEDEN) {
                if (!zombieDenLocations.contains(i)) {
                    zombieDenLocations.add(i);
                }
            }
        }
    }

    public void moveAwayFromArchon() throws GameActionException {
        if (rc.isCoreReady()) {
            if (archonLocations != null && archonLocations.size() > 0) {
                robot.targetMoveLoc = robot.nav.averageLoc((MapLocation[]) archonLocations.toArray());
                robot.targetMoveLoc = new MapLocation((2 * rc.getLocation().x) - robot.targetMoveLoc.x, (2 * rc.getLocation().y)
                        - robot.targetMoveLoc.y);

                robot.nav.move();
            }
        }
    }


    /*********************************
     * END CLASS SPECIFIC BEHAVIOR
     ************************************************/

    //Returns the first enemy in the robots array that is within the turret's blindspot
    public MapLocation enemyInTurretBlindSpot(RobotInfo[] robots) {

        if (robots != null && robots.length > 0) {

            if (turretLoc != null) {

                for (RobotInfo i : robots) {
                    int dist = i.location.distanceSquaredTo(turretLoc);

                    if (dist < RobotType.TURRET.attackRadiusSquared && dist > RobotType.TURRET.sensorRadiusSquared) {
                        return i.location;

                    }
                }

            }
        }
        return null;
    }


    public MapLocation findClosestRobot(RobotInfo[] robots, RobotType type) {
        double minDistance = 9999999;
        RobotInfo closestTarget = null;

        if(robots != null && robots.length>0){
            for (RobotInfo i : robots) {
                if (i.type == type) {
                    int sqDist = i.location.distanceSquaredTo(rc.getLocation());
                    if (sqDist < minDistance) {
                        minDistance = sqDist;
                        closestTarget = i;
                    }
                }
            }

        }

        if(closestTarget!=null){
            return closestTarget.location;
        }
        return null;

    }

    //returns dps/health of the swarm
    public double strengthOfRobotsInArray(RobotInfo[] robots){
        if (robots!=null && robots.length>0) {
            double dps = 0;
            double health = 0;
            for(RobotInfo i : robots){
                double attack = i.type.attackPower;
                double turnDelay = i.type.cooldownDelay;
                dps+=(attack/turnDelay);
                health+=i.health;
            }
            return dps*health/100;
        }else{
            return -1;
        }
    }
    
    public boolean check_strength(){
    	if(robot.fight.seenAllies != null && robot.fight.seenAllies.length>0){
    		double str = strengthOfRobotsInArray(robot.fight.seenAllies);
    		if(strength_needed < str){
    			has_strength = true;
    			return true;
    		}
    	}
    	return false;
    }


    
    public boolean can_see_robot_type(RobotType type, RobotInfo[] robots, Team team){
    	if(robot.fight.seenEnemies != null && robot.fight.seenEnemies.length > 0){
    		for(RobotInfo i : robots){
    			if(i.type == type && i.team == team){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    
}
