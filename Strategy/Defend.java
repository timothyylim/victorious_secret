package victorious_secret_defense.Strategy;
import battlecode.common.*;
import victorious_secret_defense.Robot;

import java.util.Random;

/**
 * Created by ple15 on 15/01/16.
 */
public class Defend {

	public static RobotController rc;
	public static Robot robot;
	static int[] possibleDirections = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction movingDirection = Direction.EAST;
    static int[] tryDirections = {0,-1,1,-2,2};

    static int infinity = 10000;
    static int ELECTION = 73645;
    static int MOVE_X = 787878;
    static int MOVE_Y = 797979;
    static final int PATIENCE = 100;
	static MapLocation loc = null;
    static boolean leader = false;

    static int targetX = -1;
    static int targetY = -1;

	static RobotType[] buildList = new RobotType[]{RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER ,RobotType.TURRET};
	static int current_build = 0;
	static int turretNumber = 0;
    static Random rnd;

	static int PERIMETER = 5;

	
	public Defend(RobotController _rc, Robot _robot){
		rc = _rc;
		robot = _robot;
		loc = new MapLocation(rc.getLocation().x,rc.getLocation().y);
	}


	// Build around rubble
	// Other units

	public void turtle() throws GameActionException {
        // Look for enemies

		RobotInfo[] opponentEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);

		if (opponentEnemies.length > 0 && rc.getType().canAttack()) {
			// Optimize who to attack?
			if (rc.isWeaponReady()) {

				if(rc.getType() == RobotType.TURRET && rc.getLocation().distanceSquaredTo(opponentEnemies[0].location) > 5){
					rc.attackLocation(opponentEnemies[0].location);
				}
				else if (rc.getType()!=RobotType.TURRET){
					rc.attackLocation(opponentEnemies[0].location);
				}

			}
		}

		if (rc.isCoreReady()) {

			// ARCHON
			if (rc.getType() == RobotType.ARCHON) {
				archonCode();
			}

			// Soldier
			if (rc.getType() == RobotType.SOLDIER) {
				circleArchon();
			}

			if (rc.getType() == RobotType.TURRET) {
				//rc.setIndicatorString(1, " distance : " + opponentEnemies[0].location);
			}

		}
    }


    // KILL ZOMBIES BEFORE THEIR NEST BASTARDS !!!!!!!!

    private void archonCode() throws GameActionException {
        leaderElection();

        readInstructions();
        if (leader && rc.getRoundNum() < 30){

            sendInstructions();
        }

        if (rc.isCoreReady()) {

			// Get the distance between the archon and the leader
            MapLocation target = new MapLocation(targetX, targetY);
            MapLocation thisLocation = rc.getLocation();
            Direction dir = rc.getLocation().directionTo(target);
			double distance = thisLocation.distanceSquaredTo(target);

            if( distance > 2 && distance < 1000 && rc.getRoundNum() < PATIENCE){
                RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), infinity);

                if (enemies.length > 0) {
                    Direction away = rc.getLocation().directionTo(enemies[0].location).opposite();
                    tryToMove(away);
                } else {
                    tryToMove(dir);
                }
            }else{
                buildSoldierRing();
            }

        }
    }

    public static void tryToMove(Direction forward) throws GameActionException{
        if(rc.isCoreReady()){
            for(int deltaD:tryDirections){
                Direction maybeForward = Direction.values()[(forward.ordinal()+deltaD+8)%8];
                if(rc.canMove(maybeForward)){
                    rc.move(maybeForward);
                    return;
                }
            }
            if(rc.getType().canClearRubble()){
                //failed to move, look to clear rubble
                MapLocation ahead = rc.getLocation().add(forward);
                if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
                    rc.clearRubble(forward);
                }
            }
        }
    }




    private static void leaderElection() throws GameActionException {

        if (rc.getRoundNum()  == 0) {
            // First step: elect a leader archon
            if (rc.getType() == RobotType.ARCHON) {
              rc.broadcastMessageSignal(ELECTION, 0, 1000);

                Signal[] received = rc.emptySignalQueue();
                int numArchons = 0;
                for (Signal s : received) {
                    if (s.getMessage() != null && s.getMessage()[0] == ELECTION) {
                        numArchons++;
                    }
                }
//                System.out.println(numArchons);
                if (numArchons == 0) {
                    // If you haven't received anything yet, then you're the leader.
                    leader = true;
                    rc.setIndicatorString(0, "I'm the leader!");
                } else {
                    leader = false;
                    rc.setIndicatorString(0, "I'm not the leader, i'm " + numArchons);
                }
            }
        }
    }

    private static void readInstructions() throws GameActionException {
        Signal[] signals = rc.emptySignalQueue();

        for (Signal s : signals) {
            if (s.getTeam() != rc.getTeam()) {
                continue;
            }

            if (s.getMessage() == null) {
                continue;
            }

            int command = s.getMessage()[0];
            if (command == MOVE_X) {
                targetX = s.getMessage()[1];
            } else if (command == MOVE_Y) {
                targetY = s.getMessage()[1];
            }/* else if (command == FOUND_ARCHON_X) {
                archonX = s.getMessage()[1];
            } else if (command == FOUND_ARCHON_Y) {
                archonY = s.getMessage()[1];
                archonFound = true;
            }*/
        }
    }

    private static void sendInstructions() throws GameActionException {
        // Possible improvement: stop sending the same message over and over again
        // since it will just increase our delay.
        //if (!archonFound) {
            MapLocation loc = rc.getLocation();
            rc.broadcastMessageSignal(MOVE_X, loc.x, infinity);
            rc.broadcastMessageSignal(MOVE_Y, loc.y, infinity);
       // }
    }

	private void circleArchon() throws GameActionException {


		RobotInfo[] robots = rc.senseNearbyRobots(5);
		int index = get_archon_index(robots);

		//If there is an archon around
		if (index >= 0){
			RobotInfo archon = robots[index];
			Direction archon_direction = rc.getLocation().directionTo(archon.location);


			movingDirection = forwardish(archon_direction);
			MapLocation destination = rc.getLocation().add(movingDirection,1);

			int distance_from_archon = destination.distanceSquaredTo(archon.location);

			if(rc.getType().canClearRubble()){

				// Sense around
				for (int i:possibleDirections){
					Direction candidateDirection = Direction.values()[(movingDirection.ordinal()+i+8)%8];
					if(rc.senseRubble(rc.getLocation().add(candidateDirection)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH){
						rc.clearRubble(candidateDirection);
						return;
					}
				}
				// If rubble < perimeter clear it
			}

			if(rc.canMove(movingDirection)){
				if(Math.sqrt(distance_from_archon) < PERIMETER){
					rc.move(movingDirection);
				}
			}

		}
		
	}

	private int get_archon_index(RobotInfo[] robotList){
		boolean found = false;
		int index = 0;

		for(index = 0; index < robotList.length; index ++ ){
			if(robotList[index].type == RobotType.ARCHON){
				found = true;
				break;
			}
		}

		if(found){
			return index;
		}

		return -1;
	}


	private void buildSoldierRing() throws GameActionException {

		RobotType robotType = buildList[current_build];

		RobotInfo[] nearby = rc.senseNearbyRobots(10, rc.getTeam());

		turretNumber = 0;

		for(int i = 0; i< nearby.length;i++){
			if(nearby[i].type == RobotType.TURRET){
				turretNumber ++;
			}
		}

		if(robotType == RobotType.TURRET && turretNumber >=4){
			robotType = RobotType.SOLDIER;
		}

		if(rc.getTeamParts() < robotType.partCost){
			return;
		}

		if(rc.canBuild(Direction.NORTH_EAST, robotType)){
			rc.build(Direction.NORTH_EAST, robotType);
		}
		else if(rc.canBuild(Direction.SOUTH_WEST, robotType)){
			rc.build(Direction.SOUTH_WEST, robotType);
		}
		else if(rc.canBuild(Direction.NORTH_WEST, robotType)){
			rc.build(Direction.NORTH_WEST, robotType);
		}
		else if(rc.canBuild(Direction.SOUTH_EAST, robotType)){
			rc.build(Direction.SOUTH_EAST, robotType);
		}
		else if(rc.canBuild(Direction.NORTH, robotType)){
			rc.build(Direction.NORTH, robotType);
		}
		else if(rc.canBuild(Direction.SOUTH, robotType)){
			rc.build(Direction.SOUTH, robotType);
		}
		else if(rc.canBuild(Direction.EAST, robotType)){
			rc.build(Direction.EAST, robotType);
		}
		else if(rc.canBuild(Direction.WEST, robotType)){
			rc.build(Direction.WEST, robotType);
		}

		if(current_build >= buildList.length - 1){
			current_build = 0;
		}else{
			current_build ++;
		}


	}

	private static Direction getForward(Direction archon_direction) {
		if (archon_direction == Direction.NORTH_EAST){
			return Direction.EAST;
		}
		else if (archon_direction == Direction.EAST){
			return Direction.SOUTH; 
		}
		else if (archon_direction == Direction.SOUTH_EAST){
			return Direction.SOUTH; 
		}
		else if (archon_direction == Direction.SOUTH){
			return Direction.WEST; 
		}
		else if (archon_direction == Direction.SOUTH_WEST){
			return Direction.WEST; 
		}
		else if (archon_direction == Direction.WEST){
			return Direction.NORTH; 
		}
		else if (archon_direction == Direction.NORTH_WEST){
			return Direction.NORTH; 
		}
		else if (archon_direction == Direction.NORTH){
			return Direction.EAST; 
		}
		return null;
	}

	private static Direction forwardish(Direction ahead) throws GameActionException {
		for (int i:possibleDirections){
			Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
			if(rc.canMove(candidateDirection)){
				return candidateDirection;
			}
		}
		return ahead;
		
	}
	
	private static RobotInfo[] joinRobotInfo(RobotInfo[] zombieEnemies, RobotInfo[] normalEnemies) {
		RobotInfo[] opponentEnemies = new RobotInfo[zombieEnemies.length+normalEnemies.length];
		int index = 0;
		for (RobotInfo i:zombieEnemies){
			opponentEnemies[index] = i;
			index++;
		}

		for (RobotInfo i:normalEnemies){
			opponentEnemies[index] = i;
			index++;
		}
		return opponentEnemies;
	}

    private static Direction randomDirection() {
        return Direction.values()[(int)(rnd.nextDouble()*8)];
    }
}
