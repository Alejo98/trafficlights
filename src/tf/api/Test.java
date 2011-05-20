package tf.api;

import tf.api.learning.Controller;


/**
 * A demo of getting started
 * @author hanli
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		/**
		 *  Approx. 15% of the units on the road are occupied by cars.
		 */
		double trafficIntensity = 0.15;
		TrafficSimulator simulator = new TrafficSimulator(trafficIntensity);
		TrafficModel model = simulator.getInitialTrafficModel();
		TrafficLight lights = new TrafficLight();
		
		Controller control = new Controller();
		control.init(model, lights, simulator, 200, 150);
		control.run();
	}

}
