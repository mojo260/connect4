// Defines the package this class belongs to
package org.example.connect4.model;

public class Connect4Game {

    // Constants defining the board size and symbols
    private static final int ROWS = 6;               // Number of rows on the board
    private static final int COLS = 7;               // Number of columns on the board
    private static final char EMPTY = '.';           // Symbol for an empty cell
    private static final char PLAYER = 'P';          // Symbol for the human player
    private static final char AI = 'A';              // Symbol for the AI player

    // The game board represented as a 2D char array
    private final char[][] board;

    // Constructor initializes the board with empty cells
    public Connect4Game() {
        board = new char[ROWS][COLS];                // Create the 6x7 board
        for (int i = 0; i < ROWS; i++) {              // Loop through each row
            for (int j = 0; j < COLS; j++) {          // Loop through each column
                board[i][j] = EMPTY;                  // Initialize each cell as empty
            }
        }
    }

    // Attempts to place a piece for the given player in the specified column
    public boolean makeMove(int col, char player) {
        // Invalid move: out-of-bounds or column is full
        if (col < 0 || col >= COLS || board[0][col] != EMPTY) return false;

        // Start from the bottom row and find the first empty cell
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                board[row][col] = player;             // Place the piece
                return true;                          // Move successful
            }
        }
        return false;                                 // Should not reach here unless column is full
    }

    // Undoes the last move in the specified column (used in AI search)
    public void undoMove(int col) {
        for (int row = 0; row < ROWS; row++) {
            if (board[row][col] != EMPTY) {
                board[row][col] = EMPTY;              // Remove the piece
                break;                                // Only remove the topmost piece
            }
        }
    }

    // Checks if the given player has won the game
    public boolean checkWin(char player) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                // Check all four win directions: horizontal, vertical, diagonal (2 ways)
                if (checkDirection(row, col, 1, 0, player) ||  // Vertical
                        checkDirection(row, col, 0, 1, player) ||  // Horizontal
                        checkDirection(row, col, 1, 1, player) ||  // Diagonal ↘
                        checkDirection(row, col, 1, -1, player))   // Diagonal ↙
                {
                    return true;                                // Win detected
                }
            }
        }
        return false;                                           // No win found
    }

    // Helper function to check 4 in a row in a given direction
    private boolean checkDirection(int row, int col, int dRow, int dCol, char player) {
        for (int i = 0; i < 4; i++) {
            int r = row + i * dRow;                             // Row index in direction
            int c = col + i * dCol;                             // Col index in direction
            // Check bounds and if each cell matches the player's symbol
            if (r < 0 || r >= ROWS || c < 0 || c >= COLS || board[r][c] != player) {
                return false;
            }
        }
        return true;                                            // Found 4 in a row
    }

    // Checks if the board is full (draw condition)
    public boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) return false;           // If any top cell is empty, board is not full
        }
        return true;
    }

    // Returns the best column for the AI to move using minimax with alpha-beta pruning
    public int getBestMove() {
        int bestScore = Integer.MIN_VALUE;                      // Initialize with lowest possible score
        int bestCol = 0;                                        // Default best column
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) {
                makeMove(col, AI);                              // Try AI move
                int score = alphaBeta(0, Integer.MIN_VALUE, Integer.MAX_VALUE, false); // Evaluate move
                undoMove(col);                                  // Undo the move
                if (score > bestScore) {
                    bestScore = score;                          // Update best score
                    bestCol = col;                              // Update best column
                }
            }
        }
        return bestCol;                                         // Return the best column found
    }

    // Recursive minimax function with alpha-beta pruning for AI decision-making
    private int alphaBeta(int depth, int alpha, int beta, boolean maximizingPlayer) {
        // Terminal conditions: win, loss, draw, or max depth
        if (checkWin(AI)) return 1000 - depth;                  // Prefer faster wins
        if (checkWin(PLAYER)) return -1000 + depth;             // Prefer slower losses
        if (isBoardFull() || depth == 3) return 0;              // Draw or depth limit reached

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int col = 0; col < COLS; col++) {
                if (board[0][col] == EMPTY) {
                    makeMove(col, AI);                          // AI tries a move
                    int eval = alphaBeta(depth + 1, alpha, beta, false); // Recurse as minimizing player
                    undoMove(col);                              // Undo the move
                    maxEval = Math.max(maxEval, eval);          // Track max score
                    alpha = Math.max(alpha, eval);              // Update alpha
                    if (beta <= alpha) break;                   // Beta cut-off
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col = 0; col < COLS; col++) {
                if (board[0][col] == EMPTY) {
                    makeMove(col, PLAYER);                      // Simulate player move
                    int eval = alphaBeta(depth + 1, alpha, beta, true); // Recurse as maximizing player
                    undoMove(col);                              // Undo the move
                    minEval = Math.min(minEval, eval);          // Track min score
                    beta = Math.min(beta, eval);                // Update beta
                    if (beta <= alpha) break;                   // Alpha cut-off
                }
            }
            return minEval;
        }
    }

    // Getter method to return the current board state
    public char[][] getBoard() {
        return board;
    }
}
