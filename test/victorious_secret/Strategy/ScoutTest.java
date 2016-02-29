package victorious_secret.Strategy;

import battlecode.common.GameActionException;
import org.junit.Assert;
import org.junit.Test;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;

/**
 * Created by ple15 on 25/02/16.
 */
public class ScoutTest {
    @Test
    public void testInit(){
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Scout.initialise(rc, r);
    }

    @Test
    public void testIdentify(){
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);

        Scout.initialise(rc, r);

        Scout.identify();
    }

    @Test
    public void testBroadcast(){
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);

        Scout.initialise(rc, r);

        try {
            Scout.broadcast();
        } catch (GameActionException e) {
            Assert.fail();
        }
    }
}