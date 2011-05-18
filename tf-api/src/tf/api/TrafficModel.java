/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.api;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a displayable traffic model. Captures traffic at any one time.
 * @author lawry
 */
public class TrafficModel {

    private Road roadNS;
    private Road roadSN;
    private Road roadWE;
    private Road roadEW;
    private Map<Pair, Integer> intersects;

    /**
     * Model with 4 roads.
     * Roads intersect at position 50. Each road has length 100.
     */
    public TrafficModel() {
        roadNS = new Road(Direction.NORTH_SOUTH, 100);
        roadSN = new Road(Direction.SOUTH_NORTH, 100);
        roadWE = new Road(Direction.WEST_EAST, 100);
        roadEW = new Road(Direction.EAST_WEST, 100);
        intersects = new HashMap<Pair, Integer>();
        intersects.put(new Pair(roadNS, roadEW), 50);
        intersects.put(new Pair(roadEW, roadNS), 50);
        intersects.put(new Pair(roadSN, roadEW), 50);
        intersects.put(new Pair(roadEW, roadSN), 49);
        intersects.put(new Pair(roadNS, roadWE), 51);
        intersects.put(new Pair(roadWE, roadNS), 50);
        intersects.put(new Pair(roadSN, roadWE), 49);
        intersects.put(new Pair(roadWE, roadSN), 51);
    }

    /**
     * Obtains the position on the road where 2 roads intersection.
     * @param r1 First road, the return position is on this road.
     * @param r2 The other road.
     * @return A position on r1 that is where it intersects r2.
     * @throws NoSuchElementException If either of the road does not
     * belong to this model or the roads do not intersect.
     */
    public int getIntersectPosition(Road r1, Road r2) {
        Pair p = new Pair(r1, r2);
        if (intersects.containsKey(p)) {
            return intersects.get(p);
        }
        throw new NoSuchElementException();
    }

    /**
     * Obtains a road based on direction.
     * @param dir Direction.
     * @return Road.
     * @throws NoSuchElementException If no road going that direction.
     */
    public Road getRoad(Direction dir) {
        Road[] roads = new Road[] {roadNS, roadSN, roadEW, roadWE};
        for (Road r : roads) {
            if (r.getDirection() == dir) {
                return r;
            }
        }
        throw new NoSuchElementException();
    }



    private static class Pair {
        public Road road1, road2;

        public Pair(Road r1, Road r2) {
            road1 = r1;
            road2 = r2;
        }

        @Override
        public boolean equals(Object obj) {
            assert road1 != null && road2 != null;
            if (obj != null && obj instanceof Pair) {
                Pair oth = (Pair)obj;
                assert oth.road1 != null && oth.road2 != null;
                return oth.road1.equals(road1) && oth.road2.equals(road2);
            }
            return false;
        }

        @Override
        public int hashCode() {
            assert road1 != null && road2 != null;
            return road1.hashCode() + road2.hashCode();
        }
    }
}
