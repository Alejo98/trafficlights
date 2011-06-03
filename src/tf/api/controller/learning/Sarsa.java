/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.api.controller.learning;

import java.util.HashMap;
import java.util.Map;
import tf.api.controller.TrafficSimulator;
import tf.api.experiment.Parameter;
import tf.api.model.ModelAndLight;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 *
 * @author lawry
 */
public class Sarsa extends TrafficSimulator {

	protected Class stateType = Parameter.STATE_TYPE;
	/**
	 * the database of Q values gained from the learning process
	 */
	protected Map<SAPair, Double> qValues;
	/**
	 * the learning helpers
	 */
	protected Sensor sensor;
	protected Actuator actuator;
	protected int lightDelay;
	protected Action latestAction;
	protected State latestState;

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
	 * Constructor.
	 */
	public Sarsa() {
		qValues = new HashMap<SAPair, Double>();
		sensor = Sensor.getInstance();
		actuator = Actuator.getInstance();

		// Initial traffic change is allowed immediately.
		lightDelay = 0;
		latestAction = Action.NOT_SWITCH;
	}

	@Override
	public ModelAndLight processStep(TrafficModel currentTfModel, TrafficLight trafficLights) {
		if (latestState == null) {
			latestState = sensor.getState(currentTfModel, trafficLights,
					lightDelay, stateType);
		}

		// Observe reward and next state.
		TrafficModel nextTfModel = nextStep(currentTfModel, trafficLights); // Updates waitTime.
		int localReward = -this.getWaitTime(); // waitime is more of a penalty...

		// in the next state, the lightDelay is reduced.
		// But do not reduce it beyond 0, since a state with lightDelay = -3
		// has the same effect as one with lightDelay = 0 (both means light can
		// be changed).
		--lightDelay;
		if (lightDelay < 0)
			lightDelay = 0;

		// The (reward, latestState, latestAction) form the triple for "current".

		// Next, work out the next state, which results from (latestState + latestAction).
		State nextState = sensor.getState(nextTfModel, trafficLights,
				lightDelay, stateType);

		// Choose the next Action based on nextState and our policy.
		// Note, the policy is constrained by lightDelay.
		Action nextAction = getNextAction(nextState, lightDelay);

		// Update Q value.
		updateQValues(latestState, nextState, latestAction, nextAction, localReward);

		// Update state and action.
		latestState = nextState;
		latestAction = nextAction;

		// Take action on the lights... (Do it here so it's easier for the
		// user to see what will happen next. In the text book we should have
		// done it as first thing in this method.)
		TrafficLight newLight = actuator.execute(nextAction, trafficLights);
		if (nextAction.equals(Action.SWITCH)) {
			lightDelay = Parameter.LIGHT_DELAY;
		}

		return new ModelAndLight(nextTfModel, newLight);
	}

	/*
	 * Update the Q values using the default algorithm.
	 */
	public void updateQValues(State currState, State nextState,
			Action currAction, Action nextAction, int reward) {

		double currValue = getQValue(currState, currAction);
		double nextValue = getQValue(nextState, nextAction);

		// Work out new current q value.
		double newValue = currValue + alpha * (reward + gamma * nextValue - currValue);
		putQValue(currState, currAction, newValue);

//		printData(currState, currAction, currValue);
//		printData(nextState, nextAction, nextValue);
//		System.err.println("Subst currValue " + newValue);
	}

	protected void putQValue(State state, Action action, double value) {
		SAPair pair = new SAPair(state.intValue(), action);
		qValues.put(pair, value);
	}
	
	
	protected void putQValue(int state, Action action, double value) {
		SAPair pair = new SAPair(state, action);
		qValues.put(pair, value);
	}
	

	/*
	 * ε is slowly reduced
	 * overtime so it converges in the limit to the greedy policy.
	 */
	public Action getNextAction(State state, int delay) {
		if (delay > 0) {
			// No choice, must not switch.
			return Action.NOT_SWITCH;
		}

		// Reduce the epsilon so it tends to greedy.
		epsilon *= Parameter.TERMPERATURE_ANNEAL_FACTOR;

		if (Math.random() < epsilon) {
			// Pick random action.
			if (Math.random() < 0.5) {
				return Action.NOT_SWITCH;
			} else {
				return Action.SWITCH;
			}
		} else {
			// Pick best action.
			double qn = getQValue(state, Action.NOT_SWITCH);
			double qs = getQValue(state, Action.SWITCH);
//
//			System.err.println("Deciding which to pick \\/");
//			printData(state, Action.NOT_SWITCH, qn);
//			printData(state, Action.SWITCH, qs);
//			System.err.println("Deciding which to pick /\\");

			if (qn < qs) {
				return Action.SWITCH;
			} else {
				return Action.NOT_SWITCH;
			}
		}
	}

	/**
	 * Initial q value is 0.
	 * @param state
	 * @param action
	 * @return
	 */
	protected double getQValue(State state, Action action) {
		double value = 0.0;
		SAPair pair = new SAPair(state.intValue(), action);
		if(qValues.containsKey(pair)) {
			value = qValues.get(pair);
		}
		else {
			qValues.put(pair, value);
		}
		return value;
	}
	
	
	/**
	 * Initial q value is 0.
	 * @param state
	 * @param action
	 * @return
	 */
	protected double getQValue(int state, Action action) {
		double value = 0.0;
		SAPair pair = new SAPair(state, action);
		if(qValues.containsKey(pair)) {
			value = qValues.get(pair);
		}
		else {
			qValues.put(pair, value);
		}
		return value;
	}
	
	
	protected double getMaxQValue(State state) {

		double maxValue = Double.NEGATIVE_INFINITY;

		Action[] actions = Action.values();
		for(Action action : actions) {
			SAPair pair = new SAPair(state.intValue(), action);
			double value = Math.random();
			if(qValues.containsKey(pair)) {
				value = qValues.get(pair);
			}
			else {
				qValues.put(pair, value);
			}
			if(maxValue < value) {
				maxValue = value;
			}
		}

		return maxValue;
	}

	//    private void printData(State currState, Action currAction, double qvalue) {
	//        System.err.println("Q("+currState.intValue()+","+currAction+")="+qvalue);
	//    }
}
