import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
        int score = 0;

        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            String line = iterator.next();

            int begHalf = line.length() / 2;
            Set<Character> commonCharacters = getCommonCharacters(line, begHalf);
            for (Character character : commonCharacters) {
                score += computePriorites(character);
            }

        }

        return score;

    }

    private Set<Character> getCommonCharacters(String line, int begHalf) {
        Set<Character> firstHalf = line.substring(0, begHalf).chars().mapToObj(e -> (char) e)
                .collect(Collectors.toSet());
        Set<Character> secondHalf = line.substring(begHalf).chars().mapToObj(e -> (char) e)
                .collect(Collectors.toSet());

        Set<Character> commonCharacters = new HashSet<>(firstHalf);

        commonCharacters.retainAll(secondHalf);
        return commonCharacters;
    }

    private int computePriorites(Character character) {
        if (Character.isUpperCase(character)) {
            // System.out.println(String.format("%s %d", character, character - 'A' + 1 +
            // 26));
            return character - 'A' + 1 + 26;
        } else {
            // System.out.println(String.format("%s %d", character, character - 'a' + 1));
            return character - 'a' + 1;
        }
    }

}

class SolutionPart2 implements Resolvable {
    public Integer resolve(List<String> lines) {
        int score = 0;

        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {

            Set<Character> commonCharacters = getCommonCharacters(
                    new String[] { iterator.next(), iterator.next(), iterator.next() });
            for (Character character : commonCharacters) {
                score += computePriorites(character);
            }

        }

        return score;

    }

    private Set<Character> getCommonCharacters(String[] lines) {
        Set<Character> commonCharacters = null;

        for (String line : lines) {
            Set<Character> set = line.chars().mapToObj(e -> (char) e)
                    .collect(Collectors.toSet());
            if (commonCharacters == null) {
                commonCharacters = set;
            } else {
                commonCharacters.retainAll(set);
            }
        }

        return commonCharacters;
    }

    private int computePriorites(Character character) {
        if (Character.isUpperCase(character)) {
            // System.out.println(String.format("%s %d", character, character - 'A' + 1 +
            // 26));
            return character - 'A' + 1 + 26;
        } else {
            // System.out.println(String.format("%s %d", character, character - 'a' + 1));
            return character - 'a' + 1;
        }
    }

}