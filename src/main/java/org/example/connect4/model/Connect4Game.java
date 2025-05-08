// Defines the package this class belongs to
package org.example.connect4.model;

// Public class representing the Connect 4 game logic
public class Connect4Game {

    // Number of rows on the Connect 4 board
    private static final int ROWS = 6;

    // Number of columns on the Connect 4 board
    private static final int COLS = 7;

    // Character used to represent an empty cell
    private static final char EMPTY = '.';

    // Character representing the human player
    private static final char PLAYER = 'P';

    // Character representing the AI player
    private static final char AI = 'A';

    // 2D array representing the game board
    private final char[][] board;

    // Constructor initializes the board with empty cells
    public Connect4Game() {
        board = new char[ROWS][COLS]; // Create the board with given dimensions
        for (int i = 0; i < ROWS; i++) { // Loop through each row
            for (int j = 0; j < COLS; j++) { // Loop through each column
                board[i][j] = EMPTY; // Set each cell to empty
            }
        }
    }

    // Method to make a move in a specified column for a player
    public boolean makeMove(int col, char player) {
        // If column is invalid or already full, return false
        if (col < 0 || col >= COLS || board[0][col] != EMPTY) return false;

        // Start from bottom row and place the disc in the first empty slot
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                board[row][col] = player; // Place the player's disc
                return true; // Move successful
            }
        }
        return false; // Should not reach here unless column is full
    }

    // Method to undo a move (remove topmost disc from a column)
    public void undoMove(int col) {
        // From top to bottom, find the first non-empty cell and clear it
        for (int row = 0; row < ROWS; row++) {
            if (board[row][col] != EMPTY) {
                board[row][col] = EMPTY; // Remove disc
                break; // Exit loop after removing one disc
            }
        }
    }

    // Method to check if a player has won
    public boolean checkWin(char player) {
        for (int row = 0; row < ROWS; row++) { // Loop through all rows
            for (int col = 0; col < COLS; col++) { // Loop through all columns
                // Check all 4 directions for a win
                if (checkDirection(row, col, 1, 0, player) || // Vertical
                        checkDirection(row, col, 0, 1, player) || // Horizontal
                        checkDirection(row, col, 1, 1, player) || // Diagonal ↘
                        checkDirection(row, col, 1, -1, player))  // Diagonal ↙
                {
                    return true; // Win found
                }
            }
        }
        return false; // No win
    }

    // Helper method to check 4-in-a-row in a given direction
    private boolean checkDirection(int row, int col, int dRow, int dCol, char player) {
        for (int i = 0; i < 4; i++) { // Check 4 cells in the given direction
            int r = row + i * dRow;
            int c = col + i * dCol;

            // If out of bounds or cell doesn't belong to player, return false
            if (r < 0 || r >= ROWS || c < 0 || c >= COLS || board[r][c] != player) {
                return false;
            }
        }
        return true; // Found 4 matching discs
    }

    // Check if the board is completely filled (i.e., no valid moves)
    public boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) return false; // Top row has empty slot
        }
        return true; // All columns full
    }

    // AI method to determine the best move using alpha-beta pruning
    public int getBestMove() {
        int bestScore = Integer.MIN_VALUE; // Start with the lowest score
        int bestCol = 0; // Default best column

        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) { // Only try valid moves
                makeMove(col, AI); // Simulate move
                int score = alphaBeta(0, Integer.MIN_VALUE, Integer.MAX_VALUE, false); // Evaluate move
                undoMove(col); // Undo simulated move

                if (score > bestScore) { // If better move found
                    bestScore = score;
                    bestCol = col; // Update best column
                }
            }
        }
        return bestCol; // Return the best column for AI
    }

    // Recursive alpha-beta pruning algorithm
    private int alphaBeta(int depth, int alpha, int beta, boolean maximizingPlayer) {
        // Terminal conditions: win or board full or max depth
        if (checkWin(AI)) return 100000 - depth; // Favor faster win
        if (checkWin(PLAYER)) return -100000 + depth; // Favor slower loss
        if (isBoardFull() || depth == 5) return evaluateBoard(); // Heuristic score

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (int col = 0; col < COLS; col++) {
                if (board[0][col] == EMPTY) {
                    makeMove(col, AI); // Try move
                    int eval = alphaBeta(depth + 1, alpha, beta, false); // Recurse as minimizing
                    undoMove(col); // Undo move
                    maxEval = Math.max(maxEval, eval); // Update max value
                    alpha = Math.max(alpha, eval); // Update alpha
                    if (beta <= alpha) break; // Prune branch
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            for (int col = 0; col < COLS; col++) {
                if (board[0][col] == EMPTY) {
                    makeMove(col, PLAYER); // Try opponent move
                    int eval = alphaBeta(depth + 1, alpha, beta, true); // Recurse as maximizing
                    undoMove(col); // Undo move
                    minEval = Math.min(minEval, eval); // Update min value
                    beta = Math.min(beta, eval); // Update beta
                    if (beta <= alpha) break; // Prune branch
                }
            }
            return minEval;
        }
    }

    // Heuristic evaluation of board state
    private int evaluateBoard() {
        return scorePosition(AI) - scorePosition(PLAYER); // Favor AI positions
    }

    // Score the current board for a specific player
    private int scorePosition(char player) {
        int score = 0;

        // Favor placing discs in the center column
        int centerCol = COLS / 2;
        int centerCount = 0;
        for (int row = 0; row < ROWS; row++) {
            if (board[row][centerCol] == player) centerCount++;
        }
        score += centerCount * 3; // Bonus for center control

        // Evaluate 2-, 3-, and 4-in-a-rows
        score += scoreLines(player, 4, 100); // 4-in-a-row is most valuable
        score += scoreLines(player, 3, 10);  // 3-in-a-row
        score += scoreLines(player, 2, 1);   // 2-in-a-row

        return score;
    }

    // Score the number of line segments with exactly 'count' player discs
    private int scoreLines(char player, int count, int weight) {
        int score = 0;
        char opponent = (player == AI) ? PLAYER : AI;

        // Horizontal scoring
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                int pCount = 0, oCount = 0;
                for (int i = 0; i < 4; i++) {
                    if (board[row][col + i] == player) pCount++;
                    else if (board[row][col + i] == opponent) oCount++;
                }
                if (pCount == count && oCount == 0) score += weight;
            }
        }

        // Vertical scoring
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row <= ROWS - 4; row++) {
                int pCount = 0, oCount = 0;
                for (int i = 0; i < 4; i++) {
                    if (board[row + i][col] == player) pCount++;
                    else if (board[row + i][col] == opponent) oCount++;
                }
                if (pCount == count && oCount == 0) score += weight;
            }
        }

        // Diagonal ↘ scoring
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                int pCount = 0, oCount = 0;
                for (int i = 0; i < 4; i++) {
                    if (board[row + i][col + i] == player) pCount++;
                    else if (board[row + i][col + i] == opponent) oCount++;
                }
                if (pCount == count && oCount == 0) score += weight;
            }
        }

        // Diagonal ↙ scoring
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 3; col < COLS; col++) {
                int pCount = 0, oCount = 0;
                for (int i = 0; i < 4; i++) {
                    if (board[row + i][col - i] == player) pCount++;
                    else if (board[row + i][col - i] == opponent) oCount++;
                }
                if (pCount == count && oCount == 0) score += weight;
            }
        }

        return score; // Return the total weighted score
    }

    // Getter method to return current state of the board
    public char[][] getBoard() {
        return board;
    }
}
