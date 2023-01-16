import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get("input.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Solution solution = new Solution1();

        long sol = solution.resolve(lines);

        System.out.println(sol);

    }
}

interface Solution {
    long resolve(List<String> lines) throws CloneNotSupportedException;
}

class Solution1 implements Solution {

    Pattern operationPattern = Pattern.compile("\\s*Operation: new = ((?:old)|\\d+) ([+*]) ((?:old)|\\d+)");
    Pattern moduloTesPattern = Pattern.compile("\\s*Test: divisible by (\\d+)");
    Pattern nextIfTruPattern = Pattern.compile("\\s*If true: throw to monkey (\\d+)");
    Pattern nextIfFalsePattern = Pattern.compile("\\s*If false: throw to monkey (\\d+)");
    // long maxRound = 20;
    long maxRound = 10000;

    @Override
    public long resolve(List<String> lines) throws CloneNotSupportedException {
        List<Monkey> monkeys = new ArrayList<>(lines.size() / 7);
        parseData(lines, monkeys);

        long  modulusProduct = 1;
        for (Monkey monkey : monkeys) {
            modulusProduct *= monkey.moduloTest.modulo;
        }

        for (long round = 0; round < maxRound ; round++) {
            for (Monkey monkey : monkeys) {
                monkey.turn(monkeys, modulusProduct);
            }
            // for (Monkey monkey : monkeys) {
            //     System.out.println(monkey.items);
            // }
        }

        List<Long> inspections = monkeys.stream().map(monkey -> monkey.inspectedItems)
                .sorted((a, b) -> -a.compareTo(b)).collect(Collectors.toList());

        return inspections.get(0) * inspections.get(1);
    }

    private void parseData(List<String> lines, List<Monkey> monkeys) {
        Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            // first line monkey def
            String line = iterator.next();

            // starting items
            line = iterator.next();
            line = line.split(": ")[1];

            List<Long> items = Stream.of(line.split(", ")).map(Long::parseLong).collect(Collectors.toList());

            // operation
            line = iterator.next();
            Operation operation = parseOperation(line);

            // test
            ModuloTest moduloTest = parseModuloTest(iterator.next(), iterator.next(), iterator.next());

            monkeys.add(new Monkey(new ArrayDeque<>(items), operation, moduloTest));

            if (iterator.hasNext()) {
                // empty line
                iterator.next();
            }
        }
    }

    private Operation parseOperation(String line) {

        Matcher matcher = operationPattern.matcher(line);
        if (matcher.matches()) {

            String firstTerm = matcher.group(1);
            String operation = matcher.group(2);
            String secondTerm = matcher.group(3);

            if (operation.equals("+")) {
                return new AddOperation(firstTerm, secondTerm);
            } else {
                return new TimeOperation(firstTerm, secondTerm);
            }
        } else {
            throw new RuntimeException(String.format("Nothiing group found in parse operation in %s", line));
        }

    }

    private ModuloTest parseModuloTest(String moduloLine, String nextIfTrueLine, String nextIfFalseLine) {
        Matcher moduloMatcher = moduloTesPattern.matcher(moduloLine);
        Matcher nextIfTrueMatcher = nextIfTruPattern.matcher(nextIfTrueLine);
        Matcher nextIfFalsMatcher = nextIfFalsePattern.matcher(nextIfFalseLine);

        if (!moduloMatcher.matches()) {
            throw new RuntimeException(String.format("No group found when parsing modulo in %s", moduloLine));
        }
        if (!nextIfTrueMatcher.matches()) {
            throw new RuntimeException(
                    String.format("No group found when parsing next if true line in %s", nextIfTrueLine));
        }
        if (!nextIfFalsMatcher.matches()) {
            throw new RuntimeException(
                    String.format("No group found when parsing next if false in %s", nextIfFalseLine));
        }
        long modulo = Long.parseLong(moduloMatcher.group(1));
        int nextIfTrue = Integer.parseInt(nextIfTrueMatcher.group(1));
        int nextIfFalse = Integer.parseInt(nextIfFalsMatcher.group(1));

        return new ModuloTest(modulo, nextIfTrue, nextIfFalse);
    }

}

interface Operation {
    final String variable = "old";

    long eval(long old);
}

class AddOperation implements Operation {

    boolean isFirstTermAVariable, isSecondTermVariable;
    long firstTerm, secondTerm;

    AddOperation(String firstTerm, String secondTerm) {

        if (firstTerm.equals(variable)) {
            isFirstTermAVariable = true;
        } else {
            isFirstTermAVariable = false;
            this.firstTerm = Long.parseLong(firstTerm);
        }

        if (secondTerm.equals(variable)) {
            isSecondTermVariable = true;
        } else {
            isSecondTermVariable = false;
            this.secondTerm = Long.parseLong(secondTerm);
        }
    }

    @Override
    public long eval(long old) {
        long firstTerm = isFirstTermAVariable ? old : this.firstTerm;
        long secondTerm = isSecondTermVariable ? old : this.secondTerm;

        return firstTerm + secondTerm;
    }

    @Override
    public String toString() {
        return "AddOperation [isFirstTermAVariable=" + isFirstTermAVariable + ", isSecondTermVariable="
                + isSecondTermVariable + ", firstTerm=" + firstTerm + ", secondTerm=" + secondTerm + "]";
    }

}

class TimeOperation implements Operation {

    boolean isFirstTermAVariable, isSecondTermVariable;
    long firstTerm, secondTerm;

    TimeOperation(String firstTerm, String secondTerm) {

        if (firstTerm.equals(variable)) {
            isFirstTermAVariable = true;
        } else {
            isFirstTermAVariable = false;
            this.firstTerm = Long.parseLong(firstTerm);
        }

        if (secondTerm.equals(variable)) {
            isSecondTermVariable = true;
        } else {
            isSecondTermVariable = false;
            this.secondTerm = Long.parseLong(secondTerm);
        }
    }

    @Override
    public long eval(long old) {
        long firstTerm = isFirstTermAVariable ? old : this.firstTerm;
        long secondTerm = isSecondTermVariable ? old : this.secondTerm;

        // System.out.println(String.format("firstTerm: %d, secondTerm: %d, product: %d", firstTerm, secondTerm, firstTerm * secondTerm));
        return firstTerm * secondTerm;
    }

    @Override
    public String toString() {
        return "TimeOperation [isFirstTermAVariable=" + isFirstTermAVariable + ", isSecondTermVariable="
                + isSecondTermVariable + ", firstTerm=" + firstTerm + ", secondTerm=" + secondTerm + "]";
    }

}

class ModuloTest {
    long modulo;
    int nextIfTrue, nextIfFalse;

    public ModuloTest(long modulo, int nextIfTrue, int nextIfFalse) {
        this.modulo = modulo;
        this.nextIfTrue = nextIfTrue;
        this.nextIfFalse = nextIfFalse;
    }

    @Override
    public String toString() {
        return "ModuloTest [modulo=" + modulo + ", nextIfTrue=" + nextIfTrue + ", nextIfFalse=" + nextIfFalse + "]";
    }

    int nextMonkey(long newWorry) {
        return newWorry % modulo == 0 ? nextIfTrue : nextIfFalse;
    }

    long computeNextWorry(long old) {
        // System.out.println(String.format("old: %d, modulo: %d, next: %f, %d", old, modulo, (((float) old) / 3),Math.floorDiv(old,3)));
        // return Math.floorDiv(old,3);
        return old;
    }
}

class Monkey {
    Deque<Long> items;
    Operation operation;
    ModuloTest moduloTest;
    long inspectedItems;

    public Monkey(Deque<Long> items, Operation operation, ModuloTest moduloTest) {
        this.items = items;
        this.operation = operation;
        this.moduloTest = moduloTest;
        inspectedItems = 0;
    }

    @Override
    public String toString() {
        return "Monkey [items=" + items + ", operation=" + operation + ", moduloTest=" + moduloTest
                + ", inspectedItems=" + inspectedItems + "]";
    }

    void turn(List<Monkey> monkeys, long modulusProduct) {

        Iterator<Long> iterator = items.iterator();

        inspectedItems += items.size();
        while (iterator.hasNext()) {
            Long item = iterator.next();
            long newItem = operation.eval(item);
            long newWorry = moduloTest.computeNextWorry(newItem) % modulusProduct;
            int nextMonkey = moduloTest.nextMonkey(newWorry);
            // System.out.println(String.format("item: %d, newWorry:%d, nextMonkey: %d", item, newWorry, nextMonkey));
            monkeys.get(nextMonkey).items.add(newWorry);
            iterator.remove();
        }

    }

}