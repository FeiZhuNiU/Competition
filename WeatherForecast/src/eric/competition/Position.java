package eric.competition;

import javafx.util.Pair;

public class Position {
    int row;
    int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {

        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public static Pair<Position, Integer> fromCSVLine(String csvLineData) {
        String[] datas = csvLineData.split(",");
        int index = Integer.parseInt(datas[0]);
        int row = Integer.parseInt(datas[1]) - 1;
        int col = Integer.parseInt(datas[2]) - 1;
        return new Pair<>(new Position(row, col), index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (row != position.row) return false;
        return col == position.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }
}
