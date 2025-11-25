package com.shuside.quizappperiodic_tablenb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewScores extends AppCompatActivity {

    LinearLayout scoresContainer;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_scores);

        scoresContainer = findViewById(R.id.Scores);
        backButton = findViewById(R.id.backToScoreButton);

        // Get data from intent
        int score = getIntent().getIntExtra("SCOREE", 0);
        int total = getIntent().getIntExtra("TOTALL", 0);
        ArrayList<Boolean> userAnswers = (ArrayList<Boolean>) getIntent().getSerializableExtra("ScoreUserAnswers");
        ArrayList<Boolean> correctAnswers = (ArrayList<Boolean>) getIntent().getSerializableExtra("ScoreAnswers");

        // Get all questions
        ArrayList<String> questions = getQuestionsFromStrings();

        // Display each question with answers
        displayResults(questions, userAnswers, correctAnswers);

        // Back button
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ViewScores.this, Scoree.class);
            backIntent.putExtra("SCORE", score);
            backIntent.putExtra("TOTAL", total);
            startActivity(backIntent);
            finish();
        });
    }


    private void displayResults(ArrayList<String> questions, ArrayList<Boolean> userAnswers, ArrayList<Boolean> correctAnswers) {
        scoresContainer.removeAllViews(); // Clear any existing views

        for (int i = 0; i < questions.size(); i++) {
            // Create a container for each question
            LinearLayout questionContainer = new LinearLayout(this);
            questionContainer.setOrientation(LinearLayout.VERTICAL);
            questionContainer.setPadding(16, 16, 16, 32);

            // Question number and text
            TextView questionText = new TextView(this);
            questionText.setText(getString(R.string.question) + (i + 1) + ":\n" + questions.get(i));
            questionText.setTextSize(16);
            questionText.setTextColor(Color.BLACK);
            questionText.setPadding(0, 0, 0, 16);
            questionContainer.addView(questionText);

            // User's answer
            TextView userAnswerText = new TextView(this);
            String userAnswerStr = (i < userAnswers.size() && userAnswers.get(i) != null)
                    ? (userAnswers.get(i) ? "True" : "False")
                    : "Not Answered";
            userAnswerText.setText(getString(R.string.your_answer) + userAnswerStr);
            userAnswerText.setTextSize(14);
            userAnswerText.setPadding(0, 0, 0, 8);
            questionContainer.addView(userAnswerText);

            // Correct answer
            TextView correctAnswerText = new TextView(this);
            String correctAnswerStr = (correctAnswers.get(i) != null)
                    ? (correctAnswers.get(i) ? "True" : "False")
                    : "Unknown";
            correctAnswerText.setText(getString(R.string.correct_answerr) + correctAnswerStr);
            correctAnswerText.setTextSize(14);
            correctAnswerText.setPadding(0, 0, 0, 8);
            questionContainer.addView(correctAnswerText);

            // Result indicator
            TextView resultText = new TextView(this);
            if (i < userAnswers.size() && userAnswers.get(i) != null &&
                    userAnswers.get(i).equals(correctAnswers.get(i))) {
                resultText.setText(R.string.correct);
                resultText.setTextColor(Color.GREEN);
            } else {
                resultText.setText(R.string.incorrectttttt);
                resultText.setTextColor(Color.RED);
            }
            resultText.setTextSize(16);
            resultText.setTextAppearance(android.graphics.Typeface.BOLD);
            questionContainer.addView(resultText);

            // Divider line
            android.view.View divider = new android.view.View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            ));
            divider.setBackgroundColor(Color.GRAY);
            LinearLayout.LayoutParams dividerParams = (LinearLayout.LayoutParams) divider.getLayoutParams();
            dividerParams.setMargins(0, 16, 0, 0);
            divider.setLayoutParams(dividerParams);
            questionContainer.addView(divider);

            // Add question container to main container
            scoresContainer.addView(questionContainer);
        }
    }

    private ArrayList<String> getQuestionsFromStrings() {
        ArrayList<String> questions = new ArrayList<>();
        questions.add(getString(R.string.question_01));
        questions.add(getString(R.string.question_02));
        questions.add(getString(R.string.question_03));
        questions.add(getString(R.string.question_04));
        questions.add(getString(R.string.question_05));
        questions.add(getString(R.string.question_06));
        questions.add(getString(R.string.question_07));
        questions.add(getString(R.string.question_08));
        questions.add(getString(R.string.question_09));
        questions.add(getString(R.string.question_10));
        return questions;
    }
}