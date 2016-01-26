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

	public Fight(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
	}

	public static RobotInfo[] spotEnemies()
	{
		seenEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
		return seenEnemies;
	}

	public static RobotInfo[] spotAllies()
	{
		seenAllies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
		return seenAllies;
	}

	public static RobotInfo[] spotNearbyTurrets() {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());

		int nTurrets = 0;

		for(RobotInfo i : nearbyAllies){
			if(i.type == RobotType.TURRET || i.type == RobotType.TTM){
				nTurrets++;
			}
		}
		if(nTurrets > 0) {
			RobotInfo[] turrets = new RobotInfo[nTurrets];
			int j = 0;
			for (RobotInfo i : nearbyAllies) {
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

	public static RobotInfo[] spotAlliesOfType(RobotType type)
	{
		RobotInfo[] allies = spotAllies();

		int nOfType = 0;

		for (RobotInfo r: allies) {
			if(r.type == type)
			{
				nOfType++;
			}
		}

		if(nOfType > 0)
		{
			RobotInfo[] tAllies = new RobotInfo[nOfType];
			nOfType = 0;
			for (RobotInfo r: allies) {
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

	public static RobotInfo[] spotZombies()
	{
		seenZombies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);
		return seenZombies;
	}

	public static RobotInfo[] spotOpponents()
	{
		seenOpponents = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
		return seenOpponents;
	}

	//TODO: BETTER NAME
	public static RobotInfo[] targetEnemies()
	{
		attackableEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		return attackableEnemies;
	}

	public RobotInfo[] inRangeOf()
	{
		return inRangeOf(spotEnemies(), rc.getLocation());
	}

	public boolean locationUnderThreat(RobotInfo[] listOfEnemies, MapLocation loc)
	{
		return inRangeOf(listOfEnemies, loc) != null;

	}

	public RobotInfo[] inRangeOf(RobotInfo[] listOfEnemies, MapLocation loc)
	{
		List<RobotInfo> inRange = new ArrayList<>();

		for(RobotInfo r : listOfEnemies)
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

	public RobotInfo findClosestEnemy(RobotInfo[] listOfEnemies)
	{
		return findClosestEnemy(listOfEnemies, rc.getLocation());
	}

	public RobotInfo findClosestEnemy(RobotInfo[] listOfEnemies, MapLocation loc)
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

	public MapLocation findClosestMapLocation(MapLocation[] listOfLocations, MapLocation loc)
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

	public MapLocation findClosestMapLocation(List<MapLocation> listOfLocations, MapLocation loc)
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

	public MapLocation findClosestMapLocation(Collection<MapLocation> listOfLocations, MapLocation loc)
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

	public MapLocation findClosestFreeMapLocation(List<MapLocation> listOfLocations, MapLocation loc) throws GameActionException {
		double minDistance = 9999999;
		MapLocation closestTarget = null;

		for(MapLocation i : listOfLocations)
		{
			int sqDist = i.distanceSquaredTo(loc);
			if(sqDist  < minDistance && rc.senseRobotAtLocation(i) == null)
			{
				minDistance = sqDist;
				closestTarget = i;
			}
		}
		return closestTarget;
	}


	public static RobotInfo findLastTargeted(RobotInfo[] listOfEnemies)
	{
		if(lastTargeted == null)
		{
			return null;
		}
		for(RobotInfo i : listOfEnemies)
		{
			if(i.ID == lastTargeted.ID)
			{
				return i;
			}
		}
		return null;
	}

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

	public static RobotInfo findLowestHealthEnemyWithDelay(RobotInfo[] listOfEnemies)
	{
		double minHealth = 9999999;
		RobotInfo bestTarget = null;

		for(RobotInfo i : listOfEnemies)
		{
			if(i.coreDelay > 0 && i.health < minHealth)
			{
				minHealth = i.health;
				bestTarget = i;
			}
		}
		return bestTarget;
	}

	public static RobotInfo findLowestHealthUninfectedEnemy(RobotInfo[] listOfEnemies)
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

	public static RobotInfo findLowestHealthNonTerminalEnemy(RobotInfo[] listOfEnemies)
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
		if(bestTarget == null)
		{
			return null;
		}
		else
		{
			return bestTarget;
		}
	}

	public static boolean lowestHealthAttack() throws GameActionException {
		//if(lastTargeted == null) {
		lastTargeted = findLowestHealthEnemy(attackableEnemies);
		//}

		if(rc.canAttackLocation(lastTargeted.location))
		{
			rc.attackLocation(lastTargeted.location);
			return true;
		}
		return false;
	}

	public static boolean standardAttack() throws GameActionException {
		//Default is to always shoot at the last thing we attacked
		lastTargeted = findLastTargeted(attackableEnemies);

		//Then is to always shoot at Big Zombies if they're available
		if(lastTargeted == null)
		{
			lastTargeted = findLowestHealthEnemy(attackableEnemies, RobotType.BIGZOMBIE);
		}

		//Otherwise just shoot at the lowest health zombie
		if(lastTargeted == null) {
			lastTargeted = findLowestHealthEnemy(attackableEnemies);
		}

		if(rc.canAttackLocation(lastTargeted.location))
		{
			rc.attackLocation(lastTargeted.location);
			return true;
		}
		return false;
	}
}
