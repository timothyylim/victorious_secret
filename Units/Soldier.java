/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;

/**
 * @author APOC
 * 
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SOLDIER
An all-around ranged unit.
canAttack(): true

attackDelay: 2
attackPower: 4
attackRadiusSquared: 13
buildTurns: 10
bytecodeLimit: 10000
cooldownDelay: 1
maxHealth: 60
movementDelay: 2
partCost: 30
sensorRadiusSquared: 24
spawnSource: ARCHON
turnsInto: STANDARDZOMBIE
 */
public class Soldier extends Robot {

	/**
	 * 
	 */
	public Soldier(RobotController _rc) 
	{
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		strat = Strategy.DEFEND;
	}

	@Override
	public void move() throws GameActionException 
	{
		if(!fight.fight())
		{
			broadcast();
			listen();

			if(rc.getHealth() < 20){
				
				nav.flee();	
			}else{
				nav.move();		
			}
			
		}
	}
}
