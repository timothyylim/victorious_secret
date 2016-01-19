/**
 *
 */
package victorious_secret.Behaviour;

import battlecode.common.*;
import scala.xml.PrettyPrinter;
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

    public static MapLocation averageLoc(RobotInfo[] listOfEnemies)
    {

        if(listOfEnemies.length == 0) //robot.fight.seenEnemies == null ||
        {
            return rc.getLocation();
        }

        int x = 0;
        int y = 0;

        for(RobotInfo i : listOfEnemies)
        {
            x += i.location.x;
            y += i.location.y;
        }

        x /= listOfEnemies.length;
        y /= listOfEnemies.length;

        return new MapLocation(x,  y);

    }

    public static MapLocation averageLoc(MapLocation[] listOfEnemiesLoc)
    {
        if(listOfEnemiesLoc.length == 0) //robot.fight.seenEnemies == null ||
        {
            return rc.getLocation();
        }

        int x = 0;
        int y = 0;

        for(MapLocation i : listOfEnemiesLoc)
        {
            x += i.x;
            y += i.y;
        }

        x /= listOfEnemiesLoc.length;
        y /= listOfEnemiesLoc.length;

        return new MapLocation(x,  y);
    }

	public static void flee() throws GameActionException
	{
		if(robot.fight.seenEnemies != null && robot.fight.seenEnemies.length > 0)
		{
<<<<<<< HEAD
            MapLocation[] locs = {rc.getLocation(), averageLoc(robot.fight.attackableEnemies)};
=======
            MapLocation[] locs = {rc.getLocation(), averageLoc(robot.fight.seenEnemies)};
>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0

            robot.targetMoveLoc = averageLoc(locs);
		}

        move();

	}

    public static void guard(MapLocation archonLoc) throws GameActionException
	{
		/*UPDATED GUARD STRATEGY - CLUMP TOGETHER */
		/*Assume archonLoc is the location of the nearest guard*/

		/*int x = archonLoc.x;
		int y = archonLoc.y;*/

		/* ORIGINAL GUARD FUNCTION
		MapLocation avgEnemy = averageEnemyLoc();
		int x = (rc.getLocation().x) - (int)(((avgEnemy.x - archonLoc.x) / 2)*.5);
		int y = (rc.getLocation().y) - (int)(((avgEnemy.y - archonLoc.y) / 2)*.5);
		*/
        MapLocation[] locs = {archonLoc, averageLoc(robot.fight.attackableEnemies)};

		robot.targetMoveLoc = averageLoc(locs);

        move();
	}

    public static void move() throws GameActionException
	{
        if(rc.isCoreReady()) {
            //First move to target
            if (robot.targetMoveLoc != null)
            {
                if(moveToTarget(robot.targetMoveLoc))
                {
                    //Then we sucessfully moved towards our target
                    return;
                }
            }

            //If that fails then do you rand move
            randMove();
        }
	}

    public static boolean moveToTarget(MapLocation targetMoveLoc) throws GameActionException
    {

        Direction movingDirection = rc.getLocation().directionTo(targetMoveLoc);
        if(rc.canMove(movingDirection))
        {
            rc.move(movingDirection);
            return true;
        }

        if(rc.canMove(movingDirection.rotateLeft()))
        {
            rc.move(movingDirection.rotateLeft());
            return true;
        }

        if(rc.canMove(movingDirection.rotateRight()))
        {
            rc.move(movingDirection.rotateRight());
            return true;
        }

        if(rc.canMove(movingDirection.rotateLeft().rotateLeft()))
        {
            rc.move(movingDirection.rotateLeft().rotateLeft());
            return true;
        }

        if(rc.canMove(movingDirection.rotateRight().rotateRight()))
        {
            rc.move(movingDirection.rotateRight().rotateRight());
            return true;
        }
        return false;
    }

    public static MapLocation spiralClockwise(MapLocation center) throws GameActionException
    {
        if(center != null)
        {
            return rc.getLocation().add(rc.getLocation().directionTo(center).rotateLeft());
        }
        else
        {
            return null;
        }
    }

    public static MapLocation spiralAnitClockwise(MapLocation center) throws GameActionException
    {
        if(center != null)
        {
            return rc.getLocation().add(rc.getLocation().directionTo(center).rotateRight());
        }
        else
        {
            return null;
        }
    }

	public static void randMove() throws GameActionException
	{
        int i = 0;
        do
        {
            Direction movingDirection = Direction.values()[robot.rand.nextInt(8)];
            if(rc.canMove(movingDirection))
            {
                rc.move(movingDirection);
                break;
            }
            i++;
        }while(i < 10);

	}	
}
