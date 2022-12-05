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

class Assignment {
    private int begin, end;

    Assignment(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    static Assignment from(String assignment) {
        String[] border = assignment.split("-");
        Assignment newAssignment = new Assignment(Integer.parseInt(border[0]), Integer.parseInt(border[1]));
        return newAssignment;
    }

    Boolean isFullyContaining(Assignment other) {
        return begin <= other.begin && end >= other.end;
    }

    Boolean isOverlapping(Assignment other) {
        return end >= other.begin && end <= other.end;
    }

    @Override
    public String toString() {
        return "Assignment [begin=" + begin + ", end=" + end + "]";
    }
}

class SolutionPart1 implements Resolvable {

    public Integer resolve(List<String> lines) {
        int score = 0;

        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] assignments = line.split(",");
            Assignment elf1 = Assignment.from(assignments[0]);
            Assignment elf2 = Assignment.from(assignments[1]);
            if (elf1.isFullyContaining(elf2) || elf2.isFullyContaining(elf1)) {
                score++;
            }

        }

        return score;

    }

}

class SolutionPart2 implements Resolvable {
    public Integer resolve(List<String> lines) {
        int score = 0;

        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] assignments = line.split(",");
            Assignment elf1 = Assignment.from(assignments[0]);
            Assignment elf2 = Assignment.from(assignments[1]);

            System.out.println(
                    String.format("%s %s %s %s", elf1, elf2, elf1.isOverlapping(elf2), elf2.isOverlapping(elf1)));
            if (elf1.isOverlapping(elf2) || elf2.isOverlapping(elf1)) {
                score++;
            }

        }

        return score;

    }

}