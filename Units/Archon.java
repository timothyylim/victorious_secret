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
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#ARCHON
 * An important unit that cannot be constructed; builds other robots.
 * canAttack(): false
 * 
 * bytecodeLimit: 20000
 * maxHealth: 1000
 * movementDelay: 2
 * sensorRadiusSquared: 35
 * turnsInto: BIGZOMBIE
 */
public class Archon extends Robot {

	/**
	 * 
	 */
	Defend defend;
	
	//private RobotType[] buildQueue = {RobotType.SOLDIER}; //RobotType.GUARD, 
		
	public Archon(RobotController _rc) 
	{
		
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);

		//Uncomment as necessary
		strat = Strategy.DEFEND;
		defend = new Defend(rc, this);
//		strat = Strategy.ATTACK;
//		strat = Strategy.SCOUT;
//		strat = Strategy.FLEE;
	}

	@Override
	public void move() throws GameActionException 
	{

		switch(strat)
		{
			case DEFEND:
				defend.turtle();
				break;

			case ATTACK:
				attackPete();
				break;

			case SCOUT:
				//TODO:throw exception
				break;
			
			case FLEE:
				fleeMo();
			default:
				break;


		}
		//fight.spotEnemies();

//		if(!spawn(buildQueue[rand.nextInt(buildQueue.length)]))
//		{
//			nav.flee();
//		}

	}

	private void fleeMo() {
	}

	private void attackPete() {
	}

	private void turtle() {
		//TODO: TIM
	}

	private MapLocation bestArchonLocation()
	{
		MapLocation[] aLocs = rc.getInitialArchonLocations(rc.getTeam());
		return aLocs[0];
	}
	
	private Boolean spawn(RobotType roro) throws GameActionException 
	{
		if(rc.isCoreReady() && rc.hasBuildRequirements(roro))
		{
			int i = 0;
			do
			{
				Direction buildDir = Direction.values()[rand.nextInt(8)];
				if(rc.canBuild(buildDir, roro))
				{
					rc.build(buildDir, roro);
					return true;
				}
				i++;
			}while(i > 5);
		}
		return false;
	}

}
