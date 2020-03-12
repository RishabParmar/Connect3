package com.example.parma.connect3;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    
    // Brief Connect3 algorithm:
    // First check the condition to find out what color of coin was inserted in last, blue or black
    // Next, once the coin has been inserted, remove the onclick attribute of the ImageView in question
    // Finally, update the occupied matrix and check it at the end of each click
    // Lastly, the play again button will reset all the value to -1 as it should and clear the UI of the chips

    // On winning algorithm:
    // Highlight the winning coins by reducing the alpha level on all the other coins
    // Make the text showing which player has won or the game has been drawn
    // Make the play again button visible which when clicked will run the play again algorithm
    // Stop any further clicks to the game board as the game shouldn't register any further clicks

    // Play again Algorithm(in depth):
    // Initialise the gameBoard values to -1
    // Setup the onclick of all the imageviews stored in cellViews
    // Remove the src for image for all the imageviews stored
    // Flush the cellViews array, reset the value of viewCounter = 0 and set isBlue = false

    // Sounds algorithm:
    // Add dropCoin sounds that should be played on click (specifically when the animation has been rendered)
    // Add win music when the game has been done. Similarly add draw music when drawn

    // Initialising the global variables
    int gameBoard[][] = new int[3][3];
    int viewCounter = 0;
    ImageView[] cellViews = new ImageView[9];
    boolean isBlue = false;
    boolean isFinished = false;
    // Did not set the playAgainButton by finding the id because there is no view for the member to work (findViewById won't work)
    // This view is available once onCreate fires
    Button playAgainButton;
    // For displaying the winner message
    TextView winMessage;
    // Game Title
    TextView gameTitle;

    // Initialising the game board cells to -1 for tracking winning metrics
    public void initializeGameBoardGrid(){
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 3; column++){
                gameBoard[row][column] = -1;
            }
        }

        // Setting the playAgain button properties
        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setVisibility(View.INVISIBLE);
        playAgainButton.setClickable(false);

        // Win Message Textview
        winMessage = findViewById(R.id.winMessage);
    }

    public boolean gameBoardFull(){
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 3; column++){
                if(gameBoard[row][column] == -1) { return false; }
            }
        }
        return true;
    }

    public int[] checkForWin() {
        // flags[0]: won the game, flags[1]: who won the game, blue(0) or black(1)
        int flags[] = {-1, -1};

        // For dealing with rows
        for(int row = 0; row<3; row++) {
            if (gameBoard[row][0] == gameBoard[row][1] && gameBoard[row][1] == gameBoard[row][2]) {
                if (gameBoard[row][0] != -1){
                    flags[0] = 1;
                    flags[1] = gameBoard[row][0];
                    Log.i("Win type ", "Row");
                    return flags;
                }
            }
        }

        // For dealing with columns
        for(int column = 0; column<3; column++){
            if(gameBoard[0][column] == gameBoard[1][column] && gameBoard[1][column] == gameBoard[2][column]) {
                if(gameBoard[0][column] != -1){
                    flags[0] = 1;
                    flags[1] = gameBoard[0][column];
                    Log.i("Win type ", "Column");
                    return flags;
                }
            }
        }

        // For dealing with diagonals
        if(gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2] && gameBoard[0][0] != -1) {
            flags[0] = 1;
            flags[1] = gameBoard[0][0];
            Log.i("Win type ", "Diagonal");
            return flags;
        }else if(gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0] && gameBoard[0][2] != -1) {
            flags[0] = 1;
            flags[1] = gameBoard[0][2];
            Log.i("Win type ", "Reverse Diagonal");
            return flags;
        }

        return flags;
    }

    public void resetGame() {
        // Resetting the ImageView instances to their original state
        for(int i = 0; i < viewCounter; i++) {
            cellViews[i].setImageDrawable(null);
            cellViews[i].setClickable(true);
        }
        cellViews = new ImageView[9];
        viewCounter = 0;
        
        // Resetting the game board values and the playAgain button properties
        initializeGameBoardGrid();

        // Resetting the first chip color to blue
        isBlue = false;

        // Clearing the win Message
        winMessage.setText("");

        // Resetting the game condition
        isFinished = false;
    }

    public void dropChip(View view) {
        int cellValue;
        ImageView boardCell = (ImageView) view;
        if(!isFinished) {
            cellViews[viewCounter] = boardCell;
            viewCounter++;
            if(!isBlue) {
                boardCell.setImageResource(R.drawable.blue);
                cellValue = 0;
                isBlue = true;
            }else {
                boardCell.setImageResource(R.drawable.black);
                cellValue = 1;
                isBlue = false;
            }

            // Removes the onClick attribute for the Image View in play and freezes the grid cell spot in the play space
            boardCell.setClickable(false);
            boardCell.setY(-1000f);
            boardCell.animate().translationY(0f).setDuration(500).withStartAction(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.dropchip);
                    mp.start();
                }
            });

            // Update the gameBoard matrix after chip insertion
            String boardCellTag = boardCell.getTag().toString();
            Log.i("Cell tag ", boardCellTag);
            String splitTags[] = boardCellTag.split("_", 0);
            int boardRow = Integer.parseInt(String.valueOf(splitTags[1].charAt(0)));
            // Log.i("The row value: ", Integer.toString(boardRow));
            int boardColumn = Integer.parseInt(String.valueOf(splitTags[1].charAt(1)));
            // Log.i("The cell value: ", Integer.toString(boardColumn));

            // Insert the cell value appropriately according to the color of the chip inserted
            // Blue: 0, Black: 1, Empty board cell: -1
            gameBoard[boardRow][boardColumn] = cellValue;
            Log.i("Game Board state", Arrays.deepToString(gameBoard));
        }

        // Run the winChecker to check a row, column or a diagonal for a match of 3 only after 5 chips have been inserted
        // in the grid as you require either 3blue & 2black or 3black and 2blue to win the game
        int result[] = viewCounter>=5 ?checkForWin() :new int[2];
        if(result[0] == 1 && !isFinished) {
            Log.i("The win condition has been invoked! ", "Color that has won " + result[1]);
            playAgainButton.setVisibility(View.VISIBLE);
            playAgainButton.setClickable(true);
            String winColor;
            if(result[1] == 0) { winColor = "The winner is Blue Chip!"; }
            else { winColor = "The winner is Black Leaf!"; }
            winMessage.setText(winColor);
            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.win);
            mp.start();
            isFinished = true;
        }else if(result[0] == -1 && gameBoardFull() && !isFinished){
            Log.i("Draw!", "The players have drawn the game");
            playAgainButton.setVisibility(View.VISIBLE);
            playAgainButton.setClickable(true);
            String drawMessage = "Both the players have drawn the game";
            winMessage.setText(drawMessage);
            isFinished = true;
        }
    }

    // Play Again Button:
    public void playAgain(View view) {
        resetGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeGameBoardGrid();

        // For changing the status bar color on the game activity
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FAFAFA"));

        // Game grid animation
        GridLayout gameGrid = findViewById(R.id.gameGrid);
        gameGrid.setY(1500f);
        gameGrid.animate().translationY(0f).setDuration(500).start();

        // Creating the game title animation and playing sounds on it's animation completion
        gameTitle = findViewById(R.id.gameTitle);
        gameTitle.setY(-500f);
        gameTitle.animate().translationY(0f).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.gametitle);
                mp.start();
            }
        });
    }
}
