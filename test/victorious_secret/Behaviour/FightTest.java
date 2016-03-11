package victorious_secret.Behaviour;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import org.junit.Test;
import org.junit.internal.requests.FilterRequest;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ple15 on 01/03/16.
 */
public class FightTest {

    @Test
    public void testInitialise() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
    }

    @Test
    public void testSense_map() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));

        RobotInfo[] ri = new RobotInfo[3];
        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(0, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);

        RobotInfo[] ro = new RobotInfo[3];
        ro[0] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ro[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ro[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ro);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.sense_map();
    }

    @Test
    public void testSpotEnemies() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(0, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotEnemies();

        assertArrayEquals(ri, Fight.seenEnemies);

    }

    @Test
    public void testSpotEnemies_null() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotEnemies();

        assertNull(Fight.seenEnemies);

    }

    @Test
    public void testSpotAllies() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        RobotInfo[] ra = new RobotInfo[1];

        ra[0] = ri[0];

        Fight.spotAllies();

        assertArrayEquals(ra, Fight.seenAllies);
    }

    @Test
    public void testSpotZombies() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotZombies();

        RobotInfo[] rz = new RobotInfo[1];
        rz[0] = ri[1];

        assertArrayEquals(rz, Fight.seenZombies);
    }

    @Test
    public void testSpotOpponents() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotOpponents();

        RobotInfo[] ro = new RobotInfo[1];
        ro[0] = ri[2];

        assertArrayEquals(ro, Fight.seenOpponents);
    }

    @Test
    public void testTargetEnemies() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(0, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.targetEnemies();

        assertArrayEquals(ri, Fight.attackableEnemies);

    }

    @Test
    public void testTargetEnemies_null() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));

        rc.setHostileRobots(null);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.targetEnemies();

        assertNull(Fight.attackableEnemies);

    }

    @Test
    public void testSpotNearbyTurrets() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.TURRET, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.A, RobotType.TTM, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotAllies();

        RobotInfo[] ra = new RobotInfo[2];
        ra[0] = ri[1];
        ra[1] = ri[0];

        assertArrayEquals(ra, Fight.spotNearbyTurrets());
    }

    @Test
    public void testSpotNearbyTurrets_noTurrets() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.SOLDIER, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Fight.spotAllies();

        assertNull(Fight.spotNearbyTurrets());
    }

    @Test
    public void testSpotNearbyTurrets_null() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Fight.spotAllies();

        assertNull(Fight.spotNearbyTurrets());
    }

    @Test
    public void testSpotAlliesOfType() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.SOLDIER, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        rc.setNearbyRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Fight.spotAllies();

        RobotInfo[] ra = new RobotInfo[1];
        ra[0] = ri[0];

        assertArrayEquals(ra, Fight.spotAlliesOfType(RobotType.SOLDIER));
    }

    @Test
    public void testSpotUnitsOfType() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.SOLDIER, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        RobotInfo[] ra = new RobotInfo[1];
        ra[0] = ri[0];

        assertArrayEquals(ra, Fight.spotUnitsOfType(RobotType.SOLDIER, ri));
    }

    @Test
    public void testSpotUnitsOfType_noType() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.GUARD, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertNull(Fight.spotUnitsOfType(RobotType.SOLDIER, ri));
    }

    @Test
    public void testSpotUnitsOfType_null() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = null;

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertNull(Fight.spotUnitsOfType(RobotType.SOLDIER, ri));
    }

    @Test
    public void testInRangeOf() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setTeam(Team.A);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.A, RobotType.GUARD, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.A, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(100, 101), 0, 0, 100, 100, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        RobotInfo[] rj = new RobotInfo[2];
        rj[0] = ri[0];
        rj[1] = ri[1];

        assertArrayEquals(rj, Fight.inRangeOf(ri, rc.getLocation()));
    }

    @Test
    public void testInRangeOf1() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotEnemies();

        RobotInfo[] rj = new RobotInfo[2];
        rj[0] = ri[0];
        rj[1] = ri[2];

        assertArrayEquals(rj, Fight.inRangeOf());
    }


    @Test
    public void testLocationUnderThreat() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotEnemies();

        RobotInfo[] rj = new RobotInfo[2];
        rj[0] = ri[0];
        rj[1] = ri[2];

        assertTrue(Fight.locationUnderThreat(ri, rc.getLocation()));
    }

    @Test
    public void testFindClosestEnemy() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotEnemies();

        RobotInfo[] rj = new RobotInfo[2];
        rj[0] = ri[0];
        rj[1] = ri[2];

        assertEquals(ri[0], Fight.findClosestEnemy(ri, rc.getLocation()));
    }

    @Test
    public void testFindClosestEnemy1() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.spotEnemies();

        RobotInfo[] rj = new RobotInfo[2];
        rj[0] = ri[0];
        rj[1] = ri[2];

        assertEquals(ri[0], Fight.findClosestEnemy(ri));

    }

    @Test
    public void testFindClosestMapLocation() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        MapLocation[] ml = new MapLocation[3];
        ml[0] = new MapLocation(100, 100);
        ml[1] = new MapLocation(200, 100);
        ml[2] = new MapLocation(0, 100);

        assertEquals(ml[0], Fight.findClosestMapLocation(ml, new MapLocation(100, 101)));
    }

    @Test
    public void testFindClosestMapLocation1() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Collection<MapLocation> ml = new HashSet<>();
        ml.add(new MapLocation(100, 100));
        ml.add(new MapLocation(200, 100));
        ml.add(new MapLocation(0, 100));

        assertEquals(new MapLocation(100, 100), Fight.findClosestMapLocation(ml, new MapLocation(100, 101)));

    }

    @Test
    public void testFindClosestFreeMapLocation() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        List<MapLocation> ml = new ArrayList<>();
        ml.add(new MapLocation(100, 100));
        ml.add(new MapLocation(200, 100));
        ml.add(new MapLocation(0, 100));

        assertEquals(new MapLocation(100, 100), Fight.findClosestFreeMapLocation(ml, new MapLocation(100, 101)));
    }

    @Test
    public void testHasClearMapLocation() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        List<MapLocation> ml = new ArrayList<>();
        ml.add(new MapLocation(100, 100));
        ml.add(new MapLocation(200, 100));
        ml.add(new MapLocation(0, 100));

        assertTrue(Fight.hasClearMapLocation(ml, new MapLocation(100, 101)));
    }

    @Test
    public void testHasClearMapLocation_not_clear() throws Exception {
        RobotController rc = new RobotController();
        rc.setRubble(120);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        List<MapLocation> ml = new ArrayList<>();
        ml.add(new MapLocation(100, 100));
        ml.add(new MapLocation(200, 100));
        ml.add(new MapLocation(0, 100));

        assertFalse(Fight.hasClearMapLocation(ml, new MapLocation(100, 101)));
    }

    @Test
    public void testFindLastTargeted() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);

        Fight.initialise(rc, r);
        Fight.lastTargeted = ri[0];
        Fight.spotEnemies();

        assertEquals(ri[0], Fight.findLastTargeted(ri));
    }

    @Test
    public void testFindLastTargeted_null() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);

        Fight.initialise(rc, r);
        Fight.lastTargeted = null;
        Fight.spotEnemies();

        assertNull(Fight.findLastTargeted(ri));
    }

    @Test
    public void testFindLastTargeted_notPresent() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        RobotInfo target = new RobotInfo(4, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);

        Fight.initialise(rc, r);
        Fight.lastTargeted = target;
        Fight.spotEnemies();

        assertNull(Fight.findLastTargeted(ri));
    }

    @Test
    public void testTargetLastTargeted() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setCanAttackLoc(true);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);

        Fight.initialise(rc, r);
        Fight.lastTargeted = ri[0];
        Fight.spotEnemies();

        assertEquals(ri[0], Fight.targetLastTargeted(ri));
    }

    @Test
    public void testFindLowestHealthEnemy() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.findLowestHealthEnemy(ri));
    }

    @Test
    public void testFindLowestHealthEnemy1() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.findLowestHealthEnemy(ri, RobotType.GUARD));
    }

    @Test
    public void testTargetLowestHealthEnemy() throws Exception {
        RobotController rc = new RobotController();
        rc.setCanAttackLoc(true);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.targetLowestHealthEnemy(ri));
    }

    @Test
    public void testTargetLowestHealthEnemy1() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.targetLowestHealthEnemy(ri, RobotType.GUARD));
    }

    @Test
    public void testFindLowestHealthEnemyWithDelay() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 2, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[0], Fight.findLowestHealthEnemyWithDelay(ri));
    }

    @Test
    public void testFindLowestHealthUninfectedEnemy() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];
        rc.setCanAttackLoc(true);

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 1);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.findLowestHealthUninfectedEnemy(ri));
    }

    @Test
    public void testTargetLowestHealthUninfectedEnemy() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];
        rc.setCanAttackLoc(true);

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 1);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.targetLowestHealthUninfectedEnemy(ri));
    }

    @Test
    public void testFindLowestHealthNonTerminalEnemy() throws Exception {
        RobotController rc = new RobotController();
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 1);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.findLowestHealthNonTerminalEnemy(ri));
    }

    @Test
    public void testTargetLowestHealthNonTerminalEnemy() throws Exception {
        RobotController rc = new RobotController();
        rc.setCanAttackLoc(true);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 1);
        ri[1] = new RobotInfo(2, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(3, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 1, 100, 0, 0);

        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        assertEquals(ri[2], Fight.targetLowestHealthNonTerminalEnemy(ri));
    }

    @Test
    public void testLowestHealthAttack() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setCanAttackLoc(true);
        rc.setLocation(new MapLocation(100, 100));
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(0, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.targetEnemies();

        assertTrue(Fight.lowestHealthAttack());
    }

    @Test
    public void testStandardAttack() throws Exception {
        RobotController rc = new RobotController();
        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setCanAttackLoc(true);
        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(100, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.SCOUT, new MapLocation(0, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);

        Fight.targetEnemies();

        assertTrue(Fight.standardAttack());
    }
}