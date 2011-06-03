/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.gui;

import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

import tf.api.model.Car;
import tf.api.model.Direction;
import tf.api.model.Road;
import tf.api.model.TrafficModel;

/**
 * An object of this class can manage multiple traffic models.
 * However, <b>all</b> traffic models must have the same set of
 * roads (the car arrangements can change). Supports either
 * 2 roads or 4 roads only.
 * @author lawry
 */
public class TfTableModel extends AbstractTableModel {
    public static final char CAR_EW = '<';
    public static final char CAR_NS = '\u2228';
    public static final char CAR_SN = '\u2227';
    public static final char CAR_WE = '>';
    public static final char SPACE_EMPTY = ' ';
    public static final char SPACE_KABOOM = 'X';

    /**
     * The current traffic model.
     */
    private TrafficModel tfModel;
    /**
     * On demand traffic grid based on tfModel.
     */
    private char[][] trafficGrid;


    /**
     * The columns and rows of this table will be fixed according
     * to this initial traffic model.
     * @param tfm The initial traffic model.
     */
    public TfTableModel(TrafficModel tfm) {
        tfModel = tfm;
    }

    /**
     * Called when traffic pattern has changed.
     * Also calls fireTableDataChanged() method before returning.
     * @param tfm Must have the same road and intersection structure
     * as the initial traffic model. Otherwise behaviour is undefined.
     */
    public void update(TrafficModel tfm) {
        tfModel = tfm;
        trafficGrid = null; // So will be recalculated again.
        fireTableDataChanged();
    }

    /**
     * @return Width (or x-axis) of this table.
     */
    public int getColumnCount() {
        return getColumnOrRowCount(Direction.EAST_WEST, Direction.WEST_EAST, Direction.NORTH_SOUTH);
    }

    /**
     * @return Height (or y-axis) of this table.
     */
    public int getRowCount() {
        return getColumnOrRowCount(Direction.NORTH_SOUTH, Direction.SOUTH_NORTH, Direction.EAST_WEST);
    }

    /**
     * The same algorithm can be used for row count or column count.
     * @param dirEW East-west or north-south.
     * @param dirWE West-east or south-north.
     * @param dirNS One of the cross directions, so north-south or east-west.
     * @return Column or row count.
     */
    private int getColumnOrRowCount(Direction dirEW, Direction dirWE, Direction dirNS) {
        // EW + WE roads.
        Road rdew = null, rdwe = null;
        try {
            rdew = tfModel.getRoad(dirEW);
        } catch (NoSuchElementException n) {
        }
        try {
            rdwe = tfModel.getRoad(dirWE);
        } catch (NoSuchElementException n) {
        }

        // 2 roads, easy.
        if (rdew == null) {
            return rdwe.getLength();
        } else if (rdwe == null) {
            return rdew.getLength();
        }

        // 4 roads, need to work out the intersection using one of
        // the cross roads.
        Road rdX = tfModel.getRoad(dirNS);
        int ewPos = tfModel.getIntersectPosition(rdew, rdX);
        int wePos = tfModel.getIntersectPosition(rdwe, rdX);
        return Math.max(ewPos, rdwe.getLength() - wePos - 1)
                + Math.max(wePos, rdew.getLength() - ewPos - 1)
                + 1; // +1 for the intersection itself.
    }

    /**
     * Car or road.
     * @param rowIndex
     * @param columnIndex
     * @return the object of Car or Road.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (trafficGrid == null) {
            trafficGrid = calcGrid();
        }
        return trafficGrid[columnIndex][rowIndex];
    }

    /**
     * Uses the column index.
     * @param column
     * @return the column name
     */
    @Override
    public String getColumnName(int column) {
        return Integer.toString(column);
    }

    /**
     * Turns a traffic model into a grid.
     * @return Only works with 4 roads for now.
     */
    private char[][] calcGrid() {
        int xCount = getColumnCount();
        int yCount = getRowCount();
        char[][] grid = new char[xCount][yCount];

        // Set everything to empty first.
        for (int x = 0; x < xCount; ++x) {
            for (int y = 0; y < yCount; ++y) {
                grid[x][y] = SPACE_EMPTY;
            }
        }

        // Which road is against the walls?
        boolean weAtX0, nsAtY0;
        int offsetHoriz, offsetVert;

        // Test EW + WE roads.
        Road rdew = null, rdwe = null;
        rdew = tfModel.getRoad(Direction.EAST_WEST);
        rdwe = tfModel.getRoad(Direction.WEST_EAST);

        // 4 roads, need to work out the intersection using one of
        // the cross roads.
        Road rdX = tfModel.getRoad(Direction.NORTH_SOUTH);
        int ewPos = tfModel.getIntersectPosition(rdew, rdX);
        int wePos = tfModel.getIntersectPosition(rdwe, rdX);

        offsetHoriz = wePos - (rdew.getLength() - ewPos - 1);
        if (offsetHoriz > 0) {
            // WE road is against x=0.
            weAtX0 = true;
        } else {
            // EW (or both) road is against x=0.
            weAtX0 = false;
            offsetHoriz *= -1; // in case ew is at x0.
        }
        
        // Test NS + SN roads.
        Road rdns = null, rdsn = null;
        rdns = tfModel.getRoad(Direction.NORTH_SOUTH);
        rdsn = tfModel.getRoad(Direction.SOUTH_NORTH);

        // 4 roads, need to work out the intersection using one of
        // the cross roads.
        rdX = tfModel.getRoad(Direction.EAST_WEST);
        int nsPos = tfModel.getIntersectPosition(rdns, rdX);
        int snPos = tfModel.getIntersectPosition(rdsn, rdX);

        offsetVert = nsPos - (rdsn.getLength() - snPos - 1);
        if (offsetVert > 0) {
            // NS road is against y=0.
            nsAtY0 = true;
        } else {
            // SN (or both) road is against y=0.
            nsAtY0 = false;
            offsetVert *= -1; // in case sn is at y0.
        }

        // Do based on wall position.
        if (weAtX0 && nsAtY0) {
            int pos = tfModel.getIntersectPosition(rdns, rdwe);
            updateGridWENS(grid, rdwe, pos, 0/*offset*/, true/*isHoriz*/);
            pos = tfModel.getIntersectPosition(rdwe, rdns);
            updateGridWENS(grid, rdns, pos, 0, false/*isHoriz*/);
            //
            pos = tfModel.getIntersectPosition(rdns, rdew);
            updateGridEWSN(grid, rdew, pos, offsetHoriz, true/*isHoriz*/);
            pos = tfModel.getIntersectPosition(rdwe, rdsn);
            updateGridEWSN(grid, rdsn, pos, offsetVert, false/*isHoriz*/);
        } else if (weAtX0 && !nsAtY0) {
            int pos = rdsn.getLength()- 1 - tfModel.getIntersectPosition(rdsn, rdwe);
            updateGridWENS(grid, rdwe, pos, 0/*offset*/, true/*isHoriz*/);
            pos = tfModel.getIntersectPosition(rdwe, rdns);
            updateGridWENS(grid, rdns, pos, offsetVert, false/*isHoriz*/);
            //
            pos = rdsn.getLength() - 1 - tfModel.getIntersectPosition(rdsn, rdew);
            updateGridEWSN(grid, rdew, pos, offsetHoriz, true/*isHoriz*/);
            pos = tfModel.getIntersectPosition(rdwe, rdsn);
            updateGridEWSN(grid, rdsn, pos, 0, false/*isHoriz*/);
        } else if (!weAtX0 && nsAtY0) {
            int pos = tfModel.getIntersectPosition(rdns, rdwe);
            updateGridWENS(grid, rdwe, pos, offsetHoriz, true/*isHoriz*/);
            pos = rdew.getLength() - 1 - tfModel.getIntersectPosition(rdew, rdns);
            updateGridWENS(grid, rdns, pos, 0, false/*isHoriz*/);
            //
            pos = tfModel.getIntersectPosition(rdns, rdew);
            updateGridEWSN(grid, rdew, pos, 0, true/*isHoriz*/);
            pos = rdew.getLength() - 1 - tfModel.getIntersectPosition(rdew, rdsn);
            updateGridEWSN(grid, rdsn, pos, offsetVert, false/*isHoriz*/);
        } else { // both not.
            int pos = rdsn.getLength() - 1 - tfModel.getIntersectPosition(rdsn, rdwe);
            updateGridWENS(grid, rdwe, pos, offsetHoriz, true/*isHoriz*/);
            pos = rdew.getLength() - 1 - tfModel.getIntersectPosition(rdew, rdns);
            updateGridWENS(grid, rdns, pos, offsetVert, false/*isHoriz*/);
            //
            pos = rdsn.getLength() - 1 - tfModel.getIntersectPosition(rdsn, rdew);
            updateGridEWSN(grid, rdew, pos, 0, true/*isHoriz*/);
            pos = rdew.getLength() - 1 - tfModel.getIntersectPosition(rdew, rdsn);
            updateGridEWSN(grid, rdsn, pos, 0, false/*isHoriz*/);
        }

        return grid;
    }

    private void updateGridEWSN(char[][] grid, Road rd, int pos, int offset, boolean isHoriz) {
        Set<Car> allCars = rd.getCars();
        for (Car car : allCars) {
            int carPos = rd.carPosition(car);
            if (isHoriz) { // EW
                // pos is Y. offset is X.
                updateGridWithCar(grid, offset + (rd.getLength() - carPos - 1),
                        pos, CAR_EW);
            } else { // SN
                // pos is X. offset is Y.
                updateGridWithCar(grid, pos,
                        offset + (rd.getLength() - carPos - 1), CAR_SN);
            }
        }
    }

    private void updateGridWENS(char[][] grid, Road rd, int pos, int offset, boolean isHoriz) {
        Set<Car> allCars = rd.getCars();
        for (Car car : allCars) {
            int carPos = rd.carPosition(car);
            if (isHoriz) { // WE
                // pos is Y. offset is X.
                updateGridWithCar(grid, offset + carPos, pos, CAR_WE);
            } else { // NS
                // pos is X. offset is Y.
                updateGridWithCar(grid, pos, offset + carPos, CAR_NS);
            }
        }
    }

    /**
     * If the given grid position is empty (i.e. has SPACE_EMPTY) then
     * it is assigned the car symbol. If it is not empty but not a digit,
     * then it must be a car, and it is updated to a digit to indicate
     * how many cars it is in the same space. Digits gets incremented when
     * more cars pile up.
     * @param grid The visual grid.
     * @param finalX Position on grid.
     * @param finalY Position on grid.
     * @param car Car symbol, cannot be a digit. Indicates which way the
     * car is going.
     */
    protected void updateGridWithCar(char[][] grid, int finalX, int finalY, char car) {
        char symbol = grid[finalX][finalY];
        if (symbol != SPACE_EMPTY) {
            // There is a car here already. Or several cars if there is more
            // than 1 car already (a pile up)!
            try {
                int count = Integer.parseInt(Character.toString(symbol));
                ++count;
                if (count < 10)
                    grid[finalX][finalY] = Integer.toString(count).charAt(0);
                else
                    grid[finalX][finalY] = SPACE_KABOOM; // More than 10 cars!!!!!
            } catch (NumberFormatException ex) {
                if (symbol == SPACE_KABOOM)
                    return; // already too many cars.
                // Must be a car symbol before.
                grid[finalX][finalY] = '2';
            }
        } else {
            grid[finalX][finalY] = car; // a single car.
        }
    }
}
