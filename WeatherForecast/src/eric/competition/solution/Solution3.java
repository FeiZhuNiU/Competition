package eric.competition.solution;

import eric.competition.ForecastReader;
import eric.competition.PositionReader;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eric Yu on 2018/1/30.
 */
public class Solution3 extends Solution {

    private List<Pair<Integer, Double>>[][][] dpMatrix3;

    public Solution3(ForecastReader forecastReader, int maxStepEachSlice, PositionReader positionReader) {
        super(forecastReader, maxStepEachSlice, positionReader);
    }

    @Override
    public void solveProblem() {
        preProcess(0);

        int maxStep = maxStepEachSlice * hourNum;


        this.dpMatrix3 = new List[maxStep + 1][rowNum][colNum];

        for (int n = 0; n <= maxStep; ++n) {
            for (int i = 0; i < rowNum; ++i) {
                for (int j = 0; j < colNum; ++j) {
                    dpMatrix3[n][i][j] = new ArrayList<>();
                }
            }
        }

//        for (int n = 1; n <= maxStep; ++n) {
//            for (int i = 0; i < rowNum; ++i) {
//                for (int j = 0; j < colNum; ++j) {
//
//                    if (!isBlockedInForecast[(n - 1) / maxStepEachSlice][i][j]) {
//                        if (i == startRow && j == startCol) {
//                            dpMatrix3[n][i][j].add(new Pair<>(1, 1.0));
//                        } else {
//                            List<Pair<Integer, Double>> up = ((i == 0) ? new ArrayList<>() : dpMatrix3[n - 1][i - 1][j]);
//
//                            List<Pair<Integer, Double>> down = ((i == rowNum - 1) ? new ArrayList<>() : dpMatrix3[n - 1][i + 1][j]);
//                            List<Pair<Integer, Double>> right = ((j == colNum - 1) ? new ArrayList<>() : dpMatrix3[n - 1][i][j + 1]);
//                            List<Pair<Integer, Double>> left = ((j == 0) ? new ArrayList<>() : dpMatrix3[n - 1][i][j - 1]);
//                            List<Pair<Integer, Double>> center = dpMatrix3[n - 1][i][j];
//                            Integer min = Collections.min(Arrays.asList(up, down, right, left, center));
//                            dpMatrix2[n][i][j] = (min == Integer.MAX_VALUE) ? Integer.MAX_VALUE : min + 1;
//                        }
//                    }
//                }
//            }
//        }


    }

    public static void main(String[] args) {

    }

}
