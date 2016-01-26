package victorious_secret.Behaviour;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import victorious_secret.Robot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ple15 on 15/01/16.
 */
public class Signalling {
    public enum MessageType {NO_MESSAGE, MOVE_NORTH, MOVE_NORTH_EAST, MOVE_EAST, MOVE_SOUTH_EAST, MOVE_SOUTH, MOVE_SOUTH_WEST, MOVE_WEST, MOVE_NORTH_WEST,
        ARCHON_DEFEND_ME, ARCHON_DEFEND_NORTH,ARCHON_DEFEND_NORTH_EAST, ARCHON_DEFEND_EAST, ARCHON_DEFEND_SOUTH_EAST, ARCHON_DEFEND_SOUTH, ARCHON_DEFEND_SOUTH_WEST, ARCHON_DEFEND_WEST, ARCHON_DEFEND_NORTH_WEST}

    public static Map<Integer, MessageType> messages = new HashMap<>();
    public static Map<Integer, Boolean> offsets = new HashMap<>();
    private static RobotController rc;
    private static Robot robot;
    private static int  BASE_TURNS = 3;
    private static int HEARTBEAT_TURN = 0;
    private static int  MESSAGE_BASE = 4;
    private static MessageType myMessage;
    private static MessageType nextMessage;
    private static Map<Integer, MessageType> messagesThisTurn = new HashMap<>();

    private static boolean readyToWork;

    public static Direction lastMoveSignal;

    public Signalling(RobotController _rc, Robot _robot)
    {
        rc = _rc;
        robot = _robot;
        readyToWork = false;
        myMessage = MessageType.NO_MESSAGE;
        nextMessage = MessageType.NO_MESSAGE;
    }

    private static int getTurnCount()
    {
        return BASE_TURNS - ((rc.getRoundNum()) % BASE_TURNS) - 1;
    }

    public static boolean setMessage(MessageType message) throws GameActionException {
        if(rc.getRoundNum() % BASE_TURNS == HEARTBEAT_TURN) {
            myMessage = message;
            nextMessage = MessageType.NO_MESSAGE;
            //java.lang.System.out.println("Sending " + message.ordinal() + " by " + rc.getID() + " on round " + rc.getRoundNum());
            return true;
        }
        else {
            nextMessage = message;
            return false;
        }
    }

    private static void broadcastBeep() throws GameActionException
    {
        //java.lang.System.out.print("Beep...");
        rc.broadcastSignal(rc.getType().sensorRadiusSquared);
    }

    private static void broadcastHeartbeat() throws GameActionException
    {
        //java.lang.System.out.print("Heartbeat: ");
        for (int i = 0; i < 5; i++)
        {
            broadcastBeep();
        }
        //java.lang.System.out.println();
    }

    private static void broadcastMessage(int turnCount) throws GameActionException{
        int pow = (int) Math.pow(MESSAGE_BASE, turnCount - 1);

        int iPart = myMessage.ordinal() / pow;
        int remainder = myMessage.ordinal() % pow;

        //     java.lang.System.out.println("Total number of signals to broadcast this turn[" + turnCount + "] = " + iPart + ", " + myMessage.ordinal() + " / " + pow);

        for (int i = 0; i < iPart; i++) {
            broadcastBeep();
        }

//        java.lang.System.out.println();

        myMessage = MessageType.values()[remainder];
    }

    public static void broadcast() throws GameActionException {
        if(!readyToWork)
        {
            return;
        }

        int turnCount = getTurnCount();

        if(turnCount == HEARTBEAT_TURN && nextMessage != MessageType.NO_MESSAGE)
        {
            myMessage = nextMessage;
            nextMessage = MessageType.NO_MESSAGE;
        }

//        java.lang.System.out.println("Turn count = " + turnCount);

        if(turnCount == HEARTBEAT_TURN){
            broadcastHeartbeat();
        }
        else {
            //Always broadcast the heartbeat beep
            broadcastBeep();
            broadcastMessage(turnCount);
        }
//        java.lang.System.out.println();
    }

    private static void calibrateListening()
    {
        if(!readyToWork && getTurnCount() == HEARTBEAT_TURN) {
            rc.emptySignalQueue();
            readyToWork = true;
        }
    }

    private static void processHeardMessages(Map<Integer, Integer> heardThisTurn, int turnCount)
    {
        for(int id : heardThisTurn.keySet()) {
            int val = heardThisTurn.get(id);

            if(val == 5) {
                //This is a heartbeat messsage
                //We will only listen to a message if we have also received the heartbeat
                messages.put(id, MessageType.NO_MESSAGE);

                if (!offsets.containsKey(id)) {
                    if (turnCount == HEARTBEAT_TURN) {
                        //Then the messages are synchronised
                        offsets.put(id, false);
                    } else {
                        //Then the messages are delayed
                        offsets.put(id, true);
                    }
                }
            }
            else{
                if(messages.containsKey(id)) {
                    int t_turnCount = turnCount;
                    if(offsets.get(id))
                    {
                        t_turnCount += 1;
                    }

                    int pow = (int) Math.pow(MESSAGE_BASE, t_turnCount - 1);

                    /*
                    java.lang.System.out.print("From [" + id + "] on [" + t_turnCount + "]: ");
                    for (int i = 0; i < val; i++)
                        java.lang.System.out.print(" Boop...");
                    java.lang.System.out.println();
                    */
                    int newVal = messages.get(id).ordinal() + ((val - 1) * pow);
                    messages.replace(id, MessageType.values()[newVal]);

                    if(t_turnCount == 1)
                    {
                        handleCompleteMessage(id);
                    }
                }
            }
        }

        removeIncompleteMessages(heardThisTurn);


    }

    //private static void updateArchonLocations(int )

    private static void processAdvancedMessage(int id, int [] message)
    {
        if(Integer.signum(message[0]) == 1 && Integer.signum(message[1]) == 1)
        {
            //This is an Archon location
            return;
        }
        if(Integer.signum(message[0]) == -1 && Integer.signum(message[1]) == -1)
        {
            //This is a Zombie Den location
            return;
        }
        if(Integer.signum(message[0]) == -1 && Integer.signum(message[1]) == +1)
        {
            //This is an enemy location
            return;
        }
        if(Integer.signum(message[0]) == +1 && Integer.signum(message[1]) == -1)
        {
            //This is a MessageType
            MessageType tMessage = MessageType.values()[message[0]];
            switch (tMessage) {
                case NO_MESSAGE:
                    break;
                default:
                    break;
            }
            //messagesThisTurn.put(id, );
            return;
        }
    }

    private static void removeIncompleteMessages(Map<Integer, Integer> heardThisTurn)
    {

        Iterator<Map.Entry<Integer,MessageType>> iter = messages.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, MessageType> entry = iter.next();
            if(!heardThisTurn.containsKey(entry.getKey())) {
                iter.remove();
            }
        }
    }

    private static void handleCompleteMessage(int id)
    {
        //java.lang.System.out.println("New sig set");
        //lastMoveSignal = getMeanMoveRequest();

        java.lang.System.out.println("Received " + messages.get(id) + " from " + id);
        messagesThisTurn.put(id, messages.get(id));
//        lastMoveSignal = ToDirection(messages.get(id));
        messages.remove(id);
    }

    public static void listen() {
        calibrateListening();
        messagesThisTurn.clear();

        //Always empty the signal queue
        Signal[] sigs = rc.emptySignalQueue();

        if (!readyToWork) {
            return;
        }

        int turnCount = getTurnCount();

//        java.lang.System.out.println("Received " + sigs.length + " signals on turn "  + turnCount);
        Map<Integer, Integer> heardThisTurn = new HashMap<>();

        //Update messages
        //if (sig.)
        for (Signal sig : sigs)
            if(sig.getTeam().compareTo(robot.team) == 0){
            //if (true) {
                //java.lang.System.out.println("Received something from " + sig.getID() + " on team: " + sig.getTeam() + " I am from team: " + rc.getTeam());
                int id = sig.getID();
                int [] msg = sig.getMessage();
                if(msg == null) {
                    if (heardThisTurn.containsKey(id)) {
                        int i = heardThisTurn.get(id);
                        heardThisTurn.replace(id, i + 1);
                    } else {
                        heardThisTurn.put(id, 1);
                    }
                }
                else
                {
                    processAdvancedMessage(id, msg);
                }
            }

        processHeardMessages(heardThisTurn, turnCount);

        lastMoveSignal = getMeanMoveRequest();
    }

    private static Direction getMeanMoveRequest()
    {
        int sumMessage = 0;
        int nMessage = 0;
        for(MessageType m  : messagesThisTurn.values()) {
            if (m.ordinal() >= MessageType.MOVE_NORTH.ordinal() && m.ordinal() <= MessageType.MOVE_NORTH_WEST.ordinal()) {
                sumMessage += m.ordinal();
                nMessage++;
            }
        }

        if(nMessage > 0) {
            sumMessage /= nMessage;
            MessageType m = MessageType.values()[sumMessage];

            return ToDirection(m);
        }
        else
        {
            return Direction.NONE;
        }
    }

    public static Direction ToDirection(MessageType m)
    {
        switch (m)
        {
            case  MOVE_NORTH:
                return Direction.NORTH;
            case MOVE_NORTH_EAST:
                return Direction.NORTH_EAST;
            case MOVE_EAST:
                return Direction.EAST;
            case MOVE_SOUTH_EAST:
                return Direction.SOUTH_EAST;
            case MOVE_SOUTH:
                return Direction.SOUTH;
            case MOVE_SOUTH_WEST:
                return Direction.SOUTH_WEST;
            case MOVE_WEST:
                return Direction.WEST;
            case MOVE_NORTH_WEST:
                return Direction.NORTH_WEST;
            default:
                return Direction.NONE;
        }
    }

    public static MessageType FromDirection(Direction m)
    {
        switch (m)
        {
            case  NORTH:
                return MessageType.MOVE_NORTH;
            case NORTH_EAST:
                return MessageType.MOVE_NORTH_EAST;
            case EAST:
                return MessageType.MOVE_EAST;
            case SOUTH_EAST:
                return MessageType.MOVE_SOUTH_EAST;
            case SOUTH:
                return MessageType.MOVE_SOUTH;
            case SOUTH_WEST:
                return MessageType.MOVE_SOUTH_WEST;
            case WEST:
                return MessageType.MOVE_WEST;
            case NORTH_WEST:
                return MessageType.MOVE_NORTH_WEST;
            default:
                return MessageType.NO_MESSAGE;
        }
    }

}
