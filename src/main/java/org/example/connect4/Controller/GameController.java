package com.example.connect4.Controller;

import com.example.connect4.model.Connect4Game;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pve")
public class GameController {
    private Connect4Game game = new Connect4Game();

    @PostMapping("/new-game")
    public void newGame() {
        game = new Connect4Game();
    }

    @PostMapping("/player-move")
    public void playerMove(@RequestBody MoveRequest move) {
        game.makeMove(move.getCol(), 'P');
    }

    @GetMapping("/ai-move")
    public void aiMove() {
        int aiCol = game.getBestMove();
        game.makeMove(aiCol, 'A');
    }

    @GetMapping("/board")
    public char[][] getBoard() {
        return game.getBoard();
    }

    static class MoveRequest {
        private int col;
        public int getCol() { return col; }
        public void setCol(int col) { this.col = col; }
    }
    @GetMapping("/check-status")
    public String checkStatus() {
        if (game.checkWin('P')) return "Player wins!";
        if (game.checkWin('A')) return "AI wins!";
        if (game.isBoardFull()) return "Draw!";
        return "Ongoing";
    }

}