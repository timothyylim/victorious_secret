package victorious_secret.Strategy;

<<<<<<< HEAD
/**
 * Created by ple15 on 15/01/16.
 */
public class Flee {
=======
import battlecode.common.*;

public class Flee {
	static RobotController _rc;
    static public enum STATE {BUGGING, FLOCKING};
    static STATE state= STATE.FLOCKING;
    static boolean goneAround = true;
    static int[] myProhibitedDirs = new int[] {0, 0};
    static MapLocation startLoc;
    static MapLocation desiredLoc;
    static MapLocation target= new MapLocation(57,93);
    static Direction startDesiredDir;
    static boolean hugLeft;
    static boolean BLOCK_DIRS[][][]= new boolean[16][16][16];
    static Direction moved;
    
    public static void initialiseFlee(RobotController rc){
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
    
    public static void setTarget(MapLocation _target){
    	target=_target;
    }
    
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
 
    private static Direction turn(Direction dir){
        return (hugLeft ? dir.rotateRight() : dir.rotateLeft());
    }
 
    private static Direction hug (Direction desiredDir, boolean recursed) throws GameActionException {
        if (canMove(desiredDir)) {
            return desiredDir;
        }
 
        Direction tryDir = turn(desiredDir);
        
        for (int i = 0; i < 8 && !canMove(tryDir); i++) {
            tryDir = turn(tryDir);
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
 
    private static boolean canMove(Direction dir) {
    	System.out.println(myProhibitedDirs[0]);
    	System.out.println(myProhibitedDirs[1]);
        if (BLOCK_DIRS[myProhibitedDirs[0]][myProhibitedDirs[1]][dir.ordinal()]) {
            return false;
        }
  
        if (_rc.canMove(dir)) {
            return true;
        }
        return false;
    }
    
    public static MapLocation averageLoc(RobotInfo[] listOfEnemies)
    { 
        if(listOfEnemies.length == 0) //robot.fight.seenEnemies == null ||
        {
            return _rc.getLocation();
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
            return _rc.getLocation();
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
 
    public static RobotInfo[] joinRobotInfo(RobotInfo[] zombieEnemies, RobotInfo[] normalEnemies) {
        RobotInfo[] opponentEnemies = new RobotInfo[zombieEnemies.length+normalEnemies.length];
        int index = 0;
        for(RobotInfo i:zombieEnemies){
            opponentEnemies[index]=i;
            index++;
        }
        for(RobotInfo i:normalEnemies){
            opponentEnemies[index]=i;
            index++;
        }
        return opponentEnemies;
    }

>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0
}
