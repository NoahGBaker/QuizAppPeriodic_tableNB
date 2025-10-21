package com.shuside.quizappperiodic_tablenb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Scoree extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scoree);

        TextView finalScore = findViewById(R.id.finalScoreText);
        ImageView finalImage = findViewById(R.id.imagescore);
        Button finishButton = findViewById(R.id.finishButton);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);
        finalScore.setText("Final Score: " + score + "/" + total);
        try {
            if (score == 10) {
                finalImage.setImageResource(R.drawable.tenoutaten);
            } else if (score >= 6) {
                finalImage.setImageResource(R.drawable.thumbsup2);
            } else if (score > 4) {
                finalImage.setImageResource(R.drawable.badscore);
            } else if (score >= 0) {
                finalImage.setImageResource(R.drawable.lessthan40);
            }
        } catch (Exception e) {
            Log.d("Error:", "Something went wrong");
        }

        finishButton.setOnClickListener(v ->  {
            Intent myIntent = new Intent(Scoree.this, MainActivity.class);
            startActivity(myIntent);
        });

    }
}
