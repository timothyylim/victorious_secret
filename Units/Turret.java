/**
 * 
 */
package victorious_secret_defense.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import victorious_secret_defense.Robot;
import victorious_secret_defense.Behaviour.Fight;
import victorious_secret_defense.Behaviour.Nav;
import victorious_secret_defense.Strategy.Defend;

/**
 * @author APOC
 * 
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
	 * 
	 */
	Defend defend;
	public Turret(RobotController _rc) 
	{
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		strat = Strategy.DEFEND;
		defend = new Defend(rc, this);
	}

	@Override
	public void move() throws GameActionException 
	{
		switch(strat)
		{
			case DEFEND:
				defend.turtle();
				break;
			default:
				break;

		}
	}
}
