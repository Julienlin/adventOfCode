import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main
 */
public class Main {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("test.txt"))) {
            List<List<int[]>> lines = new ArrayList<>();
            String line;
            int minWidth = 500;
            int maxWidth = 500;
            int maxHeight = 0;

            while ((line = reader.readLine()) != null) {
                var points = Arrays.stream(line.split(" -> ")).map(point -> point.split(","))
                        .map(point -> new int[] { Integer.parseInt(point[0]), Integer.parseInt(point[1]) })
                        .collect(Collectors.toList());

                for (int i = 0; i < points.size() - 1; i++) {
                    var pointA = points.get(i);
                    var pointB = points.get(i + 1);

                    minWidth = Math.min(minWidth, pointA[0]);
                    maxWidth = Math.max(maxWidth, pointA[0]);
                    maxHeight = Math.max(maxHeight, pointA[1]);

                    minWidth = Math.min(minWidth, pointB[0]);
                    maxWidth = Math.max(maxWidth, pointB[0]);
                    maxHeight = Math.max(maxHeight, pointB[1]);

                    lines.add(List.of(pointA, pointB));

                }

            }

            System.out
                    .println(String.format("minWidth: %d, maxWidth: %d, maxHeight: %d", minWidth, maxWidth, maxHeight));

            Simulable simulator;
            // simulator = new Simulator(minWidth, maxWidth, maxHeight, lines);
            simulator = new InfiniteFloorSimulator(maxHeight, lines);
            // System.out.println(simulator);
            int tours = simulator.simulate();
            // System.out.println(simulator);
            System.out.println(tours);

        } catch (Exception e) {
            // TODO: handle exception
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

interface Simulable {
    int simulate();
}

class Simulator implements Simulable {

    int ROWS, COLS;
    int minWidth;
    int maxWidth;
    int maxHeight;
    int[] source;

    int[][] isOccupied;

    static final int COL = 0;
    static final int ROW = 1;

    public Simulator(int minWidth, int maxWidth, int maxHeight, List<List<int[]>> lines) {

        assert minWidth < maxWidth;
        assert maxHeight > 0;

        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.minWidth = minWidth;

        source = new int[2];
        source[ROW] = 0;
        source[COL] = 500 - minWidth;

        COLS = maxWidth - minWidth + 1;
        ROWS = maxHeight + 1;

        // System.out.println(String.format("X: %d, Y: %d", ROWS, COLS));

        isOccupied = new int[ROWS][COLS];

        for (List<int[]> line : lines) {
            int[] pointA = line.get(0);
            int[] pointB = line.get(1);

            if (pointA[ROW] == pointB[ROW]) {
                int row = pointA[ROW];
                int leftMost = Math.min(pointA[COL], pointB[COL]) - minWidth;
                int rightmost = Math.max(pointA[COL], pointB[COL]) - minWidth;
                for (int i = leftMost; i <= rightmost; i++) {
                    System.out.println(String.format("row: %d, col: %d", row, i));
                    isOccupied[row][i] = 1;
                }
            } else {
                int col = pointA[COL] - minWidth;
                int lessHigh = Math.min(pointA[ROW], pointB[ROW]);
                int highest = Math.max(pointA[ROW], pointB[ROW]);
                for (int i = lessHigh; i <= highest; i++) {
                    isOccupied[i][col] = 1;
                }
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Integer.toString(isOccupied[0][0]);
        for (int i = 0; i < isOccupied.length; i++) {
            sb.append(String.join(" ", Arrays.stream(isOccupied[i]).mapToObj(String::valueOf).toArray(String[]::new)));
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public int simulate() {
        int tours = 0;
        boolean isInfinite = false;
        int[] sand;
        boolean isMoving;
        while (!isInfinite) {
            sand = source.clone();
            // System.out.println(String.format("sand: %d, %d", sand[ROW], sand[COL]));
            isMoving = true;
            while (isMoving) {
                // System.out.println(String.join(" ", "Moving", String.valueOf(sand[ROW] + 1),
                // String.valueOf(sand[COL] + 1), String.valueOf(sand[COL] - 1)));
                if (sand[ROW] + 1 > maxHeight || sand[COL] + 1 > maxWidth || sand[COL] - 1 < 0) {
                    isInfinite = true;
                    isMoving = false;
                    // System.out.println(String.join(" ", "coucou", String.valueOf(sand[ROW] + 1 >=
                    // maxHeight),
                    // String.valueOf(sand[COL] + 1 >= maxWidth), String.valueOf(sand[COL] - 1 <=
                    // minWidth)));
                    // System.out.println(String.join(" ", "coucou", String.valueOf(sand[ROW] + 1),
                    // String.valueOf(sand[COL] + 1), String.valueOf(sand[COL] - 1)));
                } else if (isOccupied[sand[ROW] + 1][sand[COL]] < 1) {
                    sand[ROW]++;
                } else if (isOccupied[sand[ROW] + 1][sand[COL] - 1] < 1) {
                    sand[COL]--;
                    sand[ROW]++;
                } else if (isOccupied[sand[ROW] + 1][sand[COL] + 1] < 1) {
                    sand[COL]++;
                    sand[ROW]++;
                } else {
                    isOccupied[sand[ROW]][sand[COL]] = 2;
                    isMoving = false;
                }
            }
            tours++;
            // System.out.println(this);
            // System.out.println();
        }

        return tours - 1;
    }

}

class InfiniteFloorSimulator implements Simulable {
    static final int COL = 0;
    static final int ROW = 1;

    enum Cell {
        AIR,
        WALL,
        SAND
    }

    int maxHeight;
    int[] source;
    Map<Integer, Cell> occupiedCellsMap;

    public InfiniteFloorSimulator(int maxHeight, List<List<int[]>> lines) {

        source = new int[2];
        source[ROW] = 0;
        source[COL] = 500;

        this.maxHeight = maxHeight + 1;

        occupiedCellsMap = new HashMap<>(100000);

        for (List<int[]> line : lines) {
            int[] pointA = line.get(0);
            int[] pointB = line.get(1);

            if (pointA[ROW] == pointB[ROW]) {
                int row = pointA[ROW];
                int leftMost = Math.min(pointA[COL], pointB[COL]);
                int rightmost = Math.max(pointA[COL], pointB[COL]);
                for (int i = leftMost; i <= rightmost; i++) {
                    int[] newCell = new int[2];
                    newCell[ROW] = row;
                    newCell[COL] = i;
                    occupyCell(newCell, Cell.WALL);
                }
            } else {
                int col = pointA[COL];
                int lessHigh = Math.min(pointA[ROW], pointB[ROW]);
                int highest = Math.max(pointA[ROW], pointB[ROW]);
                for (int i = lessHigh; i <= highest; i++) {
                    int[] newCell = new int[2];
                    newCell[ROW] = i;
                    newCell[COL] = col;
                    occupyCell(newCell, Cell.WALL);
                }
            }
        }

    }

    @Override
    public int simulate() {
        int tours = 0;
        int[] sand;
        boolean isMoving;
        int[] downCell, downLeftCell, downRightCell;
        while (!isOccupied(source)) {
            sand = source.clone();
            isMoving = true;
            while (isMoving) {
                downCell = new int[2];
                downCell[ROW] = sand[ROW] + 1;
                downCell[COL] = sand[COL];

                downLeftCell = new int[2];
                downLeftCell[ROW] = sand[ROW] + 1;
                downLeftCell[COL] = sand[COL] - 1;

                downRightCell = new int[2];
                downRightCell[ROW] = sand[ROW] + 1;
                downRightCell[COL] = sand[COL] + 1;

                if (sand[ROW] + 1 > maxHeight) {
                    occupyCell(downRightCell, Cell.SAND);
                    isMoving = false;
                } else if (!isOccupied(downCell)) {
                    sand[ROW]++;
                } else if (!isOccupied(downLeftCell)) {
                    sand[COL]--;
                    sand[ROW]++;
                } else if (!isOccupied(downRightCell)) {
                    sand[COL]++;
                    sand[ROW]++;
                } else {
                    occupyCell(downRightCell, Cell.SAND);
                    isMoving = false;
                }
            }
            tours++;
            // System.out.println(occupiedCells.size());
        }

        return tours;
    }

    private boolean isOccupied(int[] cell) {
        InfiniteFloorSimulator.Cell cellType = occupiedCellsMap.getOrDefault(computeCellCoordHash(cell), Cell.AIR);
        // System.out.println(String.format("cell: %s, cell type: %s", Arrays.toString(cell), cellType));
        return cellType != Cell.AIR;

    }

    private Integer computeCellCoordHash(int[] cell) {
        int a = cell[0];
        int b = cell[1];
        return (a + b) * (a + b + 1) / 2 + a;
    }

    private void occupyCell(int[] cell, Cell cellType) {
        Integer hash = computeCellCoordHash(cell);
        occupiedCellsMap.put(hash, cellType);
    }

}