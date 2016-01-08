/**
 * 
 */
package victorious_secret.Nav;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
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
		
	public void move() throws GameActionException
	{
		if(rc.isCoreReady()) 
		{
			Direction movingDirection;
			/*
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
			*/
			if(robot.targetLoc != null)
			{

				movingDirection = rc.getLocation().directionTo(robot.targetLoc);
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

