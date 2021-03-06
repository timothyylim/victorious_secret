package victorious_secret;

import battlecode.common.*;
import victorious_secret.Machine_Learning.*;
import victorious_secret.Units.*;

public class RobotPlayer
{
	static RobotController rc;
	private static Robot robot;

	public Boolean DEBUG;
	public static Boolean Q_SOLDIER = true;

			
	public static void run(RobotController _rc)
	{
		rc = _rc;
		try
		{
			//Set the unit to the correct class
			switch (rc.getType())
			{
			case ARCHON: robot = new Archon(rc); break;
			case GUARD: robot = new Guard(rc); break;
			case SCOUT: robot = new Scout(rc); break;
			case SOLDIER:
				if (Q_SOLDIER){
					robot = new qSoldier(rc);
					break;
				}else {
					robot = new Guard(rc);
					break; //sSoldier(rc); break;
				}
			case TURRET: case TTM: robot = new Turret(rc); break;
			case VIPER: robot = new Viper(rc); break;
			default: 
				throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, 
						"Whoa, I dont know what this unit is!" + " " + rc.getType().toString()); 
			}

			while (true) 
			{
				robot.move();
				Clock.yield();
			}
		}
		catch (GameActionException e) 
		{
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}
}
	
	