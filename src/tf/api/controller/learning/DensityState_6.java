package tf.api.controller.learning;

import tf.api.model.Direction;
import tf.api.model.TrafficLight;

public class DensityState_6 extends State {

	/**
	 * the number of cars at the first three positions (i.e. the first time slot) X
	 * the number of cars at the second time slot
	 * from intersection for each road (0-15)
	 */
	private int slotEW;
	private int slotWE;
	private int slotNS;
	private int slotSN;
	/**
	 * waiting at least three time-steps since the last change.
	 * light delay (0-3)
	 */
	private int lightDelay;
	private TrafficLight trafficLight;
	
	
	public DensityState_6() {
		super();
		slotEW = 0;
		slotWE = 0;
		slotNS = 0;
		slotSN = 0;
		lightDelay = 0;
	}
	

	@Override
	public int intValue() {

		if(trafficLight==null) {
			return -1;
		}

		int value = 0;
		value += slotEW + 16*slotWE + 256*slotNS + 4096*slotSN;
		value += 65536 * lightDelay;
		value += 4 * 65536 * trafficLight.intValue();

		return value;
	}



	/**
	 * @param slot the slot value on the given road.
	 * @param dir the layout direction of the road.
	 */
	public void setSlot(int slot, Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			slotEW = slot;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			slotWE = slot;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			slotSN = slot;
		}
		else if(dir.equals(Direction.NORTH_SOUTH)) {
			slotNS = slot;
		}
	}


	/**
	 * @param dir the layout direction of the road.
	 * @return the slot value on the given road
	 */
	public int getSlot(Direction dir) {
		if(dir.equals(Direction.EAST_WEST)) {
			return slotEW;
		}
		else if(dir.equals(Direction.WEST_EAST)) {
			return slotWE;
		}
		else if(dir.equals(Direction.SOUTH_NORTH)) {
			return slotSN;
		}
		else {
			return slotNS;
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
