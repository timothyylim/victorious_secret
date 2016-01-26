/**
 *
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Robot;

/**
 * @author APOC
 *
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SCOUT
A fast unit, unobstructed by rubble.
=======
import team099.Robot;
import team099.Behaviour.Fight;
import team099.Behaviour.Nav;

/**
 * @author APOC
 * 
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SCOUT
 A fast unit, unobstructed by rubble.
>>>>>>> 45b64142e44a0dd44af44b58ab8e8ebf56678b6c
canAttack(): false

buildTurns: 15
bytecodeLimit: 20000
maxHealth: 100
movementDelay: 1
partCost: 40
sensorRadiusSquared: 53
spawnSource: ARCHON
turnsInto: FASTZOMBIE
 */
public class Scout extends Robot {

	/**
<<<<<<< HEAD
	 *
	 */
	victorious_secret.Strategy.Scout scout;



	public Scout(RobotController _rc){
		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		strat = Strategy.DEFEND;
		fight = new Fight(rc, this);

		scout = new victorious_secret.Strategy.Scout(rc,this);
	}

	@Override
	public void move() throws GameActionException
	{
		scout.runScoutStrategy5();
	}
}
