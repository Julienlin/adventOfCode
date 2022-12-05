import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        // SolutionPart1 solution = new SolutionPart1();
        // int maxCaloriePerElf = solution.resolve(readInput());
        // System.out.println(maxCaloriePerElf);


        SolutionPart2 solution = new SolutionPart2();
        int maxCaloriePerElf = solution.resolve(readInput());
        System.out.println(maxCaloriePerElf);
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

class SolutionPart1 {

    public Integer resolve(List<String> lines) {
        int maxCaloriePerElf = 0;

        Iterator<String> iterator = lines.iterator();

        int caloriePerElf = 0;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (!line.isBlank()) {
                caloriePerElf += Integer.parseInt(line);
            } else {
                maxCaloriePerElf = Math.max(maxCaloriePerElf, caloriePerElf);
                caloriePerElf = 0;
            }
        }

        return maxCaloriePerElf;
    }
}

class SolutionPart2 {
    public Integer resolve(List<String> lines) {

        List<Integer> caloriesPerElf = new ArrayList<>(lines.size());

        Iterator<String> iterator = lines.iterator();

        int caloriePerElf = 0;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (!line.isBlank()) {
                caloriePerElf += Integer.parseInt(line);
            } else {
                caloriesPerElf.add(caloriePerElf);
                caloriePerElf = 0;
            }
        }

        caloriesPerElf.sort((a, b) -> -a.compareTo(b));
        return caloriesPerElf.stream().limit(3).reduce(0, (a,b)-> a + b);
    }
}