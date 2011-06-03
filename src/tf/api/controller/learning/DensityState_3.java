package tf.api.controller.learning;

import tf.api.model.Direction;
import tf.api.model.TrafficLight;

/**
 * The variant definition of state
 * Records the number of cars at the first three positions
 * from intersection for each road (0-3).
 * 
 * @author hanli
 *
 */
public class DensityState_3 extends State {

	/**
	 * number of cars at the first three numitions
	 * from intersection for each road (0-3)
	 */
	private int numEW;
	private int numWE;
	private int numNS;
	private int numSN;
	/**
	 * waiting at least three time-steps since the last change.
	 * light delay (0-3)
	 */
	private int lightDelay;
	private TrafficLight trafficLight;


	public DensityState_3() {
		super();
		numEW = 0;
		numWE = 0;
		numNS = 0;
		numSN = 0;
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
		
		value += numEW + 4*numWE + 16*numNS + 64*numSN;
		value += 256 * lightDelay;
		value += 1024 * trafficLight.intValue();

		return value;
	}


	/**
	 * @param num the closest numition of the car on the given road.
	 * @param dir the layout direction of the road.
	 */
	public void setPosition(int num, Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			numEW = num;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			numWE = num;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			numSN = num;
		}
		else if(dir.equals(Direction.NORTH_SOUTH)) {
			numNS = num;
		}
	}


	/**
	 * @param dir the layout direction of the road.
	 * @return the closest numition of the car on the given road
	 */
	public int getPosition(Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			return numEW;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			return numWE;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			return numSN;
		}
		else {
			return numNS;
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
