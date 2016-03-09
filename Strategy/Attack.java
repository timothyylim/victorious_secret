package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;

/**
 * Created by ple15 on 15/01/16.
 */
public class Attack extends Fight {
    private static MapLocation targetArchon;
    private static RobotInfo leader;

    /**
     * Initalises the Attack controller for use as static class
     * @param _rc The Robot Controller
     * @param _robot The Robot type
     */
    public static void initialise(RobotController _rc, Robot _robot) {
        rc = _rc;
        robot = _robot;
    }

    /**
     * Initalises the Attack controller for use as non-static class
     * @param _rc The Robot Controller
     * @param _robot The Robot type
     * @deprecated
     */
    public Attack(RobotController _rc, Robot _robot) {
        super(_rc, _robot);
        BugNav.initialise(_rc);
    }

    public static void attack() throws GameActionException {
        if(targetArchon == null || locationClear(targetArchon)) {

            setTargetArchon();

            if(targetArchon == null) {
                robot.strat = Robot.Strategy.DEFEND;
                return;
            }
        }

        if(attackableEnemies != null && attackableEnemies.length > 0 && rc.isWeaponReady()) {
            lowestHealthAttack();
        }
        else {
            followLeader();
            if (rc.isCoreReady()){
                rc.move(BugNav.getNextMove());
            }
        }
    }

    /***
     * This function instructs the robot to move as close as possible to the target
     * @param target
     * @throws GameActionException
     */
    public static void getClose(RobotInfo target) throws GameActionException {
        if(rc.getLocation().isAdjacentTo(target.location)) {
            return;
        }

        BugNav.setTarget(target.location);
        Direction dir = BugNav.getNextMove();
        if(rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    /***
     * This function sets the closest enemy Archon as a target, based on the known
     * Archon locations
     */
    private static void setTargetArchon()
    {
        MapLocation[] archonLocs = new MapLocation[robot.enemyArchonLocations.size()];
        int i = 0;
        for(MapLocation m : robot.enemyArchonLocations.values()) {
            archonLocs[i] = m;
            i++;
        }
        targetArchon = Fight.findClosestMapLocation(archonLocs, rc.getLocation());
    }

    //TODO: Tim to create better setLeader

    /***
     * This function sets a particular soldier as a leader who coordinates behaviour
     * @throws GameActionException
     */
    private static void setLeader() throws GameActionException {
        if(targetArchon != null) {
            RobotInfo[] allies = Fight.spotAlliesOfType(RobotType.SOLDIER);
            RobotInfo[] alliesAndI;

            if(allies == null) {
                alliesAndI = new RobotInfo[1];
                alliesAndI[0] = rc.senseRobot(rc.getID());// RobotInfo();//rc.getID(), rc.getTeam(), rc.getType(), rc.getLocation(), rc.getCoreDelay(), rc.getWeaponDelay(), rc.getType().attackPower, rc.getHealth(), rc.getType().maxHealth, );
            }
            else {
                alliesAndI = new RobotInfo[allies.length + 1];

                for (int i = 0; i < allies.length; i++) {
                    alliesAndI[i] = allies[i];
                }
                alliesAndI[allies.length] = rc.senseRobot(rc.getID());// RobotInfo();//rc.getID(), rc.getTeam(), rc.getType(), rc.getLocation(), rc.getCoreDelay(), rc.getWeaponDelay(), rc.getType().attackPower, rc.getHealth(), rc.getType().maxHealth, );
            }
            leader = Fight.findClosestEnemy(alliesAndI, targetArchon);
        }
    }

    /***
     * This function instructs the robot to move and follow a target leader, either an
     * Archon or a leader soldier
     * @throws GameActionException
     */
    private static void followLeader() throws GameActionException {
        setLeader();
        if(leader.ID == rc.getID()) {
            //I am the leader - move to the target Archon
            robot.targetMoveLoc = targetArchon;
        }
        else{
            // Someone else is the leader, follow them
            robot.targetMoveLoc = leader.location;
        }
    }

    /***
     * This function checks if a possible Archon location is clear of enemy units.
     * @param m The location the Archon should be
     * @return True if the location is clear
     */
    private static boolean locationClear(MapLocation m){
        if(rc.canSense(m) && (seenEnemies == null || seenEnemies.length == 0)) {
            //Then the location is clear, remove the location from our list of target Archon Locations
            robot.removeArchonLocation(m);
            return true;
        }
        return false;
    }
}




