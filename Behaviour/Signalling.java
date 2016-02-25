package victorious_secret.Behaviour;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import victorious_secret.Robot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ple15 on 15/01/16.
 */
public class Signalling {
    private static RobotController rc;
    private static Robot robot;

    public static final int zombieDenOffset = -16000;

    /**
     * Initalises the Signalling controller for use as static class
     * @param _rc The Robot Controller
     * @param _robot The Robot type
     */
    public static void intialize(RobotController _rc, Robot _robot)
    {
        rc = _rc;
        robot = _robot;
    }



}
