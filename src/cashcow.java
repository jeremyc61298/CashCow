// ---------------------------------------------------
// cashcow.java
// Jeremy Campbell
// Project 10 of Applied Algorithms
// ---------------------------------------------------

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

public class cashcow {

    private static class Move {
        public int column;
        public int row;

        Move(Character column, int row) {
            this.column = column - 'a';
            this.row = row - 1;
        }
    }

    private static class GameBoard {
        private int ROWS = 12;
        private int COLS = 10;
        private Character[][] board;
        private ArrayList<Move> moves;
        HashSet<Point> markedSet;
        private int numMoves;
        private int totalCharacters = ROWS * COLS;

        GameBoard(int numMoves) {
            board = new Character[ROWS][COLS];
            moves = new ArrayList<>(numMoves);
            this.numMoves = numMoves;
        }

        public void inputBoard(Scanner fin) {
            for (int i = ROWS - 1; i >= 0; i--) {
                String line = fin.nextLine();
                char[] charLine = line.toCharArray();
                for (int j = 0; j < COLS; j++) {
                    board[i][j] = charLine[j];
                }
            }
        }

        public void inputMoves(Scanner fin) {
            for (int i = 0; i < numMoves; i++) {
                String col = fin.next();
                char c = col.toCharArray()[0];
                int m = fin.nextInt();
                Move move = new Move(c, m);
                moves.add(move);
            }
        }

        public void playGame() {
            for (Move currentMove : moves) {
                Character currentChar = board[currentMove.row][currentMove.column];
                if (currentChar != null) {
                    totalCharacters -= clearCluster(currentMove);
                }
            }
        }

        // Returns the number of slots cleared
        private int clearCluster(Move current) {
            int slotsCleared = 0;
            Stack<Point> points = new Stack<>();
            markedSet = new HashSet<>();
            ArrayList<Point> pointsTested = new ArrayList<>();
            points.push(new Point(current.row, current.column));

            while(!points.empty()) {
                Point currentPoint = points.pop();

                // Check North
                slotsCleared += testPoint(new Point(currentPoint.x - 1, currentPoint.y),
                        currentPoint, pointsTested, points);

                // Check East
                slotsCleared += testPoint(new Point(currentPoint.x, currentPoint.y + 1),
                        currentPoint, pointsTested, points);

                // Check South
                slotsCleared += testPoint(new Point(currentPoint.x + 1, currentPoint.y),
                        currentPoint, pointsTested, points);

                // Check West
                slotsCleared += testPoint(new Point(currentPoint.x, currentPoint.y - 1),
                        currentPoint, pointsTested, points);

            }

            if (slotsCleared > 3) {
                compactBoard();
            }else {
                slotsCleared = 0;
                for (Point p : pointsTested) {
                    markedSet.remove(p);
                }
            }
            pointsTested.clear();
            return slotsCleared;
        }

        private int testPoint(Point pToTest, Point pOld, ArrayList<Point> pointsTested, Stack<Point> points) {
            int cleared = 0;
            // If it's a valid move
            if (pToTest.x < ROWS && pToTest.x >= 0 && pToTest.y < COLS && pToTest.y >= 0) {
                // If the point hasn't already been tested and it is the same color
                if (!markedSet.contains(pToTest) &&
                        board[pToTest.x][pToTest.y].equals(board[pOld.x][pOld.y])){
                    points.push(pToTest);

                    markedSet.add(pToTest);
                    pointsTested.add(pToTest);
                    cleared = 1;
                }
            }
            return cleared;
        }

        private Character[][] copyBoard() {
            Character[][] scratch = new Character[ROWS][COLS];
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    scratch[i][j] = board[i][j];
                }
            }
            return scratch;
        }

        private void compactBoard() {
            // Compress the board downward
            for (int i = ROWS - 2; i > 0; i--) {
                for (int j = COLS - 1; j >= 0; j--) {
                    Point current = new Point(i, j);
                    Point above = new Point(i - 1, j);
                    while (markedSet.contains(current) && !markedSet.contains(above)) {
                        markedSet.remove(current);
                        board[i][j] = board[i - 1][j];
                        markedSet.add(above);
                    }
                }
            }

            // Compress the board leftward, row by row
            for (int i = 0; i < COLS - 1; i++) {
                boolean allInvalid = true;
                for (int j = 0; j < ROWS; j++) {
                    if (!markedSet.contains(new Point(j, i))) {
                        allInvalid = false;
                        break;
                    }
                }
                if (allInvalid) {
                    for (int j = 0; j < ROWS - 1; j++) {
                        markedSet.remove(new Point(j, i));
                        board[j][i] = board[j + 1][i];
                        markedSet.add(new Point(j + 1, i));
                    }
                }
            }
        }

        // Debug function
        private void printBoard() {
            for (Character[] row : board) {
                for (Character c : row) {
                    System.out.print(c);
                }
                System.out.println();
            }
        }

        public void printScore(PrintWriter fout) {
            fout.print(totalCharacters);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner fin = new Scanner(new File("cashcow.in"));
        PrintWriter fout = new PrintWriter((new File("cashcow.out")));

        int numMoves = fin.nextInt();
        while (numMoves != 0) {
            // This is to skip to the end of the current line
            fin.nextLine();

            GameBoard gb = new GameBoard(numMoves);
            gb.inputBoard(fin);
            gb.inputMoves(fin);
            gb.playGame();
            gb.printScore(fout);
            numMoves = fin.nextInt();
            if (numMoves != 0) {
                fout.println();
            }
        }

        fin.close();
        fout.close();
    }

}
