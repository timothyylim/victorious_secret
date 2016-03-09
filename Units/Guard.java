/**
 * 
 */

package victorious_secret.Units;

import java.util.List;
import java.util.Random;

import battlecode.common.*;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Robot;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
<<<<<<< HEAD
import victorious_secret.Strategy.Attack;
=======
>>>>>>> 5770751ee26ed3c47ba42d75eb2f7cb645f8cc9e
import victorious_secret.Strategy.Defend;

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
		//nav = new Nav(rc, this);
		Nav.initialise(rc, this);
		//fight = new Fight(rc, this);
		Fight.initialise(rc, this);
		defend = new Defend(rc, this);
		BugNav.initialise(rc);
		team = rc.getTeam();
		strat = Strategy.DEFEND;
		targetMoveLoc = new MapLocation(449,172);

		setArchonLocations();
	}

	@Override
	public void move() throws GameActionException 
	{
		updateOurArchonLocations(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam()));

//		if(listenForSignal()){
//			strat = Strategy.ATTACK;
//		}
		
		switch(strat){
			case DEFEND:
				
				defend.turtle();
				break;
			case RETURN_TO_BASE:
				returnToBase();
				break;
			case ATTACK:
				maintainRadius();
				attackPattern();
				//_move();
				break;
			default:
				break;
		}
	}

	private void _move() throws GameActionException {
		if(rc.isCoreReady() && targetMoveLoc != null) {
			BugNav.setTarget(targetMoveLoc);
			Direction dir = BugNav.getNextMove();
			if(dir != null && rc.canMove(dir)) {
				rc.move(dir);
			}
		}
	}

	private void attackPattern() throws GameActionException{

		//If there are hostile units that are not Archon, ZombieDens, or Turrets, then they hold position
		//Unless their turret is in range of enemy turrets
		//Guards stay in line if their turret is threatened, they can only see Archons or Zombiedens

		RobotInfo[] enemiesInRange = Fight.attackableEnemies;
		RobotInfo t = null;
		if (enemiesInRange == null || enemiesInRange.length == 0) {
			RobotInfo[] enemiesInSight = Fight.seenEnemies;

			if(enemiesInSight != null){
				RobotInfo[] nearbyTurrets = Fight.spotNearbyTurrets();
				if(nearbyTurrets == null || nearbyTurrets.length == 0){
					t = Fight.findClosestEnemy(enemiesInSight);
				}else {
					for (RobotInfo r : enemiesInSight) {
						if (r.type == RobotType.ARCHON ||
								r.type == RobotType.ZOMBIEDEN ||
								r.type == RobotType.TURRET ||
								r.type == RobotType.TTM) {
							if (t == null || r.health < t.health) {
								t = r;
							}
						}
					}
				}
				if(t != null && rc.isCoreReady()) {
					Attack.getClose(t);
				}
			}
		} else {
			t = Fight.findLowestHealthEnemy(enemiesInRange);
			if(rc.isWeaponReady()){
				rc.attackLocation(t.location);
			}

		}
	}

	private void maintainRadius() throws GameActionException {
		if (rc.isCoreReady()) {
			MapLocation here = rc.getLocation();

			RobotInfo[] nearbyTurrets = Fight.spotNearbyTurrets();

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
					List<MapLocation> allowedTargets = Nav.findAllowedLocations(here, targetGuardRadius, targetMoveLoc);
					//System.out.println(allowedTargets);
					Nav.moveToFreeLocation(allowedTargets, here, targetMoveLoc);
				}
			} else {
				//There are no visible turrets! You're lost, go home.
				//strat = Strategy.RETURN_TO_BASE;
				//returnToBase();

				_move();
			}
		}
	}
	
	public boolean listenForSignal(){
		Signal[] sigs = rc.emptySignalQueue();
		if(sigs != null && sigs.length > 0){

			Signal sig = sigs[sigs.length-1];
			int[] message = sig.getMessage();
			if(message != null && sig.getTeam() == rc.getTeam()) {
				//System.out.println("signal received");
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
			Fight.targetEnemies();
			if(Fight.attackableEnemies != null && Fight.attackableEnemies.length>0){
				RobotInfo i = Fight.findLowestHealthEnemy(Fight.attackableEnemies);

			//	attackLoc = i.location;
			}else{
			//	attackLoc = null;
			}
		}
	}
}

