package eric.competition.solution;


import eric.competition.ForecastReader;
import eric.competition.Position;
import eric.competition.PositionReader;
import eric.competition.ResultUtils;
import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;


public abstract class Solution {

    protected int rowNum;
    protected int colNum;
    protected int hourNum;
    protected int maxStepEachSlice;
    protected int startCol;
    protected int startRow;
    protected List<Position> targets;
    protected int day;

    protected PositionReader positionReader;
    protected ForecastReader forecastReader;

    // 0 means safe, 1 means dangerous
    protected boolean isBlockedInForecast[][][];

    //Integer: firstN
    protected Map<Position, Pair<List<Position>, Integer>> results;


    public Solution(
            ForecastReader forecastReader,
            int maxStepEachSlice,
            PositionReader positionReader) {

        this.startCol = positionReader.getStartPosition().getCol();
        this.startRow = positionReader.getStartPosition().getRow();
        this.isBlockedInForecast = forecastReader.getForecast();
        this.hourNum = isBlockedInForecast.length;
        this.rowNum = isBlockedInForecast[0].length;
        this.colNum = isBlockedInForecast[0][0].length;
        this.maxStepEachSlice = maxStepEachSlice;
        this.day = forecastReader.getDay();
        this.targets = new ArrayList<>(positionReader.getEndPositionMap().keySet());
        this.results = new HashMap<>();
        this.positionReader = positionReader;
        this.forecastReader = forecastReader;

    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public List<Position> getTargets() {
        return targets;
    }

    public void setTargets(List<Position> targets) {
        this.targets = targets;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public int getHourNum() {
        return hourNum;
    }

    public void setHourNum(int hourNum) {
        this.hourNum = hourNum;
    }

    public boolean[][][] getIsBlockedInForecast() {
        return isBlockedInForecast;
    }

    public void setIsBlockedInForecast(boolean[][][] isBlockedInForecast) {
        this.isBlockedInForecast = isBlockedInForecast;
    }

    public int getMaxStepEachSlice() {
        return maxStepEachSlice;
    }

    public void setMaxStepEachSlice(int maxStepEachSlice) {
        this.maxStepEachSlice = maxStepEachSlice;
    }

    public Map<Position, Pair<List<Position>, Integer>> getResults() {
        return results;
    }

    public void setResults(Map<Position, Pair<List<Position>, Integer>> results) {
        this.results = results;
    }
    public ForecastReader getForecastReader() {
        return forecastReader;
    }

    public void setForecastReader(ForecastReader forecastReader) {
        this.forecastReader = forecastReader;
    }

    public PositionReader getPositionReader() {
        return positionReader;
    }

    public void setPositionReader(PositionReader positionReader) {
        this.positionReader = positionReader;
    }

    protected void preProcess(int region) {
        boolean[][][] temp = new boolean[isBlockedInForecast.length][isBlockedInForecast[0].length][isBlockedInForecast[0][0].length];
        for (int n = 0; n < isBlockedInForecast.length; ++n) {
            for (int i = 0; i < isBlockedInForecast[0].length; ++i) {
                for (int j = 0; j < isBlockedInForecast[0][0].length; ++j) {
                    temp[n][i][j] = false;
                }
            }
        }
        for (int n = 0; n < isBlockedInForecast.length; ++n) {
            for (int i = 0; i < isBlockedInForecast[0].length; ++i) {
                for (int j = 0; j < isBlockedInForecast[0][0].length; ++j) {
                    if (isBlockedInForecast[n][i][j]) {
                        for (int l = -region; l <= region; ++l) {
                            for (int m = -region; m <= region; ++m) {
                                if (i + l >= 0
                                        && i + l < isBlockedInForecast[0].length
                                        && j + m >= 0
                                        && j + m < isBlockedInForecast[0][0].length
                                        ) {
                                    temp[n][i + l][j + m] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        isBlockedInForecast = temp;
    }

    abstract public void solveProblem();

    public void printResult(String fileName, PositionReader positionReader) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            Set<Map.Entry<Position, Pair<List<Position>, Integer>>> entrySet = results.entrySet();

            for (Map.Entry<Position, Pair<List<Position>, Integer>> entry : entrySet) {

                int startTimeN = entry.getValue().getValue();
                for (int i = 0; i < entry.getValue().getKey().size(); ++i) {
                    writer.write(ResultUtils.toCSVLine(
                            day,
                            positionReader.getEndPositionMap().get(entry.getKey()),
                            i + startTimeN,
                            entry.getValue().getKey().get(i)));
                    writer.newLine();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


