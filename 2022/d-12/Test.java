import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

class Scratch {
    static Map<List<Integer>, Set<List<Integer>>> nbs;
    static List<Integer> end = null;

    public static void main(String[] args) throws IOException {
        Map<List<Integer>, Integer> map = new HashMap<>();
        nbs = new HashMap<>();
        List<Integer> start = null;
        List<List<Integer>> starts = new ArrayList<>();
        Path path = Paths.get("input.txt");
        List<String> lines = Files.readAllLines(path);
        Scanner scanner = new Scanner(String.join("\n", lines));
        int x, y = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (x = 0; x < line.length(); ++x) {
                char c = line.charAt(x);
                if (c == 'S') {
                    c = 'a';
                    start = List.of(x, y);
                } else if (c == 'E') {
                    c = 'z';
                    end = List.of(x, y);
                }
                if (c == 'a') {
                    starts.add(List.of(x, y));
                }
                map.put(List.of(x, y), c - 'a');
                nbs.put(List.of(x, y), new HashSet<>());
            }
            ++y;
        }
        for (var node : map.keySet()) {
            x = node.get(0);
            y = node.get(1);
            for (var coords : List.of(List.of(0, 1), List.of(1, 0), List.of(0, -1), List.of(-1, 0))) {
                int xx = x + coords.get(0);
                int yy = y + coords.get(1);
                var newCoords = List.of(xx, yy);
                if (map.containsKey(newCoords) && map.get(newCoords) <= map.get(node) + 1)
                    nbs.get(node).add(newCoords);
            }
        }

        System.out.println(bfs(List.of(start)));
        System.out.println(bfs(starts));
    }

    static int bfs(List<List<Integer>> starts) {
        Queue<List<Integer>> q = new LinkedList<>(starts);
        Map<List<Integer>, Integer> dist = new HashMap<>();
        for (var start : starts) {
            dist.put(start, 0);
        }

        while (!q.isEmpty()) {
            var node = q.poll();
            if (node.equals(end)) {
                return dist.get(node);
            }
            for (var nb : nbs.get(node)) {
                if (!dist.containsKey(nb)) {
                    dist.put(nb, dist.get(node) + 1);
                    q.add(nb);
                }
            }
        }

        return -1;
    }
}