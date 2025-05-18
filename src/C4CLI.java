import java.util.Scanner;

public class C4CLI {
    static boolean debugMode = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int xWins = 0;
        int oWins = 0;
        int draws = 0;
        Boolean vsAI = null;
        C4Difficulty difficulty = null;
        C4Player humanPlayer = null;
        Character humanSymbol = null;
        Character aISymbol = null;
        Boolean playAgain;
        System.out.println("Welcome to Connect 4!");
        while (vsAI == null) {
            System.out.print("Play in 1-player or 2-player mode? (Enter 1 or 2): ");
            String userInput = scanner.nextLine();
            vsAI = C4Utils.isOnePlayerModeParser(userInput);
        }
        if (vsAI) {
            while (difficulty == null) {
                System.out.print(
                        "Choose a difficulty: Easy (E), Medium (M), Hard (H), or Insane (I): ");
                String userInput = scanner.nextLine();
                difficulty = C4Utils.parseDifficulty(userInput);
            }
            while (humanPlayer == null) {
                System.out.print("Choose a player: X or O: ");
                String userInput = scanner.nextLine();
                humanPlayer = C4Utils.parsePlayer(userInput);
            }
            humanSymbol = humanPlayer.getSymbol();
            aISymbol = humanPlayer.next().getSymbol();
        }
        do {
            playAgain = null;
            C4Game game = new C4Game();
            C4Player currentC4Player = C4Player.X;
            // All your game logic (player/difficulty input, board setup, game loop)
            while (true) {
                game.printBoard();

                System.out.println("Player " + currentC4Player.getSymbol() + "'s turn:");

                int col;

                if (vsAI && currentC4Player != humanPlayer) {
                    System.out.println("🤖 AI is thinking...");
                    int depth = C4Utils.getDepth(difficulty);

                    if (difficulty == C4Difficulty.EASY && Math.random() < 0.5) {
                        col = C4Utils.getRandomValidMove(game.getBoard(), debugMode);
                    } else if (difficulty == C4Difficulty.MEDIUM && Math.random() < 0.25) {
                        col = C4Utils.getRandomValidMove(game.getBoard(), debugMode);
                    } else {
                        col = C4AI.getBestMove(
                                C4Utils.copyBoard(game.getBoard()), depth,
                                aISymbol, humanSymbol, difficulty, debugMode);
                    }
                    System.out.println("AI chooses column " + (col + 1));
                } else {
                    col = game.getValidColumn(scanner);
                }

                int row = game.dropPiece(col, currentC4Player.getSymbol());

                if (row == -1) {
                    System.out.println("❌ That column is full. Try again.");
                    continue;
                }
                if (C4Utils.checkWin(game.getBoard(), currentC4Player.getSymbol())) {
                    game.printBoard();
                    System.out.println("🎉 Player " + currentC4Player.getSymbol() + " wins!");

                    if (currentC4Player.getSymbol() == 'X')
                        xWins++;
                    else
                        oWins++;
                    break;
                }
                if (C4Utils.boardIsFull(game.getBoard())) {
                    game.printBoard();
                    System.out.println("🤝 It's a draw! No more moves.");

                    draws++;
                    break;
                }
                currentC4Player = currentC4Player.next();
            }
            // Prompt to play again, then set playAgain based on input
            while (playAgain == null) {
                System.out.print("Play again? (Y/N): ");
                String userInput = scanner.nextLine();
                playAgain = C4Utils.playAgainParser(userInput);
            }
        } while (playAgain);

        System.out.println("\n📊 Game Summary:");
        System.out.println("🟥 X wins: " + xWins);
        System.out.println("🟦 O wins: " + oWins);
        System.out.println("🤝 Draws:  " + draws);
        System.out.println("Thanks for playing Connect 4! 🎉");
        scanner.close();


    }
}
