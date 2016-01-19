/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;

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
	MapLocation attackLoc;

	public Turret(RobotController _rc) 
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

		//updateAttackLoc();
		listenForSignal();
		if(attackLoc != null){

			if(rc.isCoreReady()){
				if(rc.canAttackLocation(attackLoc)){
					rc.attackLocation(attackLoc);
				}
			}
		}

	}


	public boolean listenForSignal(){

		Signal[] sigs = rc.emptySignalQueue();
		if(sigs != null && sigs.length > 0){

			Signal sig = sigs[sigs.length-1];
			int[] message = sig.getMessage();
			if(message != null){
				System.out.println("signalreceved");
				attackLoc = new MapLocation(message[0],message[1]);
				return true;
			}
			/*
			int[] loc = sigs[0].getMessage();//.getMessage();
			if(loc !=null){

			}
			*/
		}
		return false;
	}

	public void updateAttackLoc(){
		if(listenForSignal()){

		}else{

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
