package eric.competition.solution;

import eric.competition.ForecastReader;
import eric.competition.Position;
import eric.competition.PositionReader;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by Eric Yu on 2018/1/30.
 */
public class Solution4 extends Solution {

    private double[][][] dpMatrix;
    private double threshold;

    public Solution4(ForecastReader forecastReader, int maxStepEachSlice, PositionReader positionReader, double threshold) {
        super(forecastReader, maxStepEachSlice, positionReader);
        this.threshold = threshold;
    }

    @Override
    public void solveProblem() {
        if (startCol < 0
                || startRow < 0
                || startCol > colNum
                || startRow > rowNum) {
            return;
        }
        preProcess(0);
        int maxStep = maxStepEachSlice * hourNum;
        this.dpMatrix = new double[maxStep + 1][rowNum][colNum];
        for (int n = 0; n <= maxStep; ++n) {
            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    dpMatrix[0][i][j] = 1;
                }
            }
        }
        dpMatrix[1][startRow][startCol] = Math.log10(forecastReader.getForecastScore()[0][startRow][startCol]);

        boolean gameOver = true;

        for (int n = 2; n <= maxStep; ++n) {

            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {

                    if (!isBlockedInForecast[(n - 1) / maxStepEachSlice][i][j]) {
                        double up = ((i != 0 && dpMatrix[n - 1][i - 1][j] < 0) ? dpMatrix[n - 1][i - 1][j] : 1);
                        double down = ((i != rowNum - 1 && dpMatrix[n - 1][i + 1][j] < 0) ? dpMatrix[n - 1][i + 1][j] : 1);
                        double right = ((j != colNum - 1 && dpMatrix[n - 1][i][j + 1] < 0) ? dpMatrix[n - 1][i][j + 1] : 1);
                        double left = ((j != 0 && dpMatrix[n - 1][i][j - 1] < 0) ? dpMatrix[n - 1][i][j - 1] : 1);
                        double center = dpMatrix[n - 1][i][j] < 0 ? dpMatrix[n - 1][i][j] : 1;
                        double maxValue = Collections.max(Arrays.asList(
                                up > 0 ? Double.NEGATIVE_INFINITY : up,
                                down > 0 ? Double.NEGATIVE_INFINITY : down,
                                right > 0 ? Double.NEGATIVE_INFINITY : right,
                                left > 0 ? Double.NEGATIVE_INFINITY : left,
                                center > 0 ? Double.NEGATIVE_INFINITY : center));
                        dpMatrix[n][i][j] = maxValue == Double.NEGATIVE_INFINITY ? 1 : maxValue + Math.log10(forecastReader.getForecastScore()[(n - 1) / maxStepEachSlice][i][j]);
                    }
                }
            }

            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    if (dpMatrix[n][i][j] < 0) {
                        gameOver = false;
                        break;
                    }
                }
                if (!gameOver)
                    break;
            }

            if (gameOver)
                break;
        }

        for (Position position : targets) {
            List<Pair<Integer, Double>> availablePaths = new ArrayList<>();
            for (int n = 1; n <= maxStep; ++n) {

                if (dpMatrix[n][position.getRow()][position.getCol()] < 0) {
                    availablePaths.add(new Pair<>(n, dpMatrix[n][position.getRow()][position.getCol()] / n));
                }
            }
            int bestN = -1;
            int bestCandidate = -1;
            double candidateScore = Double.NEGATIVE_INFINITY;
            if (availablePaths.size() > 0) {

                for (Pair<Integer, Double> availablePath : availablePaths) {
                    if (availablePath.getValue() > candidateScore) {
                        candidateScore = availablePath.getValue();
                        bestCandidate = availablePath.getKey();
                    }

                    if (availablePath.getValue() > Math.log10(threshold)) {
                        bestN = availablePath.getKey();
                        break;
                    }
                }
            }

            if (bestN != -1 || bestCandidate != -1) {
                if (bestN == -1) {
                    bestN = bestCandidate;
                    System.out.println(positionReader.getEndPositionToIndexMap().get(position) + " candidate score: " + Math.pow(10, candidateScore));
                }
                Pair<List<Position>, Integer> curResult = getPath(position.getRow(), position.getCol(), bestN);
                results.put(position, curResult);
                System.out.println(positionReader.getEndPositionToIndexMap().get(position) + " has found path. Steps: " + curResult.getKey().size() );
            }
        }
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
            double bestScore = Double.NEGATIVE_INFINITY;


            double up = ((last_row != 0 && dpMatrix[n][last_row - 1][last_col] < 0) ? dpMatrix[n][last_row - 1][last_col] : Double.NEGATIVE_INFINITY);
            if (up > bestScore) {
                bestPosition = new Position(last_row - 1, last_col);
            }
            double down = ((last_row != rowNum - 1 && dpMatrix[n][last_row + 1][last_col] < 0) ? dpMatrix[n][last_row + 1][last_col] : Double.NEGATIVE_INFINITY);
            if (down > bestScore) {
                bestPosition = new Position(last_row + 1, last_col);
            }
            double left = ((last_col != 0 && dpMatrix[n][last_row][last_col - 1] < 0) ? dpMatrix[n][last_row][last_col - 1] : Double.NEGATIVE_INFINITY);
            if (left > bestScore) {
                bestPosition = new Position(last_row, last_col - 1);
            }
            double right = ((last_col != colNum - 1 && dpMatrix[n][last_row][last_col + 1] < 0) ? dpMatrix[n][last_row][last_col + 1] : Double.NEGATIVE_INFINITY);
            if (right > bestScore) {
                bestPosition = new Position(last_row, last_col + 1);
            }

            double center = dpMatrix[n][last_row][last_col] < 0 ? dpMatrix[n][last_row][last_col] : Double.NEGATIVE_INFINITY;
            if (center > bestScore) {
                bestPosition = new Position(last_row, last_col);
            }

            if (bestPosition != null) {
                path.add(bestPosition);
            } else {
                System.out.println("Something wrong during find path");
                break;
            }

            if (bestPosition.getRow() == startRow && bestPosition.getCol() == startCol) {
                firstN = n;
                if (firstN != 1) {
                    System.out.println("warning: firstN is not 1!!");
                }
                break;
            }

        }

        if (firstN == -1) {
            System.out.println("Something wrong during find the origin position");
        }
        Collections.reverse(path);
        return new Pair<>(path, firstN);
    }
}
