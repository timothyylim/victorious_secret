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
	public enum Strategy {ATTACK, DEFEND, FLEE, SCOUT}
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

	public static void updateEnemyArchonLocations(RobotInfo[] enemies){
		updateUnitLocations(enemyArchonLocations, enemies, RobotType.ARCHON);
	}

	public static void updateOurArchonLocations(RobotInfo[] allies){
		updateUnitLocations(ourArchonLocations, allies, RobotType.ARCHON);
	}

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
}
