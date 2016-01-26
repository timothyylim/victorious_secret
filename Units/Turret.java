/**
<<<<<<< HEAD
 *
 */
package victorious_secret.Units;

import java.util.*;

import battlecode.common.*;
import scala.collection.parallel.ParIterableLike;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Strategy.Defend;
import victorious_secret.Strategy.Flee;

/**
 * @author APOC
<<<<<<< HEAD
 *
=======
 * 
>>>>>>> 45b64142e44a0dd44af44b58ab8e8ebf56678b6c
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#TURRET
An immobile unit designed to reinforce an area; transforms into a TTM in order to move.
canAttack(): true

attackDelay: 3
attackPower: 14
attackRadiusSquared: 48
buildTurns: 25
bytecodeLimit: 10000
cooldownDelay: 3
maxHealth: 100
partCost: 125
sensorRadiusSquared: 24
spawnSource: ARCHON
turnsInto: RANGEDZOMBIE

 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#TTM
Turret - Transport Mode: the mobile version of a TURRET. Cannot attack.
canAttack(): false

buildTurns: 10
bytecodeLimit: 10000
maxHealth: 100
movementDelay: 2
sensorRadiusSquared: 24
spawnSource: TURRET
turnsInto: RANGEDZOMBIE
 */
public class Turret extends Robot {

	/**
<<<<<<< HEAD
	 *
=======
	 * 
>>>>>>> 45b64142e44a0dd44af44b58ab8e8ebf56678b6c
	 */

	Defend defend;
	MapLocation attackLoc;

	int cooldown = 0;

	public Turret(RobotController _rc){
		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		strat = Strategy.DEFEND;
		defend = new Defend(rc, this);
		team = rc.getTeam();
		Flee.initialiseFlee(rc);

		strat = Strategy.ATTACK;

		targetMoveLoc = new MapLocation(449,172);

		setArchonLocations();

	}

	@Override
	public void move() throws GameActionException {

		updateOurArchonLocations(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam()));
		switch (strat) {
			case DEFEND:
				//Turrets move if they can't see an enemy
				attack();
				break;
			case ATTACK:
				//_move();
				attack();
				_move();
				break;
			case RETURN_TO_BASE:
				returnToBase();
			default:
				break;


		}
	}

	private void attack() throws GameActionException {
		RobotInfo[] opponentEnemies = rc.senseHostileRobots(rc.getLocation(), RobotType.TURRET.attackRadiusSquared);
		if (opponentEnemies != null && opponentEnemies.length > 0) {
			updateCooldown(opponentEnemies);

			if (rc.getType() == RobotType.TURRET) {
				updateAttackLoc();
				if (attackLoc != null && rc.isWeaponReady()) {
					rc.attackLocation(attackLoc);
				}

			}else {
				rc.unpack();
			}
			return;
		}else{
			cooldown--;
		}
	}

	private void _move() throws GameActionException {

		if(rc.isCoreReady() && targetMoveLoc != null) {
			if (rc.getType() == RobotType.TTM) {
				lineUp();
			} else if(cooldown <= 0) {
				rc.pack();
			}
		}
	}

	private void lineUp() throws GameActionException {
		MapLocation here = rc.getLocation();
		if (rc.isCoreReady() && here != targetMoveLoc) {
			RobotInfo[] nearbyTurrets = fight.spotNearbyTurrets();


			int distanceToTarget = here.distanceSquaredTo(targetMoveLoc);
			int radiusToTarget = (int)Math.sqrt(distanceToTarget);
			int radiusToWall = radiusToTarget;

			int nTurretsInFront = 0;
			int nTurretsAsClose = 0;
			int nTurretsBehind = 0;

			boolean allOnLineReadyToGo = true;

			if (nearbyTurrets != null) {
				for (RobotInfo t : nearbyTurrets) {
					int dt = (int)Math.sqrt(t.location.distanceSquaredTo(targetMoveLoc));
					if(dt == radiusToTarget){
						nTurretsAsClose++;
						if(t.coreDelay > 0){
							allOnLineReadyToGo = false;
						}
					}else if (dt > radiusToTarget) {
						nTurretsBehind++;
					}else{// if(dt < radius){
						if(dt < radiusToWall){
							radiusToWall = dt;
						}
						nTurretsInFront++;
					}
				}

				//System.out.println("My radius = " + radiusToTarget + " wall radius = " + radiusToWall);
			}

			//System.out.println("There are " + nTurretsAsClose + " turrets as close, " + nTurretsFurther + " turrets futher, and " + nTurretsNearer + " turrets nearer");


			if(nTurretsInFront == 0){
				//If there are no turrets that are nearer, then this is the leading edge
				if(nTurretsAsClose == 0 && nTurretsBehind == 0){
					//This turret is lost and alone
					strat = Strategy.RETURN_TO_BASE;
					returnToBase();
				}
				else if(nTurretsAsClose == 0){
					//if there is no-one else as close then this has moved too far forward and needs to retreat
					System.out.println("TOO FAR FORWARD!");
					//dir = dirToTarget.opposite();
					//moveAlongRadiusLarger(here, radius - 1);
					//moveAlongRadiusLarger(here, radius);
				}else if(nTurretsBehind == 0 && allOnLineReadyToGo) {
					//There is no-one behind us so we can march forward
					//dir = dirToTarget;
					//marching = true;
					System.out.println("MARCHING TO TARGET!");
					//moveAlongRadiusSmaller(here, radius + 1);
					moveAlongRadiusSmaller(here, radiusToTarget);
				}
				else{
					//there are other people on the line and we can hold position whilst we wait for people to catch up
					System.out.println("HOLDING THE LINE!");
					//dir = Direction.NONE;
				}

			}else {
				//There are turrets ahead of us, so we need to move into position with them
				System.out.println("MOVING TO POSITION!");
				List<MapLocation> allowedTargets = nav.findAllowedLocations(here, radiusToWall, targetMoveLoc);

				//System.out.println("   FOUND ALLOWED TARGETS!");
				MapLocation t;
				if(allowedTargets != null) {
					//Flee.setTarget(fight.findClosestFreeMapLocation(allowedTargets, here));
					t = fight.findClosestFreeMapLocation(allowedTargets, here);
				}else{
					//Flee.setTarget(targetMoveLoc);
					t = targetMoveLoc;
				}
				//System.out.println("   MOVING TO TARGET " + t);
				Flee.setTarget(t);
				Direction dir = Flee.getNextMove();
				if(rc.canMove(dir)) {
					rc.move(dir);
				}
			}

		}
	}



	/*public boolean listenForSignal() throws GameActionException {

		switch (strat) {
			case DEFEND:
				updateAttackLoc();
				if(attackLoc != null && rc.isCoreReady() && rc.canAttackLocation(attackLoc)){
					rc.attackLocation(attackLoc);
				}

				break;
			default:
				break;


		}
	}
*/

	public boolean listenForSignal(){
		Signal[] sigs = rc.emptySignalQueue();
		if(sigs != null && sigs.length > 0){

			Signal sig = sigs[sigs.length-1];
			int[] message = sig.getMessage();
			if(message != null) {
				attackLoc = new MapLocation(message[0], message[1]);
				return true;
			}
		}
		return false;
	}

	public void updateAttackLoc() throws GameActionException {
		if(!listenForSignal()){
			fight.targetEnemies();
			if(fight.attackableEnemies != null && fight.attackableEnemies.length>0){
				RobotInfo i = fight.findLowestHealthEnemy(fight.attackableEnemies);
				attackLoc = i.location;
			}else{
				attackLoc = null;
			}
		}
	}

	public void updateCooldown(RobotInfo[] enemies){
		for (RobotInfo e : enemies){
			if(e.type != RobotType.ARCHON &&
					e.type != RobotType.TTM &&
					e.type != RobotType.TURRET &&
					e.type != RobotType.ZOMBIEDEN){
				cooldown = 10;
			}
		}
	}

	private Direction findBestMove(MapLocation here, List<Direction> allowedDirs) throws GameActionException {
		int bestDistance = 99999;
		Direction bestDirection = null;
		for(Direction d : allowedDirs){
			MapLocation newLoc = here.add(d);
			if(rc.onTheMap(newLoc) &&
					rc.senseRobotAtLocation(newLoc) == null
					&&  newLoc.distanceSquaredTo(targetMoveLoc) < bestDistance){
				bestDirection = d;
				bestDistance = newLoc.distanceSquaredTo(targetMoveLoc);
				bestDistance += turnsToClear(newLoc) * 3;
				System.out.println("best = " + bestDistance + " " + bestDirection);
			}
		}
		return bestDirection;
	}

	private void moveOrClear(Direction dir) throws GameActionException {
		//System.out.println("Moving " + dir);
		if(rc.canMove(dir)){
			rc.move(dir);
		}else {
			rc.clearRubble(dir);
		}
	}

	private void moveAlongRadiusLarger(MapLocation here, int targetRadius) throws GameActionException {
		//Implementation of bug nav where units do not move beyond an allowed distance to a location
		//get the directions we can move
		List<Direction> allowedDirs = getAllowedDirectionsLarger(here, targetRadius);
		//find the best direction
		//best is defined by closeness and clearness
		Direction bestDirection = findBestMove(here, allowedDirs);

		//move there
		if (bestDirection != null) {
			moveOrClear(bestDirection);
		}
	}

	private void moveAlongRadiusSmaller(MapLocation here, int targetRadius) throws GameActionException {
		//Implementation of bug nav where units do not move beyond an allowed distance to a location
		//get the directions we can move
		List<Direction> allowedDirs = getAllowedDirectionsSmaller(here, targetRadius);
		//find the best direction
		//best is defined by closeness and clearness
		Direction bestDirection = findBestMove(here, allowedDirs);

		//move there
		if (bestDirection != null) {
			moveOrClear(bestDirection);
		}

	}

	private int turnsToClear(MapLocation loc){
		//Sets an upper bound on the number of turns to clear the rubble
		double r = rc.senseRubble(loc);
		//Tile is passable once it is below 50 rubble
		return (int) Math.max((r - 50) / 10, 0);
	}

	private  List<Direction> getAllowedDirectionsLarger(MapLocation here, int targetRadius){

		int upperRadiusSq = (int) (Math.pow(targetRadius + 1, 2) - 1);

		List<Direction> allowedDirs = new ArrayList<>();
		for(Direction d : Direction.values()){
			/*int tR = (int)Math.sqrt(here.add(d).distanceSquaredTo(targetMoveLoc));
			if(tR > targetRadius && d != Direction.OMNI && d != Direction.NONE){
				allowedDirs.add(d);
			}*/
			if(d != Direction.OMNI && d != Direction.NONE && here.add(d).distanceSquaredTo(targetMoveLoc) > upperRadiusSq){
				allowedDirs.add(d);
			}
		}
		return allowedDirs;
	}

	private  List<Direction> getAllowedDirectionsSmaller(MapLocation here, int targetRadius){
		List<Direction> allowedDirs = new ArrayList<>();
		for(Direction d : Direction.values()){
			int tR = (int)Math.sqrt(here.add(d).distanceSquaredTo(targetMoveLoc));
			if(tR < targetRadius){
				allowedDirs.add(d);
			}
		}
		return allowedDirs;
	}

	private void returnToBase() throws GameActionException {
		//find the nearest known archon location and flee to there

		if(ourArchonLocations.size() > 0) {
			MapLocation target = fight.findClosestMapLocation(ourArchonLocations.values(), rc.getLocation());
			Flee.setTarget(target);
			Direction dir = Flee.getNextMove();
			if(rc.canMove(dir)) {
				rc.move(dir);
			}
		}else{
			//we've no idea where our Archons are
		}



	}
}
