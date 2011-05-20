package tf.api;

import java.util.Iterator;
import java.util.Random;

/**
 * A helper class for generating and controlling the traffic states
 *
 */
public class TrafficSimulator {
	
	private double trafficIntensity;
	private volatile int waitTime;
	
	/**
	 * A new traffic simulator.
	 * The default value of the traffic intensity for each road is 10%. 
	 * That is, approx. 10 cars on the 100-unit road.
	 */
	public TrafficSimulator() {
		trafficIntensity = 0.1;
	}
	
	/**
	 * A new traffic simulator.
	 * @param intensity the traffic intensity of the road. The value should be 0.0 <= intensity < 1.0.
	 */
	public TrafficSimulator(double intensity) {
		if(intensity < 0.0 || intensity>=1.0) {
			throw new IllegalArgumentException();
		}
		trafficIntensity = intensity;
	}
	
	
	/**
	 * Initialize the traffic model with a number of cars at random positions on all roads.
	 * @return		the initial traffic model
	 */
	public TrafficModel getInitialTrafficModel() {
		
		TrafficModel model = new TrafficModel();
		Iterator<Road> roads = model.getRoads().iterator();
		Random randomGenerator = new Random();
		
		while(roads.hasNext()) {
			Road road = roads.next();
			int len = road.getLength();
			/**
			 * Do not put cars at the intersection
			 */
			int int1 = len/2 - 1;
			int int2 = len/2 + 1;
			
			for(int i=0; i<len; i++) {
				if((i<int1 || i>=int2) 
						&& randomGenerator.nextDouble() < getTrafficIntensity()) {
					Car car = new Car();
					road.addCar(car, i);
				}
			}
		}		
		return model;
	}
	
	
	/**
	 * Given the current state of the traffic, return the traffic model at next step.
	 * The total waiting time of all the cars is updated in the process.
	 * 
	 * @param current	the current traffic model
	 * @param lights	the traffic light
	 * @return	the traffic model at next step
	 */
	public synchronized TrafficModel processStep(TrafficModel current, 
			TrafficLight lights) {
		
		TrafficModel next = new TrafficModel();
		Iterator<Road> currRoads = current.getRoads().iterator();
		waitTime = 0;
		
		while(currRoads.hasNext()) {
			Road currRoad = currRoads.next();
			Road nextRoad = next.getRoad(currRoad.getDirection());
			Signal sgn = lights.getSignal(currRoad.getDirection());
			
			int len = currRoad.getLength();
			/**
			 * The position where a car should stop, if the traffic light is amber or red.
			 */
			int stopPosition = len/2 - 2;
			
			Car[] cars = currRoad.getCarsInOrder();
			
			/**
			 * Removes all the cars that reach the end of the road.
			 */
			int i=cars.length-1;
			for(; i>=0 && currRoad.carPosition(cars[i]) > len-2; i--);
			/**
			 * Move every car one unit forward, if it has passed the stopping position
			 */
			for(; i>=0 && currRoad.carPosition(cars[i]) > stopPosition; i--) {
				nextRoad.addCar(cars[i], currRoad.carPosition(cars[i])+1);
			}
			/**
			 * Continue to move the cars that are before the stopping position, if the signal is Green.
			 */
			if(sgn.equals(Signal.GREEN) ) {
				for(; i>=0; i--) {
					nextRoad.addCar(cars[i], currRoad.carPosition(cars[i])+1);
				}
			}
			/**
			 * Otherwise, stop the cars that are blocked 
			 * by the stopping position or by the car at the next unit.
			 */
			else {
				for(; i>=0 && currRoad.carPosition(cars[i])==stopPosition; i--) {
					nextRoad.addCar(cars[i], stopPosition);
					stopPosition--;
					waitTime++;
				}
				for(; i>=0; i--) {
					nextRoad.addCar(cars[i], currRoad.carPosition(cars[i])+1);
				}
			}
			/**
			 * Randomly adds a new car at the starting point of the road
			 */
			Random randomGenerator = new Random();
			if(randomGenerator.nextDouble() < getTrafficIntensity()) {
				Car car = new Car();
				nextRoad.addCar(car, 0);
			}
		}
		
		return next;
	}

	
	public double getTrafficIntensity() {
		return trafficIntensity;
	}

	public void setTrafficIntensity(double trafficIntensity) {
		this.trafficIntensity = trafficIntensity;
	}

	/**
	 * @return the total waiting time of all the cars at the previous processing step.
	 */
	public int getWaitTime() {
		return waitTime;
	}

}
