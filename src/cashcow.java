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
        private final static int ROWS = 12;
        private final static int COLS = 10;
        private Character[][] board;
        private ArrayList<Move> moves;
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
            Character[][] scratch = copyBoard();
            Stack<Point> points = new Stack<>();
            points.push(new Point(current.row, current.column));

            while(!points.empty()) {
                Point currentPoint = points.pop();

                // Check North
                slotsCleared += testPoint(new Point(currentPoint.x - 1, currentPoint.y),
                        currentPoint, scratch, points);

                // Check East
                slotsCleared += testPoint(new Point(currentPoint.x, currentPoint.y + 1),
                        currentPoint, scratch, points);

                // Check South
                slotsCleared += testPoint(new Point(currentPoint.x + 1, currentPoint.y),
                        currentPoint, scratch, points);

                // Check West
                slotsCleared += testPoint(new Point(currentPoint.x, currentPoint.y + 1),
                        currentPoint, scratch, points);

            }

            if (slotsCleared > 3) {
                board = scratch;
                compactBoard();
            }else {
                slotsCleared = 0;
            }
            return slotsCleared;
        }

        private int testPoint(Point pToTest, Point pOld, Character[][] currentBoard, Stack<Point> points) {
            int cleared = 0;
            if (pToTest.x < ROWS && pToTest.x >= 0 && pToTest.y < COLS && pToTest.y >= 0) {
                if (board[pToTest.x][pToTest.y]!= null &&
                        board[pToTest.x][pToTest.y].equals(board[pOld.x][pOld.y])){
                    points.push(pToTest);

                    // Need to mark here instead of setting equal to null
                    board[pToTest.x][pToTest.y] = null;
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
            for (int i = ROWS - 1; i >= 0; i--) {
                for (int j = COLS; j >= 0; j--) {
                    while (board[i][j] == null && board[i][j - 1] != null) {
                        board[i][j] = board[i][j - 1];
                        board[i][j - 1] = null;
                    }
                }
            }

            // Compress the board leftward, row by row
            for (int i = 0; i < COLS - 1; i++) {
                boolean allNull = true;
                for (int j = 0; j < ROWS; j++) {
                    if (board[j][i] != null) {
                        allNull = false;
                        break;
                    }
                }
                if (allNull) {
                    for (int j = 0; j < ROWS; j++) {
                        board[j][i] = board[j + 1][i];
                        board[j + 1][i] = null;
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
