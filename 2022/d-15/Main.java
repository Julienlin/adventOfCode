import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main
 */
public class Main {

    static Pattern pattern = Pattern
            .compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");

    public static void main(String[] args) {
        try {
            File myObj = new File("test.txt");
            Scanner myReader = new Scanner(myObj);
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE; 
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE; 
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                Matcher matcher = pattern.matcher(data);
                matcher.find();

                int sensorX = Integer.parseInt(matcher.group(1));
                int sensorY = Integer.parseInt(matcher.group(2)) ;
                int beaconX = Integer.parseInt(matcher.group(3));
                int beaconY = Integer.parseInt(matcher.group(4));

                minX = Math.min(Math.min(minX, sensorX), beaconX);
                maxX = Math.max(Math.max(maxX, sensorX), beaconX);
                minY = Math.min(Math.min(minY, sensorY), beaconY);
                maxY = Math.max(Math.max(maxY, sensorY), beaconY);


                // System.out.println(String.join("\t", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4)));

                Sensor sensor = new Sensor(sensorX, sensorY, mDist(sensorX, sensorY, beaconX, beaconY));
                System.out.println(sensor);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static int mDist(int x1, int y1, int x2, int y2){
        return Math.abs(x2-x1) + Math.abs(y2-y1);
    }
}

class Sensor {
    int x, y;
    int range;
    public Sensor(int x, int y, int range) {
        this.x = x;
        this.y = y;
        this.range = range;
    }
    @Override
    public String toString() {
        return "Sensor [x=" + x + ", y=" + y + ", range=" + range + "]";
    }

    
}