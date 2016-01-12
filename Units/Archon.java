/**
 * 
 */
package victorious_secret.Units;

import java.util.Random;
import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Fight.Fight;
import victorious_secret.Nav.Nav;


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
	
	private RobotType[] buildQueue = {RobotType.SOLDIER}; //RobotType.GUARD, 
		
	public Archon(RobotController _rc) 
	{
		rc = _rc;
		rand = new Random(rc.getID());
		nav = new Nav(rc, this);
		fight = new Fight(rc, this);
	}

	@Override
	public void move() throws GameActionException 
	{	
		fight.spotEnemies();
		if(!spawn(buildQueue[rand.nextInt(buildQueue.length)]))
		{
			nav.flee();
		}
	}

	@Override
	protected void actions() throws GameActionException {
		// TODO Auto-generated method stub
		
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
