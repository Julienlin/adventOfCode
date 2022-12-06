import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Set;
import java.util.stream.Collectors;

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

class SolutionPart1 implements Resolvable {

    public Integer resolve(List<String> lines) {

        Integer marker = 4;

        for (String line : lines) {
            for (int i = 0; i < line.length() - 1 - 4; i++) {
                Set<Character> chars = line.substring(i, i + 4).chars().mapToObj(e -> (char) e)
                        .collect(Collectors.toSet());

                if (chars.size() >= 4) {
                    break;
                }
                marker++;
            }
        }

        return marker;
    }

}

class SolutionPart2 implements Resolvable {

    public Integer resolve(List<String> lines) {

        int markerLength = 14;
        Integer marker = markerLength;

        for (String line : lines) {
            for (int i = 0; i < line.length() - 1 - markerLength; i++) {
                Set<Character> chars = line.substring(i, i + markerLength).chars().mapToObj(e -> (char) e)
                        .collect(Collectors.toSet());

                if (chars.size() >= markerLength) {
                    break;
                }
                marker++;
            }
        }

        return marker;
    }

}