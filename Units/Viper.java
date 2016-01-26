/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;

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
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		strat = Strategy.DEFEND;
	}

	@Override
	public void move() throws GameActionException 
	{

	// Look for enemies
		RobotInfo[] opponentEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);

		if (opponentEnemies != null && opponentEnemies.length > 0 && rc.getType().canAttack()) {
			RobotInfo target = fight.findLowestHealthUninfectedEnemy(opponentEnemies);
			if(target == null){
				target = fight.findLowestHealthEnemy(opponentEnemies);
			}
			if(target != null && rc.isWeaponReady()) {
				rc.attackLocation(target.location);
				return;
			}
		}

		if(rc.isCoreReady() && rc.canMove(Direction.WEST)) {
			System.out.println("Moving " + Direction.WEST);
			rc.move(Direction.WEST);
			return;
		}


	}
}
