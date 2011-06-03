package tf.api.controller.learning;

import tf.api.model.TrafficLight;

/**
 * A helper class for executing the selected action
 * @author hanli
 */
public class Actuator {
	
	private static final Actuator _instance = new Actuator();
	
	private Actuator() {}
	
	/**
	 * @return the only instance of this class
	 */
	public static Actuator getInstance() {
		return _instance;
	}
	
	/**
	 * @param action the action to be executed
	 * @param trafficLight the traffic light to be switched
	 * @return a new traffic light after the execution
	 */
	public TrafficLight execute(Action action, TrafficLight trafficLight) {
		if(action.equals(Action.SWITCH)) {
			return trafficLight.nextSignal();
		}
		else {
			return new TrafficLight(trafficLight);
		}
	}
	
}
