/**
 *
 */
package victorious_secret.Behaviour;

import battlecode.common.*;
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

    public MapLocation averageLoc(RobotInfo[] listOfEnemies)
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

    public MapLocation averageLoc(MapLocation[] listOfEnemiesLoc)
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

	public void flee() throws GameActionException
	{
		if(robot.fight.seenEnemies != null && robot.fight.seenEnemies.length > 0)
		{
            MapLocation[] locs = {rc.getLocation(), averageLoc(robot.fight.attackableEnemies)};

            robot.targetMoveLoc = averageLoc(locs);

		}
        move();

	}

    public void guard(MapLocation archonLoc) throws GameActionException
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

    public void kite(RobotInfo target) throws GameActionException
    {
        //Kiting wants to stay as close to the edge of their attack range but stay within ours
        switch (target.type)
        {
            case ARCHON:
            case ZOMBIEDEN:
                //Get close
                break;

            case TURRET:

                break;

            default:
                int ar = rc.getType().attackRadiusSquared;
                int tr = target.type.attackRadiusSquared;
                int sqDistance = rc.getLocation().distanceSquaredTo(target.location);

                if(ar < tr)
                {
                    //Then their range is greater than our range and we can't kite
                    //charge();
                    //return;
                }

                break;

        }
    }

    public void move() throws GameActionException
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

    public boolean moveToTarget(MapLocation targetMoveLoc) throws GameActionException
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

	public void randMove() throws GameActionException
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
