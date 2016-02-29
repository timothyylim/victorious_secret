package victorious_secret.Behaviour;
import battlecode.common.*;
import victorious_secret.Robot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ple15 on 15/01/16.
 */
public class Signalling {
    private static RobotController rc;
    private static Robot robot;

    public enum messageTypes {ZOMBIEDEN};

    private static final int DEFAULT_RANGE = 1000;
    private static final int messageRange = 32000;
    private static final int zombieDenOffset = -25459;


    /**
     * Initalises the Signalling controller for use as static class
     * @param _rc The Robot Controller
     * @param _robot The Robot type
     */
    public static void intialize(RobotController _rc, Robot _robot)
    {
        rc = _rc;
        robot = _robot;
    }

    /**
     * This function takes in an encoded message and decodes it into map coordinates
     * @return A three integer array containing the x, y coordinates and the type
     */
    public static int[] parseMessage(int m1, int m2){
        int[] message = new int[3];
        if (m1 > zombieDenOffset && m1 <= zombieDenOffset + messageRange){
            message[0] = m1 - zombieDenOffset;
            message[1] = m2 - zombieDenOffset;
            message[2] = messageTypes.ZOMBIEDEN.ordinal();
        }

        return  message;
    }

    /**
     * This function encodes a map location message so it can be safely broadcast. Typically this will be immediately followed
     * by a call to broadcastMessage()
     * @param loc The MapLocation we wish to encode
     * @param r The type of the thing we are encoding
     * @return Returns the encoded message
     */
    public static int[] encodeMessage(MapLocation loc, RobotType r){
        int[] message = new int[2];

        switch (r){
            case ZOMBIEDEN:
                message[0] = loc.x + zombieDenOffset;
                message[1] = loc.y + zombieDenOffset;
        }

        return message;
    }

    /**
     * This function broadcasts an encoded map location message a specific range
     * @param message The message to be broadcast, an array of {x, y}
     * @param range The squared range the message will be broadcast
     * @throws GameActionException
     */
    public static void broadcastMessage(int[] message, int range) throws GameActionException {
        rc.broadcastMessageSignal(message[0], message[1], range);
    }

    /**
     * This function broadcasts an encoded map location message a pre-specified range
     * @param message The message to be broadcast, an array of {x, y}
     * @throws GameActionException
     */
    public static void broadcastMessage(int[] message) throws GameActionException {
        broadcastMessage(message, DEFAULT_RANGE);
    }

    /**
     * This function encodes and then broadcasts a map location message a specific range
     * @param loc The MapLocation we wish to encode
     * @param r The type of the thing we are encoding
     * @param range The squared range the message will be broadcast
     * @throws GameActionException
     */
    public static void encodeAndBroadcast(MapLocation loc, RobotType r, int range) throws GameActionException {
        int [] m = encodeMessage(loc, r);
        broadcastMessage(m, range);
    }

    /**
     * This function encodes and then broadcasts a map location message a pre-specified range
     * @param loc The MapLocation we wish to encode
     * @param r The type of the thing we are encoding
     * @throws GameActionException
     */
    public static void encodeAndBroadcast(MapLocation loc, RobotType r) throws GameActionException {
        int [] m = encodeMessage(loc, r);
        broadcastMessage(m);
    }
}
