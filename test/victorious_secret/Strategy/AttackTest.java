package victorious_secret.Strategy;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import org.junit.Test;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;

import static org.junit.Assert.*;

/**
 * Created by ple15 on 07/03/16.
 */
public class AttackTest {

    @Test
    public void testInitialise() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);
    }

    @Test
    public void testAttack_lowest_health_attack() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(true);

        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Fight.targetEnemies();

        Attack.attack();
    }

    @Test
    public void testAttack_not_leader() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(true);
        rc.setID(1);

        RobotInfo[] ri = new RobotInfo[3];

        ri[0] = new RobotInfo(5, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(6, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(7, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        RobotInfo[] ra = new RobotInfo[5];
        ra[0] = ri[0];
        ra[1] = ri[1];
        ra[2] = ri[2];
        ra[3] = new RobotInfo(11, Team.A, RobotType.SOLDIER, new MapLocation(101, 101), 0, 0, 100, 100, 100, 0, 0);
        ra[4] = new RobotInfo(12, Team.A, RobotType.SOLDIER, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        rc.setNearbyRobots(ra);
        Fight.spotEnemies();
        Fight.spotAllies();

        MapLocation[] ml = new MapLocation[1];
        ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(ml);

        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);
        r.setArchonLocations();

        Attack.attack();
        rc.setID(2);
        Attack.attack();
    }

    @Test
    public void testAttack_location_clear() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(true);
        rc.setID(1);
        rc.setCanSense(true);

        MapLocation[] ml = new MapLocation[1];
        ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(ml);

        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);
        r.setArchonLocations();

        rc.setHostileRobots(null);
        Fight.targetEnemies();

        Attack.attack();
        Attack.attack();
    }
    @Test
    public void testAttack_location_not_clear() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(true);
        rc.setID(1);
        rc.setCanSense(false);

        MapLocation[] ml = new MapLocation[1];
        ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(ml);

        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);

        r.setArchonLocations();

        rc.setHostileRobots(null);
        Fight.targetEnemies();

        System.out.println("MADE IT HERE");

        Attack.attack();
        Attack.attack();
    }
    @Test
    public void testAttack_robot_defend() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(false);
        rc.setID(1);

        MapLocation[] ml = new MapLocation[1];
        ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(null);

        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);
        r.setArchonLocations();
        rc.setHostileRobots(null);

        Fight.targetEnemies();

        Attack.attack();
    }
    @Test
    public void testAttack_weapon_not_ready() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(false);

        MapLocation[] ml = new MapLocation[1];
        ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(ml);
        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);

        r.setArchonLocations();

        RobotInfo[] ri = new RobotInfo[3];
        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Fight.targetEnemies();

        Attack.attack();
    }

    @Test
    public void testAttack_weapon_not_ready_core_is_ready() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);
        BugNav.initialise(rc);
        BugNav.setTarget(new MapLocation(95, 100));

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(false);
        rc.setCoreReady(true);

        MapLocation[] ml = new MapLocation[1];
        ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(ml);
        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);

        r.setArchonLocations();

        RobotInfo[] ri = new RobotInfo[3];
        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Fight.targetEnemies();

        Attack.attack();
    }

    @Test
    public void testAttack_weapon_not_ready_core_is_ready_no_archon() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);
        BugNav.initialise(rc);
        BugNav.setTarget(new MapLocation(95, 100));

        rc.setType(RobotType.SOLDIER);
        rc.setLocation(new MapLocation(100, 100));
        rc.setTeam(Team.A);
        rc.setWeaponReady(false);
        rc.setCoreReady(true);

        MapLocation[] ml = new MapLocation[1];
        // ml[0] = new MapLocation(95, 100);
        rc.setInitialEnemyArchonsLoc(ml);
        MapLocation[] ml2 = new MapLocation[1];
        ml[0] = new MapLocation(95, 95);
        rc.setInitialFriendlyArchonsLoc(ml2);

        r.setArchonLocations();

        RobotInfo[] ri = new RobotInfo[3];
        ri[0] = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[1] = new RobotInfo(1, Team.ZOMBIE, RobotType.ZOMBIEDEN, new MapLocation(200, 100), 0, 0, 100, 100, 100, 0, 0);
        ri[2] = new RobotInfo(1, Team.B, RobotType.GUARD, new MapLocation(110, 100), 0, 0, 100, 100, 100, 0, 0);

        rc.setHostileRobots(ri);
        Fight.targetEnemies();

        Attack.attack();
    }

    @Test
    public void testGetClose_adjacent() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        Attack.initialise(rc, r);

        rc.setLocation(new MapLocation(100, 100));
        RobotInfo target = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);

        Attack.getClose(target);
    }

    @Test
    public void testGetClose_not_adjacent() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        BugNav.initialise(rc);
        Attack.initialise(rc, r);

        rc.setLocation(new MapLocation(100, 100));
        RobotInfo target = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(105, 100), 0, 0, 100, 100, 100, 0, 0);

        Attack.getClose(target);
    }

    @Test
    public void testGetClose_not_adjacent_can_ove() throws Exception {
        RobotController rc = new RobotController();
        rc.setCanMove(true);
        Robot r = new Dummy(rc);
        Fight.initialise(rc, r);
        BugNav.initialise(rc);
        Attack.initialise(rc, r);

        rc.setLocation(new MapLocation(100, 100));
        RobotInfo target = new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(105, 100), 0, 0, 100, 100, 100, 0, 0);

        Attack.getClose(target);
    }
}