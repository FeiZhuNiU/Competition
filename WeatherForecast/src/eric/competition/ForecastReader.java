package eric.competition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ForecastReader {

    private boolean forecast[][][];
    private int rowNum;
    private int colNum;
    private int day;

    public final static int totalHours = 18;

    public ForecastReader(String fileName, int row, int col) {
        this.rowNum = row;
        this.colNum = col;
        forecast = new boolean[totalHours][rowNum][colNum];
        readData(fileName);
    }

    private void readData(String fileName) {

        try {
            BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));
            String line;
            // skip head
            line = file.readLine();
            while ((line = file.readLine()) != null) {
                SingleLineData lineData = new SingleLineData(line);
                forecast[lineData.getHour() - 3][lineData.getX() - 1][lineData.getY() - 1] = lineData.isBlocked();
                this.day = lineData.getDay();
            }
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean[][][] getForecast() {
        return forecast;
    }

    public void setForecast(boolean[][][] forecast) {
        this.forecast = forecast;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    private static class SingleLineData {
        int x;
        int y;
        int day;
        int hour;
        boolean blocked;

        public SingleLineData(String line) {
            String[] datas = line.split(",");
            x = Integer.parseInt(datas[0]);
            y = Integer.parseInt(datas[1]);
            day = Integer.parseInt(datas[2]);
            hour = Integer.parseInt(datas[3]);
            blocked = (Integer.parseInt(datas[4]) == 1);
        }

        public int getX() {

            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }
    }
}
