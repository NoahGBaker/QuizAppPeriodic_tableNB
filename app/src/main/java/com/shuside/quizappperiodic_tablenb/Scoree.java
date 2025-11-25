package com.shuside.quizappperiodic_tablenb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Scoree extends AppCompatActivity {

    TextView scoreText;
    Button emailButton;
    Button retakeButton;
    ImageView scoreImage;
    Button ViewScoresButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoree);

        scoreText = findViewById(R.id.finalScoreText);
        emailButton = findViewById(R.id.emailButton);
        retakeButton = findViewById(R.id.finishButton);
        scoreImage = findViewById(R.id.imagescore);
        ViewScoresButton = findViewById(R.id.ViewScores);

        // Get score from intent
        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);
        ArrayList<Boolean> userAnswers = (ArrayList<Boolean>) getIntent().getSerializableExtra("UserAnswers");
        ArrayList<Boolean> answers = (ArrayList<Boolean>) getIntent().getSerializableExtra("Answers");

        // Display score
        String scoreMessage = getString(R.string.score_message, score, total);
        scoreText.setText(scoreMessage);

        // Display different images based on score
        displayScoreImage(score, total);

        // Email button
        emailButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"student@example.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body, score, total));

            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                android.widget.Toast.makeText(Scoree.this,
                        getString(R.string.no_email_app),
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Retake quiz button
        retakeButton.setOnClickListener(v -> {
            Intent restartIntent = new Intent(Scoree.this, MainActivity.class);
            startActivity(restartIntent);
            finish();
        });

        // View detailed scores button
        ViewScoresButton.setOnClickListener(v -> {
            Intent myintent = new Intent(Scoree.this, ViewScores.class);
            myintent.putExtra("SCOREE", score);
            myintent.putExtra("TOTALL", total);
            myintent.putExtra("ScoreUserAnswers", userAnswers);
            myintent.putExtra("ScoreAnswers", answers);
            startActivity(myintent);
        });
    }

    private void displayScoreImage(int score, int total) {
        double percentage = (score * 100.0) / total;

        if (percentage < 40) {
            scoreImage.setImageResource(R.drawable.badscore);
        } else if (percentage < 70) {
            scoreImage.setImageResource(R.drawable.lessthan40);
        } else if (percentage < 90) {
            scoreImage.setImageResource(R.drawable.thumbsup2);
        } else {
            scoreImage.setImageResource(R.drawable.lessthan40);
        }
    }
}