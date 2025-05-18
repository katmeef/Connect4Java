import java.util.Scanner;

public class C4Game {
    private final char[][] board;
    private static final int ROWS = 6;
    private static final int COLS = 7;

    public C4Game() {
        board = new char[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = ' ';
            }
        }
    }

    public void printBoard() {
        System.out.println(" 1 2 3 4 5 6 7");
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                System.out.print("|" + board[r][c]);
            }
            System.out.println("|");
        }
    }

    public int dropPiece(int column, char playerSymbol) {
        // Start from the bottom row
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == ' ') {
                board[row][column] = playerSymbol;
                return row; // success
            }
        }
        return -1; // the column is full
    }

    public int getValidColumn(Scanner scanner) {
        while (true) {
            System.out.print("Enter a column (1-7): ");

            if (scanner.hasNextInt()) {
                int col = scanner.nextInt();
                scanner.nextLine(); // consume the leftover newline
                col--;

                if (col >= 0 && col < COLS) {
                    return col;
                } else {
                    System.out.println("Sorry!  Column must be between 0 and 6.");
                }
            } else {
                System.out.println("Sorry!  Invalid input. Please enter a number.");
                scanner.next(); // Clear bad input
            }
        }
    }

    public char[][] getBoard() {
        return this.board;
    }
}