/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.api.model;

/**
 * Directions.
 * @author lawry
 */
public enum Direction {
    NORTH_SOUTH,
    SOUTH_NORTH,
    EAST_WEST,
    WEST_EAST;

    /**
     * Obtained the directions perpendicular to this.
     * @return Perp directions, always 2 elements.
     */
    public Direction[] getPerpendicular() {
        if (this == NORTH_SOUTH || this == SOUTH_NORTH) {
            return new Direction[] {EAST_WEST, WEST_EAST};
        } else {
            return new Direction[] {NORTH_SOUTH, SOUTH_NORTH};
        }
    }
}
