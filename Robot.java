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
	
	public abstract void move() throws GameActionException;
	
	protected abstract void actions() throws GameActionException;

}
