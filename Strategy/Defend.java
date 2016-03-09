package victorious_secret.Strategy;

import battlecode.common.*;
import victorious_secret.*;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;

import java.util.Random;


/**
 * Created by ple15 on 15/01/16.
 */
public class Defend {

	public static RobotController rc;
	public static Robot robot;
	static int[] possibleDirections = new int[]{0, 1, -1, 2, -2, 3, -3, 4};
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

	static boolean buildScouts = false;

	static int map_count = 0;

	static int TTMPATIENCE = 15;

	/**
	 * Initialise the defense strategy
	 *
	 * @param _rc
	 * @param _robot
	 */
	public Defend(RobotController _rc, Robot _robot) {
		rc = _rc;
		robot = _robot;

		rnd = new Random(rc.getID());
		startingLocation = new MapLocation(rc.getLocation().x, rc.getLocation().y);
		BugNav.initialise(rc);

	}

	/**
	 * Runs the defense strategy
	 *
	 * @throws GameActionException
	 */
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
			if (rc.getType() == RobotType.TURRET) {
				turretCode();
			}

			if (rc.getType() == RobotType.TTM) {
				moveTTMAway();
			}


		}
	}

	/**
	 * Look for enemies and attack them
	 *
	 * @return Returns true if the attack has been made
	 * @throws GameActionException
	 */
	public boolean lookForEnemies() throws GameActionException {
		RobotInfo[] opponentEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);

		if (opponentEnemies.length > 0 && rc.getType().canAttack()) {
			// Optimize who to attack?
			if (rc.isWeaponReady()) {
				RobotInfo toKill = Fight.findLowestHealthEnemy(opponentEnemies, RobotType.BIGZOMBIE);
				if(toKill == null){
					toKill = Fight.findLowestHealthEnemy(opponentEnemies);
				}
				if (toKill != null) {
					if (rc.getType() == RobotType.TURRET && rc.canAttackLocation(toKill.location)) {
						rc.attackLocation(toKill.location);
						return true;
					} else if (rc.getType() != RobotType.TURRET) {
						rc.attackLocation(toKill.location);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Main behaviour of the turrets in Defense strategy
	 * Shoot an enemy if there is one or
	 * Move around archon to give space for other units
	 *
	 * @throws GameActionException
	 */
	private void turretCode() throws GameActionException {

		RobotInfo[] robotsAround = rc.senseNearbyRobots(2, rc.getTeam());

		int index = get_archon_index(robotsAround);

		if(lookForEnemies())
			return;

		if (index >= 0) {
			RobotInfo archon = robotsAround[index];
			archonLoc = archon.location;

			if (!onChessBoard(archonLoc, rc.getLocation())) {
				rc.pack();
				return;
			}

		}

		receiveTargetLocation();
		MapLocation target = new MapLocation(targetX, targetY);

		if (targetX != -1 && targetY != -1 && rc.canAttackLocation(target)) {
			if (rc.isWeaponReady()) {
				rc.attackLocation(target);
			}
		}
		targetX = -1;
		targetY = -1;

	}

	public static boolean onChessBoard(MapLocation center, MapLocation current) {
		int x = Math.abs(center.x - current.x);
		int y = Math.abs(center.y - current.y);

		if ((x + y) % 2 == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Count the number of units in <b> robots </b> array of the <b>type</b>
	 *
	 * @param robots List of robots
	 * @param type   Robot type to count
	 * @return Returns the number of robots
	 */
	public static int countUnits(RobotInfo[] robots, RobotType type) {
		int count = 0;
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].type == type) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Count the number of units in <b> robots </b> array of the <b>type</b>
	 *
	 * @throws GameActionException
	 */
	private void archonCode() throws GameActionException {

		if (rc.getRoundNum() < 20) {
			get_out_corner();
		}

		MapLocation InitialArchons[] = rc.getInitialArchonLocations(rc.getTeam());

		MapLocation InitialEnemyArchons[] = rc.getInitialArchonLocations(rc.getTeam().opponent());

		MapLocation averageEnermyLoc = Flee.averageLoc(InitialEnemyArchons);

		MapLocation leaderLocation = getFurthestArchonFromEnemyLocation(InitialArchons, averageEnermyLoc);

		MapLocation thisLocation = rc.getLocation();

		if (thisLocation.equals(leaderLocation)) {
			leader = true;
		}

		if (rc.isCoreReady()) {

			double distance = thisLocation.distanceSquaredTo(leaderLocation);

			if (distance > 9 && !leader) {
				BugNav.setTarget(leaderLocation);
				Direction dir = BugNav.getNextMove();
				tryToMove(dir);
			} else {
				if (!buildUnits()) {
					RobotInfo[] alliesToHelp = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
					MapLocation weakestOne = findWeakest(alliesToHelp);
					if (weakestOne != null) {
						rc.repair(weakestOne);
						return;
					}
				}
			}
		}
	}

	public static MapLocation getFurthestArchonFromEnemyLocation(MapLocation[] initialArchons, MapLocation averageEnermyLoc) {
		int max = 0;
		int maxi = 0;

		for (int i = 0; i < initialArchons.length; i++) {
			int dx = initialArchons[i].x - averageEnermyLoc.x;
			int dy = initialArchons[i].y - averageEnermyLoc.y;

			int hypo = dx * dx + dy * dy;

			if (hypo > max) {
				max = hypo;
				maxi = i;
			}

		}
		return initialArchons[maxi];
	}


	/**
	 * Try to move in the <b>forward</b> direction
	 * If failing, clear rubble
	 *
	 * @param forward Direction where to move
	 * @throws GameActionException
	 */
	public static void tryToMove(Direction forward) throws GameActionException {

		if (rc.isCoreReady()) {
			for (int deltaD : possibleDirections) {
				Direction maybeForward = Direction.values()[(forward.ordinal() + deltaD + 8) % 8];
				if (rc.canMove(maybeForward)) {
					rc.move(maybeForward);
					return;
				}
			}

			if (rc.getType().canClearRubble()) {
				//failed to move, look to clear rubble
				MapLocation ahead = rc.getLocation().add(forward);
				if (rc.senseRubble(ahead) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
					rc.clearRubble(forward);
				}
			}
		}
	}

	// Gets attack location from a signal queue

	/**
	 * For turrets
	 * Listen for incoming messages with the target location to attack
	 * Broadcasted from one scout
	 * Set turret target location to the one in the message
	 *
	 * @throws GameActionException
	 */
	public static boolean receiveTargetLocation() throws GameActionException {
		Signal[] signals = rc.emptySignalQueue();
		boolean messageReceived = false;

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
				messageReceived = true;
			} else if (command == ATTACK_Y) {
				targetY = s.getMessage()[1];
				messageReceived = true;
			}
		}
		return messageReceived;
	}

	/**
	 * Moves a TTM in a random direction away from the closest archon
	 * To give space for other units
	 *
	 * @throws GameActionException
	 */
	private static void moveTTMAway() throws GameActionException {
		RobotInfo[] robots = rc.senseNearbyRobots(2, rc.getTeam());
		int archonIndex = get_archon_index(robots);

		if (archonIndex >= 0) {
			RobotInfo archon = robots[archonIndex];
			archonLoc = archon.location;
		}

		if (!onChessBoard(archonLoc, rc.getLocation())) {
			MapLocation dest = findBetterChessBoardPosition(archonLoc);
			BugNav.setTarget(dest);
			Direction dir = BugNav.getNextMove();
			rc.setIndicatorString(0, "destination : " + dir);
			if(rc.canMove(dir) && rc.isCoreReady()){
				rc.move(dir);
			}
		} else {
			rc.unpack();
		}

	}

	public static MapLocation findBetterChessBoardPosition(MapLocation center) throws GameActionException {
		int radius = 1;
		while (radius < 16) {
			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					MapLocation current = new MapLocation(center.x + i, center.y + j);
					if (onChessBoard(center, current) && rc.canSenseLocation(current) && !rc.isLocationOccupied(current)) {
						return current;
					}
				}
			}
			radius++;
		}
		return null;
	}

	/**
	 * Look for an enemy to kill
	 * Otherwise, circle friendly archon, clearing rubble if necessary
	 *
	 * @throws GameActionException
	 */
	private void soldierCode() throws GameActionException {

		//Try to shoot something
		if (lookForEnemies()) {
			return;
		}

		RobotInfo[] robots = rc.senseNearbyRobots(10,rc.getTeam());
		RobotInfo[] closeRobots = rc.senseNearbyRobots(1, rc.getTeam());
		int archonIndex = get_archon_index(robots);

		//If there is an archon around
		if (archonIndex >= 0) {
			RobotInfo archon = robots[archonIndex];
			archonLoc = archon.location;

			//Try to clear rubble by sensing around
			for (int i : possibleDirections) {
				Direction candidateDirection = Direction.values()[(movingDirection.ordinal() + i + 8) % 8];
				if (rc.senseRubble(rc.getLocation().add(candidateDirection)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
					rc.clearRubble(candidateDirection);
					return;
				}
			}

			//Spiral around archon
			System.out.println(archonLoc);
			tryToMove(rc.getLocation().directionTo(Nav.spiralClockwise(archonLoc)));

		} else if (countUnits(closeRobots, RobotType.TURRET) == 0) {
			if (archonLoc != null) {
				tryToMove(rc.getLocation().directionTo(archonLoc));
			}
		} else if (countUnits(closeRobots, RobotType.TURRET) > 0) {
			if (archonLoc != null) {
				tryToMove(rc.getLocation().directionTo(archonLoc));
			}
		}

	}

	/**
	 * Gets the archon index from a robot list
	 *
	 * @param robotList
	 * @return
	 */
	public static int get_archon_index(RobotInfo[] robotList) {
		boolean found = false;
		int index = 0;

		for (index = 0; index < robotList.length; index++) {
			if (robotList[index].type == RobotType.ARCHON) {
				found = true;
				break;
			}
		}

		if (found) {
			return index;
		}

		return -1;

	}

	/**
	 * Building strategy for defense
	 *
	 * @return True if something has been built
	 * @throws GameActionException
	 */
	public boolean buildUnits() throws GameActionException {
		//Our build order is to build 5 guards, then 1 scout, then try to maintain guards and
		//scouts in equal proportion, with another scout every 16 units

		RobotInfo[] nearby = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());

		// Robot type to build
		RobotType robotType;

		if (countUnits(nearby, RobotType.SOLDIER) < 5) {
			robotType = RobotType.SOLDIER;
		} else if (countUnits(nearby, RobotType.TURRET) % 7  == 0 && !buildScouts) {
			robotType = RobotType.SCOUT;
			buildScouts = true;
		} else {
			robotType = RobotType.TURRET;
		}

		Direction randomDir =  Direction.values()[(int) (rnd.nextDouble() * 8)];

		if (rc.getTeamParts() < robotType.partCost) {
			return false;
		} else {
			for (int i = 0; i <= 10; i++) {
				if (rc.canBuild(randomDir, robotType)) {
					if (robotType == RobotType.TURRET)
						buildScouts = false;
					rc.build(randomDir, robotType);
					return true;
				}
			}
			return false;
		}


	}

	/**
	 * Gets archon out of a corner
	 *
	 * @throws GameActionException
	 */
	public boolean get_out_corner() throws GameActionException {
		Direction possible = null;
		if (rc.isCoreReady()) {
			map_count = 0;
			for (Direction D : Direction.values()) {
				if (!rc.canMove(D)) {
					map_count++;
				} else if (D.isDiagonal()) {
					possible = D;
				}
			}


			if (map_count >= 5 && possible != null) {
				tryToMove(possible);
				return true;
			}
		}
		return false;
	}

	/**
	 * Find robot with the lowest health in a list of robots
	 *
	 * @param listOfRobots
	 * @return The location of the weakest enemy
	 */
	// Find robot with the lowest health in a list of robots
	public static MapLocation findWeakest(RobotInfo[] listOfRobots) {
		double weakestSoFar = 0;
		MapLocation weakestLocation = null;
		for (RobotInfo r : listOfRobots) {
			if (r.type != RobotType.ARCHON) {
				double weakness = r.maxHealth - r.health;
				if (weakness > weakestSoFar) {
					weakestLocation = r.location;
					weakestSoFar = weakness;
				}

			}

		}
		return weakestLocation;
	}


}