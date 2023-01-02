import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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
    int resolve(List<String> inputs);
}

abstract class Operation {
    int cyclesBeforeExecution;
    int term;

    public Operation(int cyclesBeforeExecution, int term) {
        this.cyclesBeforeExecution = cyclesBeforeExecution;
        this.term = term;
    }

    abstract int execute(int x);

    @Override
    public String toString() {
        return "Operation [cyclesBeforeExecution=" + cyclesBeforeExecution + ", term=" + term + "]";
    }

}

class AddtionOp extends Operation {
    public AddtionOp(int term) {
        super(2, term);
    }

    public int execute(int x) {
        return x + term;
    }
}

class NoOp extends Operation {

    public NoOp() {
        super(1, 0);
    }

    @Override
    int execute(int x) {
        return x;
    }

}

class SolutionPart1 implements Resolvable {

    int X;
    int iter;

    @Override
    public int resolve(List<String> inputs) {
        X = 1;
        iter = 1;
        int strength = 0;
        Deque<Operation> opQeue = new ArrayDeque<>();

        Iterator<String> iterator = inputs.iterator();

        iterator.next();

        while (iterator.hasNext()) {
            String input = iterator.next();
            if (input.startsWith("addx")) {
                int term = Integer.parseInt(input.substring(5, input.length()));
                opQeue.add(new AddtionOp(term));
            } else {
                opQeue.add(new NoOp());
            }
        }

        System.out.println(String.format("iter: %d", iter));

        while (!opQeue.isEmpty()) {
            execute(opQeue);

            if (iter == 19 || (iter + 1 - 20) % 40 == 0) {
                strength += X * (iter + 1);
                System.out.println(
                        String.format("iter: %d, X: %d, current: %d, strength: %d", iter + 1, X, X * (iter + 1),
                                strength));
            }

            iter++;
        }

        System.out.println(String.format("iter: %d", iter));

        return strength;
    }

    private void execute(Deque<Operation> opQeue) {
        Operation op = opQeue.peek();
        System.out.println("Before " + op);
        if (op == null) {
            return;
        }

        op.cyclesBeforeExecution--;

        if (op.cyclesBeforeExecution <= 0) {
            opQeue.remove();
            X = op.execute(X);
        }
        System.out.println("After " + op);
    }

}

class SolutionPart2 implements Resolvable {

    int X;
    int iter;

    @Override
    public int resolve(List<String> inputs) {
        X = 1;
        iter = 1;
        int strength = 0;
        Deque<Operation> opQeue = new ArrayDeque<>();

        Iterator<String> iterator = inputs.iterator();

        iterator.next();

        while (iterator.hasNext()) {
            String input = iterator.next();
            if (input.startsWith("addx")) {
                int term = Integer.parseInt(input.substring(5, input.length()));
                opQeue.add(new AddtionOp(term));
            } else {
                opQeue.add(new NoOp());
            }
        }

        StringBuilder sb = new StringBuilder();

        while (!opQeue.isEmpty()) {
            execute(opQeue);

            // during
            int pixelPos = iter % 40 + 1;
            int spritePos = X;

            if (pixelPos >= spritePos && pixelPos < spritePos + 3) {
                sb.append("#");
            } else {
                sb.append(".");
            }

            iter++;

            if (iter % 40 == 0) {
                System.out.println(sb.toString());
                sb = new StringBuilder();
            }
        }

        return strength;
    }

    private void execute(Deque<Operation> opQeue) {
        Operation op = opQeue.peek();
        if (op == null) {
            return;
        }

        op.cyclesBeforeExecution--;

        if (op.cyclesBeforeExecution <= 0) {
            opQeue.remove();
            X = op.execute(X);
        }
    }

}