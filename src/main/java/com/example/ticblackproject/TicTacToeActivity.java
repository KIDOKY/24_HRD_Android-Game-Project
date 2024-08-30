package com.example.ticblackproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeActivity extends AppCompatActivity {

    private Button[] buttons = new Button[9];
    private String[][] board = new String[3][3];
    private String currentPlayer = "X";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        // Initialize button array
        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttons[3] = findViewById(R.id.button4);
        buttons[4] = findViewById(R.id.button5);
        buttons[5] = findViewById(R.id.button6);
        buttons[6] = findViewById(R.id.button7);
        buttons[7] = findViewById(R.id.button8);
        buttons[8] = findViewById(R.id.button9);

        // Set button click listeners
        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(v -> onGridButtonClick(v, finalI));
        }

        // Initialize and set switch to Blackjack button click listener
        Button switchToBlackjackButton = findViewById(R.id.switchToBlackjackButton);
        if (switchToBlackjackButton == null) {
            throw new NullPointerException("Switch to Blackjack button not found in layout");
        }
        switchToBlackjackButton.setOnClickListener(v -> {
            Intent intent = new Intent(TicTacToeActivity.this, BlackjackGameActivity.class);
            startActivity(intent);
        });

        // Initialize and set switch to Board Game button click listener
        Button switchToBoardGameButton = findViewById(R.id.switchToBoardGameButton);
        if (switchToBoardGameButton == null) {
            throw new NullPointerException("Switch to Board Game button not found in layout");
        }
        switchToBoardGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(TicTacToeActivity.this, BoardGameActivity.class);
            startActivity(intent);
        });
    }

    private void onGridButtonClick(View view, int index) {
        int row = index / 3;
        int col = index % 3;

        if (board[row][col] == null) {
            board[row][col] = currentPlayer;
            ((Button) view).setText(currentPlayer);

            if (checkForWin()) {
                Toast.makeText(this, "Player " + currentPlayer + " wins!", Toast.LENGTH_LONG).show();
                resetBoard();
            } else if (isBoardFull()) {
                Toast.makeText(this, "It's a draw!", Toast.LENGTH_LONG).show();
                resetBoard();
            } else {
                switchPlayer();
                if (currentPlayer.equals("O")) {
                    computerMove();
                }
            }
        }
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    private void computerMove() {
        int row, col;
        do {
            int index = (int) (Math.random() * 9);
            row = index / 3;
            col = index % 3;
        } while (board[row][col] != null);

        board[row][col] = currentPlayer;
        int buttonIndex = row * 3 + col;
        buttons[buttonIndex].setText(currentPlayer);

        if (checkForWin()) {
            Toast.makeText(this, "Computer wins!", Toast.LENGTH_LONG).show();
            resetBoard();
        } else if (isBoardFull()) {
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_LONG).show();
            resetBoard();
        } else {
            switchPlayer();
        }
    }

    private boolean checkForWin() {
        // Row check
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null && board[i][0].equals(currentPlayer) &&
                    board[i][1] != null && board[i][1].equals(currentPlayer) &&
                    board[i][2] != null && board[i][2].equals(currentPlayer)) {
                return true;
            }
        }

        // Column check
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != null && board[0][i].equals(currentPlayer) &&
                    board[1][i] != null && board[1][i].equals(currentPlayer) &&
                    board[2][i] != null && board[2][i].equals(currentPlayer)) {
                return true;
            }
        }

        // Diagonal check
        if (board[0][0] != null && board[0][0].equals(currentPlayer) &&
                board[1][1] != null && board[1][1].equals(currentPlayer) &&
                board[2][2] != null && board[2][2].equals(currentPlayer)) {
            return true;
        }
        if (board[0][2] != null && board[0][2].equals(currentPlayer) &&
                board[1][1] != null && board[1][1].equals(currentPlayer) &&
                board[2][0] != null && board[2][0].equals(currentPlayer)) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBoard() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText("");
        }
        board = new String[3][3];
        currentPlayer = "X";
    }
}