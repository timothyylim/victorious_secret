/**
 * 
 */
package team099.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import team099.Robot;
import team099.Behaviour.Fight;
import team099.Behaviour.Nav;
import team099.Strategy.Defend;


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
	Defend defend;

	public Soldier(RobotController _rc) 
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
	public void move() throws GameActionException 
	{
        //System.out.println();
		//sig.listen();

	//	listen();
	//	broadcast();

		if(rc.getHealth() < 20)
		{
			//strat = Strategy.FLEE;
		}

		switch(strat) {
            case DEFEND:
				defend.turtle();
                break;
            case ATTACK:
           //     akk.kiteStratgey();
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
}
