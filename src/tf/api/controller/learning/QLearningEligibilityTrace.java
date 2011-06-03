package tf.api.controller.learning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tf.api.experiment.Parameter;

public class QLearningEligibilityTrace extends QLearning {

    /**
     * the discount factor
     */
    private double gamma = Parameter.DISCOUNT_FACTOR;
    /**
     * the learning rate
     */
    private double alpha = Parameter.LEARNING_RATE;
    /**
     * Epsilon-greedy exploration 10%
     */
	private double epsilon = Parameter.EPSILON_GREEDY;
	/**
	 * Eligibility trace-decay parameter
	 */
	private double lambda = Parameter.TRACE_DECAY;
    /**
     * the database of Eligibility traces gained from the learning process
     */
    private Map<SAPair, Double> eTrace;
    /**
     * a set of states in Eligibility Traces.
     */
    private Set<Integer> stateSet;
    
    
    public QLearningEligibilityTrace() {
    	super();
    	eTrace = new HashMap<SAPair, Double>();
    	stateSet = new HashSet<Integer>();
    }

    
    /* 
     * Update the Q values with eligibility traces.
     * 
     * @see tf.api.learning.QLearning#updateQValues(
     * tf.api.learning.State, 
     * tf.api.learning.State, 
     * tf.api.learning.Action, 
     * int)
     */
    @Override
	public void updateQValues(State prevState, State currState,
			Action action, int reward) {

		double oldValue = getQValue(prevState, action);
		double maxValue = getMaxQValue(currState);
		double delta = reward + gamma*maxValue - oldValue;
		
		int prevInt = prevState.intValue();
		double etrace = getEligibilityTrace(prevInt, action);
		maxValue = getMaxQValue(prevState);
		if(oldValue>=maxValue) {
			etrace = 1.0 + gamma * lambda * etrace;
		}
		else {
			etrace = 0.0;
		}
		putEligibilityTrace(prevInt, action, etrace);
		
		Iterator<Integer> it = stateSet.iterator();
		Action[] actions = Action.values();
		double newValue = 0.0;
		int done = prevInt;
		
		while(it.hasNext()) {
			int state = it.next();
			
			for(Action act : actions) {
				etrace = getEligibilityTrace(state, act);
				if(etrace!=0.0) {
					// update Q value
					oldValue = getQValue(state, act);
					newValue = oldValue + alpha * delta * etrace;
					putQValue(state, act, newValue);
					// update eligibility trace
					if(state==done && act.equals(action)) {
						// has been updated above
						continue;
					}
					else {
						etrace = gamma * lambda * etrace;
						putEligibilityTrace(state, act, etrace);
					}
				}
			}
		}
	}


	/* 
	 * Retrieve the next action using Epsilon-greedy strategy: 
	 * The best lever is selected for a proportion 1 − ε of the 
	 * trials, and another lever is randomly selected (with 
	 * uniform probability) for a proportion ε. 
	 * 
	 * @see tf.api.learning.QLearning#getNextAction(tf.api.learning.State)
	 */
	@Override
	public Action getNextAction(State currState) {
		Action[] actions = Action.values();
		double[] weight = new double[actions.length];

		double max = Double.NEGATIVE_INFINITY;
		int index = -1;
		for(int i=0; i<actions.length; i++) {
			double value = getQValue(currState, actions[i]);
			if(value>max) {
				index = i;
				max = value;
			}
		}

		for(int i=0; i<weight.length; i++) {
			if(i!=index) {
				weight[i] = epsilon / (weight.length-1);
			}
			else {
				weight[i] = 1 - epsilon;
			}
		}

		double sum = Math.random();
		for(int i=0; i<weight.length; i++ ) {
			sum = sum - weight[i];
			if( sum<0 ) {
				return actions[i];
			}
		}
		return actions[weight.length-1];
	}
	
	
	private double getEligibilityTrace(int state, Action action) {

		double value = 0.0;
		SAPair pair = new SAPair(state, action);
		
		if(eTrace.containsKey(pair)) {
			value = eTrace.get(pair);
		}
		else {
			eTrace.put(pair, value);
			stateSet.add(state);
			
		}
		
		return value;
	}
	
	
	private void putEligibilityTrace(int state, Action action, double value) {
		SAPair pair = new SAPair(state, action);
		
		eTrace.put(pair, value);
		stateSet.add(state);
	}
	
}
