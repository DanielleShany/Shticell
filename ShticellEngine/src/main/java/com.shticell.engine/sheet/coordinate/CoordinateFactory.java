package com.shticell.engine.sheet.coordinate;

import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory {

    private static final Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static Coordinate createCoordinate(String cellId){
       int[] index =  CoordinateFormatter.cellIdToIndex(cellId);
       return createCoordinate(index[0], index[1]);
    }
    public static Coordinate createCoordinate(int row, int column) {

        String key = row + ":" + column;
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CoordinateImpl coordinate = new CoordinateImpl(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }
}
