/**
 * 
 */
package victorious_secret.Nav;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import victorious_secret.Robot;

/**
 * @author APOC
 *
 */
public class Nav {

	/**
	 * 
	 */
	private static RobotController rc;
	private static Robot robot;
	
	public Nav(RobotController _rc, Robot _robot) 
	{
		rc = _rc;
		robot = _robot;
	}
	
	public void flee() throws GameActionException
	{
		if(robot.fight.seenEnemies != null && robot.fight.seenEnemies.length > 0)
		{
			robot.targetMoveLoc = averageEnemyLoc();
			robot.targetMoveLoc = new MapLocation((2 * rc.getLocation().x) - robot.targetMoveLoc.x, (2 * rc.getLocation().y) - robot.targetMoveLoc.y);
		}
		randMove();
	}
	
	public MapLocation averageEnemyLoc()
	{
		if(robot.fight.seenEnemies.length == 0) //robot.fight.seenEnemies == null || 
		{
			return rc.getLocation();
		}
	
		int x = 0;
		int y = 0;

		for(RobotInfo i : robot.fight.seenEnemies)
		{
			x += i.location.x;
			y += i.location.y;
		}
		
		Math.round(x /= robot.fight.seenEnemies.length);
		Math.round(y /= robot.fight.seenEnemies.length);
				
		return new MapLocation(x,  y);
	}
	
	public void guard(MapLocation archonLoc) throws GameActionException
	{
		/*UPDATED GUARD STRATEGY - CLUMP TOGETHER */
		/*Assume archonLoc is the location of the nearest guard*/
		int x = archonLoc.x;
		int y = archonLoc.y;
		
		/* ORIGINAL GUARD FUNCTION
		MapLocation avgEnemy = averageEnemyLoc();
		int x = (rc.getLocation().x) - (int)(((avgEnemy.x - archonLoc.x) / 2)*.5);
		int y = (rc.getLocation().y) - (int)(((avgEnemy.y - archonLoc.y) / 2)*.5);
		*/
		robot.targetMoveLoc = new MapLocation(x, y);
		
		randMove();	
	}
	public void move() throws GameActionException
	{
		flee();
	}
	
	public void randMove() throws GameActionException
	{
		if(rc.isCoreReady()) 
		{
			Direction movingDirection;
			
			if(robot.targetMoveLoc != null)
			{

				movingDirection = rc.getLocation().directionTo(robot.targetMoveLoc);
				if(rc.canMove(movingDirection))
				{
					rc.move(movingDirection);
					return;
				}

				if(rc.canMove(movingDirection.rotateLeft()))
				{
					rc.move(movingDirection.rotateLeft());
					return;
				}

				if(rc.canMove(movingDirection.rotateRight()))
				{
					rc.move(movingDirection.rotateRight());
					return;
				}

				if(rc.canMove(movingDirection.rotateLeft().rotateLeft()))
				{
					rc.move(movingDirection.rotateLeft().rotateLeft());
					return;
				}

				if(rc.canMove(movingDirection.rotateRight().rotateRight()))
				{
					rc.move(movingDirection.rotateRight().rotateRight());
					return;
				}
			}
			else if(robot.messageIn != null)
			{					
				if(rc.canMove(robot.messageIn)) 
				{
					rc.move(robot.messageIn);
				}
			}
			else
			{
				int i = 0;
				do
				{
					movingDirection = Direction.values()[robot.rand.nextInt(8)];
					if(rc.canMove(movingDirection)) 
					{
						rc.move(movingDirection);
						break;
					}
					i++;
				}while(i < 4);
			}

		}
	}	
}
