import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {
        List<String> lines = new ArrayList<>();
        try {
            Path path = Paths.get("input.txt");
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Solution solution = new Solution1();

        long startTime = System.nanoTime();
        long sol = solution.resolve(lines);
        long stopTime = System.nanoTime();

        System.out.println(sol);
        System.out.println(String.format("execution time: %f", (stopTime - startTime) / 1000000000f));

    }
}

interface Solution {
    long resolve(List<String> lines);
}

class Coord {
    int x, y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coord [x=" + x + ", y=" + y + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 11;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coord other = (Coord) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

}

class Solution1 implements Solution {

    int n, m;
    Map<Coord, Integer> map;
    Map<Coord, Set<Coord>> nbs;
    Coord end;

    @Override
    public long resolve(List<String> lines) {
        n = lines.size();
        m = lines.get(0).length();

        map = new HashMap<>();
        nbs = new HashMap<>();

        Coord start = new Coord(0, 0);
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                char charater = lines.get(i).charAt(j);
                Coord coord = new Coord(i, j);
                if (charater == 'S') {
                    start = coord;
                    charater = 'a';
                }
                if (charater == 'E') {
                    charater = 'z';
                    end = coord;
                }
                map.put(coord, charater - 'a');
                nbs.put(coord, new HashSet<>());
            }
        }

        for (Coord coord : map.keySet()) {
            for (Coord neighbour : List.of(new Coord(0, 1), new Coord(0, -1), new Coord(1, 0), new Coord(-1, 0))) {
                int x = coord.x + neighbour.x;
                int y = coord.y + neighbour.y;

                Coord newCoord = new Coord(x, y);
                if (map.containsKey(newCoord)) {
                    if (map.get(newCoord) <= 1 + map.get(coord)) {
                        nbs.get(coord).add(newCoord);
                    }
                }

            }
        }

        return dijkstra(start);
    }

    private long dijkstra(Coord start) {

        Deque<Coord> frontier = new LinkedList<>();
        Map<Coord, Integer> dist = new HashMap<>();

        frontier.add(start);
        dist.put(start, 0);

        while (!frontier.isEmpty()) {
            Coord coord = frontier.poll();
            if (coord.equals(end)) {
                return dist.get(coord);
            }

            for (Coord neigbour : nbs.get(coord)) {
                if (!dist.containsKey(neigbour)) {
                    dist.put(neigbour, dist.get(coord) + 1);
                    frontier.add(neigbour);
                }
            }
        }

        throw new RuntimeException("should not happen");

    }
}
