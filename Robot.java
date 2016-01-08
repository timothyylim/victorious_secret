/**
 * 
 */
package victorious_secret;

/**
 * @author APOC
 * This serves as the root class from which all other robots derive
 */

import java.util.Random;
import battlecode.common.*;

public abstract class Robot {
	public Random rand;
	public RobotController rc;
	
	public abstract void move();
	
	protected abstract void actions() throws GameActionException;
}
