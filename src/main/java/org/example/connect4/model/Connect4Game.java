package com.example.connect4.model;

public class Connect4Game {
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final char EMPTY = '.';
    private static final char PLAYER = 'P';
    private static final char AI = 'A';

    private final char[][] board;

    public Connect4Game() {
        board = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    public boolean makeMove(int col, char player) {
        if (col < 0 || col >= COLS || board[0][col] != EMPTY) return false;
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                board[row][col] = player;
                return true;
            }
        }
        return false;
    }

    public void undoMove(int col) {
        for (int row = 0; row < ROWS; row++) {
            if (board[row][col] != EMPTY) {
                board[row][col] = EMPTY;
                break;
            }
        }
    }

    public boolean checkWin(char player) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (checkDirection(row, col, 1, 0, player) ||
                        checkDirection(row, col, 0, 1, player) ||
                        checkDirection(row, col, 1, 1, player) ||
                        checkDirection(row, col, 1, -1, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDirection(int row, int col, int dRow, int dCol, char player) {
        for (int i = 0; i < 4; i++) {
            int r = row + i * dRow;
            int c = col + i * dCol;
            if (r < 0 || r >= ROWS || c < 0 || c >= COLS || board[r][c] != player) {
                return false;
            }
        }
        return true;
    }

    public boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) return false;
        }
        return true;
    }

    public int getBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestCol = 0;
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) {
                makeMove(col, AI);
                int score = alphaBeta(0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                undoMove(col);
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }
        return bestCol;
    }

    private int alphaBeta(int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (checkWin(AI)) return 1000 - depth;
        if (checkWin(PLAYER)) return -1000 + depth;
        if (isBoardFull() || depth == 3) return 0;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int col = 0; col < COLS; col++) {
                if (board[0][col] == EMPTY) {
                    makeMove(col, AI);
                    int eval = alphaBeta(depth + 1, alpha, beta, false);
                    undoMove(col);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col = 0; col < COLS; col++) {
                if (board[0][col] == EMPTY) {
                    makeMove(col, PLAYER);
                    int eval = alphaBeta(depth + 1, alpha, beta, true);
                    undoMove(col);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;
                }
            }
            return minEval;
        }
    }

    public char[][] getBoard() {
        return board;
    }
}