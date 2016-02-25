
package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Robot;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ple15 on 15/01/16.
 */
public class Scout {
    /**
     * The Scout is constructed from a RobotController and a Robot.
     * Key variables are assigned.
     * @param _rc The RobotController
     * @param _robot The Robot
     */
    public static Map<Integer, MapLocation> zombieDens = new HashMap<>();
    private static RobotController rc;
    private static Robot robot;
    public static MapLocation dangerousLoc=null;
    public static double lastHealth;
    public static int turnWhileInDanger = 0;
    public static void init(RobotController _rc, Robot _robot) {
        robot = _robot;
        rc = _rc;
    }

    //======Identify=======
    private static void identify(){
        RobotInfo[] zD = Fight.spotUnitsOfType(RobotType.ZOMBIEDEN, Fight.seenZombies);

        if (zD == null) return;

        for (RobotInfo z : zD){
            if (!zombieDens.containsKey(z.ID)){
                zombieDens.put(z.ID, z.location);
            }
        }
    }
    
    public static void dangerLocUpdate(){
    	if(rc.getHealth()<lastHealth){
    		dangerousLoc = rc.getLocation();
    		turnWhileInDanger=0;
    	}else if(dangerousLoc!= null){
    		if (turnWhileInDanger>200){
    			dangerousLoc=null;
    			turnWhileInDanger = 0;
    		}
    		turnWhileInDanger++;
    	}
    	lastHealth = rc.getHealth();
    }

    public static void moveAround() throws GameActionException{
    	dangerLocUpdate();
    	if(!rc.isCoreReady()) {
    		return;
    	}
    	Direction[] dirs = new Direction[9];
    	boolean[] canMoves = new boolean[9];
    	MapLocation[] locations = new MapLocation[9];
    	double[] attacks = new double[9];
    	double[] scouts  = new double[9];
    	double[] scores = new double[9];
    	dirs[0]=null;
    	canMoves[0]=true;
    	locations[0] = rc.getLocation();
    	int ndirs=1;
    	
    	for (int i=0;i<8;i++){
    		dirs[ndirs] = Direction.values()[i];
    		canMoves[ndirs]= rc.canMove(dirs[ndirs]);
    		if(canMoves[ndirs]){
    			locations[ndirs] = rc.getLocation().add(dirs[ndirs]);
    			ndirs++;
    		}
    	}
    	
    	RobotInfo[] infos = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
    	
    	
    	
    }

}
