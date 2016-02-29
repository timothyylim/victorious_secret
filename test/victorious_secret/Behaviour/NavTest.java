package victorious_secret.Behaviour;

import battlecode.common.*;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ple15 on 29/02/16.
 */
public class NavTest {

    @Test
    public void testInitialise() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);
    }

    @Test
    public void testAverageLoc_RobotInfo() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        RobotInfo[] ri = new RobotInfo[3];
        ri[0] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.A, RobotType.SCOUT, new MapLocation(0, 100), 0, 0, 100, 100, 100, 0, 0);

        MapLocation avLoc = Nav.averageLoc(ri);

        assertEquals(avLoc.x, 100);
        assertEquals(avLoc.y, 100);
    }

    @Test
    public void testAverageLoc_MapLocation() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation[] ml = new MapLocation[3];
        ml[0] = new MapLocation(100, 100);
        ml[1] = new MapLocation(200, 100);
        ml[2] = new MapLocation(0, 100);

        MapLocation avLoc = Nav.averageLoc(ml);

        assertEquals(avLoc.x, 100);
        assertEquals(avLoc.y, 100);
    }

    @Test
    public void testGuard_notReady() throws Exception {
        RobotController rc = new RobotController(false, false);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation archonLoc = new MapLocation(100, 100);

        Nav.guard(archonLoc);
    }

    @Test
    public void testGuard_ready() throws Exception {
        RobotController rc = new RobotController();
        rc.setCoreReady(true);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);
        Fight.initialise(rc, r);
        BugNav.initialise(rc);
        //Fight.sense_map();

        MapLocation archonLoc = new MapLocation(100, 100);

        Nav.guard(archonLoc);
    }

    @Test
    public void testSpiralClockwise() throws Exception {
        MapLocation rcLoc = new MapLocation(0, 10);
        RobotController rc = new RobotController();
        rc.setLocation(rcLoc);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation center = new MapLocation(0, 0);
        MapLocation target = Nav.spiralClockwise(center);

        assertEquals(1, target.x);
        assertEquals(9, target.y);
    }

    @Test
    public void testSpiralClockwise_null() throws Exception {
        MapLocation rcLoc = new MapLocation(0, 10);
        RobotController rc = new RobotController();
        rc.setLocation(rcLoc);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation center = null;
        MapLocation target = Nav.spiralClockwise(center);
        assertNull(target);
    }

    @Test
    public void testSpiralAntiClockwise() throws Exception {
        MapLocation rcLoc = new MapLocation(0, 10);
        RobotController rc = new RobotController(rcLoc);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation center = new MapLocation(0, 0);
        MapLocation target = Nav.spiralAntiClockwise(center);

        assertEquals(-1, target.x);
        assertEquals(9, target.y);
    }

    @Test
    public void testSpiralAntiClockwise_null() throws Exception {
        MapLocation rcLoc = new MapLocation(0, 10);
        RobotController rc = new RobotController();
        rc.setLocation(rcLoc);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation center = null;
        MapLocation target = Nav.spiralAntiClockwise(center);

        assertNull(target);
    }

    @Test
    public void testFindAllowedLocations() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        Robot r = new Dummy(rc);
        Nav.initialise(rc, r);

        MapLocation here = new MapLocation(0, 0);
        MapLocation target = new MapLocation(0, 20);
        int radiusFromHere = 10;

        List<MapLocation> ml = Nav.findAllowedLocations(here, radiusFromHere, target);

        System.out.print(ml);
    }

    @Test
    public void testFindBestMove() throws Exception {

    }

    @Test
    public void testMoveOrClear() throws Exception {

    }

    @Test
    public void testMoveAlongRadiusLarger() throws Exception {

    }

    @Test
    public void testMoveAlongRadiusSmaller() throws Exception {

    }

    @Test
    public void testTurnsToClear() throws Exception {

    }

    @Test
    public void testGetAllowedDirectionsLarger() throws Exception {

    }

    @Test
    public void testGetAllowedDirectionsSmaller() throws Exception {

    }

    @Test
    public void testMoveToFreeLocation() throws Exception {

    }
}