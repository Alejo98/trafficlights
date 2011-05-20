package tf.api.learning;

import tf.api.TrafficLight;

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
	 */
	public void execute(Action action, TrafficLight trafficLight) {
		if(action.equals(Action.SWITCH)) {
			trafficLight.switches();
		}
	}
	
}
