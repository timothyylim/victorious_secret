package victorious_secret.Machine_Learning;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;


//import org.apache.commons.math3.linear.*;

import victorious_secret.org.neuroph.core.NeuralNetwork;
import victorious_secret.org.neuroph.core.data.DataSet;
import victorious_secret.org.neuroph.nnet.MultiLayerPerceptron;
import victorious_secret.org.neuroph.util.TransferFunctionType;

import java.util.Random;

import static java.lang.Math.abs;

public class qSoldier extends Robot {
//    private enum statenames{SELF_HEALTH, SELF_DELTA_HEALTH, SELF_CORE_READY, SELF_WEAPON_READY, ENEMY_HEALTH, ENEMEY_DELTA_HEALTH, ENEMY_CORE_READY,
//        ENEMY_WEAPON_READY, DISTANCE_TO_ENEMY, DIRECTION_OF_ENEMY, ACTION, RANDOM_FACTOR}
    private enum statenames{SELF_HEALTH, SELF_X, SELF_Y, ENEMY_X, ENEMY_Y}

//    private enum actions{MOVE_SOUTH_WEST, MOVE_WEST, MOVE_SOUTH, MOVE_NORTH_WEST, DO_NOTHING, MOVE_SOUTH_EAST,
//        MOVE_NORTH, MOVE_EAST, MOVE_NORTH_EAST, ATTACK_TARGET}
    private enum actions{MOVE_SOUTH_WEST, MOVE_WEST, MOVE_SOUTH, MOVE_NORTH_WEST, DO_NOTHING, MOVE_SOUTH_EAST,
            MOVE_NORTH, MOVE_EAST, MOVE_NORTH_EAST}
    private double MAX_DISTANCE = 100;

    private Direction last_known_direction;
    private double prev_health;
    private double prev_enemy_health;
//    private NeuralNetwork _nn;
//    private DataSet _hist;
    private Matrix _weights;
    String key;
    Random rand;

    public qSoldier(RobotController _rc){

        rc = _rc;
        rand = new Random();
        prev_health = rc.getHealth();
        Fight.initialise(rc, this);
        key = String.valueOf(abs(rand.nextInt()));
        last_known_direction = Direction.EAST;

        double [][] t = new double[][]{{ 0.21557775,  0.21865859,  0.6508256 ,  0.36146865,  0.05926937,
                0.61571648,  0.11277929,  0.50247043,  0.0452972 },
        { 0.19629832,  0.50668047,  0.38456404,  0.02582065,  0.62768725,
                0.80583051,  0.10851358,  0.85488901,  0.02174413},
        { 0.68600417,  0.65586762,  0.39942346,  0.62693378,  0.43719969,
                0.18714374,  0.6988603 ,  0.42570319,  0.88364144},
        { 0.83080794,  0.50331811,  0.39368674,  0.30391639,  0.73963863,
                0.79729779,  0.26604289,  0.274821  ,  0.41919771},
        { 0.66508233,  0.53287978,  0.64754628,  0.95570877,  0.15140522,
                0.54015485,  0.66827228,  0.45904259,  0.63167369}};
        _weights = new Matrix(t);
        

//        NeuralNetwork neuralNetwork = NeuralNetwork.createFromFile("g_soldier.nnet");
//
//        _nn = new MultiLayerPerceptron(TransferFunctionType.TANH, statenames.values().length, 7, 5, 1);
//        _hist = new DataSet(statenames.values().length);
    }

    public void save_all(){
//        _hist.save("hist_".concat(key).concat(".dat"));
//        _nn.save("nn_".concat(key).concat(".nnet"));
        System.out.println("SAVED!");
    }

    @Override
    public void move() throws GameActionException {
        Fight.sense_map();
        RobotInfo enemy = Fight.findClosestEnemy(Fight.seenEnemies);



        if (enemy == null){
            move_random();
        }else{
            double[] state = read_state(enemy);
            move_q_function(state);
        }

        save_all();
    }

    private void move_random() throws GameActionException {
        actions best_action = actions.values()[rand.nextInt(actions.values().length)];
        perform_action(best_action);

        //store the state
//        state[statenames.ACTION.ordinal()] = best_action.ordinal();
    }

    private void move_q_function(double[] state) throws GameActionException {
        double best_action_score = -100;

        Matrix s = new Matrix(new double[][]{state});

        Matrix values = s.times(_weights);

        actions best_action = actions.values()[values.max_idx()[1]];
        //pick action
//        for (actions action:actions.values()) {
//            state[statenames.ACTION.ordinal()] = action.ordinal() / (double) actions.values().length;
//
//            _nn.setInput(state);
//            _nn.calculate();
//            double[] out = _nn.getOutput();
//
//            print_vector(out, "Out");
//            double p = out[0];
//
//            if(p > best_action_score){
//                best_action_score = p;
//                best_action = action;
//            }
//
//            for (double s : state) {
//                System.out.print(s);
//                System.out.print(", ");
//            }
//            System.out.println();
//            System.out.println("Action score: ".concat(String.valueOf(best_action_score)));
//        }

        perform_action(best_action);

        //store the state
        //state[statenames.ACTION.ordinal()] = best_action.ordinal();
    }

    private void perform_action(actions best_action) throws GameActionException {
        switch (best_action){
            case DO_NOTHING:
                System.out.println("DO NOTHING");
                break;
            case MOVE_NORTH:
                System.out.println("MOVE NORTH");
                rc.move(Direction.NORTH);
                break;
            case MOVE_NORTH_EAST:
                System.out.println("MOVE NORTH EAST");
                rc.move(Direction.NORTH_EAST);
                break;
            case MOVE_EAST:
                System.out.println("MOVE EAST");
                rc.move(Direction.EAST);
                break;
            case MOVE_SOUTH_EAST:
                System.out.println("MOVE SOUTH EAST");
                rc.move(Direction.SOUTH_EAST);
                break;
            case MOVE_SOUTH:
                System.out.println("MOVE SOUTH");
                rc.move(Direction.SOUTH);
                break;
            case MOVE_SOUTH_WEST:
                System.out.println("MOVE SOUTH WEST");
                rc.move(Direction.SOUTH_WEST);
                break;
            case MOVE_WEST:
                System.out.println("MOVE WEST");
                rc.move(Direction.WEST);
                break;
            case MOVE_NORTH_WEST:
                System.out.println("MOVE NORTH WEST");
                rc.move(Direction.NORTH_WEST);
                break;
//            case ATTACK_TARGET:
//                System.out.println("ATTACK TARGET");
//                rc.attackLocation(enemy.location);
//                break;
        }
    }

    private double[] read_state(RobotInfo enemy) throws GameActionException {
        double[] state = new double[statenames.values().length];
        state[statenames.SELF_HEALTH.ordinal()] = normalise_health(rc.getType(), rc.getHealth());
        state[statenames.SELF_X.ordinal()] = rc.getLocation().x;
        state[statenames.SELF_Y.ordinal()] = rc.getLocation().y;
        state[statenames.ENEMY_X.ordinal()] = enemy.location.x;
        state[statenames.ENEMY_Y.ordinal()] = enemy.location.y;

//        //Get robot state
//        state[statenames.SELF_HEALTH.ordinal()] = normalise_health(rc.getType(), rc.getHealth());
//        state[statenames.SELF_HEALTH_LAST_TURN.ordinal()] = normalise_health(rc.getType(), prev_health);
//        prev_health = rc.getHealth();
//        state[statenames.SELF_CORE_READY.ordinal()] = normalise_core_delay(rc.getType(), rc.getCoreDelay());
//        state[statenames.SELF_WEAPON_READY.ordinal()] = normalise_weapon_delay(rc.getType(), rc.getWeaponDelay());
//
//        //Get enemy state
//
//        if (enemy != null) {
//            state[statenames.ENEMY_HEALTH.ordinal()] = normalise_health(enemy.type, enemy.health);
//            state[statenames.ENEMY_CORE_READY.ordinal()] = normalise_core_delay(enemy.type, enemy.coreDelay);
//            state[statenames.ENEMY_WEAPON_READY.ordinal()] = normalise_weapon_delay(enemy.type, enemy.weaponDelay);
//        }else{
//            state[statenames.ENEMY_HEALTH.ordinal()] = 0;
//            state[statenames.ENEMY_CORE_READY.ordinal()] = 0;
//            state[statenames.ENEMY_WEAPON_READY.ordinal()] = 0;
//        }
//
//        //Get world state
//        if (enemy != null) {
//            state[statenames.DISTANCE_TO_ENEMY.ordinal()] = enemy.location.distanceSquaredTo(rc.getLocation()) / MAX_DISTANCE;
//            last_known_direction = enemy.location.directionTo(rc.getLocation());
//        }else{
//            state[statenames.DISTANCE_TO_ENEMY.ordinal()] = 1;
//        }
//        state[statenames.DIRECTION_OF_ENEMY.ordinal()] = last_known_direction.ordinal() / Direction.values().length;
//        state[statenames.RANDOM_FACTOR.ordinal()] = rand.nextDouble();

        return state;
    }

    private void print_vector(double[] v, String name){
        System.out.print(name.concat(": "));
        for (double s : v) {
            System.out.print(s);
            System.out.print(", ");
        }
        System.out.println();
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

