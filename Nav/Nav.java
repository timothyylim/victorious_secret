/**
 * 
 */
package victorious_secret.Nav;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
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
			int i = 0;
			do
			{
				Direction movingDirection = Direction.values()[robot.rand(8)];
				if(rc.canMove(movingDirection))
				{
					rc.move(movingDirection);
					break;
				}
				i++;
			}while(i > 5);
		}
	}	
}

