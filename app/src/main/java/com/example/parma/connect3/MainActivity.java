package com.example.parma.connect3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    // 0: Blue, 1: Black, -1: Empty board cell
    int r00 = -1;
    int r01 = -1;
    int r02 = -1;
    int r10 = -1;
    int r11 = -1;
    int r12 = -1;
    int r20 = -1;
    int r21 = -1;
    int r22 = -1;

    public void dropChip(View view) {
        ImageView board_cell = (ImageView) view;
        String board_cellTag = board_cell.getTag().toString();
        Log.i("Cell tag ", board_cellTag);

        // First check the condition to find out what color of coin was inserted in last, blue or black
        // Next, once the coin has been inserted, remove the onclick attribute of the imageview in question
        // Finally, update the occupied matrix and check it at the end of each click
        // Lastly, the play again button will reset all the value to -1 as it should and clear the UI of the chips

        board_cell.setImageResource(R.drawable.blue);
        board_cell.setY(-1000f);
        board_cell.animate().translationY(0f).setDuration(500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
