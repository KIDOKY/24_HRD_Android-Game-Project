package com.example.ticblackproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackjackGameActivity extends AppCompatActivity {

    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;

    private TextView playerScoreText;
    private TextView dealerScoreText;
    private TextView resultText;
    private TextView playerCardsText;
    private TextView dealerCardsText;

    private int playerScore;
    private int dealerScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackjack_game);

        playerScoreText = findViewById(R.id.playerScore);
        dealerScoreText = findViewById(R.id.dealerScore);
        resultText = findViewById(R.id.resultText);
        playerCardsText = findViewById(R.id.playerCards);
        dealerCardsText = findViewById(R.id.dealerCards);

        Button dealButton = findViewById(R.id.dealButton);
        Button hitButton = findViewById(R.id.hitButton);
        Button standButton = findViewById(R.id.standButton);

        deck = new ArrayList<>();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        initializeDeck();

        dealButton.setOnClickListener(v -> startGame());
        hitButton.setOnClickListener(v -> hit());
        standButton.setOnClickListener(v -> stand());

        // New Button to Switch to Tic-Tac-Toe
        Button switchToTicTacToeButton = findViewById(R.id.switchToTicTacToeButton);
        switchToTicTacToeButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlackjackGameActivity.this, TicTacToeActivity.class);
            startActivity(intent);
        });

        Button switchToBoardGameButton = findViewById(R.id.switchToBoardGameButton);
        switchToBoardGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlackjackGameActivity.this, BoardGameActivity.class);
            startActivity(intent);
        });
    }

    private void initializeDeck() {
        String[] suits = {"하트", "다이아몬드", "클럽", "스페이드"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        deck.clear();  // Clear any existing cards
        for (String suit : suits) {
            for (String value : values) {
                deck.add(new Card(value, suit));
            }
        }
        Collections.shuffle(deck);
    }

    private void startGame() {
        playerHand.clear();
        dealerHand.clear();
        resultText.setText("");
        playerCardsText.setText("Cards: ");
        dealerCardsText.setText("Cards: ");

        playerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));
        playerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));

        updateScores();
        updateCardTexts();
    }

    private void hit() {
        if (getScore(playerHand) < 21) {
            playerHand.add(deck.remove(0));
            updateScores();
            updateCardTexts();
            if (getScore(playerHand) > 21) {
                resultText.setText("플레이어 버스트! 딜러 승!");
                resetScores(); // Reset scores when player busts
            }
        }
    }

    private void stand() {
        while (getScore(dealerHand) < 17) {
            dealerHand.add(deck.remove(0));
        }
        updateScores();
        updateCardTexts();

        playerScore = getScore(playerHand);
        dealerScore = getScore(dealerHand);

        if (dealerScore > 21) {
            resultText.setText("딜러 버스트! 플레이어 승리!");
        } else if (playerScore > 21) {
            resultText.setText("플레이어 버스트! 딜러 승리!");
        } else if (playerScore > dealerScore) {
            resultText.setText("플레이어 승리!");
        } else if (playerScore < dealerScore) {
            resultText.setText("딜러 승리!");
        } else {
            resultText.setText("푸시!");
        }
        resetScores(); // Reset scores after game ends
    }

    private void updateScores() {
        playerScore = getScore(playerHand);
        dealerScore = getScore(dealerHand);
        playerScoreText.setText("플레이어: " + playerScore);
        dealerScoreText.setText("딜러: " + dealerScore);
    }

    private void updateCardTexts() {
        playerCardsText.setText("카드: " + getCardListText(playerHand));
        dealerCardsText.setText("카드: " + getCardListText(dealerHand));
    }

    private String getCardListText(List<Card> hand) {
        StringBuilder cardList = new StringBuilder();
        for (Card card : hand) {
            cardList.append(card).append(", ");
        }
        // Remove trailing comma and space
        if (cardList.length() > 0) {
            cardList.setLength(cardList.length() - 2);
        }
        return cardList.toString();
    }

    private int getScore(List<Card> hand) {
        int score = 0;
        int aces = 0;

        for (Card card : hand) {
            if (card.value.equals("A")) {
                aces++;
                score += 11;
            } else if (card.value.equals("K") || card.value.equals("Q") || card.value.equals("J")) {
                score += 10;
            } else {
                score += Integer.parseInt(card.value);
            }
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    private void resetScores() {
        playerScore = 0;
        dealerScore = 0;
        playerScoreText.setText("플레이어 점수: " + playerScore);
        dealerScoreText.setText("딜러 점수: " + dealerScore);
    }

    // Card class
    private static class Card {
        String value;
        String suit;

        Card(String value, String suit) {
            this.value = value;
            this.suit = suit;
        }

        @Override
        public String toString() {
            return value + " of " + suit;
        }
    }
}