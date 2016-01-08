package victorious_secret;

import battlecode.common.*;
import team158.Robot;
import team158.units.Missile;

public class RobotPlayer
{
	
	static Direction movingDirection = Direction.EAST;
	static RobotController rc;
	private static Robot robot;
	
	public static void run(RobotController _rc)
	{
		rc = _rc;
		
		if (rc.getType() == RobotType.ARCHON) {
			robot = new Archon(rc);
		}
		
		
		while (true) {
			robot.move();
			Clock.yield();
		}
		
		
		if(rc.getTeam() == Team.B)
		{
			movingDirection = Direction.WEST;
		}
		
		while(true)
		{
			try 
			{
				repeat();
				Clock.yield();
			} 
			catch (GameActionException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void repeat() throws GameActionException
	{
		RobotInfo[] zEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
		RobotInfo[] oEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		RobotInfo[] aEnemies = joinRobotInfo(zEnemies, oEnemies);
		
		if(zEnemies.length > 0)
		{
			if (rc.isWeaponReady()) 
			{
				rc.attackLocation(aEnemies[0].location);
			}
		}
		else
		{
			if(rc.isCoreReady() && rc.canMove(movingDirection))
			{
				rc.move(movingDirection);
			}
		}
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