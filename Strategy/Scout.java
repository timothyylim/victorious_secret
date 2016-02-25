
package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Nav;

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
    public static void init(RobotController _rc, Robot _robot) {
        robot = _robot;
        rc = _rc;
    }
    
    public static void explore(){
    	
    }
}
