package team099.Strategy;

import battlecode.common.*;
import team099.Robot;
import team099.Behaviour.Fight;


/**
 * Created by ple15 on 15/01/16.
 */
public class Attack extends Fight
{
    private static MapLocation targetArchon;
    private static RobotInfo leader;

    public Attack(RobotController _rc, Robot _robot) {
        super(_rc, _robot);
    }

    public static void attack() throws GameActionException {
        if(targetArchon == null || locationClear(targetArchon))
        {
            setTargetArchon();
            if(targetArchon == null)
            {
                robot.strat = Robot.Strategy.DEFEND;
            }
        }

        seenEnemies = spotEnemies();
        attackableEnemies = targetEnemies();

        if(attackableEnemies.length > 0 && rc.isWeaponReady()) {
            lowestHealthAttack();
        }
        else
        {
            followLeader();
            robot.nav.move();
        }
    }

    public static boolean kiteStratgey() throws GameActionException
    {
        seenEnemies = spotEnemies();
        attackableEnemies = targetEnemies();

        if(seenEnemies.length > 0) {
            MapLocation avgL = robot.nav.averageLoc(seenEnemies);
            robot.sig.setMessage(robot.sig.FromDirection(rc.getLocation().directionTo(avgL)));
        }

        if(attackableEnemies.length > 0)
        {
            if (rc.isWeaponReady())
            {
                //standardAttack();
                lowestHealthAttack();
                try
                {
                    //Default is to always shoot at the last thing we attacked
                    lastTargeted = findLastTargeted(attackableEnemies);

                    //Then is to always shoot at Big Zombies if they're available
                    if(lastTargeted == null)
                    {
                        lastTargeted = findLowestHealthEnemy(attackableEnemies, RobotType.BIGZOMBIE);
                    }

                    //Otherwise just shoot at the lowest health zombie
                    if(lastTargeted == null) {
                        lastTargeted = findLowestHealthEnemy(attackableEnemies);
                    }

                    if(rc.canAttackLocation(lastTargeted.location))
                    {
                        rc.attackLocation(lastTargeted.location);
                    }
                }
                catch (GameActionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return true;
            }
            else
            {
                //Kite towards target location
                if(lastTargeted != null) {
                    kiteBackFromTarget(lastTargeted);
                }
                else
                {
                    kiteBackSafest();
                }
            }
        }
        else
        {
            if(lastTargeted != null){
                robot.targetMoveLoc = lastTargeted.location;
            }
            else if(robot.sig.lastMoveSignal != null) {
                robot.targetMoveLoc = rc.getLocation().add(robot.sig.lastMoveSignal);
            }
            else{
                robot.targetMoveLoc = robot.nav.averageLoc(spotAllies());
            }
            robot.nav.move();
        }

        return false;
    }

    private static boolean kiteBackSafest() throws GameActionException {

        RobotInfo[] tooCloseEnemies = robot.fight.inRangeOf();
        MapLocation here = rc.getLocation();

        if (tooCloseEnemies.length == 0) return false;

        RobotInfo[] nearbyEnemies  = robot.fight.seenEnemies;

        Direction bestRetreatDir = null;
        RobotInfo currentClosestEnemy = robot.fight.findClosestEnemy(nearbyEnemies);

        int bestDistSq = here.distanceSquaredTo(currentClosestEnemy.location);
        for (Direction dir : Direction.values()) {
            if (!rc.canMove(dir)) continue;

            MapLocation retreatLoc = here.add(dir);
            if (robot.fight.locationUnderThreat(nearbyEnemies, retreatLoc)) continue;

            RobotInfo closestEnemy =  robot.fight.findClosestEnemy(nearbyEnemies, retreatLoc);
            int distSq = retreatLoc.distanceSquaredTo(closestEnemy.location);
            if (distSq > bestDistSq) {
                bestDistSq = distSq;
                bestRetreatDir = dir;
            }
        }

        if (bestRetreatDir == null) return false;

        robot.targetMoveLoc = robot.targetMoveLoc.add(bestRetreatDir);
        robot.nav.move();
        return true;
    }

    private static boolean kiteBackFromTarget(RobotInfo target) throws GameActionException {
        switch (target.type)
        {
            case ARCHON:
            case ZOMBIEDEN:
                getClose(target);
                break;
            case TURRET:
                getClose(target);
                break;
            default:
                int enemyAkkRange = target.type.attackRadiusSquared;
                MapLocation here = rc.getLocation();
                MapLocation bestRetreat = null;
                int bestDistSq = here.distanceSquaredTo(target.location);

                if(bestDistSq <= enemyAkkRange) {
                    for (Direction dir : Direction.values()) {
                        if (!rc.canMove(dir)) continue;

                        MapLocation retreatLoc = rc.getLocation().add(dir);
                        int distSq = retreatLoc.distanceSquaredTo(target.location);
                        if (distSq > enemyAkkRange) {
                            if (distSq > bestDistSq) {
                                bestDistSq = distSq;
                                bestRetreat = retreatLoc;
                            }
                        }
                    }
                    if(bestRetreat == null)
                    {
                        bestRetreat = here.add(here.directionTo(target.location).opposite());
                    }
                    robot.targetMoveLoc = bestRetreat;
                }
                else
                {
                    robot.targetMoveLoc = here.add(here.directionTo(target.location));
                }
                robot.nav.move();
        }

        return true;
    }

    private static void getClose(RobotInfo target) throws GameActionException {
        if(rc.getLocation().isAdjacentTo(target.location))
        {
            return;
        }

        robot.targetMoveLoc = target.location;
        robot.nav.move();
    }

    private static void setTargetArchon()
    {
        MapLocation[] archonLocs = new MapLocation[robot.enemyArchonLocations.size()];
        int i = 0;
        for(MapLocation m : robot.enemyArchonLocations.values())
        {
            archonLocs[i] = m;
            i++;
        }
        targetArchon = robot.fight.findClosestMapLocation(archonLocs, rc.getLocation());
    }


    private static void setLeader() throws GameActionException {
        if(targetArchon != null) {
            RobotInfo[] allies = robot.fight.spotAlliesOfType(RobotType.SOLDIER);
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
            leader = robot.fight.findClosestEnemy(alliesAndI, targetArchon);
            System.out.println("Leader = " + leader.toString());
        }
    }

    private static void followLeader() throws GameActionException {
        setLeader();

        if(leader.ID == rc.getID())
        {
            //I am the leader - move to the target Archon
            robot.targetMoveLoc = targetArchon;
        }
        else
        {
            robot.targetMoveLoc = leader.location;
        }
    }

    private static boolean locationClear(MapLocation m)
    {
        if(rc.canSense(m) && robot.fight.spotEnemies().length == 0)
        {
            //Then the location is clear, remove the location from our list of target Archon Locations
            robot.removeArchonLocation(m);
            return true;
        }
        return false;
    }
}




