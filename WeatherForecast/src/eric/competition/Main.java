package eric.competition;

import eric.competition.solution.*;

public class Main {

    public static void main(String[] args) {
//        boolean[][] day1 = new boolean[][]{{true, true, true}, {true, false, true}, {true, true, true}};
//        boolean[][] day2 = new boolean[][]{{true, true, true}, {true, false, true}, {true, true, false}};
//        boolean[][] day3 = new boolean[][]{{false, true, true}, {true, false, true}, {true, true, true}};
//        boolean[][] day4 = new boolean[][]{{true, true, true}, {true, false, true}, {true, true, true}};
//        boolean[][] day5 = new boolean[][]{{true, true, true}, {true, false, true}, {true, true, true}};
//
//        boolean[][][] data = new boolean[][][]{
//                {{true, true, true, true}, {false, false, true, true}, {true, true, true, true}},
//                {{true, true, true, true}, {false, false, true, true}, {true, true, false, true}},
//                {{false, true, true, true}, {true, false, true, true}, {true, true, true, true}},
//                {{true, true, true, true}, {true, false, true, true}, {true, true, true, true}},
//                {{true, true, true, true}, {true, false, true, true}, {true, true, true, true}}
//        };
//        List<Solution.Position> targets = new ArrayList<>();
//        targets.add(new Solution.Position(0,2));
        String date = "20180205";
        long time1 = System.currentTimeMillis();
        System.out.println("Reading CityData");
        PositionReader positionReader = new PositionReader("resources\\CityData.csv");
        long time2 = System.currentTimeMillis();
        System.out.println("consumed " + (time2 - time1) / 1000.0 + "s");

        for (int i = 1; i <= 5; ++i) {
            System.out.println("Reading predict Data " + i);

            ForecastReader forecastReader = new ForecastReader("resources\\" + date + "\\" + i + ".csv", 548, 421);

            long time3 = System.currentTimeMillis();
            System.out.println("consumed " + (time3 - time2) / 1000.0 + "s");
            Solution solution = new Solution5(
                    forecastReader,
                    30,
                    positionReader);

            solution.solveProblem();
            solution.printResult("resources\\result" + date + "b.csv", positionReader);

            long time4 = System.currentTimeMillis();
            System.out.println("consumed " + (time4 - time3) / 1000.0 + "s");
        }
    }
}
