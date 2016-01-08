/**
 * 
 */
package victorious_secret;

import battlecode.common.GameActionException;
import victorious_secret.Robot;

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
	public Turret() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void actions() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

}
