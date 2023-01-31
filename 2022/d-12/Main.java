import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get("test.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Solution solution = new Solution1();

        long startTime = System.nanoTime();
        long sol = solution.resolve(lines);
        long stopTime = System.nanoTime();

        System.out.println(sol);
        System.out.println(String.format("execution time: %f", (stopTime - startTime) / 1000000000f));

    }
}

interface Solution {
    long resolve(List<String> lines);
}

class Coord {
    int x, y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coord [x=" + x + ", y=" + y + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coord other = (Coord) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

}

class Solution1 implements Solution {

    int n, m;

    @Override
    public long resolve(List<String> lines) {
        n = lines.size();
        m = lines.get(0).length();
        boolean[][] adjancy = new boolean[n * m][n * m];

        Coord start = findStart(lines);

        constructDijkstraGraph(lines, adjancy, start);

        // System.out.println(Arrays.toString(adjancy[6]));

        int res = findShortestPathToEnd(lines, adjancy, start);
        return res;
    }

    private Coord findStart(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < m; j++) {
                if (lines.get(i).charAt(j) == 'S') {
                    return new Coord(i, j);
                }
            }
        }

        throw new RuntimeException("should not happened");
    }

    private int findShortestPathToEnd(List<String> lines, boolean[][] adjancy, Coord start) {
        Stack<Coord> path = new Stack<>();
        boolean[][] visited = new boolean[n][m];

        path.add(start);

        while (!path.empty()) {
            var coord = path.peek();
            var heigth = lines.get(coord.x).charAt(coord.y);
            if (heigth == 'E') {
                break;
            }

            boolean isAllChildrenVisited = true;

            // check right
            int rightX = coord.x;
            int rightY = coord.y + 1;
            // check bottom
            int bottomX = coord.x + 1;
            int bottomY = coord.y;
            // check left
            int leftX = coord.x;
            int leftY = coord.y - 1;
            // check top
            int topX = coord.x - 1;
            int topY = coord.y;

            if (rightY < m && !path.contains(new Coord(rightX, rightY))
                    && adjancy[coord.y + coord.x * m][rightY + rightX * m] && !visited[rightX][rightY]) {
                path.add(new Coord(rightX, rightY));
                isAllChildrenVisited = false;
            } else if (bottomX < n && !path.contains(new Coord(bottomX, bottomY))
                    && adjancy[coord.y + coord.x * m][bottomY + bottomX * m]
                    && !visited[bottomX][bottomY]) {
                path.add(new Coord(bottomX, bottomY));
                isAllChildrenVisited = false;
            } else if (leftY >= 0 && !path.contains(new Coord(leftX, leftY))
                    && adjancy[coord.y + coord.x * m][leftY + leftX * m] && !visited[leftX][leftY]) {
                path.add(new Coord(leftX, leftY));
                isAllChildrenVisited = false;
            } else if (topX >= 0 && !path.contains(new Coord(topX, topY))
                    && adjancy[coord.y + coord.x * m][topY + topX * m] && !visited[topX][topY]) {
                path.add(new Coord(topX, topY));
                isAllChildrenVisited = false;
            }

            if (isAllChildrenVisited) {
                path.pop();
                visited[coord.x][coord.y] = true;
            }

        }
        // System.out.println(path);
        return path.size() - 1;
    }

    private void constructDijkstraGraph(List<String> lines, boolean[][] adjancy, Coord start) {
        boolean[][] visited = new boolean[n][m];
        Deque<Coord> frontier = new ArrayDeque<>();
        visited[0][0] = true;
        frontier.add(start);
        while (!frontier.isEmpty()) {
            var coord = frontier.poll();
            var heigth = lines.get(coord.x).charAt(coord.y);
            if (heigth == 'S') {
                heigth = 'a';
            }
            if (heigth == 'E') {
                heigth = 'z';
            }

            // check right
            int rightX = coord.x;
            int rightY = coord.y + 1;
            if (rightY < m && !visited[rightX][rightY]) {
                char rightHeight = lines.get(rightX).charAt(rightY);
                if (rightHeight == 'E') {
                    rightHeight = 'z';
                }
                // if (coord.equals(new Coord(2, 4))) {
                // System.out.println(
                // String.format("Math.abs(heigth - rightHeight) <= 1: %s, heigth: %s,
                // rightHeight: %s ",
                // Math.abs(heigth - rightHeight) <= 1, heigth, rightHeight));
                // }
                if (Math.abs(heigth - rightHeight) <= 1) {
                    frontier.add(new Coord(rightX, rightY));
                    adjancy[coord.y + coord.x * m][rightY + rightX * m] = true;
                }
            }

            // check bottom
            int bottomX = coord.x + 1;
            int bottomY = coord.y;
            if (bottomX < n && !visited[bottomX][bottomY]) {
                char bottomHeight = lines.get(bottomX).charAt(bottomY);
                if (bottomHeight == 'E') {
                    bottomHeight = 'z';
                }
                // if (coord.equals(new Coord(2, 4))) {
                // System.out.println(
                // String.format("Math.abs(heigth - bottomHeight) <= 1: %s, heigth: %s,
                // bottomHeight: %s ",
                // Math.abs(heigth - bottomHeight) <= 1, heigth, bottomHeight));
                // }
                if (Math.abs(heigth - bottomHeight) <= 1) {
                    frontier.add(new Coord(bottomX, bottomY));
                    adjancy[coord.y + coord.x * m][bottomY + bottomX * m] = true;
                }
            }

            // check left
            int leftX = coord.x;
            int leftY = coord.y - 1;
            if (leftY >= 0 && !visited[leftX][leftY]) {
                char leftHeight = lines.get(leftX).charAt(leftY);
                if (leftHeight == 'E') {
                    leftHeight = 'z';
                }
                // if (coord.equals(new Coord(2, 4))) {
                // System.out.println(
                // String.format("Math.abs(heigth - leftHeight) <= 1: %s, heigth: %s,
                // leftHeight: %s ",
                // Math.abs(heigth - leftHeight) <= 1, heigth, leftHeight));
                // }
                if (Math.abs(heigth - leftHeight) <= 1) {
                    frontier.add(new Coord(leftX, leftY));
                    adjancy[coord.y + coord.x * m][leftY + leftX * m] = true;
                }
            }

            // check top
            int topX = coord.x - 1;
            int topY = coord.y;
            if (topX >= 0 && !visited[topX][topY]) {
                char topHeight = lines.get(topX).charAt(topY);
                if (topHeight == 'E') {
                    topHeight = 'z';
                }
                if (Math.abs(heigth - topHeight) <= 1) {
                    frontier.add(new Coord(topX, topY));
                    adjancy[coord.y + coord.x * m][topY + topX * m] = true;
                }
            }
            visited[coord.x][coord.y] = true;
        }
        // printMatrix(visited);
    }

    private void printMatrix(boolean[][] visited) {
        for (boolean[] visitedLine : visited) {
            System.out.println(Arrays.toString(visitedLine));
        }
        System.out.println();
    }

}
