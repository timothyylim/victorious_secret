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
		strat = Strategy.ATTACK;
		targetMoveLoc = new MapLocation(449,172);

		setArchonLocations();
	}

	@Override
	public void move() throws GameActionException 
	{
		updateOurArchonLocations(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam()));

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
				break;
			default:
				break;
		}
	}

	private void attackPattern() throws GameActionException{
		//If there are hostile units that are not Archon, ZombieDens, or Turrets, then they hold position
		//Unless their turret is in range of enemy turrets
		//Guards stay in line if their turret is threatened, they can only see Archons or Zombiedens

		RobotInfo[] enemiesInRange = fight.targetEnemies();
		RobotInfo t = null;
		if (enemiesInRange == null || enemiesInRange.length == 0) {
			RobotInfo[] enemiesInSight = fight.spotEnemies();
			if(enemiesInSight != null){

				for(RobotInfo r : enemiesInSight){
					if(r.type == RobotType.ARCHON ||
							r.type == RobotType.ZOMBIEDEN ||
							r.type == RobotType.TURRET ||
							r.type == RobotType.TTM){
						if(t == null || r.health < t.health) {
							t = r;
						}
					}
				}

				if(t != null && rc.isCoreReady()) {
					akk.getClose(t);
				}
			}
		} else {
			t = fight.findLowestHealthEnemy(enemiesInRange);
			if(rc.isWeaponReady()){
				rc.attackLocation(t.location);
			}
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
					//System.out.println(allowedTargets);
					nav.moveToFreeLocation(allowedTargets, here, targetMoveLoc);
				}
			} else {
				//There are no visible turrets! You're lost, go home.
				strat = Strategy.RETURN_TO_BASE;
				returnToBase();
			}
		}
	}
}

