package eric.competition;

public class ResultUtils {
    // timepoint: 1  --> 03:00    2--> 03:02       last --> 20:58
    public static String toCSVLine(int day, int targetIndex, int timePoint, Position curPosition) {
        int minutesPast = (timePoint - 1) * 2;
        String time = String.format("%02d:%02d", 3 + minutesPast / 60, minutesPast % 60);
        return String.format("%d,%d,%s,%d,%d", targetIndex, day, time, curPosition.getRow() + 1, curPosition.getCol() + 1);
    }

    public static String toFigure(int day, int targetIndex, int timePoint, Position curPosition) {
        return String.format("%d,%d", curPosition.getRow() + 1, curPosition.getCol() + 1);
    }
}
