package tf.api.controller.learning;


/**
 * The state for reinforcement learning
 * @author hanli
 *
 */
public abstract class State {

	public State() {

	}
	
	/**
	 * @return the value of this state as an integer
	 */
	public abstract int intValue(); 

}
