/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.gui;

import java.util.List;
import javax.swing.JOptionPane;
import org.jdesktop.application.Task;

import tf.api.controller.TrafficSimulator;
import tf.api.model.ModelAndLight;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 * Run the simulation in the background, with some GUI updates.
 * @author lawry
 */
public class SimulateTask extends Task<Void,ModelAndLight> {
    private int epochs;
    private View view;
    private long sleepTime;

    /**
     * Create task to do simulation.
     * @param v The view object, used for GUI updates.
     * @param epochs Number of times to call processStep().
     * @param delay How long to sleep thread for between each step.
     * 0 means do not sleep. Sleeping will enable the GUI to update.
     */
    SimulateTask(View v, int epochs, long delay) {
        super(App.getApplication());
        view = v;
        this.epochs = epochs;
        sleepTime = delay;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // Extract from session and process step.
        AppSessionData session = App.getApplication().getSessionData();

        for (int i = 0; i < epochs; ++i) {
            TrafficModel tfModel = session.getTfModel();
            TrafficLight tfLights = session.getTfLight();
            TrafficSimulator tfSim = session.getTfSimulator();

            ModelAndLight newEnv = tfSim.processStep(tfModel, tfLights);
            session.setTfModel(newEnv.getTfModel());
            session.setTfLight(newEnv.getTfLight());

            publish(newEnv);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    @Override
    protected void process(List<ModelAndLight> values) {
        super.process(values);
        // Just display the latest one. skipping some.
        if (! values.isEmpty()) {
            ModelAndLight env = values.get(values.size() - 1);
            // Update display.
            view.displayTrafficModel(env.getTfModel());
            view.displayTrafficLight(env.getTfLight());
        }
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        JOptionPane.showMessageDialog(null, "Simulation cancelled.");
    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
        JOptionPane.showMessageDialog(null, "Simulation terminated due to error.\n" + cause.toString());
    }

    @Override
    protected void interrupted(InterruptedException e) {
        super.interrupted(e);
        JOptionPane.showMessageDialog(null, "Simulation interrupted.");
    }

    @Override
    protected void succeeded(Void result) {
        super.succeeded(result);
        AppSessionData session = App.getApplication().getSessionData();
        TrafficSimulator tfSimulator = session.getTfSimulator();
        JOptionPane.showMessageDialog(null,
                "Simulation completed successfully after " + epochs +
                " runs with result:\n\n"
                + tfSimulator.getResult());
    }

}
