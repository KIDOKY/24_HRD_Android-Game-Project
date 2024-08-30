package com.example.ticblackproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoardGameActivity extends AppCompatActivity {

    private static final int BOARD_SIZE = 10;
    private static final int SPECIAL_TILE_COUNT = 3; // 보너스 타일 개수
    private static final int OBSTACLE_TILE_COUNT = 2; // 장애물 타일 개수
    private static final int POWER_UP_TILE_COUNT = 2; // 파워업 타일 개수
    private static final int TELEPORT_TILE_COUNT = 2; // 텔레포트 타일 개수
    private static final int WIN_TILE = BOARD_SIZE * BOARD_SIZE - 1; // 골인 타일 인덱스

    private GridLayout gridLayout;
    private Button rollDiceButton;
    private int playerPosition = 0;
    private int computerPosition = 0;
    private int playerPower = 1;
    private int computerPower = 1;
    private Button[] tiles;
    private boolean playerTurn = true;
    private int[] tileTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_game);

        gridLayout = findViewById(R.id.boardGridLayout);
        rollDiceButton = findViewById(R.id.rollDiceButton);

        setupBoard();
        rollDiceButton.setOnClickListener(v -> rollDice());

        Button switchToBlackjackGameButton = findViewById(R.id.switchToBlackjackButton);
        switchToBlackjackGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(BoardGameActivity.this, BlackjackGameActivity.class);
            startActivity(intent);
        });

        Button switchToTicTacToeButton = findViewById(R.id.switchToTicTacToeButton);
        switchToTicTacToeButton.setOnClickListener(v -> {
            Intent intent = new Intent(BoardGameActivity.this, TicTacToeActivity.class);
            startActivity(intent);
        });
    }

    private void setupBoard() {
        gridLayout.setRowCount(BOARD_SIZE);
        gridLayout.setColumnCount(BOARD_SIZE);

        tiles = new Button[BOARD_SIZE * BOARD_SIZE];
        tileTypes = new int[BOARD_SIZE * BOARD_SIZE];
        List<Integer> tileIndices = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            tileIndices.add(i);
        }
        Collections.shuffle(tileIndices); // 타일 인덱스 무작위 섞기

        int totalSpecialTiles = SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT + POWER_UP_TILE_COUNT + TELEPORT_TILE_COUNT;

        if (totalSpecialTiles > BOARD_SIZE * BOARD_SIZE - 1) {
            throw new IllegalStateException("특별 타일의 총 개수가 보드 타일의 수를 초과합니다.");
        }

        // 특별 타일 설정
        for (int i = 0; i < SPECIAL_TILE_COUNT; i++) tileTypes[tileIndices.get(i)] = 1;
        for (int i = SPECIAL_TILE_COUNT; i < SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT; i++) tileTypes[tileIndices.get(i)] = 2;
        for (int i = SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT; i < SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT + POWER_UP_TILE_COUNT; i++) tileTypes[tileIndices.get(i)] = 3;
        for (int i = SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT + POWER_UP_TILE_COUNT; i < SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT + POWER_UP_TILE_COUNT + TELEPORT_TILE_COUNT; i++) tileTypes[tileIndices.get(i)] = 4;
        // 나머지 타일은 기본 타일로 설정
        for (int i = SPECIAL_TILE_COUNT + OBSTACLE_TILE_COUNT + POWER_UP_TILE_COUNT + TELEPORT_TILE_COUNT; i < BOARD_SIZE * BOARD_SIZE - 1; i++) tileTypes[tileIndices.get(i)] = 0;

        // 타일 배치
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            Button cell = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.rowSpec = GridLayout.spec(i / BOARD_SIZE, 1f);
            params.columnSpec = GridLayout.spec(i % BOARD_SIZE, 1f);
            cell.setLayoutParams(params);
            cell.setTextSize(12);
            cell.setPadding(5, 5, 5, 5);
            cell.setTextColor(ContextCompat.getColor(this, android.R.color.black));

            setTileAppearance(cell, i, tileTypes[i]);

            gridLayout.addView(cell);
            tiles[i] = cell;
        }

        updatePlayerPositions();
    }

    private void setTileAppearance(Button cell, int index, int tileType) {
        cell.setText(String.valueOf(index));

        switch (tileType) {
            case 1: // 보너스 타일
                cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                cell.append("\n보너스");
                break;
            case 2: // 장애물 타일
                cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                cell.append("\n장애물");
                break;
            case 3: // 파워업 타일
                cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
                cell.append("\n파워업");
                break;
            case 4: // 텔레포트 타일
                cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_purple));
                cell.append("\n텔레포트");
                break;
            default: // 기본 타일
                if (index == WIN_TILE) {
                    cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                    cell.append("\n골인");
                } else {
                    cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                }
                break;
        }
    }

    private void rollDice() {
        int diceResult = new Random().nextInt(6) + 1;
        if (playerTurn) {
            Toast.makeText(this, "플레이어의 주사위 결과: " + diceResult, Toast.LENGTH_SHORT).show();
            playerPosition += diceResult * playerPower;
            if (playerPosition >= BOARD_SIZE * BOARD_SIZE) {
                playerPosition = BOARD_SIZE * BOARD_SIZE - 1;
                Toast.makeText(this, "플레이어가 보드 끝에 도착했습니다!", Toast.LENGTH_LONG).show();
                endGame(1);
            } else {
                checkSpecialTiles(true);
                playerTurn = false;
                rollDiceButton.setEnabled(false);
                rollDiceButton.postDelayed(this::computerTurn, 1000); // 1초 후 컴퓨터 턴
            }
        }
        updatePlayerPositions();
    }

    private void computerTurn() {
        int diceResult = new Random().nextInt(6) + 1;
        Toast.makeText(this, "컴퓨터의 주사위 결과: " + diceResult, Toast.LENGTH_SHORT).show();
        computerPosition += diceResult * computerPower;
        if (computerPosition >= BOARD_SIZE * BOARD_SIZE) {
            computerPosition = BOARD_SIZE * BOARD_SIZE - 1;
            Toast.makeText(this, "컴퓨터가 보드 끝에 도착했습니다!", Toast.LENGTH_LONG).show();
            endGame(2);
        } else {
            checkSpecialTiles(false);
            playerTurn = true;
            rollDiceButton.setEnabled(true);
        }
        updatePlayerPositions();
    }

    private void updatePlayerPositions() {
        for (int i = 0; i < tiles.length; i++) {
            Button cell = tiles[i];
            if (i == playerPosition) {
                cell.setText("플레이어\n" + i);
                cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            } else if (i == computerPosition) {
                cell.setText("컴퓨터\n" + i);
                cell.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else {
                setTileAppearance(cell, i, tileTypes[i]);
            }
        }
    }

    private void checkSpecialTiles(boolean isPlayer) {
        int position = isPlayer ? playerPosition : computerPosition;

        if (position == WIN_TILE) {
            endGame(isPlayer ? 1 : 2); // 게임 종료
        } else {
            int tileType = tileTypes[position];
            switch (tileType) {
                case 2: // 장애물 타일
                    Toast.makeText(this, (isPlayer ? "플레이어" : "컴퓨터") + "가 장애물에 부딪혔습니다! 3칸 뒤로 이동합니다.", Toast.LENGTH_SHORT).show();
                    if (isPlayer) playerPosition = Math.max(0, playerPosition - 3);
                    else computerPosition = Math.max(0, computerPosition - 3);
                    break;
                case 3: // 파워업 타일
                    Toast.makeText(this, (isPlayer ? "플레이어" : "컴퓨터") + "가 파워업 타일! 이동 거리가 두 배로 증가합니다.", Toast.LENGTH_SHORT).show();
                    if (isPlayer) playerPower = 2;
                    else computerPower = 2;
                    break;
                case 4: // 텔레포트 타일
                    Toast.makeText(this, (isPlayer ? "플레이어" : "컴퓨터") + "가 텔레포트 타일! 랜덤 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
                    if (isPlayer) playerPosition = new Random().nextInt(BOARD_SIZE * BOARD_SIZE);
                    else computerPosition = new Random().nextInt(BOARD_SIZE * BOARD_SIZE);
                    break;
                case 1: // 보너스 타일
                    Toast.makeText(this, (isPlayer ? "플레이어" : "컴퓨터") + "가 보너스 타일! 다시 주사위를 굴립니다.", Toast.LENGTH_SHORT).show();
                    if (isPlayer) rollDice();
                    else computerTurn();
                    break;
            }

            if (isPlayer) playerPower = 1;
            else computerPower = 1;
        }
    }

    private void endGame(int winner) {
        Toast.makeText(this, "게임 종료! " + (winner == 1 ? "플레이어" : "컴퓨터") + "의 승리입니다.", Toast.LENGTH_LONG).show();

        // 게임 상태를 초기화하여 새 게임 준비
        playerPosition = 0;
        computerPosition = 0;
        playerPower = 1;
        computerPower = 1;
        playerTurn = true; // 플레이어부터 시작

        // 보드 업데이트
        updatePlayerPositions();

        // 버튼 활성화
        rollDiceButton.setEnabled(true);
    }
}
