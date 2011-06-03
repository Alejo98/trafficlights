package tf.api.experiment;

import tf.api.controller.learning.DefaultState;
import tf.api.controller.learning.DensityState_3;
import tf.api.controller.learning.OccState_3;
import tf.api.controller.learning.DensityState_6;


/**
 * The parameters used in the experiment.
 * Change the values in this method to run
 * different sets of tests.
 * 
 * @author hanli
 *
 */
public final class Parameter {
	
	/**
	 * the discount factor of Q Learning
	 */
	public static final double DISCOUNT_FACTOR = 0.90;
	/**
	 * the learning rate
	 */
	public static final double LEARNING_RATE = 0.10;
	
	/**
	 * the epsilon greedy proportion for exploration.
	 */
	public static final double EPSILON_GREEDY = 0.1;
	
	/**
	 * the initial temperature of Boltzmann Distribution function
	 */
	public static final double BOLTZMANN_INIT_TEMPERATURE = 0.5;
	/**
	 * the annealing factor of the temperature described above.
	 */
	public static final double TERMPERATURE_ANNEAL_FACTOR = 0.9999998;
	
	/**
	 * the eligibility trace decay factor.
	 */
	public static double TRACE_DECAY = 0.85;
	
	/**
	 * the seed of random generator for the simulator.
	 */
	public static final int RANDOM_SEED = 9417;
	
	/**
	 * Controls the lights using a fixed change time.
	 * Used as a benchmark against Q learning strategies. 
	 */
	public static final int FIXED_CHANGE_TIME = 10;
	
	public static final double TRAFFIC_INTENSITY = 0.15;
	
	/**
	 * the type of state to be used in the experiments.
	 */
	//DefaultState.class;  //OccState_4.class; //DensityState_6.class; //DensityState_3.class
	public static final Class STATE_TYPE = DensityState_3.class;
	
	/**
     * See tf.api.controller.rule.OneRule for details.
     */
    public static final int ONE_RULE_THRESHOLD = 1;

    /**
     * Wait this number of time steps (as a minimal) before lights can
     * change again.
     */
    public static final int LIGHT_DELAY = 3;

}
