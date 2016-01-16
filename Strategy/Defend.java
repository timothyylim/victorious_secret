package victorious_secret.Strategy;
import battlecode.common.*;
import victorious_secret.Behaviour.Fight;
import victorious_secret.RobotPlayer;
import victorious_secret.Robot;

/**
 * Created by ple15 on 15/01/16.
 */
public class Defend {

	public static RobotController rc;
	public static Robot robot;
	static int[] possibleDirections = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction movingDirection = Direction.EAST;
	boolean ZombieRush = true;
	
	public Defend(RobotController _rc, Robot _robot){
		rc = _rc;
		robot = _robot;
	}
	
	public void turtle() throws GameActionException {
		// Look for enemies 

		RobotInfo[] opponentEnemies = Fight.targetEnemies();

		if(opponentEnemies.length > 0 && rc.getType().canAttack()){
			// Optimize who to attack?
			if(rc.isWeaponReady()){
				rc.attackLocation(opponentEnemies[0].location);
			}
		}
		if(rc.isCoreReady()){

			//************//
			//ZOMBIE RUSH!!!
			if(ZombieRush){
				// ARCHON
				if(rc.getType() == RobotType.ARCHON){			
					//If Zombie Rush
					buildGuardRing();
                    //buildSoldierRing();
				}
			}
			//GUARD
			if(rc.getType() == RobotType.GUARD){
				circleArchon();
			}
			
			//************//
			if(ZombieRush == false){
				
				
			}
		}
	}
		
	

	
	private void circleArchon() throws GameActionException {
		// TODO Auto-generated method stub

		RobotInfo[] archon = rc.senseNearbyRobots(5);
		if (archon.length > 0){
			Direction archon_direction = rc.getLocation().directionTo(archon[0].location);
			Direction forward_direction = getForward(archon_direction);
			//MapLocation final_loc = rc.getLocation().subtract(archon_direction);
			//Direction final_direction = rc.getLocation().directionTo(final_loc);
			//movingDirection = final_direction;
			if(forward_direction == null){
				movingDirection = archon_direction;
			}
			else{
				movingDirection = archon_direction;
			}


			movingDirection = forwardish(movingDirection);
			MapLocation destination = rc.getLocation().add(movingDirection,1);

			int distance_from_archon = destination.distanceSquaredTo(archon[0].location);

			System.out.println(distance_from_archon);
	

			if(rc.canMove(movingDirection)){
				if(Math.sqrt(distance_from_archon) < 3)
					rc.move(movingDirection);
					
			}
		}
		
	}

	private void buildGuardRing() throws GameActionException {
		// TODO Auto-generated method stub
		if(rc.canBuild(Direction.NORTH_EAST, RobotType.GUARD)){
			rc.build(Direction.NORTH_EAST, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.SOUTH_WEST, RobotType.GUARD)){
			rc.build(Direction.SOUTH_WEST, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.NORTH_WEST, RobotType.GUARD)){
			rc.build(Direction.NORTH_WEST, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.SOUTH_EAST, RobotType.GUARD)){
			rc.build(Direction.SOUTH_EAST, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.NORTH, RobotType.GUARD)){
			rc.build(Direction.NORTH, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.SOUTH, RobotType.GUARD)){
			rc.build(Direction.SOUTH, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.EAST, RobotType.GUARD)){
			rc.build(Direction.EAST, RobotType.GUARD);
		}
		else if(rc.canBuild(Direction.WEST, RobotType.GUARD)){
			rc.build(Direction.WEST, RobotType.GUARD);
		}
	}

	private void buildSoldierRing() throws GameActionException {
		// TODO Auto-generated method stub
		if(rc.canBuild(Direction.NORTH_EAST, RobotType.SOLDIER)){
			rc.build(Direction.NORTH_EAST, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.SOUTH_WEST, RobotType.SOLDIER)){
			rc.build(Direction.SOUTH_WEST, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.NORTH_WEST, RobotType.SOLDIER)){
			rc.build(Direction.NORTH_WEST, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.SOUTH_EAST, RobotType.SOLDIER)){
			rc.build(Direction.SOUTH_EAST, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.NORTH, RobotType.SOLDIER)){
			rc.build(Direction.NORTH, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.SOUTH, RobotType.SOLDIER)){
			rc.build(Direction.SOUTH, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.EAST, RobotType.SOLDIER)){
			rc.build(Direction.EAST, RobotType.SOLDIER);
		}
		else if(rc.canBuild(Direction.WEST, RobotType.SOLDIER)){
			rc.build(Direction.WEST, RobotType.SOLDIER);
		}
	}

	private static Direction getForward(Direction archon_direction) {
		if (archon_direction == Direction.NORTH_EAST){
			return Direction.EAST;
		}
		else if (archon_direction == Direction.EAST){
			return Direction.SOUTH; 
		}
		else if (archon_direction == Direction.SOUTH_EAST){
			return Direction.SOUTH; 
		}
		else if (archon_direction == Direction.SOUTH){
			return Direction.WEST; 
		}
		else if (archon_direction == Direction.SOUTH_WEST){
			return Direction.WEST; 
		}
		else if (archon_direction == Direction.WEST){
			return Direction.NORTH; 
		}
		else if (archon_direction == Direction.NORTH_WEST){
			return Direction.NORTH; 
		}
		else if (archon_direction == Direction.NORTH){
			return Direction.EAST; 
		}
		return null;
	}

	private static Direction forwardish(Direction ahead) throws GameActionException {
		for (int i:possibleDirections){
			Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
			if(rc.canMove(candidateDirection)){
				return candidateDirection;
				
				
			}
		}
		return ahead;
		
	}
	
	private static RobotInfo[] joinRobotInfo(RobotInfo[] zombieEnemies, RobotInfo[] normalEnemies) {
		RobotInfo[] opponentEnemies = new RobotInfo[zombieEnemies.length+normalEnemies.length];
		int index = 0;
		for (RobotInfo i:zombieEnemies){
			opponentEnemies[index] = i;
			index++;
		}

		for (RobotInfo i:normalEnemies){
			opponentEnemies[index] = i;
			index++;
		}
		return opponentEnemies;
	}
	
	
}
