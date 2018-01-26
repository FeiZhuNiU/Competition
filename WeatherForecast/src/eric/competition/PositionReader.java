package eric.competition;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PositionReader {
    private Position startPosition;
    private Map<Position, Integer> endPositionMap;

    public PositionReader(String fileName) {
        endPositionMap = new HashMap<>();
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
                endPositionMap.put(positionIntegerPair.getKey(), positionIntegerPair.getValue());
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

    public Map<Position, Integer> getEndPositionMap() {
        return endPositionMap;
    }

    public void setEndPositionMap(Map<Position, Integer> endPositionMap) {
        this.endPositionMap = endPositionMap;
    }
}
