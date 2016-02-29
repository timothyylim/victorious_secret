/**
 *
 */
package victorious_secret.Behaviour;

import battlecode.common.*;
import scala.reflect.io.File;
import victorious_secret.Robot;
import victorious_secret.Strategy.Flee;

import java.util.ArrayList;
import java.util.List;

/**
 * @author APOC
 *
 */
public class Nav {
	private static RobotController rc;
	private static Robot robot;

    /**
     * Initalises the Nav controller for use as static class
     * @param _rc The Robot Controller
     * @param _robot The Robot type
     */
    public static void initialise(RobotController _rc, Robot _robot) {
        rc = _rc;
        robot = _robot;
    }

    /**
     * Initalises the Nav controller for use as non-static class
     * @param _rc The Robot Controller
     * @param _robot The Robot type
     * @deprecated
     */
	public Nav(RobotController _rc, Robot _robot)
	{
		rc = _rc;
		robot = _robot;
	}

    /**
     * Gets the average location of a list of units. Returns your location if the list of units is empty.
     * @param units The list of units to average
     * @return      The average location of the list of units
     */
    public static MapLocation averageLoc(RobotInfo[] units)
    {

        if(units.length == 0)
        {
            return rc.getLocation();
        }

        int x = 0;
        int y = 0;

        for(RobotInfo i : units)
        {
            x += i.location.x;
            y += i.location.y;
        }

        x /= units.length;
        y /= units.length;

        return new MapLocation(x,  y);

    }

    /**
     * Gets the average location of a list of map locations.  Returns your location if the list of
     * map locations is empty.
     * @param locs  The list of Map Locations
     * @return      The average Map Location
     */
    public static MapLocation averageLoc(MapLocation[] locs)
    {
        if(locs.length == 0)
        {
            return rc.getLocation();
        }

        int x = 0;
        int y = 0;

        for(MapLocation i : locs)
        {
            x += i.x;
            y += i.y;
        }

        x /= locs.length;
        y /= locs.length;

        return new MapLocation(x,  y);
    }

    /**
     * Strategy behaviour. Sets the robot targetMoveLoc to be between the Archon and the average position of
     * seen enemies.
     * @param archonLoc     The location of the archon we want to guard
     * @throws GameActionException
     */
    public static void guard(MapLocation archonLoc) throws GameActionException
	{
        //Since this is a movement function there's no point running it if the core is not ready
        if(rc.isCoreReady()) {
            //locs is a list of two locations - an archon location and the average enemy location
            if (Fight.seenEnemies != null) {
                MapLocation[] locs = {archonLoc, averageLoc(Fight.seenEnemies)};
                robot.targetMoveLoc = averageLoc(locs);
            }
            else{
                robot.targetMoveLoc = archonLoc;
            }
            //The average of those two locations will be in be between the two, thus moving us towards a
            //high risk area


            BugNav.setTarget(robot.targetMoveLoc);
            Direction dir = BugNav.getNextMove();
            if(rc.canMove(dir)){
                rc.move(dir);
            }
        }
	}

    /**
     * Returns a target move location that would cause the robot to spiral clockwise around a target
     * @param center    The target location the robot will spiral around
     * @return          Returns the target move location
     * @throws GameActionException
     */
    public static MapLocation spiralClockwise(MapLocation center) throws GameActionException
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

    /**
     * Returns a target move location that would cause the robot to spiral anti-clockwise around a target
     * @param center    The target location the robot will spiral around
     * @return          Returns the target move location
     * @throws GameActionException
     */
    public static MapLocation spiralAntiClockwise(MapLocation center) throws GameActionException
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

    /**
     * Used for Napoleon code. It finds the points a constant distance from a target that we can see.
     * @param here              The location of the robot
     * @param radiusFromTarget  The radius to the target that should be maintained
     * @param target            The actual location of the target
     * @return                  Returns a list of map locations on the radius circle
     * @throws GameActionException
     */
    public static List<MapLocation> findAllowedLocations(MapLocation here, int radiusFromTarget, MapLocation target) throws GameActionException {

        List<MapLocation> onRadius = new ArrayList<>();

        int sightRadiusSq = rc.getType().sensorRadiusSquared;
        int sightRadius = (int) Math.sqrt(sightRadiusSq);

        //Sets the bounding square that is either what we can see or the edge of the target circle
        //(Note, it may include locations we can't see in the corners)
        int minX = Math.max(here.x - sightRadius, target.x - radiusFromTarget);
        int maxX = Math.min(here.x + sightRadius, target.x + radiusFromTarget);

        int minY = Math.max(here.y - sightRadius, target.y - radiusFromTarget);
        int maxY = Math.min(here.y + sightRadius, target.y + radiusFromTarget);

        int upperBound = (int) (Math.pow(radiusFromTarget + 1, 2) - 1);
        int lowerBound = (int) Math.pow(radiusFromTarget, 2);

        //Step through each horizontal section and add the appropriate points on the vertical
        for(int x = minX; x <= maxX; x++){
            int upperY = (int) Math.sqrt(upperBound - Math.pow(x - target.x, 2));
            int lowerY = (int) Math.sqrt(lowerBound - Math.pow(x - target.x, 2));

            int xSq = (int) Math.pow(x - here.x, 2);

            MapLocation ml;

            //Positive roots
            for(int y = Math.max(target.y + lowerY, minY); y <= Math.min(target.y + upperY, maxY); y++) {
                int ySq = (int) Math.pow(y - here.y, 2);
                //Condition that ensures we don't add anything outside our sight range
                if(xSq + ySq <= sightRadiusSq) {
                    ml = new MapLocation(x, y);
                    if(rc.onTheMap(ml)){
                        onRadius.add(ml);
                    }
                }
            }

            //Negative roots
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

    /**
     * Used for Napoleon code. Finds the best location to move to, which can include squares with rubble on.
     * @param here          Location of our robot
     * @param target        Target destination
     * @param allowedDirs   Directions that are allowed to move
     * @return              Returns a direction or null
     * @throws GameActionException
     */
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
                //Heuristic additional cost to locations that have rubble on
                bestDistance += turnsToClear(newLoc) * 3;
            }
        }
        return bestDirection;
    }

    /**
     * Move to a particular location, or clear it of rubble
     * @param dir   Direction to move to
     * @throws GameActionException
     */
    public static void moveOrClear(Direction dir) throws GameActionException {
        //System.out.println("Moving " + dir);
        if(rc.canMove(dir)){
            rc.move(dir);
        }else if(rc.getType() != RobotType.TTM){
            rc.clearRubble(dir);
        }
    }

    /**
     * Navigation where units do not move beyond an allowed distance to a location without getting closer than the
     * target radius. As such, robots on the radius will move sidways rather than forward.
     * @param here          Location of our robot
     * @param target        Target destination
     * @param targetRadius  Constraint radius
     * @throws GameActionException
     */
    public static void moveAlongRadiusLarger(MapLocation here, MapLocation target, int targetRadius) throws GameActionException {
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

    /**
     * Navigation where units do not move beyond an allowed distance to a location without getting further away than the
     * target radius. As such, robots on the radius will move sidways rather than forward.
     * @param here          Location of our robot
     * @param target        Target destination
     * @param targetRadius  Constraint radius
     * @throws GameActionException
     */
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

    /**
     * Returns an upper bound on the number of turns to clear the rubble on a square
     * @param loc   The square to evaluate
     * @return      The number turns to clear
     */
    public static int turnsToClear(MapLocation loc){

        double r = rc.senseRubble(loc);
        //Tile is passable once it is below 50 rubble
        return (int) Math.max((r - 50) / 10, 0);
    }

     /**
     * The directions that do not get you closer than a particular target distance
     * @param here          Location of our robot
     * @param target        Target destination
     * @param targetRadius  Constraint radius
     * @return              Returns a list of allowed directions
     */
    public static List<Direction> getAllowedDirectionsLarger(MapLocation here, MapLocation target, int targetRadius){

        int upperRadiusSq = (int) (Math.pow(targetRadius + 1, 2) - 1);

        List<Direction> allowedDirs = new ArrayList<>();
        for(Direction d : Direction.values()){
            if(d != Direction.OMNI && d != Direction.NONE && here.add(d).distanceSquaredTo(target) > upperRadiusSq){
                allowedDirs.add(d);
            }
        }
        return allowedDirs;
    }

     /**
     * The directions that do not get you further than a particular target distance
     * @param here          Location of our robot
     * @param target        Target destination
     * @param targetRadius  Constraint radius
     * @return              Returns a list of allowed directions
     */
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

    /**
     * Used for Napoleon code. Moves towards the nearest free spot on the permiter.
     * @param allowedTargets    Allowed map locations
     * @param here              Location of our robot
     * @param target            Target destination
     * @throws GameActionException
     */
    public static void moveToFreeLocation(List<MapLocation> allowedTargets, MapLocation here, MapLocation target) throws GameActionException {
        MapLocation t = null;
        if(allowedTargets != null  && allowedTargets.size() > 0) {
            t = Fight.findClosestFreeMapLocation(allowedTargets, here);
            if(t!=null && here.isAdjacentTo(t)){
            	moveOrClear(here.directionTo(t));
            }
        }

        if(t == null){
            t = target;
        }

        BugNav.setTarget(t);
        Direction dir = BugNav.getNextMove();
        if(rc.canMove(dir) && rc.isCoreReady()) {
            rc.move(dir);
        }
    }
    
    


    
}
