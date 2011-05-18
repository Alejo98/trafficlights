/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Units of road index from 0 to (length - 1).
 * @author lawry
 */
public class Road {
    private Map<Car,Integer> cars;
    private Direction d;
    private int length;

    /**
     * Creates a new road with no cars on it.
     * @param dir Direction of road flow.
     * @param len Length of road. (100 according to the assignment spec.)
     */
    public Road(Direction dir, int len) {
        assert len > 0;
        d = dir;
        length = len;
        cars = new HashMap<Car, Integer>();
    }

    /**
     * @return Length of road.
     */
    public int getLength() {
        return length;
    }

    /**
     * @return The flow of this road.
     */
    public Direction getDirection() {
        return d;
    }

    /**
     * Positions are from 0 to (length - 1).
     * @param c The car to add.
     * @param position The position to add the car.
     * @throws IllegalArgumentException If the car is already on this road
     * or if there is already a car at the given position.
     */
    public void addCar(Car c, int position) {
        assert 0 <= position && position < length;
        if (cars.containsKey(c)) {
            throw new IllegalArgumentException("Car is already on this road.");
        }
        if (cars.containsValue(position)) {
            throw new IllegalArgumentException("There is already a car at the given position.");
        }
        cars.put(c, position);
    }

    /**
     * Move a car to a new position on this road.
     * @param c Car to move, must exist on this road.
     * @param position Position from 0 to (length - 1).
     * @throws IllegalArgumentException If the car is not on this road
     * or if there is already a car at the given position.
     */
    public void moveCar(Car c, int position) {
        assert 0 <= position && position < length;
        if (!cars.containsKey(c)) {
            throw new IllegalArgumentException("Car is not on this road.");
        }
        if (cars.containsValue(position)) {
            throw new IllegalArgumentException("There is already a car at the given position.");
        }
        cars.put(c, position);
    }

    /**
     * If the given car doesn't exist, this method does nothing.
     * @param c Remove this car from this road.
     */
    public void removeCar(Car c) {
        if (cars.containsKey(c)) {
            cars.remove(c);
        }
    }

    /**
     * Returns a set view of the cars on this road. The set is backed by
     * this road, so changes to the road are reflected in the set, and
     * vice-versa. If the road is modified while an iteration over the
     * set is in progress (except through the iterator's own remove
     * operation), the results of the iteration are undefined.
     * The set supports element removal, which removes the corresponding
     * car from this road (similar to {@link #removeCar(tf.api.Car) }).
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * @return All of the cars currently on this road.
     */
    public Set<Car> getCars() {
        return cars.keySet();
    }

    /**
     * @param c The car must be on this road already.
     * @return The position of the car. Position is from 0 to
     * (length - 1).
     * @throws IllegalArgumentException If the car given is not on this road.
     */
    public int carPosition(Car c) {
        if (cars.containsKey(c)) {
            return cars.get(c);
        }
        throw new IllegalArgumentException("No such car on this road.");
    }
}
