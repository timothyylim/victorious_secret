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
    private double MAX_DISTANCE = 100;

    private RealMatrix _weights;

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
        state[0][statenames.SELF_HEALTH.ordinal()] = normalise_health(rc.getType(), rc.getHealth());
        state[0][statenames.SELF_CORE_READY.ordinal()] = normalise_core_delay(rc.getType(), rc.getCoreDelay());
        state[0][statenames.SELF_WEAPON_READY.ordinal()] = normalise_weapon_delay(rc.getType(), rc.getWeaponDelay());

        //Get enemy state
        Fight.sense_map();

        RobotInfo enemy = Fight.findClosestEnemy(Fight.seenEnemies);
        if (enemy != null) {
            state[0][statenames.ENEMY_HEALTH.ordinal()] = normalise_health(enemy.type, enemy.health);
            state[0][statenames.ENEMY_CORE_READY.ordinal()] = normalise_core_delay(enemy.type, enemy.coreDelay);
            state[0][statenames.ENEMY_WEAPON_READY.ordinal()] = normalise_weapon_delay(enemy.type, enemy.weaponDelay);
        }else{
            state[0][statenames.ENEMY_HEALTH.ordinal()] = 0;
            state[0][statenames.ENEMY_CORE_READY.ordinal()] = 0;
            state[0][statenames.ENEMY_WEAPON_READY.ordinal()] = 0;
        }

        //Get world state
        if (enemy != null) {
            state[0][statenames.DISTANCE_TO_ENEMY.ordinal()] = enemy.location.distanceSquaredTo(rc.getLocation()) / MAX_DISTANCE;
        }else{
            state[0][statenames.DISTANCE_TO_ENEMY.ordinal()] = MAX_DISTANCE;
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

    private double normalise_health(RobotType t, double h){
        return h / t.maxHealth;
    }

    private double normalise_core_delay(RobotType t, double d){
        return d / Math.max(t.cooldownDelay, t.movementDelay);
    }

    private double normalise_weapon_delay(RobotType t, double d){
        return d / t.attackDelay;
    }
}

