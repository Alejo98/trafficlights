/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.api.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Units of road index from 0 to (length - 1).
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
     * car from this road (similar to {@link #removeCar(tf.api.model.Car) }).
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * @return All of the cars currently on this road.
     */
    public Set<Car> getCars() {
        return cars.keySet();
    }
    
    
    /**
     * @return an array of cars on this road, in ascendant order by the position
     */
    public Car[] getCarsInOrder() {
    	Car[] array = new Car[cars.size()];
    	int[] positions = new int[cars.size()];
    	Iterator<Car> it = this.getCars().iterator();

    	for(int total = 0; it.hasNext(); total++) {
    		Car curr = it.next();
    		int pos = cars.get(curr);
    		int i = total-1;
    		for(; i>=0 && positions[i]>pos; i--) {
    			array[i+1] = array[i];
    			positions[i+1] = positions[i];
    		}
    		array[i+1] = curr;
    		positions[i+1] = pos;
    	}
    	
    	return array;
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


	/**
	 * Obtains the position of the first car that is before the given position
	 * 
	 * @param beforePosition the position of the car should be equal to, or smaller than it.
	 * @return the position of the car that is the closest to, but before the given position.
	 * The returned value is between 0 to 8, 9 if no cars.
	 */
	public int positionOfClosestCar(int beforePosition) {
		int minPos = 9;
		Iterator<Car> it = getCars().iterator();
		while(it.hasNext()) {
			int position = cars.get(it.next());
			if(position <= beforePosition 
					&& beforePosition - position < minPos) {
				minPos = beforePosition - position;
			}
		}
		return minPos;
	}
	

	/**
	 * @param position the car position on the road
	 * @return true if the position is occupied by a car; false otherwise.
	 */
	public boolean isOccupied(int position) {
		return cars.containsValue(position);
	}
	
}
