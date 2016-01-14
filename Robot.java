/**
 * 
 */
package victorious_secret;

import java.util.Random;
import victorious_secret.Nav.Nav;
import victorious_secret.Fight.Fight;
import battlecode.common.*;
/**
 * @author APOC
 * This serves as the root class from which all other robots derive
 */

public abstract class Robot {
	
	protected static RobotController rc;
	public Random rand;
	public static Nav nav;
	public static Fight fight;
	public MapLocation targetShootLoc;
	public MapLocation targetMoveLoc;
	
	public int listeningTo;
	public Direction messageIn;
	
	
	
	public abstract void move() throws GameActionException;
	
	protected abstract void actions() throws GameActionException;
	
	protected void listen() throws GameActionException
	{
		Signal sig = rc.readSignal();
		
		if(sig == null)
		{
			messageIn = null;
			return;
		}
		
		listeningTo = sig.getID();
		int n = 1;

        //ADDED COMMENT
		do
		{
			if(sig.getID() == listeningTo)					
			{
				n += 1;
			}

			sig = rc.readSignal();
		}while(sig != null);
		
		switch(n)
		{
			case 1:
				messageIn = Direction.NORTH;
				break;
			case 2:
				messageIn = Direction.EAST;
				break;
			case 3:
				messageIn = Direction.SOUTH;
				break;
			case 4:
				messageIn = Direction.WEST;
				break;
			default:
				messageIn = null;
				break;
		}
	}

	
	protected void broadcast() throws GameActionException
	{		
		Direction messageOut;
		if(fight.seenEnemies != null && fight.seenEnemies.length == 0)
		{
			messageOut = rc.getLocation().directionTo(nav.averageEnemyLoc());
			
			//start broadcast
			switch(messageOut)
			{
				case NORTH:
				case NORTH_EAST:
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					break;
					
				case EAST:
				case SOUTH_EAST:
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					break;
					
				case SOUTH:
				case SOUTH_WEST:
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					break;
					
				case WEST:
				case NORTH_WEST:
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					rc.broadcastSignal(rc.getType().sensorRadiusSquared);
					break;
				default:
					break;
			}	
		}
		 
		
	}	
}
