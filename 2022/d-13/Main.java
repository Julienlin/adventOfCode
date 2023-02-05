import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    enum OrderResult {
        InOrder,
        OutOfOrder,
        Unknown
    }

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part2() {

        try (BufferedReader reader = new BufferedReader(new FileReader("input2.txt"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }

            lines.sort((a, b) -> isInOrder(a, b) == OrderResult.OutOfOrder ? 1 : -1);

            int firstKey = lines.indexOf("[[2]]") + 1;
            int secondKey = lines.indexOf("[[6]]") + 1;

            System.out.println(
                    String.format("part 2 result: %d", firstKey * secondKey));

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void part1() {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            List<String> lines = new ArrayList<>();
            String line;
            int lineNb = 1;
            int sum = 0;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
                if (lines.size() == 2) {
                    // process 3 lines here
                    if (isInOrder(lines.get(0), lines.get(1)) != OrderResult.OutOfOrder) {
                        sum += lineNb;
                    }
                    lines.clear();
                }
                if (line.isBlank()) {
                    lineNb++;
                }
            }
            // process remaining lines, if any
            if (lines.size() > 0) {
                System.out.println(lines);
            }

            System.out.println(String.format("part 1 result: %d", sum));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static OrderResult isInOrder(String left, String right) {
        assert left.startsWith("[");
        assert right.startsWith("[");

        List<String> leftElements = parsePacket(left);
        List<String> rightElements = parsePacket(right);

        if (leftElements.size() == 0 && rightElements.size() > 0) {
            return OrderResult.InOrder;
        }

        if (leftElements.size() > 0 && rightElements.size() == 0) {
            return OrderResult.OutOfOrder;
        }

        Iterator<String> leftElementsIterator = leftElements.iterator();
        Iterator<String> rightElementsIterator = rightElements.iterator();

        while (leftElementsIterator.hasNext() && rightElementsIterator.hasNext()) {
            String leftElement = leftElementsIterator.next();
            String rightElement = rightElementsIterator.next();

            boolean isLeftAList = leftElement.startsWith("[");
            boolean isRightAList = rightElement.startsWith("[");

            if (!isLeftAList && !isRightAList) {
                // both are integers
                int leftIntVal = Integer.parseInt(leftElement);
                int rightIntVal = Integer.parseInt(rightElement);

                if (leftIntVal != rightIntVal) {
                    return leftIntVal < rightIntVal ? OrderResult.InOrder : OrderResult.OutOfOrder;
                }
            } else if (isLeftAList && isRightAList) {
                Main.OrderResult result = isInOrder(leftElement, rightElement);
                if (result != OrderResult.Unknown) {
                    return result;
                }
            } else {
                if (isLeftAList) {
                    OrderResult result = isInOrder(leftElement, "[" + rightElement + "]");
                    if (result != OrderResult.Unknown) {
                        return result;
                    }
                } else {
                    OrderResult result = isInOrder("[" + leftElement + "]", rightElement);
                    if (result != OrderResult.Unknown) {
                        return result;
                    }
                }
            }
        }

        if (leftElementsIterator.hasNext()) {
            return OrderResult.OutOfOrder;
        }

        return OrderResult.Unknown;
    }

    private static List<String> parsePacket(String packet) {
        List<String> res = new ArrayList<>();

        int bracketCount = 0;
        for (int i = 1; i < packet.length() - 1; i++) {
            if (packet.charAt(i) == '[') {
                bracketCount++;
                int j;
                for (j = i + 1; bracketCount > 0; j++) {
                    if (packet.charAt(j) == ']') {
                        bracketCount--;
                    } else if (packet.charAt(j) == '[') {
                        bracketCount++;
                    }
                }

                String substring = packet.substring(i, j);
                res.add(substring);
                i = j;
            } else {
                int j;
                for (j = i + 1; j < packet.length() - 1 && packet.charAt(j) != ','; j++) {
                }
                String substring = packet.substring(i, j);
                res.add(substring);
                i = j;
            }
        }

        return res;
    }

}