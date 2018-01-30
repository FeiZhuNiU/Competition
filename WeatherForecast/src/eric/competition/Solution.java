package eric.competition;


import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;


public class Solution {

    private int rowNum;
    private int colNum;
    private int hourNum;
    private int maxStepEachSlice;
    private int startCol;
    private int startRow;
    private List<Position> targets;
    private int day;
    private boolean hasFlied = false;
    private PositionReader positionReader;
    private ForecastReader forecastReader;

    public ForecastReader getForecastReader() {
        return forecastReader;
    }

    public void setForecastReader(ForecastReader forecastReader) {
        this.forecastReader = forecastReader;
    }

    public boolean isHasFlied() {
        return hasFlied;
    }

    public void setHasFlied(boolean hasFlied) {
        this.hasFlied = hasFlied;
    }

    public PositionReader getPositionReader() {
        return positionReader;
    }

    public void setPositionReader(PositionReader positionReader) {
        this.positionReader = positionReader;
    }

    // 0 means safe, 1 means dangerous
    private boolean isBlockedInForecast[][][];
    // can be optimized to 2 dimensions
    private boolean[][][] dpMatrix;

    private int[][][] dpMatrix2;

    //Integer: firstN
    private Map<Position, Pair<List<Position>, Integer>> results;


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

    public boolean[][][] getDpMatrix() {
        return dpMatrix;
    }

    public void setDpMatrix(boolean[][][] dpMatrix) {
        this.dpMatrix = dpMatrix;
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

    public void solveProblem2() {
        if (startCol < 0
                || startRow < 0
                || startCol > colNum
                || startRow > rowNum) {
            return;
        }
        preProcess(0);
        this.dpMatrix2 = new int[maxStepEachSlice * hourNum + 1][rowNum][colNum];
        int maxStep = maxStepEachSlice * hourNum;
        for (int n = 0; n <= maxStep; ++n) {
            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    dpMatrix2[n][i][j] = Integer.MAX_VALUE;
                }
            }
        }

        for (int n = 1; n <= maxStep; ++n) {
            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {

                    if (!isBlockedInForecast[(n - 1) / maxStepEachSlice][i][j]) {
                        if (i == startRow && j == startCol) {
                            dpMatrix2[n][i][j] = 1;
                        } else {
                            int up = ((i == 0) ? Integer.MAX_VALUE : dpMatrix2[n - 1][i - 1][j]);
                            int down = ((i == rowNum - 1) ? Integer.MAX_VALUE : dpMatrix2[n - 1][i + 1][j]);
                            int right = ((j == colNum - 1) ? Integer.MAX_VALUE : dpMatrix2[n - 1][i][j + 1]);
                            int left = ((j == 0) ? Integer.MAX_VALUE : dpMatrix2[n - 1][i][j - 1]);
                            int center = dpMatrix2[n - 1][i][j];
                            Integer min = Collections.min(Arrays.asList(up, down, right, left, center));
                            dpMatrix2[n][i][j] = (min == Integer.MAX_VALUE) ? Integer.MAX_VALUE : min + 1;
                        }
                    }
                }
            }
        }


        for (Position position : targets) {
            int minStep = Integer.MAX_VALUE;
            int lastN = Integer.MAX_VALUE;
            for (int timePoint = maxStep; timePoint > 0; --timePoint) {
                int stepUsed = dpMatrix2[timePoint][position.getRow()][position.getCol()];
                if (stepUsed != Integer.MAX_VALUE) {
                    minStep = Math.min(minStep, stepUsed);
                    if (minStep == stepUsed) {
                        lastN = timePoint;
                    }
                }
            }
            if (minStep != Integer.MAX_VALUE && lastN != Integer.MAX_VALUE) {
                System.out.println(positionReader.getEndPositionMap().get(position) + " has found path");
                Pair<List<Position>, Integer> pathResult = getPath2(position.getRow(), position.getCol(), lastN, minStep);
                results.put(position, pathResult);
            }
        }
    }

    public void solveProblem() {
        if (startCol < 0
                || startRow < 0
                || startCol > colNum
                || startRow > rowNum) {
            return;
        }
        preProcess(0);
        this.dpMatrix = new boolean[maxStepEachSlice * hourNum + 1][rowNum][colNum];
        int maxStep = maxStepEachSlice * hourNum;
        for (int i = 0; i < rowNum; ++i) {
            for (int j = 0; j < colNum; ++j) {
                dpMatrix[0][i][j] = false;
            }
        }

        for (int n = 1; n <= maxStep; ++n) {

            if (!hasFlied && !isBlockedInForecast[(n - 1) / maxStepEachSlice][startRow][startCol]) {
                dpMatrix[n][startRow][startCol] = true;
                hasFlied = true;
            } else {

                for (int i = 0; i < rowNum; ++i) {
                    for (int j = 0; j < colNum; ++j) {

                        if (isBlockedInForecast[(n - 1) / maxStepEachSlice][i][j]) {
                            dpMatrix[n][i][j] = false;
                        } else if (dpMatrix[n - 1][i][j]) {
                            dpMatrix[n][i][j] = true;
                        } else {
                            boolean up = (i != 0 && dpMatrix[n - 1][i - 1][j]);
                            boolean down = (i != rowNum - 1 && dpMatrix[n - 1][i + 1][j]);
                            boolean right = (j != colNum - 1 && dpMatrix[n - 1][i][j + 1]);
                            boolean left = (j != 0 && dpMatrix[n - 1][i][j - 1]);
                            dpMatrix[n][i][j] = up || right || down || left;
                        }


                    }
                }
                hasFlied = false;
                for (int i = 0; i < rowNum; ++i) {
                    for (int j = 0; j < colNum; ++j) {
                        if (dpMatrix[n][i][j]) {
                            hasFlied = true;
                            break;
                        }
                        if (hasFlied)
                            break;
                    }
                }
            }

            for (Position position : targets) {
                if (dpMatrix[n][position.getRow()][position.getCol()] && results.get(position) == null) {
                    Pair<List<Position>, Integer> curResult = getPath(position.getRow(), position.getCol(), n);
                    results.put(position, curResult);
                    System.out.println(positionReader.getEndPositionMap().get(position) + " has found path");
                }
            }
        }
    }

    private void preProcess(int region) {
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


    private Pair<List<Position>, Integer> getPath2(int targetRow, int targetCol, int lastN, int steps) {
        List<Position> path = new LinkedList<>();
        path.add(new Position(targetRow, targetCol));
        for (int n = lastN - 1; n > lastN - steps; --n) {
            Position last = path.get(path.size() - 1);
            int last_row = last.getRow();
            int last_col = last.getCol();
            Position bestPosition = null;
            float curScore = 0;
            if (dpMatrix2[n][last_row][last_col] == steps - (lastN - n)) {
                float score = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col];
                if (score > curScore) {
                    curScore = score;
                    bestPosition = new Position(last_row, last_col);
                }
            } else if (last_row > 0 && dpMatrix2[n][last_row - 1][last_col] == steps - (lastN - n)) {
                float score = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row - 1][last_col];
                if (score > curScore) {
                    curScore = score;
                    bestPosition = new Position(last_row - 1, last_col);
                }
            } else if (last_row < rowNum - 1 && dpMatrix2[n][last_row + 1][last_col] == steps - (lastN - n)) {
                float score = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row + 1][last_col];
                if (score > curScore) {
                    curScore = score;
                    bestPosition = new Position(last_row + 1, last_col);
                }
            } else if (last_col > 0 && dpMatrix2[n][last_row][last_col - 1] == steps - (lastN - n)) {
                float score = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col - 1];
                if (score > curScore) {
                    curScore = score;
                    bestPosition = new Position(last_row, last_col - 1);
                }
            } else if (last_col < colNum - 1 && dpMatrix2[n][last_row][last_col + 1] == steps - (lastN - n)) {
                float score = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col + 1];
                if (score > curScore) {
                    curScore = score;
                    bestPosition = new Position(last_row, last_col + 1);
                }
            }
            if (bestPosition != null) {
                path.add(bestPosition);
            } else {
                System.out.println("Something wrong when find path");
                break;
            }
        }
        Collections.reverse(path);
        if (path.get(0).getRow() != startRow || path.get(0).getCol() != startCol) {
            System.out.println("Something wrong when find path");
        }
        return new Pair<>(path, lastN - steps + 1);
    }


    private Pair<List<Position>, Integer> getPath(int targetRow, int targetCol, int lastN) {

        List<Position> path = new LinkedList<>();
        int firstN = -1;
        path.add(new Position(targetRow, targetCol));
        for (int n = lastN - 1; n > 0; --n) {
            Position last = path.get(path.size() - 1);
            int last_row = last.getRow();
            int last_col = last.getCol();
            Position bestPosition = null;
            if (dpMatrix[n][last_row][last_col]) {
                path.add(new Position(last_row, last_col));
            } else {
                float bestScore = -1;
                if (last_row > 0 && dpMatrix[n][last_row - 1][last_col]) {
                    float upScore = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row - 1][last_col];
                    if (upScore > bestScore) {
                        bestScore = upScore;
                        bestPosition = new Position(last_row - 1, last_col);
                    }
                }

                if (last_row < rowNum - 1 && dpMatrix[n][last_row + 1][last_col]) {
                    float downScore = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row + 1][last_col];
                    if (downScore > bestScore) {
                        bestScore = downScore;
                        bestPosition = new Position(last_row + 1, last_col);
                    }
                }

                if (last_col > 0 && dpMatrix[n][last_row][last_col - 1]) {
                    float leftScore = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col - 1];
                    if (leftScore > bestScore) {
                        bestScore = leftScore;
                        bestPosition = new Position(last_row, last_col - 1);
                    }
                }

                if (last_row < colNum - 1 && dpMatrix[n][last_row][last_col + 1]) {
                    float rightScore = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col + 1];
                    if (rightScore > bestScore) {
                        bestScore = rightScore;
                        bestPosition = new Position(last_row, last_col + 1);
                    }
                }
                if (bestPosition != null) {
                    path.add(bestPosition);
                } else {
                    System.out.println("Something wrong during find path");
                    break;
                }
            }
            if (bestPosition != null && bestPosition.getRow() == startRow && bestPosition.getCol() == startCol) {
                firstN = n;
                break;
            }

        }

        if (firstN == -1) {
            System.out.println("Something wrong during find the origin position");
        }
        Collections.reverse(path);
        return new Pair<>(path, firstN);
    }

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


