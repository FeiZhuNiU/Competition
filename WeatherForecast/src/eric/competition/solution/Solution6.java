package eric.competition.solution;

import eric.competition.ForecastReader;
import eric.competition.Position;
import eric.competition.PositionReader;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by Eric Yu on 2018/1/30.
 */
public class Solution6 extends Solution {

    public Solution6(ForecastReader forecastReader, int maxStepEachSlice, PositionReader positionReader) {
        super(forecastReader, maxStepEachSlice, positionReader);
    }

    private boolean[][][] dpMatrix;

    @Override
    public void solveProblem() {
        if (startCol < 0
                || startRow < 0
                || startCol > colNum
                || startRow > rowNum) {
            return;
        }
        preProcess(1);
        this.dpMatrix = new boolean[maxStepEachSlice * hourNum + 1][rowNum][colNum];
        int maxStep = maxStepEachSlice * hourNum;
        for (int n = 0; n <= maxStep; ++n) {
            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    dpMatrix[n][i][j] = false;
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
                            dpMatrix[n][i][j] = true;
                        } else {
                            boolean up = ((i != 0) && dpMatrix[n - 1][i - 1][j]);
                            boolean down = ((i != rowNum - 1) && dpMatrix[n - 1][i + 1][j]);
                            boolean right = ((j != colNum - 1) && dpMatrix[n - 1][i][j + 1]);
                            boolean left = ((j != 0) && dpMatrix[n - 1][i][j - 1]);
                            boolean center = dpMatrix[n - 1][i][j];
                            dpMatrix[n][i][j] = up || down || right || left || center;
                        }
                    }
                }
            }

            for (Position position : targets) {

                if (dpMatrix[n][position.getRow()][position.getCol()]) {
                    Pair<List<Position>, Integer> path = getPath(position.getRow(), position.getCol(), n);
                    int firstN = path.getValue();
                    if (firstN != -1) {
                        if (!allPossiblePathStartEndN.containsKey(position)) {
                            allPossiblePathStartEndN.put(position, new TreeMap<>());
                        }
                        //TODO to find optimal path
                        if (!allPossiblePathStartEndN.get(position).containsKey(firstN)) {
                            allPossiblePathStartEndN.get(position).put(firstN, n);
                        }
                    }
                }
            }
        }

        // targetPosition, lastN
        Map<Position, Integer> pathInfos = choosePath(allPossiblePathStartEndN, 5);

        for (Map.Entry<Position, Integer> pathInfo : pathInfos.entrySet()) {

            Position target = pathInfo.getKey();
            Pair<List<Position>, Integer> path = getPath(
                    target.getRow(),
                    target.getCol(),
                    pathInfo.getValue());
            results.put(target, path);
            System.out.println(positionReader.getEndPositionToIndexMap().get(target) + " has found path. " +
                    "StartN: " + path.getValue() +
                    " EndN: " + pathInfo.getValue() +
                    " Steps: " + path.getKey().size() +
                    " " + (path.getValue() == pathInfo.getValue() + 1 - path.getKey().size()));
        }
    }

    // position, lastN
    private Map<Position, Integer> choosePath(LinkedHashMap<Position, TreeMap<Integer, Integer>> allPossiblePathStartEndN, int minPrepareTime) {

        //TODO reorder allPossiblePathStartEndN
        // <firstFirstN, firstNLength>
        TreeMap<PossiblePathInfo, Position> reorderedInfo = new TreeMap<>();
        allPossiblePathStartEndN.forEach((k, v) -> {
            reorderedInfo.put(new PossiblePathInfo(v.firstKey(), v.lastKey() - v.firstKey()), k);
        });

        LinkedHashMap<Position, TreeMap<Integer, Integer>> reorderedMap = new LinkedHashMap<>();
        for (Map.Entry<PossiblePathInfo, Position> tmp : reorderedInfo.entrySet()) {
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

    //<list, firstN>
    private Pair<List<Position>, Integer> getPath(int targetRow, int targetCol, int lastN) {

        List<Position> path = new LinkedList<>();
        int firstN = -1;
        path.add(new Position(targetRow, targetCol));
        for (int n = lastN - 1; n > 0; --n) {
            Position last = path.get(path.size() - 1);
            int last_row = last.getRow();
            int last_col = last.getCol();
            Position bestPosition = null;
            double bestScore = Double.NEGATIVE_INFINITY;


            double up = ((last_row != 0 && dpMatrix[n][last_row - 1][last_col]) ? forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row - 1][last_col] : Double.NEGATIVE_INFINITY);
            if (up > bestScore) {
                bestPosition = new Position(last_row - 1, last_col);
                bestScore = up;
            }
            double down = ((last_row != rowNum - 1 && dpMatrix[n][last_row + 1][last_col]) ? forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row + 1][last_col] : Double.NEGATIVE_INFINITY);
            if (down > bestScore) {
                bestPosition = new Position(last_row + 1, last_col);
                bestScore = down;
            }
            double left = ((last_col != 0 && dpMatrix[n][last_row][last_col - 1]) ? forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col - 1] : Double.NEGATIVE_INFINITY);
            if (left > bestScore) {
                bestPosition = new Position(last_row, last_col - 1);
                bestScore = left;
            }
            double right = ((last_col != colNum - 1 && dpMatrix[n][last_row][last_col + 1]) ? forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col + 1] : Double.NEGATIVE_INFINITY);
            if (right > bestScore) {
                bestPosition = new Position(last_row, last_col + 1);
                bestScore = right;
            }

            if (bestScore == Double.NEGATIVE_INFINITY) {
                double center = dpMatrix[n][last_row][last_col] ? forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][last_row][last_col] : Double.NEGATIVE_INFINITY;
                if (center > bestScore) {
                    bestPosition = new Position(last_row, last_col);
                    bestScore = center;
                }
            }

            if (bestPosition != null) {
                path.add(bestPosition);
            } else {
                System.out.println("Something wrong during find path");
                break;
            }

            if (bestPosition.getRow() == startRow && bestPosition.getCol() == startCol) {
                firstN = n;
                break;
            }

        }

        if (firstN == -1) {
            System.out.println("Something wrong during find the origin position");
            return new Pair<>(null, -1);
        }
        Collections.reverse(path);
        return new Pair<>(path, firstN);
    }

    class PossiblePathInfo implements Comparable<PossiblePathInfo> {
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
