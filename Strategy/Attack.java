package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Robot;

/**
 * Created by ple15 on 15/01/16.
 */
public class Attack extends Fight
{
    public Attack(RobotController _rc, Robot _robot) {
        super(_rc, _robot);
    }

    public static boolean kiteStratgey() throws GameActionException
    {
        seenEnemies = spotEnemies();
        attackableEnemies = targetEnemies();
        if(attackableEnemies.length > 0)
        {
            if (rc.isWeaponReady())
            {
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
            if(lastTargeted != null)
            {
                robot.targetMoveLoc = lastTargeted.location;
            }
            else
            {
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


}




