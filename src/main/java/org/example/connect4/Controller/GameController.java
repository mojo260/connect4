// Declares the package where this class belongs
package org.example.connect4.Controller;

// Imports the Connect4Game model class containing the game logic
import org.example.connect4.model.Connect4Game;

// Imports Spring Web annotations to expose HTTP endpoints
import org.springframework.web.bind.annotation.*;

// Marks this class as a REST controller, meaning it will handle web requests and return data directly as JSON
@RestController

// Allows cross-origin requests from any domain (needed for frontend-backend communication across domains)
@CrossOrigin(origins = "*")

// Base URL mapping for all endpoints in this controller; all paths will be prefixed with /pve
@RequestMapping("/pve")
public class GameController {

    // Creates an instance of the Connect4Game, representing the current game state
    private Connect4Game game = new Connect4Game();

    // Endpoint to start a new game; resets the game object
    @PostMapping("/new-game")
    public void newGame() {
        game = new Connect4Game(); // Re-initializes the game
    }

    // Endpoint to handle the player's move
    @PostMapping("/player-move")
    public void playerMove(@RequestBody MoveRequest move) {
        // Makes a move in the specified column for the player ('P' stands for Player)
        game.makeMove(move.getCol(), 'P');
    }

    // Endpoint to let the AI make its move
    @GetMapping("/ai-move")
    public void aiMove() {
        // Gets the best column for the AI to move
        int aiCol = game.getBestMove();
        // Makes the move in that column for the AI ('A' stands for AI)
        game.makeMove(aiCol, 'A');
    }

    // Endpoint to return the current game board
    @GetMapping("/board")
    public char[][] getBoard() {
        // Returns the 2D array representing the game board
        return game.getBoard();
    }

    // Inner static class used to encapsulate the column number sent in a player's move request
    static class MoveRequest {
        private int col; // The column where the player wants to place their token

        // Getter method to access the column number
        public int getCol() { return col; }

        // Setter method to update the column number
        public void setCol(int col) { this.col = col; }
    }

    // Endpoint to check the current status of the game
    @GetMapping("/check-status")
    public String checkStatus() {
        // Checks if the player has won
        if (game.checkWin('P')) return "Player wins!";
        // Checks if the AI has won
        if (game.checkWin('A')) return "AI wins!";
        // Checks if the board is full, indicating a draw
        if (game.isBoardFull()) return "Draw!";
        // If no winner and the board isn't full, the game is still ongoing
        return "Ongoing";
    }
}
