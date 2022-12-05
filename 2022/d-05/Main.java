import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Resolvable solution = new SolutionPart2();
        String answer = solution.resolve(readInput());
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
    String resolve(List<String> lines);
}

class Crate {
    private Character name;

    public Crate(Character name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Crate [name=" + name + "]";
    }

    public Character getName() {
        return name;
    }

}

class Movement {
    int nb, from, to;

    public Movement(int nb, int from, int to) {
        this.nb = nb;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Movement [nb=" + nb + ", from=" + from + ", to=" + to + "]";
    }

}

abstract class AbstractSolution implements Resolvable {

    protected List<Deque<Crate>> stacks = new ArrayList<>();
    protected List<Movement> movements = new ArrayList<>();

    @Override
    abstract public String resolve(List<String> lines);

    protected void loadData(List<String> lines) {
        // clear the list if the instance is reused
        stacks.clear();
        movements.clear();

        initStacks(lines);

        int i = 0;

        // load stacks
        while (i < lines.size() && !lines.get(i).startsWith(" 1")) {
            String line = lines.get(i);

            for (int j = 0, stack = 0; j < line.length() && stack < stacks.size(); j += 4, stack++) {
                int endSubString = j + 4 <= line.length() ? j + 4 : j + 3;

                // stackEl should be as follow: <SPACE>[<NAME>]<SPACE>
                String stackEl = line.substring(j, endSubString);

                if (!stackEl.isBlank()) {
                    stacks.get(stack).add(new Crate(stackEl.charAt(1)));
                }

            }

            i++;
        }

        // skip empty line
        i += 2;

        String pattern = "move (\\d+) from (\\d+) to (\\d+)";
        Pattern r = Pattern.compile(pattern);
        // load movements
        while (i < lines.size() && !lines.get(i).isBlank()) {
            Matcher m = r.matcher(lines.get(i));
            if (m.find()) {
                int nb = Integer.parseInt(m.group(1));
                int from = Integer.parseInt(m.group(2));
                int to = Integer.parseInt(m.group(3));

                Movement movement = new Movement(nb, from, to);
                movements.add(movement);
            }

            i++;
        }

    }

    private void initStacks(List<String> lines) {
        String line = lines.get(0);

        // each stack is formatted to use 4 character except the last one.
        int nbStacks = (line.length() + 1) / 4;
        for (int i = 0; i < nbStacks; i++) {
            stacks.add(new LinkedList<>());
        }
    }

    protected void printStack(int stackIndex) {
        Deque<Crate> stack = stacks.get(stackIndex);

        System.out.print(String.format("Stack %d :", stackIndex));
        Iterator<Crate> iterator = stack.iterator();
        while (iterator.hasNext()) {
            System.out.print(String.format("%s", iterator.next()));

            if (iterator.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

}

class SolutionPart1 extends AbstractSolution {

    @Override
    public String resolve(List<String> lines) {
        loadData(lines);

        for (Movement movement : movements) {
            Deque<Crate> fromStack = stacks.get(movement.from - 1);
            Deque<Crate> toStack = stacks.get(movement.to - 1);
            for (int i = 0; i < movement.nb; i++) {
                Crate head = fromStack.pop();
                toStack.push(head);
            }
        }

        char[] heads = new char[stacks.size()];

        for (int i = 0; i < stacks.size(); i++) {
            heads[i] = stacks.get(i).getFirst().getName();
        }

        return new String(heads);
    }

}

class SolutionPart2 extends AbstractSolution {

    @Override
    public String resolve(List<String> lines) {
        loadData(lines);

        for (Movement movement : movements) {
            Deque<Crate> fromStack = stacks.get(movement.from - 1);
            Deque<Crate> toStack = stacks.get(movement.to - 1);
            Deque<Crate> temp = new LinkedList<>();
            for (int i = 0; i < movement.nb; i++) {
                Crate head = fromStack.pop();
                temp.push(head);
            }
            for (int i = 0; i < movement.nb; i++) {
                Crate head = temp.pop();
                toStack.push(head);
            }
        }

        char[] heads = new char[stacks.size()];

        for (int i = 0; i < stacks.size(); i++) {
            heads[i] = stacks.get(i).getFirst().getName();
        }

        return new String(heads);
    }

}