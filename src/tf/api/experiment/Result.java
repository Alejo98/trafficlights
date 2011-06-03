package tf.api.experiment;

import tf.api.model.Direction;
import tf.api.model.Road;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 * Obtains the experimental results in this class
 * You might need to override/implements methods
 * to obtain the results you want.
 * 
 * @author hanli
 *
 */
public class Result {
	
	private int totalWaitTime;
	
	public Result() {
		totalWaitTime = 0;
	}
	
	/**
	 * Display the traffic state at each step.
	 * Override this method to write logs in file for analysis.
	 *
	 * @param model
	 * @param lights
	 * @param waitTime
	 */
	public void consolePrinter(TrafficModel model, TrafficLight lights, int waitTime) {

//		System.out.print("Light: ("
//				+ lights.getSignal(Direction.EAST_WEST) +", " 
//				+ lights.getSignal(Direction.EAST_WEST) + ")\t");
//
//		System.out.print("Total Waiting: " + waitTime + "\tCars: (");
//
//		int i = 0;
//		for(Direction dir : Direction.values()) {
//			i++;
//			Road road = model.getRoad(dir);
//			int stopPosition = road.getLength()/2 - 2;
//			int pos = road.positionOfClosestCar(stopPosition);
//			if(i>=4) {
//				System.out.println(pos + ")");
//			}
//			System.out.print(pos + ", ");
//		}
	}
	
	/**
	 * Override this method to return any significant results.
	 * This method will be called by the GUI after a simulation run has
	 * completed successfully.
	 * @return Anything you want to display to the user. Can be multi-lined.
	 */
	public String getResult() {
		System.out.println(totalWaitTime);
		return "Total Waiting Time: " + totalWaitTime 
		+ "\n\nPlease override this method to return any significant results.";
	}

	
	public int getTotalWaitTime() {
		return totalWaitTime;
	}

	
	public void setTotalWaitTime(int totalWaitTime) {
		this.totalWaitTime = totalWaitTime;
	}

	
	/**
	 * Increase the total waiting time.
	 * 
	 * @param waitTime
	 */
	public void addToTotalWaitTime(int waitTime) {
		this.totalWaitTime += waitTime;
	}
}
