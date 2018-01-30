package eric.competition.solution;

import eric.competition.ForecastReader;
import eric.competition.PositionReader;

import java.util.List;
import java.util.Map;

/**
 * Created by Eric Yu on 2018/1/30.
 */
public class Solution3 extends Solution {

    private List<List<List<List<Map.Entry<Integer, Double>>>>> dpMatrix3;

    public Solution3(ForecastReader forecastReader, int maxStepEachSlice, PositionReader positionReader) {
        super(forecastReader, maxStepEachSlice, positionReader);
    }

    @Override
    public void solveProblem() {
        preProcess(0);
        
    }
}
