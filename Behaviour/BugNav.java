package victorious_secret.Behaviour;
import battlecode.common.*;
/**
 * This is a Bug Navigation that will approach a target and if the unit approaches an obstacle
 * the unit will avoid the obstacle by bugging left and then if no path found, it will bug right.
 * @author Karlson Lee & Mohammed Al-Hakim
 * Made to Inspire
 */
public class BugNav {
    static RobotController _rc;
    static public enum STATE {BUGGING, FLOCKING};
    static STATE state= STATE.FLOCKING;
    static boolean goneAround = true;
    static int[] myProhibitedDirs = new int[] {0, 0};
    static MapLocation target=null;
    static boolean hugLeft;
    static boolean BLOCK_DIRS[][][]= new boolean[16][16][16];
    static MapLocation startLoc;
    static MapLocation desiredLoc;
    static Direction moved;
    static Direction startDesiredDir;

    /**
     * Setter method that sets the last direction moved
     * @param _moved						Parameter that is the previously moved direction
     *
     */
    public static void setMoved(Direction _moved){
        moved=_moved;
    }

    /**
     * Getter method to get the current target location
     * @return								Returns a MapLocation that is the current target
     *
     */
    public static MapLocation getTargetLoc() {
        return target;
    }


    /**
     * Method to flock in a desired direction if there is no obstacle approaching.
     * @param desiredDir					Parameter that is the desired direction to flock. i.e the target that is towards the target
     * @return								Returns a direction to flock to, returns null if no direction available.
     */
    private static Direction flockInDir(Direction desiredDir){
        Direction[] directions = new Direction[3];
        directions[0] = desiredDir;
        Direction left = desiredDir.rotateLeft();
        Direction right = desiredDir.rotateRight();
        boolean leftIsBetter = (_rc.getLocation().add(left).distanceSquaredTo(target) < _rc.getLocation().add(right).distanceSquaredTo(target));
        directions[1] = (leftIsBetter ? left : right);
        directions[2] = (leftIsBetter ? right : left);


        for (int i = 0; i < directions.length; i++){
            if (_rc.canMove(directions[i])){
                return directions[i];
            }
        }
        return null;
    }

    /**
     * Method to either rotate left or rotate right depending on the hugging boolean.
     * @param dir							Parameter that defines the direction to which unit will hug the wall.
     * @return								Returns a direction to turn into.
     */
    private static Direction turn(Direction dir){
        return (hugLeft ? dir.rotateRight() : dir.rotateLeft());
    }

    /**
     * Method to check if the unit can move to given direction if not prohibited.
     * @param dir							Parameter that defines a direction to move to.
     * @return								Returns true if it can move.
     */
    private static boolean canMove(Direction dir) {
        if (BLOCK_DIRS[myProhibitedDirs[0]][myProhibitedDirs[1]][dir.ordinal()]) {
            return false;
        }

        if (_rc.canMove(dir)) {
            return true;
        }
        return false;
    }

    /**
     * Method to hug the wall in one direction and if already recursed, try hugging the wall on opposite direction.
     * @param desiredDir					Parameter that is the direction of the target.
     * @param recursed						Parameter that checks whether it has already hugged the direction before, boolean parameter.
     * @return								Returns a direction to move to avoid obstacles by hugging the wall.
     * @throws GameActionException
     */
    private static Direction hug (Direction desiredDir, boolean recursed) throws GameActionException {
        if (canMove(desiredDir)) {
            return desiredDir;
        }

        Direction tryDir = turn(desiredDir);
        MapLocation tryLoc = _rc.getLocation().add(tryDir);

        for (int i = 0; i < 8 && !canMove(tryDir); i++) {
            tryDir = turn(tryDir);
            tryLoc = _rc.getLocation().add(tryDir);
        }

        // If the loop failed (found no directions or encountered the map edge)
        if (!canMove(tryDir)) {
            hugLeft = !hugLeft;
            if (recursed) {
                if(myProhibitedDirs[0]!=0 && myProhibitedDirs[1]!=0){
                    myProhibitedDirs[1]=0;
                    return hug(desiredDir,false);
                }else{
                    // Complete failure. Reset the state and start over.
                    state = STATE.FLOCKING;
                    //reset() LOOK LATER <-----------
                    return null;
                }
            }
            // mark "recursed" as true and try hugging the other direction
            return hug(desiredDir, true);
        }
        // If we're moving in a new cardinal direction, store it.
        if(!tryDir.equals(moved)&&!tryDir.isDiagonal()){
            myProhibitedDirs[1]=myProhibitedDirs[0];
            myProhibitedDirs[0]=tryDir.opposite().ordinal();
        }
        return tryDir;
    }

    /**
     * Method to check whether the state is in Flocking or Bugging, if the robot can moved directly towards
     * i.e to flock, if not, it turns into bugging mode and use the hug method to follow walls. It also adds the directions moved to prohibited
     * directions array
     * @return								Returns the direction that bug around a wall
     * @throws GameActionException
     */
    public static Direction getNextMove() throws GameActionException {
        Direction desiredDir = _rc.getLocation().directionTo(target);

        if (desiredDir == Direction.NONE || desiredDir == Direction.OMNI)
            return desiredDir;

        // If we are bugging around an object, see if we have gotten past it

        if (state == STATE.BUGGING) {
            // If we are closer to the target than when we started, and we can
            // move in the ideal direction, then we are past the object
            if (_rc.getLocation().distanceSquaredTo(target) < startLoc.distanceSquaredTo(target) && _rc.canMove(desiredDir)) {
                //goneAround = false;
                state = STATE.FLOCKING;
            }
        }

        switch(state) {
            case FLOCKING:
                Direction newDir = flockInDir(desiredDir);
                if (newDir != null)
                    return newDir;

                state = STATE.BUGGING;
                startLoc = _rc.getLocation();
                startDesiredDir = desiredDir;
                // intentional fallthrough
            case BUGGING:
                Direction moveDir = hug(desiredDir, false);
                if (moveDir == null) {
                    moveDir = desiredDir;
                }
                return moveDir;
        }
        return desiredDir;
    }

    /**
     * Setter method that sets target location to a given location
     * @param _target						Parameter that is the target location
     */
    public static void setTarget(MapLocation _target){
        target = _target;
    }

    /**
     * Method to initialize	the RobotController
     * @param rc            Parameter that is the BugNav RobotController (rc)
     */
    public static void initialise(RobotController rc){
        _rc=rc;

        for (Direction d: Direction.values()) {
            if (d == Direction.NONE || d == Direction.OMNI || d.isDiagonal())
                continue;
            for (Direction b: Direction.values()) {
                // Blocking a dir that is the first prohibited dir, or one
                // rotation to the side
                BLOCK_DIRS[d.ordinal()][b.ordinal()][d.ordinal()] = true;
                BLOCK_DIRS[d.ordinal()][b.ordinal()][d.rotateLeft().ordinal()] = true;
                BLOCK_DIRS[d.ordinal()][b.ordinal()][d.rotateRight().ordinal()] = true;
                // b is diagonal, ignore it
                if (!b.isDiagonal() && b != Direction.NONE && b != Direction.OMNI) {
                    // Blocking a dir that is the second prohibited dir, or one
                    // rotation to the side
                    BLOCK_DIRS[d.ordinal()][b.ordinal()][b.ordinal()] = true;
                    BLOCK_DIRS[d.ordinal()][b.ordinal()][b.rotateLeft().ordinal()] = true;
                    BLOCK_DIRS[d.ordinal()][b.ordinal()][b.rotateRight().ordinal()] = true;
                }
            }
        }
    }
}
