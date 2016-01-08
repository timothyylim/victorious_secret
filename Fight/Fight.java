package victorious_secret.Fight;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import victorious_secret.Robot;

public class Fight {
	
	private static RobotController rc;
	private static Robot robot;
	
	public Fight(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;
	}
	
	public Boolean fight()
	{
		RobotInfo[] zEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
		RobotInfo[] oEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		RobotInfo[] aEnemies = joinRobotInfo(zEnemies, oEnemies);
		
		if(zEnemies.length > 0)
		{
			if (rc.isWeaponReady()) 
			{
				try 
				{
					rc.attackLocation(aEnemies[0].location);
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
