package eric.competition.solution;

import eric.competition.ForecastReader;
import eric.competition.Position;
import eric.competition.PositionReader;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by Eric Yu on 2018/1/30.
 */
public class Solution5 extends Solution {

    public Solution5(ForecastReader forecastReader, int maxStepEachSlice, PositionReader positionReader) {
        super(forecastReader, maxStepEachSlice, positionReader);
    }

    private int[][][] dpMatrix2;

    @Override
    public void solveProblem() {
        if (startCol < 0
                || startRow < 0
                || startCol > colNum
                || startRow > rowNum) {
            return;
        }
        preProcess(2);
        this.dpMatrix2 = new int[maxStepEachSlice * hourNum + 1][rowNum][colNum];
        int maxStep = maxStepEachSlice * hourNum;
        for (int n = 0; n <= maxStep; ++n) {
            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    dpMatrix2[n][i][j] = Integer.MAX_VALUE;
                }
            }
        }

        // <TargetPosition,    <startN, endN>>
        LinkedHashMap<Position, TreeMap<Integer, Integer>> allPossiblePathStartEndN = new LinkedHashMap<>();

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

            for (Position position : targets) {
                int curSteps = dpMatrix2[n][position.getRow()][position.getCol()];
                if (curSteps != Integer.MAX_VALUE) {
                    int firstN = n - curSteps + 1;

                    if (!allPossiblePathStartEndN.containsKey(position)) {
                        allPossiblePathStartEndN.put(position, new TreeMap<>());
                    }
                    //TODO to find optimal path
                    if (!allPossiblePathStartEndN.get(position).containsKey(firstN)) {
                        allPossiblePathStartEndN.get(position).put(firstN, n);
                    }
                }
//                int minStep = Integer.MAX_VALUE;
//                int lastN = Integer.MAX_VALUE;
//                for (int timePoint = maxStep; timePoint > 0; --timePoint) {
//                    int stepUsed = dpMatrix2[timePoint][position.getRow()][position.getCol()];
//                    if (stepUsed != Integer.MAX_VALUE) {
//                        minStep = Math.min(minStep, stepUsed);
//                        if (minStep == stepUsed) {
//                            lastN = timePoint;
//                        }
//                    }
//                }
//                if (minStep != Integer.MAX_VALUE && lastN != Integer.MAX_VALUE) {
//                    System.out.println(positionReader.getEndPositionToIndexMap().get(position) + " has found path");
//                    Pair<List<Position>, Integer> pathResult = getPath2(position.getRow(), position.getCol(), lastN, minStep);
//                    results.put(position, pathResult);
//                }
            }
        }
        // targetPosition, lastN
        Map<Position, Integer> pathInfos = choosePath(allPossiblePathStartEndN, 5);
        for (Map.Entry<Position, Integer> pathInfo : pathInfos.entrySet()) {

            Position target = pathInfo.getKey();
            Pair<List<Position>, Integer> path = getPath2(
                    target.getRow(),
                    target.getCol(),
                    pathInfo.getValue(),
                    dpMatrix2[pathInfo.getValue()][target.getRow()][target.getCol()]);
            results.put(target, path);
            System.out.println(positionReader.getEndPositionToIndexMap().get(target) + " has found path. " +
                    "StartN: " + (pathInfo.getValue() + 1 - dpMatrix2[pathInfo.getValue()][target.getRow()][target.getCol()]) +
                    " EndN: " + pathInfo.getValue() + " Steps: " + dpMatrix2[pathInfo.getValue()][target.getRow()][target.getCol()]);
        }
    }
    // position, lastN
    private Map<Position, Integer> choosePath(LinkedHashMap<Position, TreeMap<Integer, Integer>> allPossiblePathStartEndN, int minPrepareTime) {

        //TODO reorder allPossiblePathStartEndN
        // <firstFirstN, firstNLength>
        TreeMap<PossiblePathInfo, Position> reorderedInfo = new TreeMap<>();
        allPossiblePathStartEndN.forEach((k,v)->{
            reorderedInfo.put(new PossiblePathInfo(v.firstKey(), v.lastKey() - v.firstKey()), k);
        });

        LinkedHashMap<Position, TreeMap<Integer, Integer>> reorderedMap = new LinkedHashMap<>();
        for(Map.Entry<PossiblePathInfo, Position> tmp : reorderedInfo.entrySet()){
            reorderedMap.put(tmp.getValue(), allPossiblePathStartEndN.get(tmp.getValue()));
        }


        Map<Position, Integer> ret = new HashMap<>();
        int lastStartN = Integer.MIN_VALUE;
        for (Map.Entry<Position, TreeMap<Integer, Integer>> possiblePath_for_oneTarget : reorderedMap.entrySet()) {
            for (Map.Entry<Integer, Integer> solution : possiblePath_for_oneTarget.getValue().entrySet()) {
                if (solution.getKey() >= lastStartN + minPrepareTime) {
                    lastStartN = solution.getKey();
                    ret.put(possiblePath_for_oneTarget.getKey(), solution.getValue());
                    break;
                }
            }
        }
        return ret;
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
                float bestScore = forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col];
                if (bestScore > curScore) {
                    curScore = bestScore;
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

    class PossiblePathInfo implements Comparable<PossiblePathInfo>{
        int firstFirstN;
        int firstNLength;

        public PossiblePathInfo(int firstFirstN, int firstNLength) {
            this.firstFirstN = firstFirstN;
            this.firstNLength = firstNLength;
        }

        @Override
        public int compareTo(PossiblePathInfo o) {
            if (this.firstFirstN == o.firstFirstN)
                return this.firstNLength - o.firstNLength;
            return this.firstFirstN - o.firstFirstN;
        }
    }
}
