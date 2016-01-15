package victorious_secret.Behaviour;

import battlecode.common.*;
import victorious_secret.Robot;

public class Fight {
	
	private static RobotController rc;
	private static Robot robot;
	private RobotInfo lastTargeted;
	public RobotInfo[] seenEnemies;
	public RobotInfo[] attackableEnemies;
	
	public Fight(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
	}
	
	public RobotInfo[] spotEnemies()
	{
		seenEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
		return seenEnemies;
	}
	
	//TODO: BETTER NAME
	public RobotInfo[] targetEnemies()
	{
		attackableEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		return attackableEnemies;
	}

	public RobotInfo findLastTargeted(RobotInfo[] listOfEnemies)
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

	public RobotInfo findLowestHealthEnemy(RobotInfo[] listOfEnemies)
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

	public RobotInfo findLowestHealthEnemy(RobotInfo[] listOfEnemies, RobotType targetType)
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

	public Boolean fight()
	{
		seenEnemies = spotEnemies();
		attackableEnemies = targetEnemies();
		if(attackableEnemies.length > 0)
		{
			if (rc.isWeaponReady()) 
			{
				try 
				{
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
					}
				} 
				catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		}
		else 
		{
			lastTargeted = null;
		}
		
		return false;
	}
}
