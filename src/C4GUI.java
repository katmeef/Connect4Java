import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

public class C4GUI extends JFrame {

    private final JButton[] columnButtons = new JButton[7]; // Buttons for each column
    private final JLabel[][] cellLabels = new JLabel[6][7];  // Grid of display labels
    private C4Game game = new C4Game(); // Uses your actual game logic
    private C4Player currentPlayer = C4Player.X;
    private boolean vsAI;
    private C4Player humanPlayer;
    private C4Player aiPlayer;
    private C4Difficulty difficulty;
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private static final Logger logger = Logger.getLogger(C4GUI.class.getName());

    public C4GUI() {
        promptGameSettings();
        game = new C4Game();
        currentPlayer = C4Player.X;

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateBoard(); // refresh icon sizes on resize
            }
        });

        setTitle("Connect 4");
        setSize(700, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());



        // Top panel for column buttons
        JPanel topPanel = new JPanel(new GridLayout(1, 7));
        for (int c = 0; c < 7; c++) {
            JButton button = new JButton("↓");
            final int col = c;
            button.addActionListener(e -> handleColumnClick(col));
            columnButtons[c] = button;
            topPanel.add(button);
        }
        JPanel paddedTop = new JPanel(new BorderLayout());
        paddedTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // top, left, bottom, right
        paddedTop.add(topPanel, BorderLayout.CENTER);
        add(paddedTop, BorderLayout.NORTH);

        // Center panel for grid
        JPanel gridPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                JLabel label = getJLabel();
                cellLabels[r][c] = label;
                gridPanel.add(label);
            }
        }
        JPanel paddedGrid = new JPanel(new BorderLayout());
        paddedGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // top, left, bottom, right padding
        paddedGrid.add(gridPanel, BorderLayout.CENTER);
        add(paddedGrid, BorderLayout.CENTER);
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(newGameButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        pack(); // sets window size based on layout and preferred sizes
    }

    private static @NotNull JLabel getJLabel() {
        JLabel label = new JLabel(" ", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(64, 64)); // match image size
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return label;
    }

    private void handleColumnClick(int col) {
        // ❗ Prevent input if it's not the human's turn
        if (vsAI && currentPlayer != humanPlayer) {
            return; // Ignore click — not your turn!
        }
        // Attempt to drop the piece in the column using your game logic
        int row = game.dropPiece(col, currentPlayer.getSymbol());

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Column is full. Try another.");
            return;
        }

        ImageIcon icon = getIconForPlayer(currentPlayer);  // 🧩 use your player piece icons
        animateDrop(col, row, icon, () -> {
            // 🧠 After animation finishes:
            if (checkGameOver()) return;

            currentPlayer = currentPlayer.next();  // switch turn

            if (vsAI && currentPlayer == aiPlayer) {
                statusLabel.setText("AI is thinking...");
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                disableInput();  // ✅ disables input before AI starts

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        handleAIMove();  // This method must call animateDrop for the AI move too
                        return null;
                    }

                    @Override
                    protected void done() {
                        setCursor(Cursor.getDefaultCursor());
                        statusLabel.setText(" ");
                        // ✅ now that AI is done and GUI updated:
                        if (!checkGameOver()) {
                            setColumnButtonsEnabled(true);
                        }
                    }
                }.execute();
            }
        });
    }

    private void promptGameSettings() {
        // Ask 1P or 2P
        String[] modeOptions = {"1 Player", "2 Player"};
        String modeChoice = (String) JOptionPane.showInputDialog(
                this, "Play mode:", "Game Mode", JOptionPane.QUESTION_MESSAGE, null,
                modeOptions, modeOptions[0]);
        vsAI = "1 Player".equals(modeChoice);

        // Ask X or O
        if (vsAI) {
            String[] symbolOptions = {"Red", "Yellow"};
            String symbolChoice = (String) JOptionPane.showInputDialog(
                    this, "Choose your color:", "Player Color", JOptionPane.QUESTION_MESSAGE, null,
                    symbolOptions, symbolOptions[0]);
            humanPlayer = "Red".equals(symbolChoice) ? C4Player.X : C4Player.O;
            aiPlayer = humanPlayer.next();

            // Ask difficulty
            String[] diffOptions = {"Easy", "Medium", "Hard", "Insane"};
            String diffChoice = (String) JOptionPane.showInputDialog(
                    this, "Choose difficulty:", "Difficulty", JOptionPane.QUESTION_MESSAGE, null,
                    diffOptions, diffOptions[1]);
            difficulty = C4Utils.parseDifficulty(diffChoice);
        } else {
            humanPlayer = null;
            aiPlayer = null;
            difficulty = null;
        }
    }

    private String getColorName(char symbol) {
        switch (symbol) {
            case 'X':
                return "Red";
            case 'O':
                return "Yellow";
            default:
                return "Unknown";
        }
    }

    private boolean checkGameOver() {
        char[][] board = game.getBoard();

        if (C4Utils.checkWin(board, currentPlayer.getSymbol())) {
            JOptionPane.showMessageDialog(this,
                    "🎉 " + getColorName(currentPlayer.getSymbol()) + " wins!");
            setColumnButtonsEnabled(false); // ✅ disable input
            return true;
        }

        if (C4Utils.boardIsFull(board)) {
            JOptionPane.showMessageDialog(this, "🤝 It's a draw!");
            setColumnButtonsEnabled(false); // ✅ disable input

            return true;
        }

        return false;
    }

    private ImageIcon getIconForPlayer(C4Player player) {
        String fileName = player == C4Player.X ? "red_piece.png" : "yellow_piece.png";
        URL imageUrl = getClass().getClassLoader().getResource("images/" + fileName);  // ✅ updated path

        if (imageUrl == null) {
            System.err.println("⚠️ Could not load image: images/" + fileName);
            return null;
        }

        return new ImageIcon(imageUrl);
    }

    private void animateDrop(int col, int targetRow, ImageIcon pieceIcon, Runnable onFinish) {
        // Disable buttons during animation
        SwingUtilities.invokeLater(() -> setColumnButtonsEnabled(false));

        new Thread(() -> {
            try {
                for (int r = 0; r <= targetRow; r++) {
                    int finalR = r;

                    SwingUtilities.invokeAndWait(() -> {
                        JLabel label = cellLabels[finalR][col];
                        int width = label.getWidth();
                        int height = label.getHeight();

                        // 🔄 Scale icon per cell size
                        Image img = pieceIcon.getImage();
                        ImageIcon scaledIcon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));

                        if (finalR > 0) {
                            cellLabels[finalR - 1][col].setIcon(null);
                            cellLabels[finalR - 1][col].setText("");  // clear fallback
                        }

                        label.setIcon(scaledIcon);
                        label.setText("");  // clear fallback
                    });

                    Thread.sleep(50);
                }

                SwingUtilities.invokeLater(() -> {
                    if (onFinish != null) {
                        onFinish.run(); // Let the caller handle switching players or AI logic
                    }

                    // ✅ Always re-enable input after animation *if the game is still ongoing*
                    if (!checkGameOver()) {
                        setColumnButtonsEnabled(true);
                    }
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Animation error", e);
            }
        }).start();
    }

    private void setColumnButtonsEnabled(boolean enabled) {
        for (JButton button : columnButtons) {
            button.setEnabled(enabled);
        }
    }

    private void disableInput() {
        setColumnButtonsEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void enableInput() {
        setColumnButtonsEnabled(true);
        setCursor(Cursor.getDefaultCursor());
    }
    private void resetGame() {
        promptGameSettings();  // ask all questions again

        game = new C4Game();
        currentPlayer = C4Player.X;

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                cellLabels[r][c].setIcon(null);
                cellLabels[r][c].setText(" ");
            }
        }

        for (JButton button : columnButtons) {
            button.setEnabled(true);
        }

        if (vsAI && currentPlayer == aiPlayer) {
            handleAIMove();
        }
    }

    private void handleAIMove() {
        int depth = C4Utils.getDepth(difficulty);
        boolean debugMode = false;

        int col = C4AI.getBestMove(
                C4Utils.copyBoard(game.getBoard()),
                depth,
                aiPlayer.getSymbol(),
                humanPlayer.getSymbol(),
                difficulty,
                debugMode
        );

        int row = game.dropPiece(col, aiPlayer.getSymbol());

        ImageIcon icon = getIconForPlayer(aiPlayer);

        // 👇 Animate first, then check for win/draw before switching players
        animateDrop(col, row, icon, () -> {
            if (checkGameOver()) return;
            currentPlayer = currentPlayer.next(); // only switch turn if the game is not over
        });
    }

    private void updateBoard() {
        char[][] board = game.getBoard();

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                JLabel label = cellLabels[r][c];
                char symbol = board[r][c];
                label.setText("");

                if (symbol == 'X') {
                    ImageIcon icon = getScaledIconForPlayer(C4Player.X, label.getWidth(), label.getHeight());
                    label.setIcon(icon);
                } else if (symbol == 'O') {
                    ImageIcon icon = getScaledIconForPlayer(C4Player.O, label.getWidth(), label.getHeight());
                    label.setIcon(icon);
                } else {
                    label.setIcon(null);
                }
            }
        }
    }

    private ImageIcon getScaledIconForPlayer(C4Player player, int width, int height) {
        String fileName = player == C4Player.X ? "red_piece.png" : "yellow_piece.png";
        URL imageUrl = getClass().getClassLoader().getResource(fileName);

        if (imageUrl == null) {
            System.err.println("⚠️ Could not load image: " + fileName);
            return null;
        }

        Image img = new ImageIcon(imageUrl).getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static void main(String[] args) {
        C4GUI gui = new C4GUI();  // no args now
        gui.setVisible(true);
    }
}
