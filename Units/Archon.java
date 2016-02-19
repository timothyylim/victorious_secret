/**
 * 
 */
package victorious_secret.Units;

import battlecode.common.*;
import victorious_secret.Behaviour.BugNav;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Strategy.Defend;
import victorious_secret.Strategy.Flee;

import java.util.List;
import java.util.Random;


/**
 * @author APOC
 * 
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#ARCHON
 * An important unit that cannot be constructed; builds other robots.
 * canAttack(): false
 * 
 * bytecodeLimit: 20000
 * maxHealth: 1000
 * movementDelay: 2
 * sensorRadiusSquared: 35
 * turnsInto: BIGZOMBIE
 */
public class Archon extends Robot {

	/**
	 *
	 */
	Defend defend;
	int buildQueue;

	public Archon(RobotController _rc) {

		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		defend = new Defend(rc, this);

		BugNav.initialise(rc);

		strat = Strategy.DEFEND;
		buildQueue = 0;
	}

	@Override
	public void move() throws GameActionException {
		if (listenForSignal()) {
			strat = Strategy.ATTACK;
		}

		RobotInfo[] hostiles = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
		RobotInfo[] allies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());

		if (rc.getHealth() < 650 && hostiles.length != 0) {
			strat = Strategy.FLEE;
		} else if (rc.getHealth() < 900 && hostiles.length != 0 && allies.length < 5) {
			strat = Strategy.FLEE;
		} else if (strat == Strategy.FLEE && hostiles.length == 0 && allies.length > 5) {
			strat = Strategy.DEFEND;
		}


		switch (strat) {
			case DEFEND:
				//defend.turtle();
				//turtle();
				break;

			case ATTACK:
				attackPete();
				break;

			case SCOUT:

				//spawn(RobotType.SCOUT);
				break;

			case FLEE:
				Flee.runFlee(rc);
				break;
			default:
				break;


		}
	}

	private void call_for_help() throws GameActionException {
		MapLocation loc = null; //= rc.getLocation();
		RobotInfo i = fight.findLowestHealthEnemy(fight.seenEnemies);
		if (i == null) {
			int broadcastRange = 150;
			rc.broadcastMessageSignal(loc.x, loc.y, broadcastRange);
		}
	}

	private void attackPete() throws GameActionException {
		//Move to signal
		maintainRadius();
		//then call defend code
		defend.turtle();
	}

	private void _move() throws GameActionException {
		if (rc.isCoreReady() && targetMoveLoc != null) {
			BugNav.setTarget(targetMoveLoc);
			Direction dir = BugNav.getNextMove();
			if (dir != null && rc.canMove(dir)) {
				rc.move(dir);
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

				int targetGuardRadius = Math.max(radiusToWall + 2, 1);

				if (radiusToTarget != targetGuardRadius) {
					//move into position
					List<MapLocation> allowedTargets = nav.findAllowedLocations(here, targetGuardRadius, targetMoveLoc);
					//System.out.println(allowedTargets);
					nav.moveToFreeLocation(allowedTargets, here, targetMoveLoc);
				}
			} else {
				//There are no visible turrets! You're lost, go home.
				//strat = Strategy.RETURN_TO_BASE;
				//returnToBase();

				_move();
			}
		}
	}

	public boolean listenForSignal() {
		Signal[] sigs = rc.emptySignalQueue();
		if (sigs != null && sigs.length > 0) {

			Signal sig = sigs[sigs.length - 1];
			int[] message = sig.getMessage();

			if (message != null && sig.getTeam() == rc.getTeam()) {
				//	System.out.println("signal received");
				targetMoveLoc = new MapLocation(message[0], message[1]);
				return true;
			}
		}
		return false;
	}

}


	//////////////////////////////////////////

	// ^^ Attack Methods ^^

	// vv Defend Methods vv

	////////////////////////////////////////

