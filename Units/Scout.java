/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import victorious_secret.Nav.Nav;
import victorious_secret.Fight.Fight;
import victorious_secret.Robot;

/**
 * @author APOC
 * 
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SCOUT
 A fast unit, unobstructed by rubble.
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
	 * 
	 */
	public Scout(RobotController _rc) 
	{
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		//fight = new Fight(rc, this); Scouts cannot fight
	}

	@Override
	public void move() throws GameActionException 
	{
		nav.move();
	}
	@Override
	protected void actions() throws GameActionException 
	{
		// TODO Auto-generated method stub
		
	}

}
