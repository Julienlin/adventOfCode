import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

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

enum Dimension {
    ROW, COL;
}

enum Direction {
    TO_RIGHT,
    TO_DOWN,
    TO_LEFT,
    TO_UP;
}

class SolutionPart1 implements Resolvable {

    int rows, cols;

    @Override
    public Integer resolve(List<String> lines) {
        rows = lines.size();
        cols = lines.get(0).length();

        int[][] heights = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                heights[i][j] = Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
            }
        }

        boolean[][] isVisible = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            isVisible[i][0] = true;
            isVisible[i][cols - 1] = true;
        }

        for (int i = 0; i < cols; i++) {
            isVisible[0][i] = true;
            isVisible[rows - 1][i] = true;
        }

        for (int i = 1; i < rows - 1; i++) {
            updateIsVisible(isVisible, heights, i, 0, Dimension.ROW, Direction.TO_RIGHT);
            updateIsVisible(isVisible, heights, i, cols - 1, Dimension.ROW, Direction.TO_LEFT);
        }

        for (int i = 1; i < cols - 1; i++) {
            updateIsVisible(isVisible, heights, 0, i, Dimension.COL, Direction.TO_DOWN);
            updateIsVisible(isVisible, heights, rows - 1, i, Dimension.COL,
                    Direction.TO_UP);
        }

        return countVisible(isVisible);
    }

    void updateIsVisible(boolean[][] isVisible, int[][] heights, int currentRow, int currentCol, Dimension dimension,
            Direction direction) {
        int maxHeight = heights[currentRow][currentCol];
        if (dimension == Dimension.ROW) {
            if (direction == Direction.TO_RIGHT) {
                for (int i = currentCol + 1; i < cols - 1; i++) {
                    int nextHeight = heights[currentRow][i];
                    if (maxHeight < nextHeight) {
                        isVisible[currentRow][i] = true;
                        maxHeight = nextHeight;
                    }
                }
            } else {
                for (int i = currentCol - 1; i > 0; i--) {
                    int nextHeight = heights[currentRow][i];

                    if (maxHeight < nextHeight) {
                        isVisible[currentRow][i] = true;
                        maxHeight = nextHeight;
                    }
                }
            }
        } else {
            if (direction == Direction.TO_DOWN) {
                for (int i = currentRow + 1; i < rows - 1; i++) {
                    int nextHeight = heights[i][currentCol];
                    if (maxHeight < nextHeight) {
                        isVisible[i][currentCol] = true;
                        maxHeight = nextHeight;
                    }
                }
            } else {
                for (int i = currentRow - 1; i > 0; i--) {
                    int nextHeight = heights[i][currentCol];
                    if (maxHeight < nextHeight) {
                        isVisible[i][currentCol] = true;
                        maxHeight = nextHeight;
                    }
                }
            }
        }
    }

    Integer countVisible(boolean[][] isVisible) {
        int count = 0;

        for (boolean[] bs : isVisible) {
            for (boolean tree : bs) {
                if (tree) {
                    count++;
                }
            }
        }
        return count;
    }

    void displayGrid(boolean[][] isVisible) {
        for (boolean[] ts : isVisible) {
            for (boolean elemenT : ts) {
                System.out.print(elemenT ? 1 : 0);
            }
            System.out.println();
        }
    }

    void displayGrid(int[][] heights) {
        for (int[] ts : heights) {
            for (int elemenT : ts) {
                System.out.print(elemenT);
            }
            System.out.println();
        }
    }

}

class SolutionPart2 implements Resolvable {

    int rows, cols;

    @Override
    public Integer resolve(List<String> lines) {
        rows = lines.size();
        cols = lines.get(0).length();

        int[][] heights = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                heights[i][j] = Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
            }
        }

        // boolean[][] isVisible = new boolean[rows][cols];

        // for (int i = 0; i < rows; i++) {
        // isVisible[i][0] = true;
        // isVisible[i][cols - 1] = true;
        // }

        // for (int i = 0; i < cols; i++) {
        // isVisible[0][i] = true;
        // isVisible[rows - 1][i] = true;
        // }

        // for (int i = 1; i < rows - 1; i++) {
        // updateIsVisible(isVisible, heights, i, 0, Dimension.ROW, Direction.TO_RIGHT);
        // updateIsVisible(isVisible, heights, i, cols - 1, Dimension.ROW,
        // Direction.TO_LEFT);
        // }

        // for (int i = 1; i < cols - 1; i++) {
        // updateIsVisible(isVisible, heights, 0, i, Dimension.COL, Direction.TO_DOWN);
        // updateIsVisible(isVisible, heights, rows - 1, i, Dimension.COL,
        // Direction.TO_UP);
        // }
        // displayGrid(isVisible);
        // displayGrid(heights);

        int maxScore = 0;

        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                int score = scenicScore(heights, i, j);
                if (maxScore < score) {
                    maxScore = score;
                }
            }
        }

        return maxScore;
    }

    void updateIsVisible(boolean[][] isVisible, int[][] heights, int currentRow, int currentCol, Dimension dimension,
            Direction direction) {
        int maxHeight = heights[currentRow][currentCol];
        if (dimension == Dimension.ROW) {
            if (direction == Direction.TO_RIGHT) {
                for (int i = currentCol + 1; i < cols - 1; i++) {
                    int nextHeight = heights[currentRow][i];
                    if (maxHeight < nextHeight) {
                        isVisible[currentRow][i] = true;
                        maxHeight = nextHeight;
                    }
                }
            } else {
                for (int i = currentCol - 1; i > 0; i--) {
                    int nextHeight = heights[currentRow][i];

                    if (maxHeight < nextHeight) {
                        isVisible[currentRow][i] = true;
                        maxHeight = nextHeight;
                    }
                }
            }
        } else {
            if (direction == Direction.TO_DOWN) {
                for (int i = currentRow + 1; i < rows - 1; i++) {
                    int nextHeight = heights[i][currentCol];
                    if (maxHeight < nextHeight) {
                        isVisible[i][currentCol] = true;
                        maxHeight = nextHeight;
                    }
                }
            } else {
                for (int i = currentRow - 1; i > 0; i--) {
                    int nextHeight = heights[i][currentCol];
                    if (maxHeight < nextHeight) {
                        isVisible[i][currentCol] = true;
                        maxHeight = nextHeight;
                    }
                }
            }
        }
    }

    Integer scenicScore(int[][] heights, int currentRow, int currentCol) {
        int currentHeight = heights[currentRow][currentCol];
        int countUp = 0;
        int i = currentRow - 1;

        while (i >= 0) {
            int height = heights[i][currentCol];
            countUp++;
            if (height < currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out.println(String.format("height[%d|[%d] = %d < %d", i, currentCol,
                // height, currentHeight));
                // }
            } else if (height == currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out
                // .println(String.format("height[%d|[%d] = %d == %d", i, currentCol, height,
                // currentHeight));
                // }
                break;
            } else {
                break;
            }
            i--;
        }

        // if (currentRow == 3 && currentCol == 2) {
        // System.out.println(String.format("[%d|[%d] => countUp %d", currentRow,
        // currentCol, countUp));
        // }

        int countDown = 0;
        i = currentRow + 1;

        while (i < rows) {
            int height = heights[i][currentCol];
            countDown++;
            if (height < currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out.println(String.format("height[%d|[%d] = %d < %d", i, currentCol,
                // height, currentHeight));
                // }
            } else if (height == currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out
                // .println(String.format("height[%d|[%d] = %d == %d", i, currentCol, height,
                // currentHeight));
                // }
                break;
            } else {
                break;
            }
            i++;
        }

        // if (currentRow == 3 && currentCol == 2) {
        // System.out.println(String.format("[%d|[%d] => countDown %d", currentRow,
        // currentCol, countDown));
        // }

        int countLeft = 0;
        i = currentCol - 1;

        while (i >= 0) {
            int height = heights[currentRow][i];
            countLeft++;
            if (height < currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out.println(String.format("height[%d|[%d] = %d < %d", currentRow, i,
                // height, currentHeight));
                // }
            } else if (height == currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out
                // .println(String.format("height[%d|[%d] = %d == %d", currentRow, i, height,
                // currentHeight));
                // }
                break;
            } else {
                break;
            }
            i--;
        }

        // if (currentRow == 3 && currentCol == 2) {
        // System.out.println(String.format("[%d|[%d] => countLeft %d", currentRow,
        // currentCol, countLeft));
        // }

        int countRight = 0;
        i = currentCol + 1;

        while (i < cols) {
            int height = heights[currentRow][i];
            countRight++;
            if (height < currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out.println(String.format("height[%d|[%d] = %d < %d", currentRow, i,
                // height, currentHeight));
                // }
            } else if (height == currentHeight) {
                // if (currentRow == 3 && currentCol == 2) {
                // System.out
                // .println(String.format("height[%d|[%d] = %d == %d", currentRow, i, height,
                // currentHeight));
                // }
                break;
            } else {
                break;
            }
            i++;
        }

        // if (currentRow == 3 && currentCol == 2) {
        // System.out.println(String.format("[%d|[%d] => countRight %d", currentRow,
        // currentCol, countRight));
        // }
        return countUp * countDown * countLeft * countRight;

    }

    void displayGrid(boolean[][] isVisible) {
        for (boolean[] ts : isVisible) {
            for (boolean elemenT : ts) {
                System.out.print(elemenT ? 1 : 0);
            }
            System.out.println();
        }
    }

    void displayGrid(int[][] heights) {
        for (int[] ts : heights) {
            for (int elemenT : ts) {
                System.out.print(elemenT);
            }
            System.out.println();
        }
    }

}