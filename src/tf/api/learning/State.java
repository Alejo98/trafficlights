package tf.api.learning;

import tf.api.Direction;
import tf.api.Signal;
import tf.api.TrafficLight;

/**
 * The state for reinforcement learning
 * @author hanli
 *
 */
public class State {
	
	/**
	 * closest car position from intersection for each road (0-8, 9 if no cars)
	 */
	private int posEW;
	private int posWE;
	private int posNS;
	private int posSN;
	/**
	 * waiting at least three time-steps since the last change.
	 * light delay (0-3)
	 */
	private int lightDelay;
	private TrafficLight trafficLight;
	
	public State() {
		super();
		posEW = 9;
		posWE = 9;
		posNS = 9;
		posSN = 9;
		lightDelay = 0;
		trafficLight = new TrafficLight();
	}
	
	/**
	 * @return the value of this state as an integer
	 */
	public int intValue() {
		int value = posEW + 10*posWE + 100*posNS + 1000*posSN + 10000*lightDelay;
		
		Direction dir = Direction.EAST_WEST;
		int weight;
		if( trafficLight.getSignal(dir).equals(Signal.GREEN) ) {
			weight = 0;
		}
		else if( trafficLight.getSignal(dir).equals(Signal.AMBER) ) {
			weight = 1;
		}
		else {
			if(trafficLight.getSignal(Direction.SOUTH_NORTH).equals(Signal.GREEN) ) {
				weight = 2;
			}
			else {
				weight = 3;
			}
		}
		value += 100000*weight;
		
		return value;
	}
	
	
	/**
	 * @param pos the closest position of the car on the given road.
	 * @param dir the layout direction of the road.
	 */
	public void setPosition(int pos, Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			posEW = pos;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			posWE = pos;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			posSN = pos;
		}
		else if(dir.equals(Direction.NORTH_SOUTH)) {
			posNS = pos;
		}
	}
	
	
	/**
	 * @param dir the layout direction of the road.
	 * @return the closest position of the car on the given road
	 */
	public int getPosition(Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			return posEW;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			return posWE;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			return posSN;
		}
		else {
			return posNS;
		}
	}
	

	public TrafficLight getTrafficLight() {
		return trafficLight;
	}

	public void setTrafficLight(TrafficLight trafficLight) {
		this.trafficLight = new TrafficLight(trafficLight);
	}

	public int getLightDelay() {
		return lightDelay;
	}

	public void setLightDelay(int lightDelay) {
		this.lightDelay = lightDelay;
	}

}
