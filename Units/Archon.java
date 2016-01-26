/**
 * 
 */
package victorious_secret.Units;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;
import victorious_secret.Behaviour.Nav;
import victorious_secret.Strategy.Defend;
import victorious_secret.Strategy.Flee;

import java.util.Random;


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
	int buildQueue;

	public Archon(RobotController _rc) 
	{
		
		rc = _rc;
		//rand = new Random(rc.getID());
		rand = new Random();
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);

		defend = new Defend(rc, this);
		
		strat = Strategy.DEFEND;
		buildQueue = 0;
	}

	@Override
	public void move() throws GameActionException 
	{

		RobotInfo[] hostiles = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
		RobotInfo[] allies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
		
		if(rc.getHealth()<650 && hostiles.length != 0){
			strat=Strategy.FLEE;
		}
		else if(rc.getHealth()<900 && hostiles.length != 0 && allies.length < 5){
			strat=Strategy.FLEE;
		}
		else if (strat == Strategy.FLEE && hostiles.length == 0 && allies.length > 5){
			strat=Strategy.DEFEND;
		}


		switch(strat)
		{
			case DEFEND:
				defend.turtle();
				break;

			case ATTACK:
				attackPete();
				break;

			case SCOUT:

				//spawn(RobotType.SCOUT);
				break;
			
			case FLEE:
				Flee.runFlee(rc);
				break;
			default:
				break;


		}


	}
	
	private void attackPete() throws GameActionException {
		switch(buildQueue){
		case 0:
			spawn(RobotType.SCOUT);
			buildQueue++;
			break;
		case 1:
			if(spawn(RobotType.TURRET)){
				buildQueue++;
			}

			break;
		case 2:
			if(spawn(RobotType.GUARD)){
				buildQueue = 1;
			}
			break;
		
			
		}
		
		
		
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
