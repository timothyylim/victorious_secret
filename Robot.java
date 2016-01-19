/**
 * 
 */
package victorious_secret;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import victorious_secret.Behaviour.Nav;
import victorious_secret.Behaviour.Fight;
import battlecode.common.*;
<<<<<<< HEAD
import victorious_secret.Behaviour.Signalling;
import victorious_secret.Strategy.Attack;
=======
<<<<<<< HEAD
import victorious_secret.Strategy.Scout;
=======
import victorious_secret.Strategy.Attack;
>>>>>>> 1144482c476d6468aa36dc2c137eb235c3e56a8f
>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0

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
<<<<<<< HEAD
	public static Signalling sig;

	public static Team team;
	//public static int readyTurn;

=======
>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0
	public MapLocation targetShootLoc;
	public MapLocation targetMoveLoc;
	public static Map<Integer, MapLocation> enemyArchonLocations = new HashMap<>();
    public MapLocation[] zombieDenLocations;
    public Map<Integer, MapLocation> enemyUnitLocations;
	
	public int listeningTo;
	public Direction messageIn;

	//TODO: put enum into own class
	public enum Strategy {ATTACK, DEFEND, FLEE, SCOUT}
	public Strategy strat;
<<<<<<< HEAD
	
	public abstract void move() throws GameActionException;

    public static void setArchonLocations()
    {
        enemyArchonLocations = new HashMap<>();
        MapLocation[] initialArchonLocations = rc.getInitialArchonLocations(team.opponent());
        int i = -1;
        for(MapLocation m : initialArchonLocations){
            enemyArchonLocations.put(i, m);
            i--;
        }

		enemyArchonLocations.put(0, nav.averageLoc(initialArchonLocations));
    }

	public static void removeArchonLocation(MapLocation m)
	{
		Iterator<Map.Entry<Integer,MapLocation>> iter = enemyArchonLocations.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, MapLocation> entry = iter.next();
			if(entry.getValue().compareTo(m) == 0) {
				iter.remove();
			}
		}
	}

=======

	
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
>>>>>>> 321bfbdb6299f51b1140e2e12dc4fa03819995f0
}
