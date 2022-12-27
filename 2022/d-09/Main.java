import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Resolvable solution = new SolutionPart2();
        int answer = solution.resolve(readInput());
        System.out.println(answer);
    }

    private static List<String> readInput() throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        File myObj = new File("input.txt");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            lines.add(data);
        }
        myReader.close();
        return lines;

    }
}

interface Resolvable {
    public Integer resolve(List<String> lines);
}

class Rope {
    int begX, begY;
    int endX, endY;

    Rope(int x, int y) {
        begX = x;
        begY = y;
        endX = x;
        endY = y;
    }

    void moveUp() {
        begY++;
    }

    void moveDown() {
        begY--;
    }

    void moveRight() {
        begX++;
    }

    void moveLeft() {
        begX--;
    }

    boolean shouldMoveTail() {
        return Math.abs(endX - begX) > 1 || Math.abs(endY - begY) > 1;
    }

    Coordinate moveTail() {
        int distX = begX - endX;
        int distY = begY - endY;

        if (Math.abs(distX) > 1) {
            endX += distX / Math.abs(distX);

            if (distY != 0) {
                endY += distY / Math.abs(distY);
            }
        }

        if (Math.abs(distY) > 1) {
            endY += distY / Math.abs(distY);

            if (distX != 0) {
                endX += distX / Math.abs(distX);
            }
        }

        return new Coordinate(endX, endY);

    }
}

class Coordinate {
    final int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
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
        Coordinate other = (Coordinate) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Coordinate [x=" + x + ", y=" + y + "]";
    }

}

class SolutionPart1 implements Resolvable {

    @Override
    public Integer resolve(List<String> lines) {
        Rope rope = new Rope(0, 0);

        Set<Coordinate> visited = new HashSet<>();
        visited.add(new Coordinate(0, 0));

        Iterator<String> iterator = lines.iterator();

        Pattern pattern = Pattern.compile("(.)\\s(\\d+)");

        while (iterator.hasNext()) {
            String line = iterator.next();
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            String movement = matcher.group(1);
            int movementCount = Integer.parseInt(matcher.group(2));

            for (int i = 0; i < movementCount; i++) {
                switch (movement) {
                    case "L":
                        rope.moveLeft();
                        break;
                    case "R":
                        rope.moveRight();
                        break;
                    case "U":
                        rope.moveUp();
                        break;
                    case "D":
                        rope.moveDown();
                        ;
                        break;
                }

                if (rope.shouldMoveTail()) {
                    visited.add(rope.moveTail());
                }

            }
        }

        int nbVisited = visited.size();
        // System.out.println(visited);
        return nbVisited;
    }

}

class ExtendedRope {
    Coordinate[] knots;
    final int head = 0;
    final int tail;

    public ExtendedRope(int x, int y) {
        knots = new Coordinate[10];
        tail = 9;
        for (int i = 0; i < knots.length; i++) {
            knots[i] = new Coordinate(x, y);
        }
    }

    Coordinate moveUp() {
        knots[head] = new Coordinate(knots[head].x, knots[head].y + 1);
        moveTail();
        return knots[tail];
    }

    Coordinate moveDown() {
        knots[head] = new Coordinate(knots[head].x, knots[head].y - 1);
        moveTail();
        return knots[tail];
    }

    Coordinate moveRight() {
        knots[head] = new Coordinate(knots[head].x + 1, knots[head].y);
        moveTail();
        return knots[tail];
    }

    Coordinate moveLeft() {
        knots[head] = new Coordinate(knots[head].x - 1, knots[head].y);
        moveTail();
        return knots[tail];
    }

    private void moveTail() {
        for (int i = head + 1; i < knots.length; i++) {
            Coordinate currentKnot = knots[i];
            Coordinate previousKnot = knots[i - 1];
            int distX = previousKnot.x - currentKnot.x;
            int distY = previousKnot.y - currentKnot.y;

            int newCurX = currentKnot.x;
            int newCurY = currentKnot.y;
            if (Math.abs(distX) > 1) {
                newCurX += distX / Math.abs(distX);

                if (distY != 0) {
                    newCurY += distY / Math.abs(distY);
                }
            } else if (Math.abs(distY) > 1) {
                newCurY += distY / Math.abs(distY);

                if (distX != 0) {
                    newCurX += distX / Math.abs(distX);
                }
            }

            knots[i] = new Coordinate(newCurX, newCurY);
        }
    }
}

class SolutionPart2 implements Resolvable {

    @Override
    public Integer resolve(List<String> lines) {
        ExtendedRope rope = new ExtendedRope(0, 0);

        Set<Coordinate> visited = new HashSet<>();
        visited.add(new Coordinate(0, 0));

        Iterator<String> iterator = lines.iterator();

        Pattern pattern = Pattern.compile("(.)\\s(\\d+)");

        while (iterator.hasNext()) {
            String line = iterator.next();
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            String movement = matcher.group(1);
            int movementCount = Integer.parseInt(matcher.group(2));

            for (int i = 0; i < movementCount; i++) {
                switch (movement) {
                    case "L":
                        visited.add(rope.moveLeft());
                        break;
                    case "R":
                        visited.add(rope.moveRight());
                        break;
                    case "U":
                        visited.add(rope.moveUp());
                        break;
                    case "D":
                        visited.add(rope.moveDown());
                        ;
                        break;
                }

            }
        }

        int nbVisited = visited.size();
        System.out.println(visited);
        return nbVisited;
    }

}
