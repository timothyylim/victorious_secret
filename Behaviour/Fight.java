
package victorious_secret.Behaviour;

import battlecode.common.*;
import victorious_secret.Robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Fight {
	protected static RobotController rc;
	protected static Robot robot;
	protected static RobotInfo lastTargeted;
	public static RobotInfo[] seenEnemies;
	public static RobotInfo[] seenZombies;
	public static RobotInfo[] seenOpponents; //array of all units belonging to the other player
	public static RobotInfo[] seenAllies;
	public static RobotInfo[] attackableEnemies;
	private static int VIPER_INFECTION_DAMAGE = 2;


	/**
	 * Initalises the Fight controller for use as static class
	 * @param _rc The Robot Controller
	 * @param _robot The Robot type
	 */
	public static void initialise(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
	}

	/**
	 * Initalises the Fight controller for use as non-static class
	 * @param _rc The Robot Controller
	 * @param _robot The Robot type
	 * @deprecated
	 */
	public Fight(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
	}

	/**
	 * This should be called at the start of each turn as it updates all of sensor readings. i.e. this is useful for
	 * bytecode efficiency
	 * @throws GameActionException
     */
	public static void sense_map() throws GameActionException {
		spotEnemies();
		spotOpponents();
		spotZombies();
		spotAllies();
		targetEnemies();
	}

	/**
	 * Senses enemies within sensor range
	 */
	public static void spotEnemies()
	{
		seenEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
	}

	/**
	 * Senses allies within sensor range
	 */
	public static void spotAllies()
	{
		seenAllies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
	}

	/**
	 * Senses zombies within sensor range
	 */
	public static void spotZombies()
	{
		seenZombies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);
	}

	/**
	 * Senses opponents within sensor range
	 */
	public static void spotOpponents()
	{
		seenOpponents = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
	}

	/**
	 * Senses enemies in weapons range
	 */
	public static void targetEnemies()
	{
		attackableEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
	}

	/**
	 * Senses nearby allied Turrets and TTMs
	 * @return	Returns a list of sensible turrets.
     */
	public static RobotInfo[] spotNearbyTurrets() {
		int nTurrets = 0;

		for(RobotInfo i : seenAllies){
			if(i.type == RobotType.TURRET || i.type == RobotType.TTM){
				nTurrets++;
			}
		}
		if(nTurrets > 0) {
			RobotInfo[] turrets = new RobotInfo[nTurrets];
			int j = 0;
			for (RobotInfo i : seenAllies) {
				if (i.type == RobotType.TURRET || i.type == RobotType.TTM) {
					turrets[j] = i;
					j++;
				}
			}

			return turrets;
		}
		else{
			return null;
		}
	}

	/**
	 * Senses allies of a particular type
	 * @param type	The type of robot to sense
	 * @return		Returns a list of robots of that type, or null if none can be sensed.
     */
	public static RobotInfo[] spotAlliesOfType(RobotType type)
	{
		int nOfType = 0;

		for (RobotInfo r: seenAllies) {
			if(r.type == type)
			{
				nOfType++;
			}
		}

		if(nOfType > 0)
		{
			RobotInfo[] tAllies = new RobotInfo[nOfType];
			nOfType = 0;
			for (RobotInfo r: seenAllies) {
				if(r.type == type)
				{
					tAllies[nOfType] = r;
					nOfType++;
				}
			}

			return tAllies;
		}
		else
		{
			return null;
		}

	}

	/**
	 * Finds the list of enemies that we can see and that we are in range of
	 * @return Returns the list of enemies that we can see and that we are in range of
     */
	public static RobotInfo[] inRangeOf()
	{
		return inRangeOf(seenEnemies, rc.getLocation());
	}

	/**
	 * Finds the list of units that are within range of a specific location
	 * @param units List of units to evaluate
	 * @param loc	Location the units can target
     * @return		Returns the list of units that can target the location
     */
	public static RobotInfo[] inRangeOf(RobotInfo[] units, MapLocation loc)
	{
		List<RobotInfo> inRange = new ArrayList<>();

		for(RobotInfo r : units)
		{
			if(loc.distanceSquaredTo(r.location) <= r.type.attackRadiusSquared) {
				inRange.add(r);
			}
		}
		if(inRange.size() > 0)
		{
			RobotInfo[] out = new RobotInfo[inRange.size()];
			inRange.toArray(out);
			return out;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Boolean function that returns true if the location can be targeted by at least one unit
	 * @param units List of units to evaluate
	 * @param loc	Location the units can target
	 * @return		Returns true if at least one unit can target the location
     */
	public boolean locationUnderThreat(RobotInfo[] units, MapLocation loc)
	{
		return inRangeOf(units, loc) != null;

	}

	/**
	 * Finds the enemy closest to our location
	 * @param listOfEnemies	List of units to evaluate
	 * @return				The closest enemy
     */
	public static RobotInfo findClosestEnemy(RobotInfo[] listOfEnemies)
	{
		return findClosestEnemy(listOfEnemies, rc.getLocation());
	}

	/**
	 * Finds the unit closest to a specific map location
	 * @param listOfEnemies List of units to evaluate
	 * @param loc			Location to measure distance from
     * @return				The closest enemy
     */
	public static RobotInfo findClosestEnemy(RobotInfo[] listOfEnemies, MapLocation loc)
	{
		double minDistance = 9999999;
		RobotInfo closestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			int sqDist = i.location.distanceSquaredTo(loc);
			if(sqDist  < minDistance)
			{
				minDistance = sqDist;
				closestTarget = i;
			}
		}
		return closestTarget;
	}

	/**
	 * Finds the map location closest to a target map location
	 * @param listOfLocations	List of locations to evaluate
	 * @param loc				Location to measure distance from
     * @return					The closest map location
     */
	public static MapLocation findClosestMapLocation(MapLocation[] listOfLocations, MapLocation loc)
	{
		double minDistance = 9999999;
		MapLocation closestTarget = null;

		for(MapLocation i : listOfLocations)
		{
			int sqDist = i.distanceSquaredTo(loc);
			if(sqDist  < minDistance)
			{
				minDistance = sqDist;
				closestTarget = i;
			}
		}
		return closestTarget;
	}

	/**
	 * Finds the map location closest to a target map location
	 * @param listOfLocations	Collection of locations to evaluate
	 * @param loc				Location to measure distance from
	 * @return					The closest map location
	 */
	public static MapLocation findClosestMapLocation(Collection<MapLocation> listOfLocations, MapLocation loc)
	{
		double minDistance = 9999999;
		MapLocation closestTarget = null;

		for(MapLocation i : listOfLocations)
		{
			int sqDist = i.distanceSquaredTo(loc);
			if(sqDist  < minDistance)
			{
				minDistance = sqDist;
				closestTarget = i;
			}
		}
		return closestTarget;
	}

	 /**
	 * Finds the map location closest to a target map location that doesn't have a unit already on it
	 * @param listOfLocations	List of locations to evaluate
	 * @param loc				Location to measure distance from
	 * @return					The closest map location
	 */
	public static MapLocation findClosestFreeMapLocation(List<MapLocation> listOfLocations, MapLocation loc) throws GameActionException {
		double minDistance = 9999999;
		MapLocation closestTarget = null;

		for(MapLocation i : listOfLocations)
		{
			int sqDist = i.distanceSquaredTo(loc);
			if(sqDist < minDistance && rc.senseRobotAtLocation(i) == null)
			{
				minDistance = sqDist;
				closestTarget = i;
			}
		}
		return closestTarget;
	}

	/**
	 * Evaluates to true if at least one map location does not have a unit already on it
	 * @param listOfLocations	List of locations to evaluate
	 * @param loc				Location to measure distance from
	 * @return					True if at least one location is clear
	 */
	public static boolean hasClearMapLocation(List<MapLocation> listOfLocations, MapLocation loc) throws GameActionException {
		double minDistance = 9999999;
		MapLocation closestTarget = null;

		for(MapLocation i : listOfLocations)
		{
			int sqDist = i.distanceSquaredTo(loc);
			if(sqDist  < minDistance && rc.senseRobotAtLocation(i) == null && rc.senseRubble(i) < 100)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the RobotInfo of the robot that we last targeted, if it is still sensible.
	 * @param listOfEnemies	List of enemies to evaluate
	 * @return				Returns the robot info of the last targeted or null
     */
	public static RobotInfo findLastTargeted(RobotInfo[] listOfEnemies)
	{
		if(lastTargeted == null)
		{
			return null;
		}
		for(RobotInfo i : listOfEnemies)
		{
			if(i.ID == lastTargeted.ID && rc.canAttackLocation(i.location))
			{
				return i;
			}
		}
		return null;
	}

	/**
	 * Finds the lowest health enemy that is sensible
	 * @param listOfEnemies	list of enemies to be evaluated
	 * @return				Lowest health enemy
     */
	public static RobotInfo findLowestHealthEnemy(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.health < minHealth)
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	 /**
	 * Finds the lowest health enemy that is sensible and of a particular type
	 * @param targetType 	The target type of unit
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy
	 */
	public static RobotInfo findLowestHealthEnemy(RobotInfo[] listOfEnemies, RobotType targetType)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.type == targetType && i.health < minHealth)
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	/**
	 * Finds the lowest health enemy that is sensible and of a particular type and in attack range
	 * @param targetType 	The target type of unit
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy
	 */
	public static RobotInfo targetLowestHealthEnemy(RobotInfo[] listOfEnemies, RobotType targetType)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.type == targetType && i.health < minHealth && rc.canAttackLocation(i.location))
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	 /**
	 * Finds the lowest health enemy that is sensible and within weapon range
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy that can be shot
	 */
	public static RobotInfo targetLowestHealthEnemy(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.health < minHealth && rc.canAttackLocation(i.location))
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	/**
	 * Finds the lowest health enemy that is sensible and has a delay of at least two turns
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy that will still be there
	 */
	public static RobotInfo findLowestHealthEnemyWithDelay(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.coreDelay > 1 && i.health < minHealth)
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	/**
	 * Finds the lowest health enemy that is sensible and does not have an infection
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy that is uninfected
	 */
	public static RobotInfo findLowestHealthUninfectedEnemy(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.viperInfectedTurns < 1 && i.health < minHealth)
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	/**
	 * Finds the lowest health enemy that is sensible and does not have an infection
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy that can be infected
	 */
	public static RobotInfo targetLowestHealthUninfectedEnemy(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.viperInfectedTurns < 1 && i.health < minHealth && rc.canAttackLocation(i.location))
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	/**
	 * Finds the lowest health enemy that is sensible and is not going to die anyway
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy
	 */
	public static RobotInfo findLowestHealthNonTerminalEnemy(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.health > (i.viperInfectedTurns * VIPER_INFECTION_DAMAGE) &&
					i.health < minHealth ){
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}


	/**
	 * Finds the lowest health enemy that is sensible and is not going to die anyway
	 * @param listOfEnemies	List of enemies to be evaluated
	 * @return				Lowest health enemy that can be shot
	 */
	public static RobotInfo targetLowestHealthNonTerminalEnemy(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.health > (i.viperInfectedTurns * VIPER_INFECTION_DAMAGE) &&
					i.health < minHealth &&
					rc.canAttackLocation(i.location)){
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	/**
	 * Basic attack pattern that always shoots at the lowest health enemy. Returns true if attack succeeded
	 * @return True if attack succeeded
	 * @throws GameActionException
     */
	public static boolean lowestHealthAttack() throws GameActionException {
		lastTargeted = targetLowestHealthEnemy(attackableEnemies);

		if(lastTargeted != null)
		{
			rc.attackLocation(lastTargeted.location);
			return true;
		}
		return false;
	}

	/**
	 * Basic attack pattern that cycles through a priority of targets. Firstly it shoots at the last unit it attacked.
	 * Then it targets big zombies. Finally it will taget the lowest health enemy.
	 * @return True if attack succeeded
	 * @throws GameActionException
	 */
	public static boolean standardAttack() throws GameActionException {
		//Default is to always shoot at the last thing we attacked
		lastTargeted = findLastTargeted(attackableEnemies);

		//Then is to always shoot at Big Zombies if they're available
		if(lastTargeted == null)
		{
			lastTargeted = targetLowestHealthEnemy(attackableEnemies, RobotType.BIGZOMBIE);
		}

		//Otherwise just shoot at the lowest health zombie
		if(lastTargeted == null) {
			lastTargeted = targetLowestHealthEnemy(attackableEnemies);
		}

		if(lastTargeted != null)
		{
			rc.attackLocation(lastTargeted.location);
			return true;
		}
		return false;
	}
}
