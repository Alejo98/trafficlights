/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.gui;

import tf.api.controller.TrafficSimulator;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 *
 * @author lawry
 */
public class AppSessionData {
    private TrafficModel tfModel;
    private TrafficLight tfLight;
    private TrafficSimulator tfSimulator;

    public TrafficLight getTfLight() {
        return tfLight;
    }

    public void setTfLight(TrafficLight tfLights) {
        this.tfLight = tfLights;
    }

    public TrafficModel getTfModel() {
        return tfModel;
    }

    public void setTfModel(TrafficModel tfModel) {
        this.tfModel = tfModel;
    }

    public TrafficSimulator getTfSimulator() {
        return tfSimulator;
    }

    public void setTfSimulator(TrafficSimulator tfSimulator) {
        this.tfSimulator = tfSimulator;
    }

}
