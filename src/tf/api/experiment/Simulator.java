package tf.api.experiment;

import java.util.Random;

/**
 * Use a dedicated class to generate random numbers,
 * so that the input values (i.e. arrivals of cars) 
 * remain the same in different tests. In other words,
 * the experiment is repeatable.
 * 
 * Change the random seed to generate a different set of inputs.
 * 
 * @author hanli
 *
 */
public class Simulator {
	
	private Random randomGenerator;
	
	public Simulator() {
		randomGenerator = new Random(Parameter.RANDOM_SEED);
	}
	
	public double getNextDouble() {
		return randomGenerator.nextDouble();
	}

}
