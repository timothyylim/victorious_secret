package victorious_secret.Fight;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import victorious_secret.Robot;

public class Fight {
	
	private static RobotController rc;
	private static Robot robot;
	private Team opponent;
	private int lastTargeted = 0;
	public RobotInfo[] seenEnemies;
	public RobotInfo[] attackableEnemies;
	
	public Fight(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
		opponent = rc.getTeam().opponent();
	}
	
	public void spotEnemies()
	{
		RobotInfo[] zEnemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);
		RobotInfo[] oEnemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, opponent);
		seenEnemies = joinRobotInfo(zEnemies, oEnemies);
	}
	
	//TODO: BETTER NAME
	public void targetkEnemies()
	{
		RobotInfo[] zEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
		RobotInfo[] oEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, opponent);
		attackableEnemies = joinRobotInfo(zEnemies, oEnemies);
	}
	
	public Boolean fight()
	{
		spotEnemies();
		targetkEnemies();
		if(attackableEnemies.length > 0)
		{
			if (rc.isWeaponReady()) 
			{
				try 
				{
					//Look for the last robot we targeted
					Boolean found = false;
					RobotInfo target = null;
					
					for(RobotInfo i : seenEnemies)
					{
						if(i.type == RobotType.BIGZOMBIE){
							found = true;
							target = i;
							break;
						}
					}
					if(!found)
					{
						double minHealth = 9999999;
						for(RobotInfo i : seenEnemies)
						{
							if(i.health < minHealth)
							{
								minHealth = i.health;
								target = i;
							}
						}
					}
						
					//Attack the targets last known location
					robot.targetShootLoc = target.location;
					//robot.targetMoveLoc = robot.targetShootLoc;
					if(rc.canAttackLocation(robot.targetShootLoc))
					{
						rc.attackLocation(robot.targetShootLoc);
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
			lastTargeted = 0;
			robot.targetShootLoc = null;
			//robot.targetMoveLoc = null;	
		}
		
		return false;
	} 
	
	private static RobotInfo[] joinRobotInfo(RobotInfo[] zEnemies, RobotInfo[] oEnemies) 
	{
		RobotInfo[] out = new RobotInfo[zEnemies.length + oEnemies.length];
		int i = 0;
		
		for(RobotInfo j:zEnemies)
		{
			out[i] = j;
			i++;
		}
		
		for(RobotInfo j:oEnemies)
		{
			out[i] = j;
			i++;
		}
		
		return out;
	}	
}
