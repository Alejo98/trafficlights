package tf.api.controller.learning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tf.api.experiment.Parameter;

public class Ultimate extends Sarsa {
	
	/**
     * the discount factor
     */
    private double gamma = Parameter.DISCOUNT_FACTOR;
    /**
     * the learning rate
     */
    private double alpha = Parameter.LEARNING_RATE;
    /**
     * the temperature of Boltzmann distribution
     */
    private double tau = Parameter.BOLTZMANN_INIT_TEMPERATURE;
    /**
     * the temperature annealing factor
     */
    private double anneal = Parameter.TERMPERATURE_ANNEAL_FACTOR;
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
    
    public Ultimate() {
    	super();
    	eTrace = new HashMap<SAPair, Double>();
    	stateSet = new HashSet<Integer>();
    }

    @Override
	public void updateQValues(State currState, State nextState,
			Action currAction, Action nextAction, int reward) {

    	double currValue = getQValue(currState, currAction);
    	double nextValue = getQValue(nextState, nextAction);
		double delta = reward + gamma * nextValue - currValue;
		
		int currInt = currState.intValue();
		double etrace = getEligibilityTrace(currInt, currAction);
		double maxValue = getMaxQValue(currState);
		if(currValue>=maxValue) {
			etrace = 1.0 + gamma * lambda * etrace;
		}
		else {
			etrace = 0.0;
		}
		putEligibilityTrace(currInt, currAction, etrace);
		
		Iterator<Integer> it = stateSet.iterator();
		Action[] actions = Action.values();
		int done = currInt;
		
		while(it.hasNext()) {
			int state = it.next();
			
			for(Action act : actions) {
				etrace = getEligibilityTrace(state, act);
				if(etrace!=0.0) {
					// update Q value
					double oldValue = getQValue(state, act);
					double newValue = oldValue + alpha * delta * etrace;
					putQValue(state, act, newValue);
					// update eligibility trace
					if(state==done && act.equals(currAction)) {
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


    @Override
	public Action getNextAction(State state, int delay) {
		if (delay > 0) {
			// No choice, must not switch.
			return Action.NOT_SWITCH;
		}

		Action[] actions = Action.values();
		double[] weight = new double[actions.length];
		double sum = 0.0;
	
		for(int i=0; i<actions.length; i++) {
			double value = getQValue(state, actions[i]);
			weight[i] = Math.exp(value/tau);
			sum += weight[i];
		}
	
		double random = Math.random();
		sum = sum * random;
		for(int i=0; i<weight.length; i++ ) {
			sum = sum - weight[i];
			if( sum<0 ) {
				return actions[i];
			}
		}
		
		updateTemperature();
		
		return actions[weight.length-1];
	}
    
    
    private void updateTemperature() {
		tau *= anneal;
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
