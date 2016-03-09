package victorious_secret.Strategy;
import battlecode.common.*;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Behaviour.Nav;

public class Flee{
    static RobotController _rc;
    static MapLocation temp=null;
    static MapLocation mine;
    static Direction moved;

    static RobotInfo[] hostiles;
    static RobotInfo[] neutrals;
    static MapLocation[] parts;
    static MapLocation target=null;


    /**
     * Method to initialize the RobotController
     * @param rc            Parameter that is the Fleeing RobotController (rc)
     */
    public static void initialiseFlee(RobotController rc){
        _rc = rc;
        BugNav.initialise(_rc);
    }

    /**
     *     /**
     * Setter method that sets target location to a given location
     * @param targetMoveLoc
     */
    public static void setTarget(MapLocation targetMoveLoc){
        BugNav.setTarget(targetMoveLoc);
    }

    /**
     * Method to set target the best location to flee, depending on the average enermies direction
     * and friendly units.
     * @throws GameActionException
     */
    public static void flee() throws GameActionException{
        target = BugNav.getTargetLoc();
        if(_rc.getRoundNum()==0||target==null){
            setTarget(_rc.getLocation());
        }

        hostiles = _rc.senseHostileRobots(_rc.getLocation(), _rc.getType().sensorRadiusSquared);
        neutrals = _rc.senseNearbyRobots(_rc.getLocation(), 1, Team.NEUTRAL);
        parts = _rc.sensePartLocations(1);

        temp = Nav.averageLoc(hostiles);
        mine = _rc.getLocation();

        int dx,dy;

        //when there are enemies
        if(temp!=null){
            //set target location opposite to where the enemies is

            dx = temp.x-mine.x;
            dy = temp.y-mine.y;

            target = new MapLocation(mine.x-2*dx,mine.y-2*dy);

            if(_rc.canSense(target)){
                if(!_rc.onTheMap(target)){
                    target = new MapLocation(mine.x+dx,mine.y-dy);

                }
                if(!_rc.onTheMap(target)){
                    target = new MapLocation(mine.x-dx,mine.y+dy);
                }
                if(!_rc.onTheMap(target)){
                    target = new MapLocation(mine.x+2*dx,mine.y+2*dy);
                }
            }
            _rc.setIndicatorString(0, target.toString());
            BugNav.setTarget(target);
        }

        // if we arrived near target and realised target is not on map
        if(_rc.canSense(target)){
            if(!_rc.onTheMap(target)){
                for (Direction d: Direction.values()){
                    if(_rc.canMove(d)){
                        target = _rc.getLocation().add(d);
                        BugNav.setTarget(target);
                        return;
                    }
                }
            }
        }

        if(temp!=null&&target.equals(_rc.getLocation())){
            for (Direction d: Direction.values()){
                if(_rc.canMove(d)){
                    target = _rc.getLocation().add(d);
                    BugNav.setTarget(target);
                    break;
                }
            }
        }
    }

    /**
     * Method for the rc to do the actual movements that makes the robots to flee to a target direction
     * but also consider other complimentary cases, like awaking the neutral unit and picking up parts
     * along the way while they are flee-ing
     * @param rc
     */
    public static void runFlee(RobotController rc) {
        initialiseFlee(rc);
        try {
            flee();
            Direction nextMove =BugNav.getNextMove();

            if(neutrals.length != 0 && _rc.getLocation().distanceSquaredTo(neutrals[0].location) == 1){
                if(_rc.isCoreReady()){
                    _rc.activate(neutrals[0].location);
                }
            }else if(temp == null ){
                for(Direction dir:Direction.values()){
                    if(_rc.isCoreReady()&&_rc.canBuild(dir, RobotType.GUARD)){
                        _rc.build(dir, RobotType.GUARD);
                    }else if(_rc.senseRubble(_rc.getLocation().add(dir))>GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                        if(_rc.isCoreReady()){
                            _rc.clearRubble(dir);
                        }
                    }
                }

            }else if(_rc.getRoundNum()%25!=0&&_rc.getRoundNum()!=0){
                if(_rc.isCoreReady()&&_rc.canMove(nextMove)){
                    if(parts.length != 0 && _rc.senseRubble(parts[0]) < GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                        _rc.move(_rc.getLocation().directionTo(parts[0]));
                        moved = _rc.getLocation().directionTo(parts[0]);
                        BugNav.setMoved(moved);
                    }else{
                        int i=0;
                        boolean lrubble = true;
                        MapLocation nextLoc = _rc.getLocation().add(nextMove);
                        for (Direction dir : Direction.values()){
                            MapLocation temp = nextLoc.add(dir);
                            if(_rc.senseRubble(temp)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                                if(lrubble){
                                    i++;
                                }
                                lrubble = true;
                            }else{
                                lrubble = false;
                            }
                        }
                        //pete is gay
                        //if i is greater than or equal to 4 don't go there.
                        //otherwise ignore it.
                        //corner avoid
                        if(i>=4){
                            if(_rc.canMove(nextMove.rotateLeft().rotateLeft()))
                                _rc.move(nextMove.rotateLeft().rotateLeft());
                            else if(_rc.canMove(nextMove.rotateRight().rotateRight())){
                                _rc.move(nextMove.rotateRight().rotateRight());
                            }
                        }else{
                            _rc.move(nextMove);
                            moved=nextMove;
                            BugNav.setMoved(moved);
                        }
                    }
                }
            }else{
                if(temp!=null){
                    if(_rc.isCoreReady()&&_rc.canBuild(_rc.getLocation().directionTo(temp), RobotType.GUARD)){
                        _rc.build(_rc.getLocation().directionTo(temp), RobotType.GUARD);
                    }
                }
            }

        } catch (GameActionException e) {
            e.printStackTrace();
        }
    }

    // to be deleted, duplicate code from pete
    public static MapLocation averageLoc(RobotInfo[] listOfEnemies) throws GameActionException
    {
        return Nav.averageLoc(listOfEnemies);

    }

    public static MapLocation averageLoc(MapLocation[] listOfEnemies) throws GameActionException
    {
        return Nav.averageLoc(listOfEnemies);

    }
}