/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Strategy.Defend;




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
	MapLocation attackLoc;

	public Turret(RobotController _rc) 
	{
		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		strat = Strategy.DEFEND;
		defend = new Defend(rc, this);
	}

	@Override
	public void move() throws GameActionException {

		switch (strat) {
			case DEFEND:
//				updateAttackLoc();
//				if(attackLoc != null && rc.isCoreReady() && rc.canAttackLocation(attackLoc)){
//					rc.attackLocation(attackLoc);
//				}

				defend.turtle();

				break;
			default:
				break;


		}
	}


	public boolean listenForSignal(){

		Signal[] sigs = rc.emptySignalQueue();
		if(sigs != null && sigs.length > 0){

			Signal sig = sigs[sigs.length-1];
			int[] message = sig.getMessage();
			if(message != null) {
				attackLoc = new MapLocation(message[0], message[1]);
				return true;
			}
		}
		return false;
	}

	public void updateAttackLoc(){
		if(!listenForSignal()){
			fight.targetEnemies();
			if(fight.attackableEnemies != null && fight.attackableEnemies.length>0){

				RobotInfo i = fight.findClosestEnemy(fight.attackableEnemies);
				attackLoc = i.location;
//						fight.findLowestHealthEnemy(fight.attackableEnemies).location;
			}else{
				attackLoc = null;
			}
		}
	}
}
