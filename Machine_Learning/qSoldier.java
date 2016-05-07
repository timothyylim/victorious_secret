package victorious_secret.Machine_Learning;

import battlecode.common.*;
import victorious_secret.Robot;
import victorious_secret.Behaviour.Fight;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.Random;

public class qSoldier extends Robot {
    private enum statenames{SELF_HEALTH, SELF_HEALTH_LAST_TURN, SELF_CORE_READY, SELF_WEAPON_READY, ENEMY_HEALTH, ENEMY_CORE_READY,
        ENEMY_WEAPON_READY, DISTANCE_TO_ENEMY, DIRECTION_OF_ENEMY, ACTION, RANDOM_FACTOR}
    private enum actions{DO_NOTHING, MOVE_NORTH, MOVE_NORTH_EAST, MOVE_EAST, MOVE_SOUTH_EAST, MOVE_SOUTH,
        MOVE_SOUTH_WEST, MOVE_WEST, MOVE_NORTH_WEST, ATTACK_TARGET}
    private double MAX_DISTANCE = 100;

    private Direction last_known_direction;
    private double prev_health;
    private NeuralNetwork _nn;
    private DataSet _hist;
    String key;


    public qSoldier(RobotController _rc){
        rc = _rc;
        rand = new Random();
        prev_health = rc.getHealth();
        Fight.initialise(rc, this);
        key = String.valueOf(rand.nextDouble());
        //NeuralNetwork neuralNetwork = NeuralNetwork.createFromFile("g_soldier.nnet");

        _nn = new MultiLayerPerceptron(TransferFunctionType.TANH, statenames.values().length, 7, 5, 1);
        _hist = new DataSet(statenames.values().length);
    }

    public void save_all(){
        String turn = String.valueOf(rc.getRoundNum());
        _hist.save("hist_".concat(key).concat("_").concat(turn));
        _nn.save("nn_".concat(key).concat("_").concat(turn));
    }

    @Override
    public void move() throws GameActionException
    {
        Fight.sense_map();
        RobotInfo enemy = Fight.findClosestEnemy(Fight.seenEnemies);

        double[] state = read_state(enemy);
        double best_action_score = 0;
        actions best_action = actions.DO_NOTHING;
        //pick action
        for (actions action:actions.values()) {
            state[statenames.ACTION.ordinal()] = action.ordinal() / (double) actions.values().length;

            _nn.setInput(state);
            _nn.calculate();
            double[] out = _nn.getOutput();

            double p = out[0];

            if(p > best_action_score){
                best_action_score = p;
                best_action = action;
            }

            for (double s : state) {
                System.out.print(s);
                System.out.print(", ");
            }
            System.out.println();
            System.out.println("Action score: ".concat(String.valueOf(best_action_score)));
        }

        //store the state
        state[statenames.ACTION.ordinal()] = best_action.ordinal();
        _hist.addRow(state);

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
            case ATTACK_TARGET:
                System.out.println("ATTACK TARGET");
                rc.attackLocation(enemy.location);
                break;
        }
        save_all();
    }

    private double[] read_state(RobotInfo enemy) throws GameActionException {
        double[] state = new double[statenames.values().length];
        //Get robot state
        state[statenames.SELF_HEALTH.ordinal()] = normalise_health(rc.getType(), rc.getHealth());
        state[statenames.SELF_HEALTH_LAST_TURN.ordinal()] = normalise_health(rc.getType(), prev_health);
        prev_health = rc.getHealth();
        state[statenames.SELF_CORE_READY.ordinal()] = normalise_core_delay(rc.getType(), rc.getCoreDelay());
        state[statenames.SELF_WEAPON_READY.ordinal()] = normalise_weapon_delay(rc.getType(), rc.getWeaponDelay());

        //Get enemy state

        if (enemy != null) {
            state[statenames.ENEMY_HEALTH.ordinal()] = normalise_health(enemy.type, enemy.health);
            state[statenames.ENEMY_CORE_READY.ordinal()] = normalise_core_delay(enemy.type, enemy.coreDelay);
            state[statenames.ENEMY_WEAPON_READY.ordinal()] = normalise_weapon_delay(enemy.type, enemy.weaponDelay);
        }else{
            state[statenames.ENEMY_HEALTH.ordinal()] = 0;
            state[statenames.ENEMY_CORE_READY.ordinal()] = 0;
            state[statenames.ENEMY_WEAPON_READY.ordinal()] = 0;
        }

        //Get world state
        if (enemy != null) {
            state[statenames.DISTANCE_TO_ENEMY.ordinal()] = enemy.location.distanceSquaredTo(rc.getLocation()) / MAX_DISTANCE;
            last_known_direction = enemy.location.directionTo(rc.getLocation());
        }else{
            state[statenames.DISTANCE_TO_ENEMY.ordinal()] = 1;
        }
        state[statenames.DIRECTION_OF_ENEMY.ordinal()] = last_known_direction.ordinal() / Direction.values().length;
        state[statenames.RANDOM_FACTOR.ordinal()] = rand.nextDouble();

        return state;
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

