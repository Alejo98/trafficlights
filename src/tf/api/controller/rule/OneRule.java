/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.api.controller.rule;

import java.util.Set;
import tf.api.controller.TrafficSimulator;
import tf.api.experiment.Parameter;
import tf.api.model.Direction;
import tf.api.model.ModelAndLight;
import tf.api.model.Road;
import tf.api.model.Signal;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 * A decision stump or 1 rule. The rule is hard-coded, serves as a basis
 * for performance benchmarking. The rule is to change traffic lights
 * whenever there is a certain number of cars waiting. This certain number
 * of cars or threshold change be changed via Parameter.ONE_RULE_THRESHOLD.
 * @author lawry
 */
public class OneRule extends TrafficSimulator {
    private int threshold;
    private int lightDelay;

    public OneRule() {
        super();
        threshold = Parameter.ONE_RULE_THRESHOLD;
        lightDelay = 0; // First light change is allowed to happen immediately.
    }

    /**
     * The lights are changed BEFORE the next step.
     */
    @Override
    public ModelAndLight processStep(TrafficModel tfm, TrafficLight lights) {
        // Next step.
        TrafficModel nextTfm = this.nextStep(tfm, lights);
        --lightDelay;

        // Keep the current light first.
        TrafficLight nextLight = new TrafficLight(lights);

        // If the threshold is reached, change the light.
        Set<Road> roads = nextTfm.getRoads();
        for (Road rd : roads) {
            int stoppedCars = numberOfStoppedCars(nextTfm, rd, lights);
            System.err.println("Stopped cars for " + rd.getDirection() + " is " + stoppedCars);
            if (stoppedCars >= threshold) {
                // This road must be getting a red light (otherwise there would
                // be no car stopping), so change it.
                if (lightDelay <= 0) {
                    nextLight = lights.nextSignal();
                    System.err.println("Lights changed");
                    lightDelay = Parameter.LIGHT_DELAY;
                }
                // Cannot change lights yet, so wait until next round.
                break;
            }
        }
        
        return new ModelAndLight(nextTfm, nextLight);
    }

    /**
     * In the 4 road traffic model, a single road will have 2
     * perpendicular roads intersecting it.
     * @param tfm Traffic model.
     * @param rd A road on the traffic model.
     * @return The perpendicular road intersecting at the
     * smaller position (almost always 49, since 49 and 50
     * are the default intersection positions for all roads).
     */
    public static int intersectionPosition(TrafficModel tfm, Road rd) {
        // Work out the intersection points.
        Direction roadDir = rd.getDirection();
        Direction[] xDirs = roadDir.getPerpendicular();
        int intsec0 = tfm.getIntersectPosition(rd, tfm.getRoad(xDirs[0]));
        int intsec1 = tfm.getIntersectPosition(rd, tfm.getRoad(xDirs[1]));
        return Math.min(intsec0, intsec1);
    }

    /**
     * Work out the number of cars stopped at the intersection.
     * @param tfm The current traffic.
     * @param rd The road to work on.
     * @param light The current light signal.
     * @return 0 if the road is having green light. Otherwise the number
     * of cars that cannot move on this road.
     */
    protected static int numberOfStoppedCars(TrafficModel tfm, Road rd, TrafficLight light) {
        Direction roadDir = rd.getDirection();

        if (light.getSignal(roadDir) == Signal.GREEN) {
            // No cars need to stop.
            return 0;
        }

        // Cars stop on amber or red.
        // Work out the intersection points.
        int intsec = intersectionPosition(tfm, rd);

        // Count how many cars back up from the intersection.
        int carsWaiting = 0;
        // Cars on the intersection doesn't count, so start at intsec-1.
        for (int pos = intsec - 1; pos >= 0; --pos) {
            if (rd.isOccupied(pos)) {
                ++carsWaiting;
            } else {
                // An empty space, so stop counting.
                break;
            }
        }

        return carsWaiting;
    }
    
}
