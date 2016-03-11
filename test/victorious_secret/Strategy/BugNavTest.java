package victorious_secret.Behaviour;
 
import org.junit.Test;
 
import static org.junit.Assert.*;
 
import battlecode.common.*;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import victorious_secret.Robot;
import victorious_secret.Testing_stubs.Dummy;
import victorious_secret.Testing_stubs.RobotController;
 
import java.util.List;
 
import static org.junit.Assert.*;
 
/**
 * Created by doddarliu on 01/03/2016.
 */
public class BugNavTest {
 
    @Test
    public void testInitialise() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        BugNav.initialise(rc);
    }
 
    @Test
    public void testSetMoved() throws Exception {
        RobotController rc = new RobotController();
        BugNav.initialise(rc);
 
        Direction dir = Direction.EAST;
 
        BugNav.setMoved(dir);
 
        dir = BugNav.getMovedDir();
 
        assertEquals(dir,Direction.EAST);
 
    }
 
    @Test
    public void testGetTargetLoc() throws Exception {
        RobotController rc = new RobotController();
        BugNav.initialise(rc);
 
        MapLocation rcLoc = new MapLocation(0,10);
 
        BugNav.setTarget(rcLoc);
 
        MapLocation rcloc2 =  BugNav.getTargetLoc();
 
        assertEquals(rcLoc,rcloc2);
 
    }
 
    @Test
    public void testGetNextMove_OMNI() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        BugNav.initialise(rc);
        MapLocation rcLoc = new MapLocation(0,10);
        rc.setLocation(rcLoc);
 
        BugNav.target = rcLoc;
 
        Direction dir = BugNav.getNextMove();
 
        assertEquals(Direction.OMNI,dir);
 
    }
 
    @Test
    public void testGetNextMove_NONE() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        //rc.setCanMove(false);
        BugNav.initialise(rc);
        MapLocation rcLoc = new MapLocation(0,10);
 
        rc.setLocation(rcLoc);
//
//        BugNav.target = null;
        Direction d2 = rc.getLocation().directionTo(new MapLocation(1,10));
        System.out.println(d2.ordinal());
//        Direction dir = BugNav.getNextMove();
//
//        assertEquals(Direction.NONE,dir);
 
    }
 
    @Test
    public void testGetNextMove_FLOCKING() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
 
        BugNav.initialise(rc);
        MapLocation rcLoc = new MapLocation(0,10);
        MapLocation TarLoc = new MapLocation(0,20);
        rc.setLocation(rcLoc);
 
        BugNav.target = TarLoc;
 
        Direction dir = BugNav.getNextMove();
 
        assertEquals(Direction.SOUTH,dir);
 
    }
 
    @Test
    public void testGetNextMove_BUGGING() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        BugNav.initialise(rc);
        MapLocation rcLoc = new MapLocation(0,10);
        MapLocation TarLoc = new MapLocation(-10,10);
        rc.setLocation(rcLoc);
 
        BugNav.target = TarLoc;
 
        Direction dir = BugNav.getNextMove();
 
        assertEquals(Direction.WEST,dir);
    }
 
    @Test
    public void testSetTarget() throws Exception {
        RobotController rc = new RobotController();
        Robot r = new Dummy(rc);
        BugNav.initialise(rc);
 
        MapLocation loc = new MapLocation(100,100);
        BugNav.setTarget(loc);
 
        assertEquals(BugNav.target,loc);
    }
}