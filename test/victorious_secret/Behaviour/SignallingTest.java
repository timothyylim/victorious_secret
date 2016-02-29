package victorious_secret.Behaviour;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Signal;
import org.junit.Test;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;

import static org.junit.Assert.*;

/**
 * Created by ple15 on 29/02/16.
 */
public class SignallingTest {

    @Test
    public void testIntialize() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);
    }

    @Test
    public void testEncodeMessage_100_100() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        int[] message = Signalling.encodeMessage(ml, RobotType.ZOMBIEDEN);
        assertArrayEquals(message, new int[]{100 - 25459, 100 - 25459});
    }

    @Test
    public void testEncodeMessage_m100_m100() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(-100, -100);

        int[] message = Signalling.encodeMessage(ml, RobotType.ZOMBIEDEN);

        assertArrayEquals(message, new int[]{-100 - 25459, -100 - 25459});
    }

    @Test
    public void testEncodeMessage_ARCHON() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        int[] message = Signalling.encodeMessage(ml, RobotType.ARCHON);
        assertNull(message);
    }

    @Test
    public void testBroadcastMessage_default() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        int[] message = Signalling.encodeMessage(ml, RobotType.ZOMBIEDEN);
        Signalling.broadcastMessage(message);
    }

    @Test
    public void testBroadcastMessage_ranged() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        int[] message = Signalling.encodeMessage(ml, RobotType.ZOMBIEDEN);
        Signalling.broadcastMessage(message, 100);
    }

    @Test
    public void testEncodeAndBroadcast_default() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        Signalling.encodeAndBroadcast(ml, RobotType.ZOMBIEDEN);
    }

    @Test
    public void testEncodeAndBroadcast_ranged() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        Signalling.encodeAndBroadcast(ml, RobotType.ZOMBIEDEN, 100);
    }

    @Test
    public void testParseMessage() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Signalling.initialise(rc, r);

        MapLocation ml = new MapLocation(100, 100);

        int[] message = Signalling.encodeMessage(ml, RobotType.ZOMBIEDEN);
        int[] decoded = Signalling.decodeMessage(message[0], message[1]);
        assertArrayEquals(decoded, new int[]{100, 100, 0});
    }
}