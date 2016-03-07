package victorious_secret.Testing_stubs;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Robot;
import victorious_secret.Strategy.Defend;
import victorious_secret.Strategy.Flee;

import java.util.Random;

/**
 * Created by ple15 on 25/02/16.
 */
public class Dummy extends Robot {
    public Dummy(RobotController _rc) {
        rc = _rc;
    }

    @Override
    public void move() throws GameActionException {

    }

}