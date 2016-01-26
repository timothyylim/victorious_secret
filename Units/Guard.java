/**
 * 
 */

package victorious_secret.Units;

import java.util.List;
import java.util.Random;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Strategy.Defend;
import victorious_secret.Strategy.Flee;

/**
 * @author APOC
 *
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#GUARD
public static final RobotType GUARD
A melee unit equipped for zombie combat.
canAttack(): true

attackDelay: 1
attackPower: 2
attackRadiusSquared: 2
buildTurns: 10
bytecodeLimit: 10000
cooldownDelay: 1
maxHealth: 150
movementDelay: 2
partCost: 30
sensorRadiusSquared: 24
spawnSource: ARCHON
turnsInto: STANDARDZOMBIE
 */
public class Guard extends Robot {

	/**
	 * 
	 */
	
	RobotInfo archon;
	Defend defend;
	
	
	public Guard(RobotController _rc) 
	{
		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		defend = new Defend(rc, this);
		Flee.initialiseFlee(rc);

		team = rc.getTeam();
		strat = Strategy.DEFEND;
		strat = Strategy.ATTACK;
		targetMoveLoc = new MapLocation(449,172);

		setArchonLocations();
	}

	@Override
	public void move() throws GameActionException 
	{
		updateOurArchonLocations(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam()));


		switch(strat)
		{
			case DEFEND:
				defend.turtle();
				break;
			case RETURN_TO_BASE:
				returnToBase();
				break;
			case ATTACK:
				
				listenForSignal();
				maintainRadius();
				attackPattern();
				break;
			default:
				break;
		}

	}
	private void attackPattern() throws GameActionException{
		RobotInfo t = fight.findClosestEnemy(fight.spotEnemies());
		if(t != null && rc.isCoreReady()) {
			if(rc.canAttackLocation(t.location)){
				rc.attackLocation(t.location);
							
			}
			
//			akk.getClose(t);
		}
	}
	private void maintainRadius() throws GameActionException {
		if (rc.isCoreReady()) {
			MapLocation here = rc.getLocation();

			RobotInfo[] nearbyTurrets = fight.spotNearbyTurrets();

			int distanceToTarget = here.distanceSquaredTo(targetMoveLoc);
			int radiusToTarget = (int) Math.sqrt(distanceToTarget);
			int radiusToWall = 9999;

			if (nearbyTurrets != null) {
				for (RobotInfo t : nearbyTurrets) {
					int dt = (int) Math.sqrt(t.location.distanceSquaredTo(targetMoveLoc));
					if (dt < radiusToWall) {
						radiusToWall = dt;
					}
				}

				int targetGuardRadius = Math.max(radiusToWall - 2, 1);

				if (radiusToTarget != targetGuardRadius) {
					//move into position
					List<MapLocation> allowedTargets = nav.findAllowedLocations(here, targetGuardRadius, targetMoveLoc);
					if(allowedTargets != null) {
						nav.moveToFreeLocation(allowedTargets, here, targetMoveLoc);
					}
				}
			} else {
				//There are no visible turrets! You're lost, go home.
				strat = Strategy.RETURN_TO_BASE;
				returnToBase();
			}
		}
	}
	
	public boolean listenForSignal(){
		Signal[] sigs = rc.emptySignalQueue();
		if(sigs != null && sigs.length > 0){

			Signal sig = sigs[sigs.length-1];
			int[] message = sig.getMessage();
			if(message != null && sig.getTeam() == rc.getTeam()) {
				System.out.println("signal received");
				targetMoveLoc = new MapLocation(message[0], message[1]);
				//attackLoc = new MapLocation(message[0], message[1]);
				return true;
			}
		}
		return false;
	}

	public void updateAttackLoc() throws GameActionException {
		//System.out.println("update attack loc called");
		if(!listenForSignal()){
			fight.targetEnemies();
			if(fight.attackableEnemies != null && fight.attackableEnemies.length>0){
				RobotInfo i = fight.findLowestHealthEnemy(fight.attackableEnemies);
			//	attackLoc = i.location;
			}else{
			//	attackLoc = null;
			}
		}
	}
}

