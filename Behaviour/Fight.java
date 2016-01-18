package victorious_secret.Behaviour;

import battlecode.common.*;
import victorious_secret.Robot;

import java.util.ArrayList;
import java.util.List;

public class Fight {
	
	protected static RobotController rc;
	protected static Robot robot;
	protected static RobotInfo lastTargeted;
	public static RobotInfo[] seenEnemies;
	public static RobotInfo[] seenZombies;
	public static RobotInfo[] attackableEnemies;

	
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
        seenEnemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
        return seenEnemies;
    }

	public static RobotInfo[] spotZombies()
	{
		seenZombies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);
		return seenZombies;
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


}
