/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.api.controller.learning;

import tf.api.experiment.Parameter;


/**
 * The agent tries out actions probabilistically based on
 * their Q-values using Boltzmann distribution.
 *
 * @author hanli
 */
public class QLearningBoltzmann extends QLearning {

    /**
     * the discount factor
     */
    private double gamma = Parameter.DISCOUNT_FACTOR;
    /**
     * the learning rate
     */
    private double alpha = Parameter.LEARNING_RATE;
    /**
     * the temperature of Boltzmann distribution
     */
    private double tau = Parameter.BOLTZMANN_INIT_TEMPERATURE;
    /**
     * the temperature annealing factor
     */
    private double anneal = Parameter.TERMPERATURE_ANNEAL_FACTOR;
    /**
     * Creates a simulator that is (suppose) to work like Controller.
     */
    public QLearningBoltzmann() {
    	super();
    }

    
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
	 * Selects the next action probabilistically based on
	 * their Q-values using Boltzmann distribution.
	 * 
	 * @see tf.api.learning.QLearning#getNextAction(tf.api.learning.State)
	 */
	@Override
	public Action getNextAction(State currState) {
		Action[] actions = Action.values();
		double[] weight = new double[actions.length];
		double sum = 0.0;
	
		for(int i=0; i<actions.length; i++) {
			double value = getQValue(currState, actions[i]);
			weight[i] = Math.exp(value/tau);
			sum += weight[i];
		}
	
		double random = Math.random();
		sum = sum * random;
		for(int i=0; i<weight.length; i++ ) {
			sum = sum - weight[i];
			if( sum<0 ) {
				return actions[i];
			}
		}
		
		updateTemperature();
		
		return actions[weight.length-1];
	}


	private void updateTemperature() {
		tau *= anneal;
	}

}
