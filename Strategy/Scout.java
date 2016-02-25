
package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Signalling;
import victorious_secret.Robot;

import java.util.HashMap;
import java.util.Iterator;
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
    private static Map<Integer, MapLocation> zombieDens = new HashMap<>();
    private static Map<Integer, Boolean> zombieDensBroadcast = new HashMap<>();
    private static final int broadcastRange = 1000;
    private static RobotController rc;
    private static Robot robot;
    public static MapLocation dangerousLoc=null;
    public static double lastHealth;
    public static int turnWhileInDanger = 0;

    public static void initialise(RobotController _rc, Robot _robot) {
        robot = _robot;
        rc = _rc;
    }

    //======Identify=======

    /**
     * Identifies the zombie dens within sensor range and adds any new ones to the zombieDen list
     */
    public static void identify(){
        RobotInfo[] zD = Fight.spotUnitsOfType(RobotType.ZOMBIEDEN, Fight.seenZombies);

        if (zD == null) return;

        for (RobotInfo z : zD){
            if (!zombieDens.containsKey(z.ID)){
                zombieDens.put(z.ID, z.location);
                zombieDensBroadcast.put(z.ID, Boolean.FALSE);
            }
        }
    }

    /**
     * Broadcasts the location of all recently discovered Zombie Dens. The broadcast range is constant regardless of
     * map size.
     */
    public static void broadcast() throws GameActionException {
        Iterator<Map.Entry<Integer,Boolean>> iter = zombieDensBroadcast.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iter.next();
            if(!entry.getValue()) {
                //This has not been broadcast
                MapLocation zdLoc = zombieDens.get(entry.getKey());
                rc.broadcastMessageSignal(zdLoc.x + Signalling.zombieDenOffset, zdLoc.y + Signalling.zombieDenOffset,
                        broadcastRange);
                entry.setValue(true);
            }
        }
    }
    //====End Identify=====

    //=======Explore=======
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


}
