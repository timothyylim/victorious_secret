/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import victorious_secret.Robot;
import victorious_secret.Fight.Fight;
import victorious_secret.Nav.Nav;

/**
 * @author APOC
 * 
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#VIPER
A special unit capable of infecting robots with a damaging strain of the zombie virus.
canAttack(): true

attackDelay: 3
attackPower: 2
attackRadiusSquared: 13
buildTurns: 30
bytecodeLimit: 10000
cooldownDelay: 1
maxHealth: 120
movementDelay: 2
partCost: 150
sensorRadiusSquared: 24
spawnSource: ARCHON
turnsInto: RANGEDZOMBIE
 */
public class Viper extends Robot {

	/**
	 * 
	 */
	public Viper(RobotController _rc) 
	{
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
	}

	@Override
	public void move() throws GameActionException 
	{
		if(!fight.fight())
		{
			nav.move();
		}
	}

	@Override
	protected void actions() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

}