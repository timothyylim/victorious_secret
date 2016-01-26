/**
<<<<<<< HEAD
 *
 */
package victorious_secret.Units;

import java.util.Random;

import battlecode.common.*;

import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;


import victorious_secret.Strategy.Attack;
import victorious_secret.Strategy.Defend;
import victorious_secret.Strategy.Flee;

/**
 * @author APOC
<<<<<<< HEAD
 *
=======
 * 
>>>>>>> 45b64142e44a0dd44af44b58ab8e8ebf56678b6c
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
	Defend defend;

	public static Flee flee;

	public Soldier(RobotController _rc){
		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);

		strat = Strategy.DEFEND;
		defend = new Defend(rc, this);

		/*TESTING*/
		Flee.initialiseFlee(rc);
		Flee.setTarget(rc.getLocation());
		akk = new Attack(rc, this);

	}

	@Override
	public void move() throws GameActionException 
	{

		if(rc.getHealth() < 20)
		{
			//strat = Strategy.FLEE;
		}

		switch(strat) {
			case DEFEND:
				defend.turtle();
				break;
			case ATTACK:
           		/*Swarm Strategy:*/
				//System.out.println("signal received");
				fight.spotEnemies();
				fight.targetEnemies();
				RobotInfo attackEnemy;
				if(rc.isCoreReady() && rc.isWeaponReady()){
					if(fight.attackableEnemies != null && fight.attackableEnemies.length>0){
						attackEnemy = fight.findLowestHealthEnemy(fight.attackableEnemies);
						if(rc.isCoreReady() && rc.isWeaponReady() && rc.canAttackLocation(attackEnemy.location)){
							rc.attackLocation(attackEnemy.location);
						}
					}
				}
				
				listenForSignal();
				if (rc.isCoreReady()) {
					Direction dir = flee.getNextMove();
					if(rc.canMove(dir)){
						rc.move(flee.getNextMove());
					}
				}
				//System.out.println(flee.getNextMove());

				break;

			case SCOUT:
				//TODO:throw exception
				break;

			case FLEE:
				//    nav.flee();
			default:
				break;
		}



		//sig.setMessage(Signalling.MessageType.MOVE_EAST);
		//sig.broadcast();
	}

	public boolean listenForSignal(){

		Signal[] sigs = rc.emptySignalQueue();
		if(sigs != null && sigs.length > 0){

			Signal sig = sigs[sigs.length-1];
			int[] message = sig.getMessage();
			if(message != null && sig.getTeam() == rc.getTeam()) {
				MapLocation loc = new MapLocation(message[0],message[1]);
				Flee.setTarget(loc);


				return true;
			}
		}
		//flee.setTarget(rc.getLocation());
		return false;

	}


}
