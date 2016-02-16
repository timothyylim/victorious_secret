package victorious_secret.Behaviour;

/**
 * Created by APOC on 16/02/2016.
 */

import battlecode.common.*;
/**
 *
 * @author Karlson Lee And Mohammed Al-Hakim
 *
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
     *
     * @param _moved        _moved is the previously moved direction
     *
     */
    public static void setMoved(Direction _moved){
        moved=_moved;
    }

    /**
     *
     * @return              It returns a Maplocation that is current set target
     *
     */
    public static MapLocation getTargetLoc() {
        return target;
    }


    /**
     *
     * @param desiredDir            Input the desired direction to flock. i.e the target that is towards the target
     * @return                      return a direction to flock, return null if no direction avalible
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
     *
     * @param dir
     * @return
     */
    private static Direction turn(Direction dir){
        return (hugLeft ? dir.rotateRight() : dir.rotateLeft());
    }

    /**
     *
     * @param dir               check if the direction is moveable
     * @return                  return true if possible, otherwise false
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
     *
     * @param desiredDir                    The direction to hug the wall
     * @param recursed                      Boolean check whether it hugged a direction before
     * @return
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
     *
     * @return                  Return the Direction that bug around a wall
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
     *
     * @param _target           It set the target location, the location to arrive to.
     */
    public static void setTarget(MapLocation _target){
        target = _target;
    }

    /**
     *
     * @param rc            Initiatise the BugNav with the rc Robotcontroller
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

