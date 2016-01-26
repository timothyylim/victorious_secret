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
					//System.out.println("TOO FAR FORWARD!");
					//dir = dirToTarget.opposite();
					//moveAlongRadiusLarger(here, radius - 1);
					//moveAlongRadiusLarger(here, radius);
				}else if(nTurretsBehind == 0 && allOnLineReadyToGo) {
					//There is no-one behind us so we can march forward
					//dir = dirToTarget;
					//marching = true;
					//System.out.println("MARCHING TO TARGET!");
					//moveAlongRadiusSmaller(here, radius + 1);
					nav.moveAlongRadiusSmaller(here, targetMoveLoc, radiusToTarget);
				}
				else{
					//there are other people on the line and we can hold position whilst we wait for people to catch up
					//System.out.println("HOLDING THE LINE!");
					//dir = Direction.NONE;
				}

			}else {
				//There are turrets ahead of us, so we need to move into position with them
				//System.out.println("MOVING TO POSITION!");
				List<MapLocation> allowedTargets = nav.findAllowedLocations(here, radiusToWall, targetMoveLoc);
				nav.moveToFreeLocation(allowedTargets, here, targetMoveLoc);
			}

		}
	}

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


}
