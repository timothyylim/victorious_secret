/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;

/**
 * @author APOC
 *
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#GUARD
public static final RobotType GUARD
A melee unit equipped for zombie combat.
canAttack(): true

attackDelay: 1
attackPower: 2
attackRadiusSquared: 2
buildTurns: 10
bytecodeLimit: 10000
cooldownDelay: 1
maxHealth: 150
movementDelay: 2
partCost: 30
sensorRadiusSquared: 24
spawnSource: ARCHON
turnsInto: STANDARDZOMBIE
 */
public class Guard extends Robot {

	/**
	 * 
	 */
	
	RobotInfo archon;
	
	
	public Guard(RobotController _rc) 
	{
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
		strat = Strategy.DEFEND;
	}

	private void spot_archon()
	{
		RobotInfo[] team = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
		for(RobotInfo i : team)
		{
			if(i.type == RobotType.GUARD) //was Archon
			{
				archon = i;
				return;
			}
		}
	}

	@Override
	public void move() throws GameActionException 
	{
		if(archon == null)
		{
			spot_archon();
		}
//
//		if(!fight.fight())
//		{
//
//			if(archon != null)
//			{
//				spot_archon();
//				nav.guard(archon.location);
//			}
//			else
//			{
//				if(rc.getHealth()<20){
//					nav.flee();
//				}else{
//					nav.move();
//				}
//			}
//
//		}
	}
}
