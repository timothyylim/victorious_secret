package victorious_secret.Machine_Learning;

import battlecode.common.*;
import victorious_secret.Robot;

import org.apache.commons.math3.linear.*;

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


    double[][] _weights;

    public qSoldier(RobotController _rc){
        rc = _rc;
        _weights = get_weights();
    }

    @Override
    public void move() throws GameActionException
    {
        double[][] state = read_state();

        RealMatrix s = MatrixUtils.createRealMatrix(state);
        RealMatrix w = MatrixUtils.createRealMatrix(_weights);

        RealMatrix p = s.multiply(w);

    }

    private double[][] read_state(){
        double[][] state = new double[1][10];
        //Get state
        return state;
    }

    private double[][] get_weights(){
        double[][] weights = new double[1][10];
        //WRITE WEIGHTS HERE

        //STOP WRITE
        return weights;
    }

}

