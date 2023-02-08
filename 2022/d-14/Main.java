import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

            System.out.println(String.format("minWidth: %d, maxWidth: %d, maxHeight: %d", minWidth, maxWidth, maxHeight));

            Simulator simulator = new Simulator(minWidth, maxWidth, maxHeight, lines);


        } catch (Exception e) {
            // TODO: handle exception
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

class Simulator {

    int X, Y;
    int minWidth;
    int maxWidth;
    int maxHeight;

    boolean[][] isOccupied;

    static final int COL = 1;
    static final int ROW = 0;

    public Simulator(int minWidth, int maxWidth, int maxHeight, List<List<int[]>> lines) {

        assert minWidth < maxWidth;
        assert maxHeight > 0;

        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.minWidth = minWidth;

        X = maxWidth - minWidth;
        Y = maxHeight;

        System.out.println(String.format("X: %d, Y: %d", X, Y));

        isOccupied = new boolean[Y][X];

        for (List<int[]> line : lines) {
            int[] pointA = line.get(0);
            int[] pointB = line.get(1);

            if (pointA[ROW] == pointB[ROW]) {
                int row = pointA[ROW];
                int leftMost = Math.min(pointA[COL], pointB[COL]) - minWidth;
                int rightmost = Math.max(pointA[COL], pointB[COL]) - minWidth;
                for (int i = leftMost; i < rightmost; i++) {
                    isOccupied[row][i] = true;
                }
            } else {
                int col = pointA[COL] - minWidth;
                int lessHigh = Math.min(pointA[ROW], pointB[ROW]);
                int highest = Math.max(pointA[ROW], pointB[ROW]);
                for (int i = lessHigh; i < highest; i++) {
                    isOccupied[i][col] = true;
                }
            }
        }

    }

}