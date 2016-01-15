/**
 * 
 */
package victorious_secret;

import java.util.Random;

import victorious_secret.Behaviour.Nav;
import victorious_secret.Behaviour.Fight;
import battlecode.common.*;
<<<<<<< HEAD
import victorious_secret.Strategy.Scout;
=======
import victorious_secret.Strategy.Attack;
>>>>>>> 1144482c476d6468aa36dc2c137eb235c3e56a8f

/**
 * @author APOC
 * This serves as the root class from which all other robots derive
 */

public abstract class Robot {
	
	protected static RobotController rc;
	public Random rand;
	public static Nav nav;
	public static Fight fight;
    public static Attack akk;
	public MapLocation targetShootLoc;
	public MapLocation targetMoveLoc;
	
	public int listeningTo;
	public Direction messageIn;

	//TODO: put enum into own class
	public enum Strategy {ATTACK, DEFEND, FLEE, SCOUT}
	public Strategy strat;

	
	public abstract void move() throws GameActionException;
	
	protected void listen() throws GameActionException
	{
		Signal[] sigs = rc.emptySignalQueue();
		
		if(sigs.length == 0)
		{
			messageIn = null;
			return;
		}

        int n = 0;
        for(Signal sig : sigs)
        {
            if(listeningTo == 0)
            {
                listeningTo = sig.getID();
            }

			if(sig.getID() == listeningTo)					
			{
				n += 1;
			}
		}
        listeningTo = 0;
		
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
		if(Fight.seenEnemies != null && Fight.seenEnemies.length == 0)
		{
			messageOut = rc.getLocation().directionTo(Nav.averageLoc(Fight.seenEnemies));
			
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
