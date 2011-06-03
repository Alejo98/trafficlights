package tf.api.controller.learning;

import tf.api.model.Direction;
import tf.api.model.TrafficLight;

/**
 * The default definition of state, as in the assignment specification
 * Records the closest car position from intersection for each road (0-8, 9 if no cars).
 * 
 * @author hanli
 *
 */
public class DefaultState extends State {

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


	public DefaultState() {
		super();
		posEW = 9;
		posWE = 9;
		posNS = 9;
		posSN = 9;
		lightDelay = 0;
	}

	/**
	 * @return the value of this state as an integer
	 */
	@Override
	public int intValue() {
		
		if(trafficLight==null) {
			return -1;
		}
		
		int value = 0;
		
		value += posEW + 10*posWE + 100*posNS + 1000*posSN;
		value += 10000 * lightDelay;
		value += 40000 * trafficLight.intValue();

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
