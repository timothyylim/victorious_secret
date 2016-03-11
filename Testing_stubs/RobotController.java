package victorious_secret.Testing_stubs;

import battlecode.common.*;

/**
 * Created by ple15 on 25/02/16.
 */
public class RobotController implements battlecode.common.RobotController {
        private boolean coreReady = false;
        private boolean weaponReady = false;
        private boolean canAttackLoc = false;
        private MapLocation location;
        private RobotType robotType;
        private boolean ifCanMove = false;
        private double rubble = 0;
        private Team team;
        private int id;
        private boolean canSense;
        private boolean onMap = false;

        private Direction possibleMoves[];
        private boolean rubbleCleared = false;
        private MapLocation occupiedLocations[];
        private RobotInfo[] hostileRobots;
        private Signal signals[];
        private int parts;
        private boolean attacked = false;
        private boolean pack = false;
        private boolean repaired = false;
        private RobotInfo[] nearbyRobots;
        private MapLocation[] initialFriendlyArchonsLoc;
        private MapLocation[] initialEnemyArchonsLoc;
        private int round;
        private boolean moved = false;
        public RobotController(){}


        public void setType(RobotType rt){robotType = rt;}
        public void setCoreReady(boolean _coreReady) {coreReady = _coreReady;}
        public void setWeaponReady(boolean _weaponReady) {weaponReady = _weaponReady;}
        public void setLocation(MapLocation loc){location = loc;}
        public void setParts(int _parts){parts=_parts;}
        public void setRound(int _round){round=_round;}
        public void setCanSense(boolean _canSense){canSense = _canSense;}
        public void setCanMove(Direction dir[]){possibleMoves = dir;}
        public void setCanMove(boolean _canMove){ifCanMove = _canMove;}
        public void setRubble(int _rubble){rubble = _rubble;}
        public void setOccupiedLocations(MapLocation locations[]){occupiedLocations = locations;}
        public void setHostileRobots(RobotInfo[] robots){hostileRobots = robots;}
        public void setNearbyRobots(RobotInfo[] robots){nearbyRobots = robots;}

        public void setTeam(Team team1){team = team1;}
        public void setCanAttackLoc(boolean canAttackLoc1){canAttackLoc = canAttackLoc1;}

        public void setInitialFriendlyArchonsLoc(MapLocation[] archonsLoc){initialFriendlyArchonsLoc = archonsLoc;}
        public void setInitialEnemyArchonsLoc(MapLocation[] archonsLoc){initialEnemyArchonsLoc = archonsLoc;}

        public void setID(int _id){id = _id;}
        public void setSignals(Signal[] incoming){signals = incoming;}
        public void setOnMap(boolean _onMap){onMap = _onMap;}

        public boolean hasPacked(){return pack;}
        public boolean hasAttacked(){return attacked;}
        public boolean hasRepaired(){return repaired;}
        public boolean hasMoved(){return moved;}
        public boolean hasCleared(){return rubbleCleared;}


        @Override
        public int getRoundLimit() {
            return 0;
        }

        @Override
        public double getTeamParts() {
            return parts;
        }

        @Override
        public int getRoundNum() {
            return round;
        }

        @Override
        public boolean isArmageddon() {
            return false;
        }

        @Override
        public boolean isArmageddonDaytime() {
            return false;
        }

        @Override
        public ZombieSpawnSchedule getZombieSpawnSchedule() {
            return null;
        }

        @Override
        public int getRobotCount() {
            return 0;
        }

        @Override
        public MapLocation[] getInitialArchonLocations(Team team) {
            if(team == this.team){
                return initialFriendlyArchonsLoc;
            }else{
                return initialEnemyArchonsLoc;
            }
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public Team getTeam() {
            return team;
        }

        @Override
        public RobotType getType() {
            return robotType;
        }

        @Override
        public MapLocation getLocation() {
            return location;
        }

        @Override
        public double getCoreDelay() {
            return 0;
        }

        @Override
        public double getWeaponDelay() {
            return 0;
        }

        @Override
        public double getHealth() {
            return 0;
        }

        @Override
        public int getInfectedTurns() {
            return 0;
        }

        @Override
        public int getZombieInfectedTurns() {
            return 0;
        }

        @Override
        public int getViperInfectedTurns() {
            return 0;
        }

        @Override
        public boolean isInfected() {
            return false;
        }

        @Override
        public int getBasicSignalCount() {
            return 0;
        }

        @Override
        public int getMessageSignalCount() {
            return 0;
        }

        @Override
        public boolean canSense(MapLocation mapLocation) {
            return canSense;
        }

        @Override
        public boolean onTheMap(MapLocation mapLocation) throws GameActionException {
            return onMap;
        }

        @Override
        public double senseRubble(MapLocation mapLocation) {
            return rubble;
        }

        @Override
        public double senseParts(MapLocation mapLocation) {
            return parts;
        }

        @Override
        public MapLocation[] sensePartLocations(int i) {
            return new MapLocation[0];
        }

        @Override
        public boolean canSenseLocation(MapLocation mapLocation) {
            return true;
        }

        @Override
        public boolean isLocationOccupied(MapLocation mapLocation) throws GameActionException {
            for(MapLocation loc: occupiedLocations){
                if(mapLocation.x == loc.x && mapLocation.y == loc.y){
                    return true;
                }
            }
            return false;
        }

        @Override
        public RobotInfo senseRobotAtLocation(MapLocation mapLocation) throws GameActionException {
            return null;
        }

        @Override
        public boolean canSenseRobot(int i) {
            return canSense;
        }

        @Override
        public RobotInfo senseRobot(int i) throws GameActionException {
            return new RobotInfo(1, Team.B, RobotType.ARCHON, new MapLocation(101, 100), 0, 0, 100, 100, 100, 0, 0);
        }

        @Override
        public RobotInfo[] senseNearbyRobots() {
            return nearbyRobots;
        }

        @Override
        public RobotInfo[] senseNearbyRobots(int i) {
            return nearbyRobots;
        }

        @Override
        public RobotInfo[] senseNearbyRobots(int _i, Team _team) {
            return senseNearbyRobots(location, _i, _team);
        }

        @Override
        public RobotInfo[] senseNearbyRobots(MapLocation mapLocation, int i, Team team) {
            if (nearbyRobots == null){return null;}
            int n = 0;
            for (RobotInfo r : nearbyRobots){
                if (r.team == team){
                    n++;
                }
            }

            RobotInfo[] ri = new RobotInfo[n];

            for (RobotInfo r : nearbyRobots){
                if (r.team == team){
                    n--;
                    ri[n] = r;
                }
            }

            return ri;
        }

        @Override
        public RobotInfo[] senseHostileRobots(MapLocation mapLocation, int i) {
            return hostileRobots;
        }

        @Override
        public boolean isCoreReady() {
            return coreReady;
        }

        @Override
        public boolean isWeaponReady() {
            return weaponReady;
        }

        @Override
        public void clearRubble(Direction direction) throws GameActionException {
            rubbleCleared = true;
        }

        @Override
        public boolean canMove(Direction direction) {
            if(possibleMoves!=null){
                for(Direction d:possibleMoves){
                    if(direction == d){
                        return true;
                    }
                }
                return false;
            }else{
                return ifCanMove;
            }
        }

        @Override
        public void move(Direction direction) throws GameActionException {
            moved = true;
        }

        @Override
        public boolean canAttackLocation(MapLocation mapLocation) {
            return canAttackLoc;
        }

        @Override
        public void attackLocation(MapLocation mapLocation) throws GameActionException {
            attacked = true;
        }

        @Override
        public Signal readSignal() {
            return null;
        }

        @Override
        public Signal[] emptySignalQueue() {
            return signals;
        }

        @Override
        public void broadcastSignal(int i) throws GameActionException {

        }

        @Override
        public void broadcastMessageSignal(int i, int i1, int i2) throws GameActionException {

        }

        @Override
        public boolean hasBuildRequirements(RobotType robotType) {
            return false;
        }

        @Override
        public boolean canBuild(Direction direction, RobotType robotType) {
            return true;
        }

        @Override
        public void build(Direction direction, RobotType robotType) throws GameActionException {

        }

        @Override
        public void activate(MapLocation mapLocation) throws GameActionException {

        }

        @Override
        public void repair(MapLocation mapLocation) throws GameActionException {
            repaired = true;
        }

        @Override
        public void pack() throws GameActionException {
            pack = true;
        }

        @Override
        public void unpack() throws GameActionException {

        }

        @Override
        public void disintegrate() {

        }

        @Override
        public void resign() {

        }

        @Override
        public void setTeamMemory(int i, long l) {

        }

        @Override
        public void setTeamMemory(int i, long l, long l1) {

        }

        @Override
        public long[] getTeamMemory() {
            return new long[0];
        }

        @Override
        public void setIndicatorString(int i, String s) {

        }

        @Override
        public void setIndicatorDot(MapLocation mapLocation, int i, int i1, int i2) {

        }

        @Override
        public void setIndicatorLine(MapLocation mapLocation, MapLocation mapLocation1, int i, int i1, int i2) {

        }

        @Override
        public long getControlBits() {
            return 0;
        }

        @Override
        public void addMatchObservation(String s) {

        }
    }
