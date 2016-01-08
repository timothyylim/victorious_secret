package victorious_secret.Fight;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import victorious_secret.Robot;

public class Fight {
	
	private static RobotController rc;
	private static Robot robot;
	private Team opponent;
	private int lastTargeted = 0;
	
	public Fight(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
		opponent = rc.getTeam().opponent();
	}
	
	public Boolean fight()
	{
		RobotInfo[] zEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
		RobotInfo[] oEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, opponent);
		RobotInfo[] aEnemies = joinRobotInfo(zEnemies, oEnemies);
		
		if(zEnemies.length > 0)
		{
			if (rc.isWeaponReady()) 
			{
				try 
				{
					int target = 0;
					//Look for the last robot we targeted
					Boolean found = false;
					for(RobotInfo i : aEnemies)
					{
						if(i.ID == lastTargeted)
						{
							found = true;
							break;
						}
						target ++;
					}
					//If we can't find one then just choose one at random
					if(!found)
					 {
						target = robot.rand.nextInt(aEnemies.length);
						lastTargeted = aEnemies[target].ID;
					 }
					
					//Attack the targets last known location
					robot.targetLoc = aEnemies[target].location;
					rc.attackLocation(robot.targetLoc);
				} 
				catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
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
