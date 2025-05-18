import java.util.Arrays;

public class C4AI {

    //This is my Heuristic function
    public static int evaluateWindow(char[] window, char aiSymbol, char opponentSymbol) {
        int aiCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;

        for (char c : window) {
            if (c == aiSymbol) aiCount++;
            else if (c == opponentSymbol) opponentCount++;
            else emptyCount++;
        }

        // Scoring based on counts — must come BEFORE mixed window check
        if (aiCount == 3 && emptyCount == 1) return +10000;
        if (opponentCount == 3 && emptyCount == 1) return -10000;
        if (aiCount == 2 && emptyCount == 2) return +100;
        if (opponentCount == 2 && emptyCount == 2) return -100;

        // Mixed window — least important
        if (aiCount > 0 && opponentCount > 0) return -5;

        return 0;
    }

    public static int evaluateBoard(char[][] board, char aiSymbol, char opponentSymbol) {
        int score = 0;
        for (char[] window : C4Utils.getAllWindows(board)) {
            int windowScore = evaluateWindow(window, aiSymbol, opponentSymbol);

            score += windowScore;
        }
        return score;
    }



    public static int getBestMove(char[][] board, int depth, char aiSymbol,
                                  char opponentSymbol, C4Difficulty difficulty, boolean debugMode){

        // 🎲 Randomness for Easy and Medium difficulties
        if (difficulty == C4Difficulty.EASY && Math.random() < 0.5) {
            return C4Utils.getRandomValidMove(board, debugMode);
        }

        int bestEval = Integer.MIN_VALUE;
        int bestCol = -1;

        for (int col = 0; col < board[0].length; col++) {

            if (C4Utils.isValidMove(board, col)) {
                char[][] copy = C4Utils.copyBoard(board);
                C4Utils.makeMove(copy, col, aiSymbol);
                if (C4Utils.checkWin(copy, aiSymbol)) {
                    return col; // take the win immediately
                }
                int eval = minimax(copy, depth - 1, false, aiSymbol, opponentSymbol,
                        Integer.MIN_VALUE, Integer.MAX_VALUE, debugMode);
                if (debugMode) {
                    System.out.println("Evaluating column " + col + ": score = " + eval);
                }
                if (eval > bestEval) {
                    bestEval = eval;
                    bestCol = col;
                }
            }
        }
        if (bestCol == -1) {
            // As a last resort, return any valid move.
            bestCol = C4Utils.getRandomValidMove(board, false);
        }
        return bestCol;
    }



    public static int minimax(char[][] board, int depth, boolean isMaximizing,
                              char aiSymbol, char opponentSymbol, int alpha, int beta, boolean debugMode) {

        // Base case
        if (C4Utils.checkWin(board, aiSymbol)) {
            return 100000; // big score for AI win
        }

        if (C4Utils.checkWin(board, opponentSymbol)) {
            return -100000; // big penalty if opponent has won
        }
        if (depth == 0 || C4Utils.boardIsFull(board)) {
            int score = evaluateBoard(board, aiSymbol, opponentSymbol);
            if (debugMode) {
                System.out.println("📄 Leaf heuristic: " + score);
                // Optionally: print the board state too
                // C4Utils.printBoard(board);
            }
            return score;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col = 0; col < board[0].length; col++) {
                if (C4Utils.isValidMove(board, col)) {
                    char[][] copy = C4Utils.copyBoard(board);
                    C4Utils.makeMove(copy, col, aiSymbol);
                    int eval = minimax(copy, depth - 1, false, aiSymbol,
                            opponentSymbol, alpha, beta, debugMode);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);

                    if (beta <= alpha) {
                        if (debugMode) System.out.println("🔪 Pruned branch at column " + col);
                        break; // prune remaining siblings
                    }
                }
            }
            return maxEval;
        } else { // isMinimizing
            int minEval = Integer.MAX_VALUE;
            for (int col = 0; col < board[0].length; col++) {
                if (C4Utils.isValidMove(board, col)) {
                    char[][] copy = C4Utils.copyBoard(board);
                    C4Utils.makeMove(copy, col, opponentSymbol);
                    int eval = minimax(copy, depth - 1, true, aiSymbol,
                            opponentSymbol, alpha, beta, debugMode);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);

                    if (beta <= alpha) {
                        if (debugMode) System.out.println("🔪 Pruned branch at column " + col);
                        break; // prune remaining siblings
                    }
                }
            }
            return minEval;
        }
    }
}