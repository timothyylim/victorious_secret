/**
 *
 */
package victorious_secret.Units;

import java.util.Random;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Strategy.Flee;

/**
 * @author APOC
 *
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SCOUT
A fast unit, unobstructed by rubble.
=======
import team099.Robot;
import team099.Behaviour.Fight;
import team099.Behaviour.Nav;

/**
 * @author APOC
 * 
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SCOUT
 A fast unit, unobstructed by rubble.
>>>>>>> 45b64142e44a0dd44af44b58ab8e8ebf56678b6c
canAttack(): false

buildTurns: 15
bytecodeLimit: 20000
maxHealth: 100
movementDelay: 1
partCost: 40
sensorRadiusSquared: 53
spawnSource: ARCHON
turnsInto: FASTZOMBIE
 */
public class Scout extends Robot {
	private static RobotController rc;
	private static Robot robot;
	private static BugNav flee;

	private static int ATTACK_X = 15151515;
	private static int ATTACK_Y = 14141414;

	/*Scout Strategy 3 Variables: Turrent information*/
	public MapLocation turretLoc;

	public Scout(RobotController _rc) 
	{

		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		//nav = new Nav(rc, this);
		Nav.initialise(rc, this);
		strat = Strategy.DEFEND;
		//fight = new Fight(rc, this);
		Fight.initialise(rc, this);
		flee = new BugNav();
		flee.initialise(rc);
	}

	@Override
	public void move() throws GameActionException
	{
		runScoutStrategy4();

	}


	/**
	 * Strategy Method Stub: Originally this was intended to be a basic scout strategy.
	 * The scout would explore the map and collect information about the layout of the
	 * map and the locations of relevent game pieces. The scout would then attempt to
	 * communicate this information to the other units.
	 * After several buggy attempts, this method was left unimplemented.
	 * @throws GameActionException
	 */
	public void runScoutStrategy1() throws GameActionException {


	}


	/**
	 * Strategy Method Stub: Originally this was intended to be a redhearing strategy.
	 * The scout would attempt to find enemies and draw them into the enemy base.
	 * After several buggy attempts, this method was left unimplemented.
	 * @throws GameActionException
	 */
	public void runScoutStrategy2() throws GameActionException {


	}

	/**
	 * Runs a turret strategy. In this strategy a scout moves according to the flee.target behavior
	 * until it finds a turret. Once a turret is found, the scout will circle that turrent and
	 * scan for nearby enemies in the turret's blindspot. If one such enemy appears, the scout sends
	 * a signal containing the location of that enemy to the close by turret.
	 * @throws GameActionException
	 */
	public void runScoutStrategy3() throws GameActionException {
		Fight.sense_map();
		turretLoc = findClosestRobot(robot.fight.seenAllies, RobotType.TURRET);

		if(turretLoc != null){
			flee.setTarget(turretLoc);
		}else{
			flee.setTarget(rc.getLocation());
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


	/**
	 * Runs a swarm assist strategy. In this strategy a scout waits at it's spawning location and
	 * waits for an appropriate army strength to form around the scout. Once it senses the
	 * threshold level of army strength is present, the scout moves towards the enemy archon.
	 * The scout will signal its location constantly.
	 * <b>Note:</b> This method needs to be refined to contain a more sensible way of finding
	 * enemy archons and broadcasting its location
	 * @throws GameActionException
	 */
	public void runScoutStrategy4() throws GameActionException {
		double attackPowerNeeded = 550;
		double attackPowerOfSeenAllies;
		Fight.sense_map();
		attackPowerOfSeenAllies = strengthOfRobotsInArray(robot.fight.seenAllies);

		if(attackPowerOfSeenAllies>attackPowerNeeded){
			//Need to create a more sensible way to find opponent archons

			flee.setTarget(rc.getInitialArchonLocations(rc.getTeam().opponent())[1]);
			if (rc.isCoreReady()) {
				Direction dir = flee.getNextMove();
				if(rc.canMove(dir)){
					rc.move(dir);
				}
			}

		}
		MapLocation loc = rc.getLocation();
		//Need to find a more sensible way of broadcasting a message
		int broadcastRange = rc.getLocation().distanceSquaredTo(rc.getInitialArchonLocations(rc.getTeam())[0]);
		rc.broadcastMessageSignal(loc.x,loc.y,broadcastRange);

	}

	/**
	 * Strategy Method Stub: Attempts to run an altered swarm assist strategy. In this strategy
	 * runScoutStrategy4() is implemented except the scout attempts to find zombie dens instead of enemy archons.
	 * <b>Note:</b> This strategy was originally implemented but was buggy and not very effective. This method
	 * has therefore been cleared for a new design.
	 * @throws GameActionException
	 */
	public void runScoutStrategy5() throws GameActionException{

	}


	/**
	 * This method calls all relevant robot.fight methods that spot units on the map.
	 * This method should be called at the beginning of each scout move to ensure
	 * that the scout has up to date information of all hostile and friendly enemies close by.
	 * @throws GameActionException
	 */

	/**
	 * This method returns the MapLocation of the closest robot of a specific type
	 * from an array of robot information
	 * @param robots An array of RobotInfo of any makeup
	 * @param type A specific type of robot
	 * @return Returns a MapLocation of the closest robot of type
	 */
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

	/**
	 * The scout signals the location of of an enemy in a turret's blindspot.
	 * This turret is the turret closest to the scout.
	 * The scout will broadcast the signal just far enough to reach the turret.
	 * A risk of this broadcast is that the signal is misinterpreted by other turrets
	 * or overlaps with another scout's signal.
	 * @throws GameActionException
	 */
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

	/**
	 * If the scout has a turret close by and it has seen enemies then the MapLocation
	 * of an enemy in that turret's blindspot is returned. If multiple enemies meet
	 * this condition a random enemy's location is returned.
	 * @param robots An array of RobotInfo representing a collection of enemy robots
	 * @return Returns a MapLocation of an enemy in the blindspot of a nearby turret. Otherwise returns null.
	 */
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

	/**
	 * Returns a measure of the strength of a group of robots. The damage per second and the
	 * health of the group are totaled and the following measure of strength is returned:
	 * total (total damage per second * total health)/100
	 * @param robots The group of robots of which to measure the strength
	 * @return (DPS per second * Health) divided by 100
	 */
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

}
