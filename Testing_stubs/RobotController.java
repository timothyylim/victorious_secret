package victorious_secret.Testing_stubs;

import battlecode.common.*;

/**
 * Created by ple15 on 25/02/16.
 */
public class RobotController implements battlecode.common.RobotController {
    private boolean coreReady = false;
    private boolean weaponReady = false;
    private MapLocation location;
    private RobotType robotType;
    private boolean ifCanMove = false;
    private double rubble = 0;
    private int roundNum=0;
    private boolean canSense=false;
    private boolean onMap=false;
    public RobotController(){}

    /**
     *
     * @param _coreReady
     * @param _weaponReady
     * @deprecated
     */
    public RobotController(boolean _coreReady, boolean _weaponReady){
        coreReady = _coreReady;
        weaponReady = _weaponReady;
    }

    /**
     *
     * @param loc
     * @deprecated
     */
    public RobotController(MapLocation loc){
        location = loc;
    }

    public void setType(RobotType rt){robotType = rt;}
    public void setCoreReady(boolean _coreReady) {coreReady = _coreReady;}
    public void setWeaponReady(boolean _weaponReady) {weaponReady = _weaponReady;}
    public void setLocation(MapLocation loc){location = loc;}
    public void setCanMove(boolean _canMove){ifCanMove = _canMove;}
    public void setRubble(int _rubble){rubble = _rubble;}

    @Override
    public int getRoundLimit() {
        return 0;
    }


    @Override
    public double getTeamParts() {
        return 0;
    }

    @Override
    public int getRoundNum() {
        return 0;
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
        return new MapLocation[0];
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Team getTeam() {
        return null;
    }

    @Override
    public RobotType getType() {
        return RobotType.ARCHON;
    }

    @Override
    public MapLocation getLocation() {
        return new MapLocation(0,0);
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

    public void setCanSense(boolean sense){
        canSense= sense;

    }
    @Override
    public boolean onTheMap(MapLocation mapLocation) throws GameActionException {
        return onMap;
    }
    public void setOnMap(boolean map){
        onMap=map;
    }

    @Override
    public double senseRubble(MapLocation mapLocation) {
        return rubble;
    }

    @Override
    public double senseParts(MapLocation mapLocation) {
        return 0;
    }

    @Override
    public MapLocation[] sensePartLocations(int i) {
        return new MapLocation[0];
    }

    @Override
    public boolean canSenseLocation(MapLocation mapLocation) {
        return false;
    }

    @Override
    public boolean isLocationOccupied(MapLocation mapLocation) throws GameActionException {
        return false;
    }

    @Override
    public RobotInfo senseRobotAtLocation(MapLocation mapLocation) throws GameActionException {
        return null;
    }

    @Override
    public boolean canSenseRobot(int i) {
        return false;
    }

    @Override
    public RobotInfo senseRobot(int i) throws GameActionException {
        return null;
    }

    @Override
    public RobotInfo[] senseNearbyRobots() {
        return new RobotInfo[0];
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int i) {
        return new RobotInfo[0];
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int i, Team team) {
        return new RobotInfo[0];
    }

    @Override
    public RobotInfo[] senseNearbyRobots(MapLocation mapLocation, int i, Team team) {
        return new RobotInfo[0];
    }

    @Override
    public RobotInfo[] senseHostileRobots(MapLocation mapLocation, int i) {
        MapLocation[] loc = new MapLocation[3];

        loc[0] = new MapLocation(0,0);
        loc[1] = new MapLocation(0,0);
        loc[2] = new MapLocation(0,0);

        RobotInfo[] infos = new RobotInfo[3];

        infos[0]= new RobotInfo(1, Team.A, RobotType.ARCHON,loc[0], 1,2,2,2,2,2,2);
        infos[1]= new RobotInfo(1, Team.A, RobotType.ARCHON,loc[1], 1,2,2,2,2,2,2);
        infos[2]= new RobotInfo(1, Team.A, RobotType.ARCHON,loc[2], 1,2,2,2,2,2,2);


        return infos;
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

    }

    @Override
    public boolean canMove(Direction direction) {
        return ifCanMove;
    }

    @Override
    public void move(Direction direction) throws GameActionException {

    }

    @Override
    public boolean canAttackLocation(MapLocation mapLocation) {
        return false;
    }

    @Override
    public void attackLocation(MapLocation mapLocation) throws GameActionException {

    }

    @Override
    public Signal readSignal() {
        return null;
    }

    @Override
    public Signal[] emptySignalQueue() {
        return new Signal[0];
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
        return false;
    }

    @Override
    public void build(Direction direction, RobotType robotType) throws GameActionException {

    }

    @Override
    public void activate(MapLocation mapLocation) throws GameActionException {

    }

    @Override
    public void repair(MapLocation mapLocation) throws GameActionException {

    }

    @Override
    public void pack() throws GameActionException {

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