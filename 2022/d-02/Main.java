import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

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
    Integer resolve(List<String> lines);
}

enum Shape {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    private Integer value;

    private Shape(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    static Shape from(String s) {
        if (s.equals("A") || s.equals("X")) {
            return ROCK;
        } else if (s.equals("B") || s.equals("Y")) {
            return PAPER;
        }
        return SCISSORS;
    }

    static Shape getWinnerShape(Shape shape) {
        if (shape == ROCK) {
            return PAPER;
        }
        if (shape == PAPER) {
            return SCISSORS;
        }
        return ROCK;
    }

    static Shape getLoserShape(Shape shape) {
        if (shape == ROCK) {
            return SCISSORS;
        }
        if (shape == PAPER) {
            return ROCK;
        }
        return PAPER;
    }
}

class SolutionPart1 implements Resolvable {

    public Integer resolve(List<String> lines) {
        int score = 0;

        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] shapes = line.split(" ");
            Shape opponent = Shape.from(shapes[0]);
            Shape our = Shape.from(shapes[1]);

            int roundResult = play(our, opponent);

            score += our.getValue();
            score += roundResult;
        }

        return score;

    }

    private int play(Shape our, Shape opponent) {
        if (our == opponent) {
            return 3;
        }
        if (our == Shape.ROCK && opponent == Shape.SCISSORS) {
            return 6;
        }
        if (our == Shape.PAPER && opponent == Shape.ROCK) {
            return 6;
        }
        if (our == Shape.SCISSORS && opponent == Shape.PAPER) {
            return 6;
        }

        return 0;
    }
}

class SolutionPart2 implements Resolvable {
    public Integer resolve(List<String> lines) {
        int score = 0;

        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] shapes = line.split(" ");
            Shape opponent = Shape.from(shapes[0]);

            int roundResult = play(shapes[1]);
            Shape our;

            if (roundResult == 0) {
                our = Shape.getLoserShape(opponent);
            } else if (roundResult == 6) {
                our = Shape.getWinnerShape(opponent);
            } else {
                our = opponent;
            }

            score += our.getValue();
            score += roundResult;

            // System.out.println(String.format("%s %s %s %d %d",  opponent, shapes[1], our, our.getValue(), roundResult));

        }

        return score;
    }

    private int play(String outcoume) {
        if (outcoume.equals("X")) {
            return 0;
        } else if (outcoume.equals("Y")) {
            return 3;
        }
        return 6;
    }
}