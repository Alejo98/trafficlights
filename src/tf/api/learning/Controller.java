package tf.api.learning;

import java.util.HashMap;
import java.util.Map;

import tf.api.Car;
import tf.api.Direction;
import tf.api.Road;
import tf.api.TrafficLight;
import tf.api.TrafficModel;
import tf.api.TrafficSimulator;

/**
 * A controller using reinforcement learning to switch the traffic light
 * @author hanli
 *
 */
public class Controller implements Runnable {

	/**
	 * the discount factor
	 */
	private double gamma = 0.9;
	/**
	 * the learning rate
	 */
	private double alpha = 0.1;
	/**
	 * Epsilon-greedy exploration 10%
	 */
//	private double epsilon = 0.1;
	/**
	 * the temperature of Boltzmann distribution
	 */
	private double tau = 4.0;
	/**
	 * the database of Q values gained from the learning process
	 */
	private Map<Pair, Double> qValues;
	
	/**
	 * the environment variables
	 */
	private TrafficModel trafficModel;
	private TrafficLight trafficLight;
	private TrafficSimulator simulator;
	
	/**
	 * the learning helpers
	 */
	private Sensor sensor;
	private Actuator actuator;
	
	/**
	 * the duration of each time-step in milliseconds.
	 */
	private long epochDuration;
	/**
	 * the total time-steps of this experiment
	 */
	private int timesteps;
	
	public Controller() {
		qValues = new HashMap<Pair, Double>();
		
		sensor = Sensor.getInstance();
		actuator = Actuator.getInstance();
	}
	
	
	/**
	 * @param model the traffic model
	 * @param lights the traffic light
	 * @param simulator the traffic simulator
	 * @param epochDuration the duration of each time-step in milliseconds
	 * @param timesteps the total time-steps of this experiment
	 */
	public void init(TrafficModel model, 
			TrafficLight lights, 
			TrafficSimulator simulator,
			long epochDuration,
			int timesteps) {
		
		assert model!=null && lights!=null && simulator!=null;
		
		this.trafficModel = model;
		this.trafficLight = lights;
		this.simulator = simulator;
		this.epochDuration = epochDuration;
		this.timesteps = timesteps;
	}
	
	
	
	/**
	 * Use the default values.
	 * epochDuration = 100;
	 * timesteps = 100;
	 * 
	 * @param model the traffic model
	 * @param lights the traffic light
	 * @param simul the traffic simulator
	 */
	public void init(TrafficModel model, TrafficLight lights, TrafficSimulator simul) {
		init(model, lights, simul, 100, 100);
	}
	
	
	/**
	 * @param epochDuration the duration of each time-step in milliseconds
	 * @param timesteps the total time-steps of this experiment
	 */
	public void init(long epochDuration,
			int timesteps) {
		
		TrafficSimulator simul = new TrafficSimulator();
		init(simul.getInitialTrafficModel(), 
				new TrafficLight(),
				simul, 
				epochDuration, 
				timesteps);
	}
	
	
	/**
	 * Use the default values.
	 * epochDuration = 100;
	 * timesteps = 100;
	 */
	public void init() {
		init(100, 100);
	}
	

	@Override
	public void run() {
		
		if(trafficModel==null || trafficLight==null || simulator==null) {
			throw new IllegalStateException("Experimental environment not initilized.");
		}
		
		int lightDelay = 3;
		State prevState = sensor.getState(trafficModel, trafficLight, lightDelay);
		Action action = Action.NOT_SWITCH;
		int reward = 0;
		
		for(int count=0; count<timesteps; count++) {			
			/**
			 *  wait for one time-step
			 */
			sleep(epochDuration);
			reward += nextStep();
			/**
			 * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
			 * ||||||||     Display the new traffic model here    |||||||||
			 * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
			 */
			samplePrinter(trafficModel, trafficLight, 0-reward);
			
			lightDelay--;
			if(lightDelay > 0) {
				continue;
			}
			else {
				State currState = sensor.getState(trafficModel, trafficLight, lightDelay);
				updateQValues(prevState, currState, action, reward);
				
				action = getNextAction(currState);
				actuator.execute(action, trafficLight);
				prevState = currState;
				reward = 0;
				
				if(action.equals(Action.SWITCH)) {
					lightDelay = 3;
				}
				else {
					lightDelay = 0;
				}
			}
		}
	}
	
	
	private void updateQValues(State prevState, State currState,
			Action action, int reward) {
		
		Pair pair = new Pair(prevState.intValue(), action);
		
		double oldValue = 0.0;
		if(qValues.containsKey(pair)) {
			oldValue = qValues.get(pair);
		}
		else {
			qValues.put(pair, oldValue);
		}

		double maxValue = getMaxFutureValue(currState);
		
		double newValue = oldValue + alpha * (
				reward + gamma*maxValue - oldValue );
		
		qValues.put(pair, newValue);
	}

	
	private double getMaxFutureValue(State state) {

		double maxValue = Double.MIN_VALUE;
		
		Action[] actions = Action.values();
		for(Action action : actions) {
			Pair pair = new Pair(state.intValue(), action);
			double value = 0.0;
			if(qValues.containsKey(pair)) {
				value = qValues.get(pair);
			}
			else {
				qValues.put(pair, value);
			}
			if(maxValue < value) {
				maxValue = value;
			}
		}
		
		return maxValue;
	}

	
	/**
	 * Move the environment forwards to the next step.
	 * 
	 * @return the negative value of total waiting time as the reward.
	 */
	private int nextStep() {
		trafficModel = simulator.processStep(trafficModel, trafficLight);
		return 0 - simulator.getWaitTime();
	}

	
	private Action getNextAction(State currState) {
		Action[] actions = Action.values();
		double[] weight = new double[actions.length];
		double sum = 0.0;
		
		for(int i=0; i<actions.length; i++) {
			double value = 0.0;
			Pair pair = new Pair(currState.intValue(), actions[i]);
			if(qValues.containsKey(pair)) {
				value = qValues.get(pair);
			}
			else {
				qValues.put(pair, value);
			}
			
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
		return actions[weight.length-1];
	}

	
	
	private static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * A sample printer for demonstrating the output.
	 * 
	 * @param model
	 * @param lights
	 * @param reward
	 */
	private void samplePrinter(TrafficModel model,
			TrafficLight lights, int waitTime) {
		/**
		 *  Display the car positions on the road
		 */
		Direction dir = Direction.SOUTH_NORTH;
		Road road = model.getRoad(dir);
		System.out.print(lights.getSignal(dir) 
				+ " (Total Waiting: " + waitTime + ")");
		Car[] cars = road.getCarsInOrder();
		for(Car car : cars) {
			System.out.print("\t" + road.carPosition(car));
		}
		System.out.println();
	}


	/**
	 * The State-Action pair for Q Learning
	 * @author hanli
	 *
	 */
	private static class Pair {
		private int state;
		private Action action;

		public Pair(int s, Action a) {
			state = s;
			action = a;
		}

		@Override
		public boolean equals(Object obj) {
			assert action != null;
			if (obj != null && obj instanceof Pair) {
				Pair oth = (Pair)obj;
				assert oth.action != null;
				return oth.state==this.state && oth.action.equals(this.action);
			}
			return false;
		}

		@Override
		public int hashCode() {
			assert action != null;
			return state + action.hashCode();
		}
	}

}
