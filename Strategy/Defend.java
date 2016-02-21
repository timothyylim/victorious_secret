package victorious_secret.Strategy;
import battlecode.common.*;
import victorious_secret.*;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Behaviour.Fight;

import java.util.Random;


/**
 * Created by ple15 on 15/01/16.
 */
public class Defend {

	public static RobotController rc;
	public static Robot robot;
	static int[] possibleDirections = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction movingDirection = Direction.EAST;


	private static int ATTACK_X = 15151515;
	private static int ATTACK_Y = 14141414;

	static boolean MOVED = false;
	static MapLocation startingLocation = null;
    static boolean leader = false;

	static MapLocation archonLoc = null;

    static int targetX = -1;
    static int targetY = -1;


    static Random rnd;

	static int nUnitsBuilt = 0;

	static int map_count = 0;

	static int TTMPATIENCE = 15;

	
	public Defend(RobotController _rc, Robot _robot){
		rc = _rc;
		robot = _robot;

		rnd = new Random(rc.getID());
		startingLocation = new MapLocation(rc.getLocation().x,rc.getLocation().y);
		BugNav.initialise(rc);

	}


	public void turtle() throws GameActionException {


		if (rc.isCoreReady()) {

			// ARCHON
			if (rc.getType() == RobotType.ARCHON) {
				archonCode();

			}

			// Soldier
			if (rc.getType() == RobotType.SOLDIER) {
				soldierCode();
			}

			// Guard
			if (rc.getType() == RobotType.GUARD) {
				soldierCode();
			}

			// Turret
			if(rc.getType() == RobotType.TURRET){
				turretCode();
			}

			if(rc.getType() == RobotType.TTM){
				moveTTMAway();
			}


		}
    }


	private boolean lookForEnemies() throws GameActionException {
		RobotInfo[] opponentEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);

		if (opponentEnemies.length > 0 && rc.getType().canAttack()) {
			// Optimize who to attack?
			if (rc.isWeaponReady()) {
				RobotInfo toKill = Fight.findLowestHealthEnemy(opponentEnemies,RobotType.BIGZOMBIE);

				if(toKill!=null){
					if(rc.getType() == RobotType.TURRET && rc.getLocation().distanceSquaredTo(toKill.location) > 5){
						rc.attackLocation(toKill.location);
						return true;
					}
					else if (rc.getType()!=RobotType.TURRET){
						rc.attackLocation(toKill.location);
						return true;
					}
				}

				for(RobotInfo enemy : opponentEnemies){
					if(rc.getType() == RobotType.TURRET && rc.getLocation().distanceSquaredTo(enemy.location) > 5){
						rc.attackLocation(enemy.location);
						return true;
					}
					else if (rc.getType()!=RobotType.TURRET){
						rc.attackLocation(enemy.location);
						return true;
					}
				}


			}

		}
		return false;
	}

	private void turretCode() throws GameActionException {

		RobotInfo[] robotsAround = rc.senseNearbyRobots(2,rc.getTeam());

		int index = get_archon_index(robotsAround);

		if(!lookForEnemies() && index>=0){
			RobotInfo archon = robotsAround[index];
			archonLoc = archon.location;
			RobotInfo[] closeRobots = rc.senseNearbyRobots(archonLoc, 2, rc.getTeam());

			int unitsAround = countUnits(closeRobots,RobotType.TURRET) + countUnits(closeRobots,RobotType.ARCHON);

			if(unitsAround >= 7){
				rc.pack();
				return;
			}
		}

		getAttackLoc();
		MapLocation target = new MapLocation(targetX,targetY);

		if(!lookForEnemies() && targetX!=-1 && targetY!=-1 && rc.canAttackLocation(target)){
			if(rc.isWeaponReady()){
				rc.attackLocation(target);
			}
		}
		targetX = -1;
		targetY = -1;

	}

	private static int countUnits(RobotInfo[] robots,RobotType type){
		int count = 0;
		for(int i = 0; i<robots.length;i++){
			if(robots[i].type == type){
				count ++;
			}
		}
		return count;
	}
    // KILL ZOMBIES BEFORE THEIR NEST BASTARDS !!!!!!!!

    private void archonCode() throws GameActionException {

		if(rc.getRoundNum() < 20){
			get_outta_here();
		}

		MapLocation InitialArchons[] = rc.getInitialArchonLocations(rc.getTeam());

		MapLocation InitialEnemyArchons[] = rc.getInitialArchonLocations(rc.getTeam().opponent());

		MapLocation averageEnermyLoc = Flee.averageLoc(InitialEnemyArchons);

		int dx =0;
		int dy=0;

		int hypo=0;

		int max=0;
		int maxi=0;

		for (int i = 0; i < InitialArchons.length; i++){
			dx=InitialArchons[i].x-averageEnermyLoc.x;
			dy=InitialArchons[i].y-averageEnermyLoc.y;

			hypo = dx*dx+dy*dy;

			if(hypo>max){
				max=hypo;
				maxi=i;
			}

		}

		MapLocation leaderLocation = rc.getInitialArchonLocations(rc.getTeam())[maxi];

		MapLocation thisLocation = rc.getLocation();

		if(thisLocation.equals(leaderLocation)){
			leader = true;
		}

		if (rc.isCoreReady()) {

			double distance = thisLocation.distanceSquaredTo(leaderLocation);

            if(distance > 4 && !leader){
				BugNav.setTarget(leaderLocation);
				Direction dir = BugNav.getNextMove();
				tryToMove(dir);
            }else{
				if(!buildUnits()){
					RobotInfo[] alliesToHelp = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,rc.getTeam());
					MapLocation weakestOne = findWeakest(alliesToHelp);
					if(weakestOne!=null){
						rc.repair(weakestOne);
						return;
					}
				}
            }
        }
    }

    public static void tryToMove(Direction forward) throws GameActionException{

        if(rc.isCoreReady()){
			for (int deltaD : possibleDirections) {
				Direction maybeForward = Direction.values()[(forward.ordinal() + deltaD + 8) % 8];
				if (rc.canMove(maybeForward)) {
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

    private static void getAttackLoc() throws GameActionException {
        Signal[] signals = rc.emptySignalQueue();

        for (Signal s : signals) {
            if (s.getTeam() != rc.getTeam()) {
                continue;
            }

            if (s.getMessage() == null) {
                continue;
            }

            int command = s.getMessage()[0];
            if (command == ATTACK_X) {
                targetX = s.getMessage()[1];
            } else if (command == ATTACK_Y) {
				targetY = s.getMessage()[1];
			}
        }
    }

	private static void moveTTMAway() throws GameActionException{
		RobotInfo[] robots = rc.senseNearbyRobots(1,rc.getTeam());
		int archonIndex = get_archon_index(robots);

		if(archonIndex>=0){
			RobotInfo archon = robots[archonIndex];
			archonLoc = archon.location;
			Direction movementDirection = rc.getLocation().directionTo(archon.location).opposite();
			if(!MOVED){
				tryToMove(movementDirection);
				return;
			}
		}else if (countUnits(robots,RobotType.TURRET)>0){
			if(TTMPATIENCE == 0){
				rc.unpack();
				TTMPATIENCE = 15;
				return;
			}

			tryToMove(randomDirection());
		}else{
			rc.unpack();
		}

	}

	private void soldierCode() throws GameActionException {

		//Try to shoot something
		if(lookForEnemies()){
			return;
		}

		RobotInfo[] robots = rc.senseNearbyRobots(10);
		RobotInfo[] closeRobots = rc.senseNearbyRobots(1,rc.getTeam());
		int archonIndex = get_archon_index(robots);

		rc.setIndicatorString(0,""+archonIndex);
		//If there is an archon around
		if (archonIndex >= 0){
			RobotInfo archon = robots[archonIndex];
			archonLoc = archon.location;
			Direction archon_direction = rc.getLocation().directionTo(archon.location);

			MapLocation destination = rc.getLocation().add(archon_direction,1);

			int distance_from_archon = destination.distanceSquaredTo(archon.location);

			if(distance_from_archon <= 2){
				tryToMove(archon_direction.opposite());
				return;
			}


			//Else try to clear rubble
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

			tryToMove(randomDirection());

		}else if(countUnits(closeRobots,RobotType.TURRET)==0){
			if(archonLoc!=null) {
				tryToMove(rc.getLocation().directionTo(archonLoc));
			}
		}else if(countUnits(closeRobots,RobotType.TURRET)>0){
			if(archonLoc!=null){
				tryToMove(rc.getLocation().directionTo(archonLoc));
			}
		}
		
	}

	private static int get_archon_index(RobotInfo[] robotList){
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

	private boolean buildUnits() throws GameActionException {
		//Our build order is to build 5 guards, then 1 scout, then try to maintain guards and
		//scouts in equal proportion, with another scout every 16 units

		RobotInfo[] nearby =  rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
		RobotInfo[] nearbyEnemies =  rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());

		RobotType robotType;
		if(nearby == null || nearby.length < 5 || nearbyEnemies.length > 0){
			robotType = RobotType.SOLDIER;
		}else{
			int nGuards = 0;
			int nTurrets = 0;

			for(RobotInfo r : nearby){
				switch (r.type){
					case SOLDIER:
						nGuards++;
						break;
					case TURRET:
					case TTM:
						nTurrets++;
						break;
				}
			}

			if(nUnitsBuilt % 14 == 0){
				robotType = RobotType.SCOUT;
			}else if(nGuards > nTurrets){
				robotType = RobotType.TURRET;
			}else {
				robotType = RobotType.SOLDIER;
			}
		}

		Direction randomDir = randomDirection();

		if(rc.getTeamParts() < robotType.partCost){
			return false;
		}else{

			for(int i = 0; i <= 10; i++){
				if(rc.canBuild(randomDir, robotType)) {
					nUnitsBuilt++;
					rc.build(randomDir, robotType);
					return true;
				}
			}
			return false;
		}

	}

    private static Direction randomDirection() {
        return Direction.values()[(int)(rnd.nextDouble()*8)];
    }

	private void get_outta_here() throws GameActionException{
		Direction possible = null;
		if(rc.isCoreReady()){
			map_count = 0;
			for (Direction D : Direction.values()) {
				if (!rc.canMove(D)) {
					map_count++;
				} else if (D.isDiagonal()) {
					possible = D;
				}
			}


			if(map_count >= 5 && possible !=null){
				tryToMove(possible);

			}
		}
	}

	private static MapLocation findWeakest(RobotInfo[] listOfRobots){
		double weakestSoFar = 0;
		MapLocation weakestLocation = null;
		for(RobotInfo r:listOfRobots){
			if (r.type != RobotType.ARCHON) {
				double weakness = r.maxHealth-r.health;
				if(weakness>weakestSoFar){
					weakestLocation = r.location;
					weakestSoFar=weakness;
				}

			}

		}
		return weakestLocation;
	}


}
