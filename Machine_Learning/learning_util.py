class ACTION:
    MOVE_SW, MOVE_W, MOVE_S, MOVE_NW, NO_ACTION, MOVE_SE, MOVE_N, MOVE_E, MOVE_NE = range(9)

class STATE_INDEX:
    UNIT_HEALTH, UNIT_X_LOC, UNIT_Y_LOC, ENEMY_X_LOC, ENEMY_Y_LOC = range(5)

NUM_ACTIONS = 9
NUM_STATES = 5
weights_filename = 'neurons.nn'

#IMPORTANT: Assumes only one player unit and one ememy unit
def get_state(round_dictionary):
    #Declare method variables. Will eventually return state
    state = []
    target_unit = {}
    other_unit = {}
    #Isolate the units from the round_dictionary
    for dict in round_dictionary:
        if(round_dictionary[dict]['team']=='A'):
            target_unit = round_dictionary[dict]
        else:
            other_unit = round_dictionary[dict]
    #Begin building the state list variable from variables of interest
    state.append(float(target_unit['health']))
    x_loc, y_loc = parse_loc(target_unit['loc'])
    state.append(x_loc)
    state.append(y_loc)
    x_loc, y_loc = parse_loc(other_unit['loc'])
    state.append(x_loc)
    state.append(y_loc)
    return state

def get_action(current_state, next_state):
    c_x_loc = current_state[STATE_INDEX.UNIT_X_LOC]
    c_y_loc = current_state[STATE_INDEX.UNIT_Y_LOC]
    n_x_loc = next_state[STATE_INDEX.UNIT_X_LOC]
    n_y_loc = next_state[STATE_INDEX.UNIT_Y_LOC]
    x_offset = (n_x_loc-c_x_loc) * 3
    y_offset = (n_y_loc-c_y_loc) * 2
    if((x_offset < -3) or (x_offset >3) or (y_offset < -2) or (y_offset > 2)):
        print('GetAction error: unexpected relative positions')
        return 4
    index = x_offset + y_offset
    if(index == 5):
        index = index-1
    elif(index == -5):
        index = index + 1
    return index + 4

def reward_at_state(current_state,next_state):
    return (next_state[STATE_INDEX.UNIT_HEALTH]-current_state[STATE_INDEX.UNIT_HEALTH])+1

def parse_loc(location):
    [x_loc, y_loc] = location.split(',')
    return int(x_loc), int(y_loc)


#Slight rounding error when converting floats to str
def write_weights_to_file(weights):
    open(weights_filename, 'w').close()
    f = open(weights_filename,'wb')
    for node in weights:
        for weight in range(0,len(node)):
            f.write(str(node[weight]))
            if(weight<(len(node)-1)):
                f.write(',')
        f.write('\n')

def read_weights_from_file():
    weights = []
    f = open(weights_filename)
    l1 = f.read().splitlines()
    for l in l1:
        temp = l.split(',')
        fresh = []
        for e in temp:
            fresh.append(float(e))
        weights.append(fresh)
    return weights

