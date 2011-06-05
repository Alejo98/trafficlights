package tf.api.experiment;

import tf.api.controller.TrafficSimulator;
import tf.api.controller.learning.Ultimate;
import tf.api.model.ModelAndLight;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

public class Main {

	/**
	 * Entry class for the command-line Traffic Simulator.
	 * 
	 * The Ultimate algorithm has been hard-coded for the CLI application.
	 * Use the GUI for simulating other algorithms. 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int epochs = 10000000;
		TrafficModel tfModel = new TrafficModel();
        TrafficLight tfLights = new TrafficLight();
        TrafficSimulator tfSim = new Ultimate();
		
		int total = 0;
        for (int i = 0; i < epochs; ++i) {
            ModelAndLight newEnv = tfSim.processStep(tfModel, tfLights);
            tfModel = newEnv.getTfModel();
            tfLights = newEnv.getTfLight();
            
            if(i%100000==99999) {
            	int curr = tfSim.getTotalWaitTime();
            	System.out.println((i+1)/100000 + "\t" + (curr-total));
            	total = curr;
            }
        }
	}

}
