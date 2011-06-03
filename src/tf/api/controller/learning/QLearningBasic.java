/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.api.controller.learning;

import tf.api.experiment.Parameter;

/**
 * @author hanli
 *
 */
public class QLearningBasic extends QLearning {

	/**
	 * the discount factor
	 */
	private double gamma = Parameter.DISCOUNT_FACTOR;
	/**
	 * the learning rate
	 */
	private double alpha = Parameter.LEARNING_RATE;
	/**
	 * Epsilon-greedy exploration 10%
	 */
	private double epsilon = Parameter.EPSILON_GREEDY;


	/**
	 * Creates a simulator that (suppose) to work like Controller.
	 */
	public QLearningBasic() {
		super();
	}


	/* 
	 * Update the Q values using the default algorithm.
	 * 
	 * @see tf.api.learning.QLearning#updateQValues(
	 * tf.api.learning.State, 
	 * tf.api.learning.State, 
	 * tf.api.learning.Action, 
	 * int)
	 */
	@Override
	public void updateQValues(State prevState, State currState,
			Action action, int reward) {

		double oldValue = getQValue(prevState, action);

		double maxValue = getMaxQValue(currState);
		double newValue = oldValue + alpha * (
				reward + gamma*maxValue - oldValue );

		putQValue(prevState, action, newValue);
	}


	/* 
	 * Retrieve the next action using Epsilon-greedy strategy: 
	 * The best lever is selected for a proportion 1 − ε of the 
	 * trials, and another lever is randomly selected (with 
	 * uniform probability) for a proportion ε. 
	 * 
	 * @see tf.api.learning.QLearning#getNextAction(tf.api.learning.State)
	 */
	@Override
	public Action getNextAction(State currState) {
		Action[] actions = Action.values();
		double[] weight = new double[actions.length];

		double max = Double.NEGATIVE_INFINITY;
		int index = -1;
		for(int i=0; i<actions.length; i++) {
			double value = getQValue(currState, actions[i]);
			if(value>max) {
				index = i;
				max = value;
			}
		}

		for(int i=0; i<weight.length; i++) {
			if(i!=index) {
				weight[i] = epsilon / (weight.length-1);
			}
			else {
				weight[i] = 1 - epsilon;
			}
		}

		double sum = Math.random();
		for(int i=0; i<weight.length; i++ ) {
			sum = sum - weight[i];
			if( sum<0 ) {
				return actions[i];
			}
		}
		
		return actions[weight.length-1];
	}

}
