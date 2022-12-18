import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files
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
    final static String changeDirectoryCommandPrefix = "$ cd";
    final static String listingCommandPrefix = "$ ls";
    final static String directoryPrefix = "dir";
    final static String parentDirectory = "..";
    final static String fileSizePattern = "(\\d+)\\s([a-zA-Z0-9\\s\\.]+)";
    static final Pattern fileSize = Pattern.compile(fileSizePattern);

    Integer resolve(List<String> lines);
}

class SolutionPart1 implements Resolvable {

    public Integer resolve(List<String> lines) {
        DeviceDirectory root = new DeviceDirectory("/", null);

        Iterator<String> iterator = lines.iterator();

        DeviceDirectory pwd = root;

        // skip the cd /
        iterator.next();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (line.startsWith(changeDirectoryCommandPrefix)) {
                String directoryName = line.substring(changeDirectoryCommandPrefix.length() + 1);
                if (directoryName.equals(parentDirectory)) {
                    pwd = pwd.parent;

                } else {
                    DeviceDirectory newDir = new DeviceDirectory(directoryName, pwd);
                    pwd.addElement(newDir);
                    pwd = newDir;
                }
            } else if (line.startsWith(listingCommandPrefix)) {

            } else if (line.startsWith(directoryPrefix)) {

            } else {
                Matcher matcher = fileSize.matcher(line);

                matcher.matches();

                Integer size = Integer.parseInt(matcher.group(1));
                String name = matcher.group(2);

                DeviceFile file = new DeviceFile(name, pwd, size);
                pwd.addElement(file);
            }
        }

        return getAnswer(root);
    }

    Integer getAnswer(DeviceDirectory root) {
        final int threshold = 100000;
        int sum = 0;

        for (DeviceElement element : root.content) {
            Integer size = element.getSize();
            if (element instanceof DeviceDirectory) {
                if (size < threshold) {
                    sum += size;
                }
                sum += getAnswer((DeviceDirectory) element);
            }

        }

        return sum;
    }

}

abstract class DeviceElement {
    final String name;
    final DeviceDirectory parent;

    abstract Integer getSize();

    public DeviceElement(String name, DeviceDirectory parent) {
        this.name = name;
        this.parent = parent;
    }

}

class DeviceFile extends DeviceElement {

    final Integer size;

    public DeviceFile(String name, DeviceDirectory parent, Integer size) {
        super(name, parent);
        this.size = size;
    }

    @Override
    Integer getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "DeviceFile [name= " + name + ", size=" + size + "]";
    }

}

class DeviceDirectory extends DeviceElement {

    final List<DeviceElement> content;
    Integer size;

    public DeviceDirectory(String name, DeviceDirectory parent) {
        super(name, parent);
        this.content = new ArrayList<>();
        size = null;
    }

    @Override
    Integer getSize() {
        if (size != null) {
            return size;
        }

        int sum = 0;
        for (DeviceElement deviceElement : content) {
            sum += deviceElement.getSize();
        }

        size = sum;
        return sum;
    }

    void addElement(DeviceElement element) {
        content.add(element);
    }

    @Override
    public String toString() {
        return "DeviceDirectory [name=" + name + ", size=" + size + ", content=" + content + "]";
    }

}

class SolutionPart2 implements Resolvable {

    public Integer resolve(List<String> lines) {
        DeviceDirectory root = new DeviceDirectory("/", null);

        Iterator<String> iterator = lines.iterator();

        DeviceDirectory pwd = root;

        // skip the cd /
        iterator.next();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (line.startsWith(changeDirectoryCommandPrefix)) {
                String directoryName = line.substring(changeDirectoryCommandPrefix.length() + 1);
                if (directoryName.equals(parentDirectory)) {
                    pwd = pwd.parent;

                } else {
                    DeviceDirectory newDir = new DeviceDirectory(directoryName, pwd);
                    pwd.addElement(newDir);
                    pwd = newDir;
                }
            } else if (line.startsWith(listingCommandPrefix)) {

            } else if (line.startsWith(directoryPrefix)) {

            } else {
                Matcher matcher = fileSize.matcher(line);

                matcher.matches();

                Integer size = Integer.parseInt(matcher.group(1));
                String name = matcher.group(2);

                DeviceFile file = new DeviceFile(name, pwd, size);
                pwd.addElement(file);
            }
        }

        int threshold = 30000000 - (70000000 - root.getSize());
        List<DeviceDirectory> answer = getAnswer(root, threshold);
        return answer.stream().min((a, b) -> a.getSize().compareTo(b.getSize())).get().getSize();
    }

    List<DeviceDirectory> getAnswer(DeviceDirectory root, Integer threshold) {
        Integer rootSize = root.getSize();

        List<DeviceDirectory> list = new ArrayList<>();

        if (rootSize >= threshold) {
            list.add(root);
        }

        for (DeviceElement element : root.content) {
            if (element instanceof DeviceDirectory) {
                DeviceDirectory dir = (DeviceDirectory) element;
                list.addAll(getAnswer(dir, threshold));
            }
        }

        return list;
    }

}