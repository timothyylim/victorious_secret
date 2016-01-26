/**
 *
 */
package victorious_secret.Behaviour;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Strategy.Flee;

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

    public static void randMove() throws GameActionException {
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

    public static Direction findBestMove(MapLocation here, MapLocation target, List<Direction> allowedDirs) throws GameActionException {
        int bestDistance = 99999;
        Direction bestDirection = null;
        for(Direction d : allowedDirs){
            MapLocation newLoc = here.add(d);
            if(rc.onTheMap(newLoc) &&
                    rc.senseRobotAtLocation(newLoc) == null
                    &&  newLoc.distanceSquaredTo(target) < bestDistance){
                bestDirection = d;
                bestDistance = newLoc.distanceSquaredTo(target);
                bestDistance += turnsToClear(newLoc) * 3;
                //System.out.println("best = " + bestDistance + " " + bestDirection);
            }
        }
        return bestDirection;
    }

    public static void moveOrClear(Direction dir) throws GameActionException {
        //System.out.println("Moving " + dir);
        if(rc.canMove(dir)){
            rc.move(dir);
        }else if(rc.getType() != RobotType.TTM){
            rc.clearRubble(dir);
        }
    }

    public static void moveAlongRadiusLarger(MapLocation here, MapLocation target, int targetRadius) throws GameActionException {
        //Implementation of bug nav where units do not move beyond an allowed distance to a location
        //get the directions we can move
        List<Direction> allowedDirs = getAllowedDirectionsLarger(here, target, targetRadius);
        //find the best direction
        //best is defined by closeness and clearness
        Direction bestDirection = findBestMove(here, target, allowedDirs);

        //move there
        if (bestDirection != null) {
            moveOrClear(bestDirection);
        }
    }

    public static void moveAlongRadiusSmaller(MapLocation here, MapLocation target, int targetRadius) throws GameActionException {
        //Implementation of bug nav where units do not move beyond an allowed distance to a location
        //get the directions we can move
        List<Direction> allowedDirs = getAllowedDirectionsSmaller(here, target, targetRadius);
        //find the best direction
        //best is defined by closeness and clearness
        Direction bestDirection = findBestMove(here, target, allowedDirs);

        //move there
        if (bestDirection != null) {
            moveOrClear(bestDirection);
        }

    }

    public static int turnsToClear(MapLocation loc){
        //Sets an upper bound on the number of turns to clear the rubble
        double r = rc.senseRubble(loc);
        //Tile is passable once it is below 50 rubble
        return (int) Math.max((r - 50) / 10, 0);
    }

    public static List<Direction> getAllowedDirectionsLarger(MapLocation here, MapLocation target, int targetRadius){

        int upperRadiusSq = (int) (Math.pow(targetRadius + 1, 2) - 1);

        List<Direction> allowedDirs = new ArrayList<>();
        for(Direction d : Direction.values()){
			/*int tR = (int)Math.sqrt(here.add(d).distanceSquaredTo(targetMoveLoc));
			if(tR > targetRadius && d != Direction.OMNI && d != Direction.NONE){
				allowedDirs.add(d);
			}*/
            if(d != Direction.OMNI && d != Direction.NONE && here.add(d).distanceSquaredTo(target) > upperRadiusSq){
                allowedDirs.add(d);
            }
        }
        return allowedDirs;
    }

    public static List<Direction> getAllowedDirectionsSmaller(MapLocation here, MapLocation target, int targetRadius){
        List<Direction> allowedDirs = new ArrayList<>();
        for(Direction d : Direction.values()){
            int tR = (int)Math.sqrt(here.add(d).distanceSquaredTo(target));
            if(tR < targetRadius){
                allowedDirs.add(d);
            }
        }
        return allowedDirs;
    }

    public static void moveToFreeLocation(List<MapLocation> allowedTargets, MapLocation here, MapLocation target) throws GameActionException {
        MapLocation t = null;
        if(allowedTargets != null  && allowedTargets.size() > 0) {
            //Flee.setTarget(fight.findClosestFreeMapLocation(allowedTargets, here));
            t = robot.fight.findClosestFreeMapLocation(allowedTargets, here);
        }

        if(t == null){
            //Flee.setTarget(targetMoveLoc);
            t = target;
        }
        //System.out.println("-->" + t);
        //System.out.println("   MOVING TO TARGET " + t);
        Flee.setTarget(t);
        Direction dir = Flee.getNextMove();
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
