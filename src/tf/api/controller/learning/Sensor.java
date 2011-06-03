package tf.api.controller.learning;

import java.util.Iterator;

import tf.api.model.Road;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

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
	
	
	public State getState(TrafficModel model, 
			TrafficLight lights, 
			int delay,
			Class state) {
		
		if(state.equals(DefaultState.class)) {
			return getDefaultState(model, lights, delay);
		}
		if(state.equals(OccState_3.class)) {
			return getOccState_3(model, lights, delay);
		}
		if(state.equals(OccState_4.class)) {
			return getOccState_4(model, lights, delay);
		}
		if(state.equals(DensityState_6.class)) {
			return getDensityState_6(model, lights, delay);
		}
		if(state.equals(DensityState_3.class)) {
			return getDensityState_3(model, lights, delay);
		}
		return null;
	}
	
	
	/**
	 * Generates a default state object
	 * from the environment variables.
	 * Refer to DefaultState for the definition.
	 * 
	 * @param model the current traffic model
	 * @param lights the traffic light
	 * @param delay the light delay since the last change
	 * @return the state object
	 */
	private State getDefaultState(TrafficModel model, TrafficLight lights, int delay) {
		assert model!=null && lights!=null;
		
		DefaultState state = new DefaultState();
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
	
	
	/**
	 * Generates a 4-bit occupancy state object
	 * from the environment variables.
	 * Refer to OccState_4 for the definition.
	 * 
	 * @param model the current traffic model
	 * @param lights the traffic light
	 * @param delay the light delay since the last change
	 * @return the state object
	 */
	private State getOccState_4(TrafficModel model, TrafficLight lights, int delay) {
		assert model!=null && lights!=null;
		
		OccState_4 state = new OccState_4();
		state.setTrafficLight(lights);
		state.setLightDelay(delay);
		
		Iterator<Road> roads = model.getRoads().iterator();
		while(roads.hasNext()) {
			Road road = roads.next();
			int stopPosition = road.getLength()/2 - 2;
			
			int occ = 0;
			for(int i=0; i<4; i++) {
				if(road.isOccupied(stopPosition-i)==true) {
					occ += Math.pow(2, 3-i);
				}
			}
			state.setPosition(occ, road.getDirection());
		}
		
		return state;
	}
	
	
	/**
	 * Generates a 3-bit occupancy state object
	 * from the environment variables.
	 * Refer to OccState_3 for the definition.
	 * 
	 * @param model the current traffic model
	 * @param lights the traffic light
	 * @param delay the light delay since the last change
	 * @return the state object
	 */
	private State getOccState_3(TrafficModel model, TrafficLight lights, int delay) {
		assert model!=null && lights!=null;
		
		OccState_3 state = new OccState_3();
		state.setTrafficLight(lights);
		state.setLightDelay(delay);
		
		Iterator<Road> roads = model.getRoads().iterator();
		while(roads.hasNext()) {
			Road road = roads.next();
			int stopPosition = road.getLength()/2 - 2;
			
			int occ = 0;
			for(int i=0; i<3; i++) {
				if(road.isOccupied(stopPosition-i)==true) {
					occ += Math.pow(2, 2-i);
				}
			}
			state.setPosition(occ, road.getDirection());
		}
		
		return state;
	}

	
	/**
	 * Generates a slot state object
	 * from the environment variables.
	 * Refer to SlotState for the definition.
	 * 
	 * @param model the current traffic model
	 * @param lights the traffic light
	 * @param delay the light delay since the last change
	 * @return the state object
	 */
	private State getDensityState_6(TrafficModel model, TrafficLight lights, int delay) {
		assert model!=null && lights!=null;
		
		DensityState_6 state = new DensityState_6();
		state.setTrafficLight(lights);
		state.setLightDelay(delay);
		
		Iterator<Road> roads = model.getRoads().iterator();
		while(roads.hasNext()) {
			Road road = roads.next();
			int stopPosition = road.getLength()/2 - 2;
			
			int num = 0;
			for(int i=0; i<3; i++) {
				if(road.isOccupied(stopPosition-i)==true) {
					num++;
				}
			}
			
			int slot = num*4;
			
			num = 0;
			for(int i=3; i<6; i++) {
				if(road.isOccupied(stopPosition-i)==true) {
					num++;
				}
			}
			slot += num;
			
			state.setSlot(slot, road.getDirection());
		}
		
		return state;
	}

	
	/**
	 * Generates a Density state object
	 * from the environment variables.
	 * Refer to DensityState for the definition.
	 * 
	 * @param model the current traffic model
	 * @param lights the traffic light
	 * @param delay the light delay since the last change
	 * @return the state object
	 */
	private State getDensityState_3(TrafficModel model, TrafficLight lights, int delay) {
		assert model!=null && lights!=null;
		
		DensityState_3 state = new DensityState_3();
		state.setTrafficLight(lights);
		state.setLightDelay(delay);
		
		Iterator<Road> roads = model.getRoads().iterator();
		while(roads.hasNext()) {
			Road road = roads.next();
			int stopPosition = road.getLength()/2 - 2;
			
			int num = 0;
			for(int i=0; i<3; i++) {
				if(road.isOccupied(stopPosition-i)==true) {
					num++;
				}
			}
			
			state.setPosition(num, road.getDirection());
		}
		
		return state;
	}

	
}
