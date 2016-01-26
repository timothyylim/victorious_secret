/**
 * 
 */

package victorious_secret;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import battlecode.common.*;

import victorious_secret.Behaviour.*;
import victorious_secret.Strategy.Attack;
import victorious_secret.Strategy.Flee;

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

	public static Signalling sig;
	public static Team team;

	public MapLocation targetShootLoc;
	public MapLocation targetMoveLoc;
	public static Map<Integer, MapLocation> enemyArchonLocations = new HashMap<>();
	public static Map<Integer, MapLocation> ourArchonLocations = new HashMap<>();
    public MapLocation[] zombieDenLocations;
    public Map<Integer, MapLocation> enemyUnitLocations;
	
	public int listeningTo;
	public Direction messageIn;

	//TODO: put enum into own class
	public enum Strategy {ATTACK, DEFEND, FLEE, SCOUT, RETURN_TO_BASE}
	public Strategy strat;
	
	public abstract void move() throws GameActionException;

	private static void updateUnitLocations(Map<Integer, MapLocation> locationMap, RobotInfo[] units,
											RobotType rType){
		for(RobotInfo r : units){
			if(r.type == rType){
				if(locationMap.containsKey(r.ID)){
					locationMap.replace(r.ID, r.location);
				}else{
					locationMap.put(r.ID, r.location);
				}
			}
		}
	}



	protected static void returnToBase() throws GameActionException {
		//TODO: if(can see archon){ DEFEND ARCHON }
		//else{ ... }
		MapLocation t = fight.findClosestMapLocation(ourArchonLocations.values(), rc.getLocation());
		System.out.println("LOST! RETURN TO BASE: " + t);
		Flee.setTarget(t);
		Direction dir = Flee.getNextMove();
		if(rc.canMove(dir)) {
			rc.move(dir);
		}
	}

	public static void updateOurArchonLocations(RobotInfo[] allies){
		updateUnitLocations(ourArchonLocations, allies, RobotType.ARCHON);
	}

	public static void updateEnemyArchonLocations(RobotInfo[] enemies){
		updateUnitLocations(enemyArchonLocations, enemies, RobotType.ARCHON);
	}

    public static void setArchonLocations()
    {
		setEnemyArchonLocations();
		setOurArchonLocations();
    }

	private static void setEnemyArchonLocations(){
		enemyArchonLocations = new HashMap<>();
		MapLocation[] initialArchonLocations = rc.getInitialArchonLocations(team.opponent());
		int i = -1;
		for(MapLocation m : initialArchonLocations){
			enemyArchonLocations.put(i, m);
			i--;
		}
		enemyArchonLocations.put(0, nav.averageLoc(initialArchonLocations));
	}

	private static void setOurArchonLocations(){
		ourArchonLocations = new HashMap<>();
		MapLocation[] initialArchonLocations = rc.getInitialArchonLocations(team);
		int i = -1;
		for(MapLocation m : initialArchonLocations){
			ourArchonLocations.put(i, m);
			i--;
		}
		ourArchonLocations.put(0, nav.averageLoc(initialArchonLocations));
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
}
