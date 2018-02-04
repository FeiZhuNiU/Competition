package eric.competition.solution;

import eric.competition.ForecastReader;
import eric.competition.Position;
import eric.competition.PositionReader;
import javafx.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eric Yu on 2018/1/30.
 */
public class Solution1 extends Solution {

    private boolean[][][] dpMatrix;

    public Solution1(ForecastReader forecastReader, int maxStepEachSlice, PositionReader positionReader) {
        super(forecastReader, maxStepEachSlice, positionReader);
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
        this.dpMatrix = new boolean[maxStepEachSlice * hourNum + 1][rowNum][colNum];
        int maxStep = maxStepEachSlice * hourNum;
        for (int i = 0; i < rowNum; ++i) {
            for (int j = 0; j < colNum; ++j) {
                dpMatrix[0][i][j] = false;
            }
        }
        dpMatrix[1][startRow][startCol] = true;

        boolean gameOver = true;
        for (int n = 2; n <= maxStep; ++n) {

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

            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    if (dpMatrix[n][i][j]) {
                        gameOver = false;
                        break;
                    }
                }
                if(!gameOver)
                    break;
            }

            if(gameOver)
                break;

            for (Position position : targets) {
                if (dpMatrix[n][position.getRow()][position.getCol()] && results.get(position) == null) {
                    Pair<List<Position>, Integer> curResult = getPath(position.getRow(), position.getCol(), n);
                    results.put(position, curResult);
                    System.out.println(positionReader.getEndPositionToIndexMap().get(position) + " has found path");
                }
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
}
