package tf.api.learning;

import java.util.Iterator;

import tf.api.Road;
import tf.api.TrafficLight;
import tf.api.TrafficModel;

/**
 * A helper class for getting the state from environmental variables.
 * @author hanli
 *
 */
public class Sensor {
	
	private static final Sensor _instance = new Sensor();
	
	private Sensor() {}

	public static Sensor getInstance() {
		return _instance;
	}
	
	
	/**
	 * Generates a state object from the environment variables
	 * 
	 * @param model the current traffic model
	 * @param lights the traffic light
	 * @param delay the light delay since the last change
	 * @return the state object
	 */
	public State getState(TrafficModel model, TrafficLight lights, int delay) {
		assert model!=null && lights!=null;
		
		State state = new State();
		state.setTrafficLight(lights);
		state.setLightDelay(delay);
		
		Iterator<Road> roads = model.getRoads().iterator();
		while(roads.hasNext()) {
			Road road = roads.next();
			int beforePosition = (road.getLength()-1)/2;
			int pos = road.positionOfClosestCar(beforePosition);
			state.setPosition(pos, road.getDirection());
		}
		
		return state;
	}

}
