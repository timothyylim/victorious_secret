/**
 *
 */
package victorious_secret.Behaviour;

import battlecode.common.*;
import victorious_secret.Robot;

import java.util.ArrayList;
import java.util.List;

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
            MapLocation[] locs = {rc.getLocation(), averageLoc(robot.fight.seenEnemies)};

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

    public static List<MapLocation> findAllowedLocations(MapLocation here, int radiusFromTarget, MapLocation target) throws GameActionException {
        //System.out.println("FINDING ALLOWED LOCATIONS!");
        List<MapLocation> onRadius = new ArrayList<>();

        int sightRadiusSq = rc.getType().sensorRadiusSquared;
        int sightRadius = (int) Math.sqrt(sightRadiusSq);

        //Assume exterior
        int minX = Math.max(here.x - sightRadius, target.x - radiusFromTarget);
        int maxX = Math.min(here.x + sightRadius, target.x + radiusFromTarget);

        int minY = Math.max(here.y - sightRadius, target.y - radiusFromTarget);
        int maxY = Math.min(here.y + sightRadius, target.y + radiusFromTarget);

        int upperBound = (int) (Math.pow(radiusFromTarget + 1, 2) - 1);
        int lowerBound = (int) Math.pow(radiusFromTarget, 2);

        for(int x = minX; x <= maxX; x++){
            int upperY = (int) Math.sqrt(upperBound - Math.pow(x - target.x, 2));
            int lowerY = (int) Math.sqrt(lowerBound - Math.pow(x - target.x, 2));

            int xSq = (int) Math.pow(x - here.x, 2);

            MapLocation ml;

            for(int y = Math.max(target.y + lowerY, minY); y <= Math.min(target.y + upperY, maxY); y++) {
                int ySq = (int) Math.pow(y - here.y, 2);
                if(xSq + ySq <= sightRadiusSq) {
                    ml = new MapLocation(x, y);
                    if(rc.onTheMap(ml)){
                        onRadius.add(ml);
                    }
                }
            }

            for(int y = Math.max(target.y - lowerY, minY); y >= Math.min(target.y - upperY, maxY); y--) {
                //Note, this can allow double insertion of a map location
                int ySq = (int) Math.pow(y - here.y, 2);
                if(xSq + ySq <= sightRadiusSq) {
                    ml = new MapLocation(x, y);
                    if(rc.onTheMap(ml)){
                        onRadius.add(ml);
                    }
                }
            }
        }

        return onRadius;
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
