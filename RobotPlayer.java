package victorious_secret;

import battlecode.common.*;

public class RobotPlayer
{
	
	static Direction movingDirection = Direction.EAST;
	static RobotController rc;
	
	
	public static void run(RobotController _rc)
	{
		rc = _rc;
		
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
		
		if(zEnemies.length > 0)
		{
			if (rc.isWeaponReady()) 
			{
				rc.attackLocation(zEnemies[0].location);
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
	
}