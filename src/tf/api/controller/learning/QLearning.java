package tf.api.controller.learning;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tf.api.controller.TrafficSimulator;
import tf.api.experiment.Parameter;
import tf.api.model.ModelAndLight;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

public abstract class QLearning extends TrafficSimulator {

	private Class stateType = Parameter.STATE_TYPE;
	/**
	 * the database of Q values gained from the learning process
	 */
	private Map<SAPair, Double> qValues;
	/**
	 * the learning helpers
	 */
	protected Sensor sensor;
	protected Actuator actuator;
	protected int lightDelay;
	protected int reward;
	protected Action latestAction;
	/**
	 * Used to remember the previous state, which may not actually be
	 * the immediate previous if we have to wait for the light delay.
	 * E.g. previousState = just before light switched.
	 * currentState = lightDelay steps after light switched.
	 */
	protected State previousState;

	public QLearning() {
		qValues = new HashMap<SAPair, Double>();
		sensor = Sensor.getInstance();
		actuator = Actuator.getInstance();

		// Wait 3 steps so there are no cars left in the intersection
		// (i.e. no car crashes in the intersection).
		lightDelay = 3;
		reward = 0;
		latestAction = Action.NOT_SWITCH;
	}


	@Override
	public ModelAndLight processStep(TrafficModel currentTfModel, TrafficLight trafficLights) {
		if (previousState == null) {
			// Why lightDelay?
			previousState = sensor.getState(currentTfModel, trafficLights, 
					lightDelay, stateType);
		}

		TrafficModel nextTfModel = nextStep(currentTfModel, trafficLights); // Updates waitTime.
		reward -= this.getWaitTime(); // waitime is more of a penalty...

		// Display to console.
		consolePrinter(nextTfModel, trafficLights, 0-reward);

		--lightDelay;
		if (lightDelay <= 0) {
			State currState = sensor.getState(nextTfModel, trafficLights, 
					lightDelay, stateType);
			updateQValues(previousState, currState, latestAction, reward);

			latestAction = getNextAction(currState);
			// a new copy of traffic light
			trafficLights = actuator.execute(latestAction, trafficLights);
			previousState = currState;
			reward = 0;

			if (latestAction.equals(Action.SWITCH)) {
				lightDelay = 3;
			} else {
				lightDelay = 0;
			}
		}

		return new ModelAndLight(nextTfModel, trafficLights);
	}


	protected double getQValue(State state, Action action) {

//		double value = Math.random();
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
	
	
	protected double getQValue(int state, Action action) {

//		double value = Math.random();
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


	protected void putQValue(State state, Action action, double value) {
		SAPair pair = new SAPair(state.intValue(), action);
		qValues.put(pair, value);
	}
	
	
	protected void putQValue(int state, Action action, double value) {
		SAPair pair = new SAPair(state, action);
		qValues.put(pair, value);
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


	/**
	 * @param prevState
	 * @param currState
	 * @param action
	 * @param reward
	 */
	public abstract void updateQValues(
			State prevState, 
			State currState,
			Action action, 
			int reward);

	/**
	 * @param currState
	 * @return the next action to be executed
	 */
	public abstract Action getNextAction(
			State currState);
	
	
	public String getResult() {
		Iterator<Double> it = qValues.values().iterator();
		for(int i=0; i<1000 && it.hasNext(); i++) {
			System.out.println(it.next());
		}
		return null;
	}

}
