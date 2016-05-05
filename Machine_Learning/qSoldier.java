package victorious_secret.Machine_Learning;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;

import org.apache.commons.math3.linear.*;

import java.util.Random;

/**
 * @author APOC
 * http://s3.amazonaws.com/battlecode-releases-2016/releases/javadoc/battlecode/common/RobotType.html#SOLDIER
An all-around ranged unit.
canAttack(): true

attackDelay: 2
attackPower: 4
attackRadiusSquared: 13
buildTurns: 10
bytecodeLimit: 10000
cooldownDelay: 1
maxHealth: 60
movementDelay: 2
partCost: 30
sensorRadiusSquared: 24
spawnSource: ARCHON
turnsInto: STANDARDZOMBIE
 */
public class qSoldier extends Robot {

    /**
     *
     */
    private enum statenames{SELF_HEALTH, SELF_CORE_READY, SELF_WEAPON_READY, ENEMY_HEALTH, ENEMY_CORE_READY,
        ENEMY_WEAPON_READY, DISTANCE_TO_ENEMY, ACTION}
    private enum actions{DO_NOTHING, MOVE_NORTH, MOVE_NORTH_EAST, MOVE_EAST, MOVE_SOUTH_EAST, MOVE_SOUTH,
        MOVE_SOUTH_WEST, MOVE_WEST, MOVE_NORTH_WEST, ATTACK_TARGET}

    RealMatrix _weights;

    public qSoldier(RobotController _rc){
        rc = _rc;
        _weights =  MatrixUtils.createRealMatrix(get_weights());
        rand = new Random();
    }

    @Override
    public void move() throws GameActionException
    {
        double[][] state = read_state();
        //pick action
        state[0][statenames.ACTION.ordinal()] = rand.nextInt(statenames.values().length);

        RealMatrix s = MatrixUtils.createRealMatrix(state);
        RealMatrix p = s.multiply(_weights);

    }

    private double[][] read_state() throws GameActionException {
        double[][] state = new double[1][statenames.values().length];
        //Get robot state
        state[0][statenames.SELF_HEALTH.ordinal()] = rc.getHealth();
        state[0][statenames.SELF_CORE_READY.ordinal()] = rc.getCoreDelay();
        state[0][statenames.SELF_WEAPON_READY.ordinal()] = rc.getWeaponDelay();

        //Get enemy state
        Fight.sense_map();

        RobotInfo enemy = Fight.findClosestEnemy(Fight.seenEnemies);
        if (enemy != null) {
            state[0][statenames.ENEMY_HEALTH.ordinal()] = enemy.health;
            state[0][statenames.ENEMY_CORE_READY.ordinal()] = enemy.coreDelay;
            state[0][statenames.ENEMY_WEAPON_READY.ordinal()] = enemy.weaponDelay;
        }else{
            state[0][statenames.ENEMY_HEALTH.ordinal()] = 0;
            state[0][statenames.ENEMY_CORE_READY.ordinal()] = 0;
            state[0][statenames.ENEMY_WEAPON_READY.ordinal()] = 0;
        }

        //Get world state
        if (enemy != null) {
            state[0][statenames.DISTANCE_TO_ENEMY.ordinal()] = enemy.location.distanceSquaredTo(rc.getLocation());
        }else{
            state[0][statenames.DISTANCE_TO_ENEMY.ordinal()] = 100;
        }

        return state;
    }

    private double[][] get_weights(){
        double[][] weights = new double[1][statenames.values().length];
        //WRITE WEIGHTS HERE
        //initialise it to random
        for(int i = 0; i < statenames.values().length; i++){
            weights[0][i] = rand.nextDouble();
        }

        //STOP WRITE
        return weights;
    }

}

