// ---------------------------------------------------
// cashcow.java
// Jeremy Campbell
// Project 10 of Applied Algorithms
// I am unsatisfied with a lot of my coding practices
// in this project but I cannot afford to rewrite at
// the moment.
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

        @Override
        public String toString() {
            return "(" + row + ", " + column + ")";
        }
    }

    private static class GameBoard {
        private int ROWS = 12;
        private int COLS = 10;
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
            Stack<Point> points = new Stack<>();
            Character[][] scratch = board.clone();
            points.push(new Point(current.row, current.column));
            Character toFind = board[current.row][current.column];

            System.out.println("Gameboard before move : " + current);
            printGameState();

            while(!points.empty()) {
                Point currentPoint = points.pop();

                // Check North
                slotsCleared += testPoint(new Point(currentPoint.x - 1, currentPoint.y),
                        scratch, toFind, points);

                // Check East
                slotsCleared += testPoint(new Point(currentPoint.x, currentPoint.y + 1),
                        scratch, toFind, points);

                // Check South
                slotsCleared += testPoint(new Point(currentPoint.x + 1, currentPoint.y),
                        scratch, toFind, points);

                // Check West
                slotsCleared += testPoint(new Point(currentPoint.x, currentPoint.y - 1),
                        scratch, toFind, points);

            }

            if (slotsCleared >= 3) {
                board = scratch;
                System.out.println("Gameboard before compaction: ");
                printGameState();
                compactBoard();
            }else {
                slotsCleared = 0;
            }

            System.out.println("Gameboard after move : " + current);
            printGameState();

            return slotsCleared;
        }

        private int testPoint(Point pToTest, Character[][] gameboard, Character toFind, Stack<Point> points) {
            int cleared = 0;
            // If it's a valid move
            if (pToTest.x < ROWS && pToTest.x >= 0 && pToTest.y < COLS && pToTest.y >= 0) {
                // If the point hasn't already been tested and it is the same color
                if (gameboard[pToTest.x][pToTest.y] != null &&
                        gameboard[pToTest.x][pToTest.y].equals(toFind)){
                    points.push(pToTest);
                    gameboard[pToTest.x][pToTest.y] = null;
                    cleared = 1;
                }
            }
            return cleared;
        }

        private void compactBoard() {
            compactBoardUp();
            System.out.println("Gameboard after moving up: ");
            printGameState();
            compactBoardLeft();
        }

        private void compactBoardUp() {
            // Compress the board downward (which is actually upward in the way I've stored the data)
            for (int i = 0; i < COLS; i++) {
                for (int j = 0; j < ROWS - 1; j++) {
                    int column = i;
                    int row = j;
                    boolean noneFoundAbove = false;

                    while (!noneFoundAbove && board[row][column] == null) {
                        row++;
                        if (row >= ROWS) {
                            noneFoundAbove = true;
                        }
                    }
                    if (!noneFoundAbove && board[j][i] == null) {
                        // move was valid, move the value down
                        board[j][i] = board[row][column];
                        board[row][column] = null;
                    }
                }
            }
        }

        private void compactBoardLeft() {
            // Compress the board leftward, column by column
            for (int i = 0; i < COLS - 1; i++) {
                boolean currentColNull = checkIfColNull(i);

                // Go until either I find a column that is not null or there are no more columns to check
                if (currentColNull) {
                    int column = i + 1;
                    boolean nextColNull = true;
                    while (column < COLS && nextColNull) {
                        nextColNull = checkIfColNull(column);
                        column++;
                    }
                    if (!nextColNull) {
                        column--;
                        swapCols(i, column);
                    }
                }
            }
        }

        private boolean checkIfColNull(int col) {
            boolean allNull = true;
            for (int j = 0; j < ROWS; j++) {
                if (board[j][col] != null) {
                    allNull = false;
                    break;
                }
            }
            return allNull;
        }

        private void swapCols(int first, int second) {
            for (int i = 0; i < ROWS; i++) {
                board[i][first] = board[i][second];
                board[i][second] = null;
            }
        }

        // Debug function
        private void printGameState() {
            System.out.println("--------------------------");
            for (Character[] row : board) {
                for (Character c : row) {
                    if (c == null)
                        System.out.print(".");
                    else
                        System.out.print(c);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
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
