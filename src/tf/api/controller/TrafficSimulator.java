package tf.api.controller;

import java.util.Iterator;

import tf.api.experiment.Parameter;
import tf.api.experiment.Result;
import tf.api.experiment.Simulator;
import tf.api.model.Car;
import tf.api.model.ModelAndLight;
import tf.api.model.Road;
import tf.api.model.Signal;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 * Implement a subclass of this class for each new algorithm.
 * You probably want to override processStep() and getResult().
 */
public class TrafficSimulator extends Result {

	private Simulator simulator;
	private double trafficIntensity;
	private volatile int waitTime;

	/**
	 * a clock is added. The traffic light switches every 10 steps.
	 */
	private int clock;
	/**
	 * A new traffic simulator.
	 * The default value of the traffic intensity for each road is 10%. 
	 * That is, approx. 10 cars on the 100-unit road.
	 */
	public TrafficSimulator() {
		super();
		trafficIntensity = Parameter.TRAFFIC_INTENSITY;
		waitTime = 0;
		
		simulator = new Simulator();
		clock = 1; 
	}

	/**
	 * A new traffic simulator.
	 * @param intensity the traffic intensity of the road. The value should be 0.0 <= intensity < 1.0.
	 */
	@Deprecated
	public TrafficSimulator(double intensity) {
		super();
		if(intensity < 0.0 || intensity>=1.0) {
			throw new IllegalArgumentException();
		}
		trafficIntensity = intensity;
		waitTime = 0;
		
		simulator = new Simulator();
		clock = 1; 
	}

	/**
	 * Used for testing (by LY) creates the TrafficModel with
	 * 10x10, and populates every unit of road with a single car.
	 * @return 10x10 TrafficModel packed full of cars.
	 * @see TrafficModel#TrafficModel(int)
	 */
	public TrafficModel getTestingTrafficModel() {
		TrafficModel tm = new TrafficModel(999);
		Iterator<Road> itr = tm.getRoads().iterator();
		while (itr.hasNext()) {
			Road rd = itr.next();
			for (int pos = 0; pos < rd.getLength(); ++pos) {
				rd.addCar(new Car(), pos);
			}
		}
		return tm;
	}

	/**
	 * Initialize the traffic model with a number of cars at random positions on all roads.
	 * @return		the initial traffic model
	 */
	public TrafficModel getInitialTrafficModel() {

		TrafficModel model = new TrafficModel();
		Iterator<Road> roads = model.getRoads().iterator();

		while(roads.hasNext()) {
			Road road = roads.next();
			int len = road.getLength();
			/**
			 * Do not put cars at the intersection
			 */
			int int1 = len/2 - 1;
			int int2 = len/2 + 1;

			for(int i=0; i<len; i++) {
				if((i<int1 || i>=int2) && hasNextCar()==true ) {
					Car car = new Car();
					road.addCar(car, i);
				}
			}
		}
		return model;
	}

	/**
	 * You can override to change the initial traffic light state.
	 * @return new TrafficLight();
	 */
	public TrafficLight getInitialTrafficLight() {
		return new TrafficLight();
	}

	/**
	 * Implement your reinforcement learning logic here.
	 * This method simply calls nextStep and returns, the lights don't even
	 * change.
	 * @param current The current environment.
	 * @param lights The traffic lights.
	 * @return The environment at the next step. The returns traffic model
	 * and traffic light must be NEW objects, otherwise the GUI doesn't work.
	 */
	public ModelAndLight processStep(TrafficModel current, TrafficLight lights) {
		TrafficModel next = nextStep(current, lights);
		if( clock%Parameter.FIXED_CHANGE_TIME == 0 ) {
			clock = 1;
			lights = lights.nextSignal();
		}
		else {
			clock++;
			lights = new TrafficLight(lights);
		}
		return new ModelAndLight(next, lights);
	}

	/**
	 * Given the current state of the traffic, return the traffic model at next step.
	 * The total waiting time of all the cars is updated in the process.
	 * 
	 * @param current	the current traffic model
	 * @param lights	the traffic light
	 * @return	the traffic model at next step
	 */
	protected TrafficModel nextStep(TrafficModel current,
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

			if(hasNextCar()==true && nextRoad.isOccupied(0)==false) {
				Car car = new Car();
				nextRoad.addCar(car, 0);
			}
		}

		addToTotalWaitTime(waitTime);

		return next;
	}
	
	/**
	 * Randomly adds a new car at the starting point of the road
	 */
	private boolean hasNextCar() {
		return simulator.getNextDouble() < getTrafficIntensity();
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
