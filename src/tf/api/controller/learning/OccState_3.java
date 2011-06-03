package tf.api.controller.learning;

import tf.api.model.Direction;
import tf.api.model.TrafficLight;


/**
 * A variant definition of state.
 * Records the occupancy of cars at the first three positions 
 * from intersection for each road (0-7). 
 * The value on each road is represented as 3-bit integer in binary.
 * For example, 000 means no car exists at the first three positions,
 * while 110 means the first and the second position are occupied.
 * 
 * @author hanli
 *
 */
public class OccState_3 extends State {

	/**
	 * the occupancy of cars at the first three positions 
	 * from intersection for each road (0-7)
	 */
	private int occEW;
	private int occWE;
	private int occNS;
	private int occSN;
	/**
	 * waiting at least three time-steps since the last change.
	 * light delay (0-3)
	 */
	private int lightDelay;
	private TrafficLight trafficLight;
	
	
	public OccState_3() {
		super();
		occEW = 0;
		occWE = 0;
		occNS = 0;
		occSN = 0;
		lightDelay = 0;
	}
	

	@Override
	public int intValue() {

		if(trafficLight==null) {
			return -1;
		}

		int value = 0;
		value += occEW + 8*occWE + 64*occNS + 512*occSN;
		value += 4096 * lightDelay;
		value += 4 * 4096 * trafficLight.intValue();

		return value;
	}



	/**
	 * @param occ the occupancy of the first fourth positions on the given road.
	 * @param dir the layout direction of the road.
	 */
	public void setPosition(int occ, Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			occEW = occ;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			occWE = occ;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			occSN = occ;
		}
		else if(dir.equals(Direction.NORTH_SOUTH)) {
			occNS = occ;
		}
	}


	/**
	 * @param dir the layout direction of the road.
	 * @return the occupancy of the first fourth positions on the given road
	 */
	public int getPosition(Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			return occEW;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			return occWE;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			return occSN;
		}
		else {
			return occNS;
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
