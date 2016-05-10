import sys
import parseReplayFile as prp
import learning_util as util
import tensorflow as tf
import numpy as np

#Check that a filename was passed as a commandline argument
if len(sys.argv) !=2:
    print('Unexpected number of arguments')
    exit

#Parse replay file information
gameInformation,unitInformation,otherGameplayInfo = prp.parseReplayFile(sys.argv[1])


#Neural Net variables and initialisation
GAMMA = 0.5
session = tf.Session()
state = tf.placeholder("float",[None, util.NUM_STATES])
targets = tf.placeholder("float",[None, util.NUM_ACTIONS])
#hidden_weights = tf.Variable(tf.constant(0., shape=[util.NUM_STATES,util.NUM_ACTIONS]))
hidden_weights = tf.Variable(util.read_weights_from_file())
output = tf.matmul(state,hidden_weights)
loss = tf.reduce_mean(tf.square(output-targets))
train_operation = tf.train.AdamOptimizer(0.1).minimize(loss)
session.run(tf.initialize_all_variables())


#The empty dataset:
state_batch = []
rewards_batch = []

#GENERATE THE DATASET:
#Assume only two units in the match, one team A (the player) and team B (the enemy)
for match in range(0,len(unitInformation)):
    for round in range(0,len(unitInformation[match])-1):

        if(len(unitInformation[match][round+1])<2):
            break

        current_state = util.get_state(unitInformation[match][round])
        next_state = util.get_state(unitInformation[match][round+1])
        action_at_state = util.get_action(current_state,next_state)

        state_batch.append(current_state)
        immediate_state_reward = util.reward_at_state(current_state, next_state)
        reward_vector = []
        for action_index in range(util.NUM_ACTIONS):
            next_state = np.array(next_state)
            next_state = next_state.reshape((1,util.NUM_STATES))
            q_func_index = session.run(output,feed_dict={state:next_state})
            q_func = np.max(q_func_index)
            if(action_at_state == action_index):
                reward_vector.append(immediate_state_reward + GAMMA * q_func)
            else:
                reward_vector.append(0.0)

        rewards_batch.append(reward_vector)


session.run(train_operation,feed_dict={
   state: state_batch,
   targets: rewards_batch
})

#print session.run(hidden_weights)
util.write_weights_to_file(session.run(hidden_weights))

print 1000*session.run(output,feed_dict={state:np.array([60.0,431,159,44,33]).reshape((1,5))})

#NOTES__________________________________
# 1) May need to consider each state as a series of state/action pairs, as reward is delayed
# 2) Not sure whether the reward will be for actions not taken. Therefore estimate
#    using the q-function. However, this is inaccurate?
#
#