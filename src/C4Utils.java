import java.util.ArrayList;
import java.util.List;

public class C4Utils {
    public static char[][] getAllWindows(char[][] board) {
        int ROWS = board.length;
        int COLS = board[0].length;

        // Count how many windows there will be
        int windowCount = 0;

        windowCount += ROWS * (COLS - 3);          // horizontal
        windowCount += (ROWS - 3) * COLS;          // vertical
        windowCount += (ROWS - 3) * (COLS - 3);     // diagonal down-right
        windowCount += (ROWS - 3) * (COLS - 3);     // diagonal up-right

        char[][] windows = new char[windowCount][4];
        int index = 0;

        // Horizontal
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                windows[index++] = new char[]{
                        board[row][col],
                        board[row][col + 1],
                        board[row][col + 2],
                        board[row][col + 3]
                };
            }
        }

        // Vertical
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col < COLS; col++) {
                windows[index++] = new char[]{
                        board[row][col],
                        board[row + 1][col],
                        board[row + 2][col],
                        board[row + 3][col]
                };
            }
        }

        // Diagonal down-right
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                windows[index++] = new char[]{
                        board[row][col],
                        board[row + 1][col + 1],
                        board[row + 2][col + 2],
                        board[row + 3][col + 3]
                };
            }
        }

        // Diagonal up-right
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                windows[index++] = new char[]{
                        board[row][col],
                        board[row - 1][col + 1],
                        board[row - 2][col + 2],
                        board[row - 3][col + 3]
                };
            }
        }

        return windows;
    }

    public static boolean checkWin(char[][] board, char symbol) {
        for (char[] window : getAllWindows(board)) {
            if (window[0] == symbol &&
                    window[1] == symbol &&
                    window[2] == symbol &&
                    window[3] == symbol) {
                return true;
            }
        }
        return false;
    }

    public static boolean boardIsFull(char[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidMove(char[][] board, int col) {
        return col >= 0 && col < board[0].length && board[0][col] == ' ';
    }

    public static char[][] copyBoard(char[][] board) {
        int ROWS = board.length;
        int COLS = board[0].length;
        char[][] copy = new char[ROWS][COLS];

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                copy[row][col] = board[row][col];
            }
        }

        return copy;
    }

    public static void makeMove(char[][] board, int col, char symbol) {
        for (int row = board.length - 1; row >= 0; row--) {
            if (board[row][col] == ' ') {
                board[row][col] = symbol;
                break;
            }
        }
    }

    public static int getDepth(C4Difficulty c4Difficulty) {
        switch (c4Difficulty) {
            case EASY:
                return 2;
            case MEDIUM:
                return 4;
            case HARD:
                return 6;
            case INSANE:
                return 8;
            default:
                throw new IllegalArgumentException("Unhandled difficulty: " + c4Difficulty);
        }
    }

    public static C4Difficulty parseDifficulty(String input) {
        input = input.trim().toUpperCase();

        switch (input) {
            case "E":
            case "EASY":
                return C4Difficulty.EASY;
            case "M":
            case "MEDIUM":
                return C4Difficulty.MEDIUM;
            case "H":
            case "HARD":
                return C4Difficulty.HARD;
            case "I":
            case "INSANE":
                return C4Difficulty.INSANE;
            default:
                return null; // invalid input
        }
    }

    //Wrapped as Boolean so I can return null.  Interestingly,
    //this makes Boolean a 3-choice thing. True, false, or null :)
    public static Boolean isOnePlayerModeParser(String input) {
        input = input.trim().toUpperCase();

        switch (input) {
            case "1":
                return true;
            case "2":
                return false;
            default:
                return null;
        }
    }

    public static C4Player parsePlayer(String input) {
        input = input.trim().toUpperCase();

        switch (input) {
            case "X":
                return C4Player.X;
            case "O":
                return C4Player.O;
            default:
                return null;
        }
    }

    public static Boolean playAgainParser(String input) {
        input = input.trim().toUpperCase();

        switch (input) {
            case "Y":
                return true;
            case "N":
                return false;
            default:
                return null;
        }
    }

    public static int getRandomValidMove(char[][] board, boolean debugMode) {
        List<Integer> validMoves = new ArrayList<>();
        for (int col = 0; col < board[0].length; col++) {
            if (isValidMove(board, col)) {
                validMoves.add(col);
            }
        }
        if (debugMode) {
            System.out.println("Making random move!");
        }
        if (validMoves.isEmpty()) {
            throw new IllegalStateException("No valid moves available.");
        }
        int index = (int) (Math.random() * validMoves.size());
        return validMoves.get(index);
    }
}