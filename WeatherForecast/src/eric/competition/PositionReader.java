package eric.competition;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PositionReader {
    private Position startPosition;
    private Map<Position, Integer> endPositionToIndexMap;
    private Map<Integer, Position> indexToEndPositionMap;

    public PositionReader(String fileName) {
        endPositionToIndexMap = new HashMap<>();
        indexToEndPositionMap = new HashMap<>();
        try {
            BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));
            String line;
            // skip header
            line = file.readLine();
            // startPosition
            line = file.readLine();
            startPosition = Position.fromCSVLine(line).getKey();

            while ((line = file.readLine()) != null) {
                Pair<Position, Integer> positionIntegerPair = Position.fromCSVLine(line);
                endPositionToIndexMap.put(positionIntegerPair.getKey(), positionIntegerPair.getValue());
                indexToEndPositionMap.put(positionIntegerPair.getValue(), positionIntegerPair.getKey());
            }

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Map<Integer, Position> getIndexToEndPositionMap() {
        return indexToEndPositionMap;
    }

    public void setIndexToEndPositionMap(Map<Integer, Position> indexToEndPositionMap) {
        this.indexToEndPositionMap = indexToEndPositionMap;
    }

    public Map<Position, Integer> getEndPositionToIndexMap() {
        return endPositionToIndexMap;
    }

    public void setEndPositionToIndexMap(Map<Position, Integer> endPositionToIndexMap) {
        this.endPositionToIndexMap = endPositionToIndexMap;
    }
}
