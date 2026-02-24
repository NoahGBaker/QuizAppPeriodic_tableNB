package com.shuside.quizappperiodic_tablenb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ViewScores extends AppCompatActivity {

    LinearLayout scoresContainer;
    Button backButton;
    ArrayList<Question> questions;
    // Get all questions
    ArrayList<String> questionsText;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_scores);

        scoresContainer = findViewById(R.id.Scores);
        backButton = findViewById(R.id.backToScoreButton);

        // Get data from intent
        int score = getIntent().getIntExtra("SCOREE", 0);
        int total = getIntent().getIntExtra("TOTALL", 0);
        ArrayList<String> userAnswers = (ArrayList<String>) getIntent().getSerializableExtra("ScoreUserAnswers");
        ArrayList<String> correctAnswers = (ArrayList<String>) getIntent().getSerializableExtra("ScoreAnswers");

        loadQuestionsFromFile();

        for (Question ques : questions) {
            questionsText.add(ques.getQuestionText());
        }


        // Display each question with answers
        displayResults(questionsText, userAnswers, correctAnswers);

        // Back button
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ViewScores.this, Scoree.class);
            backIntent.putExtra("SCORE", score);
            backIntent.putExtra("TOTAL", total);
            startActivity(backIntent);
            finish();
        });
    }


    private void displayResults(ArrayList<String> questionText, ArrayList<String> userAnswers, ArrayList<String> correctAnswers) {
        scoresContainer.removeAllViews(); // Clear any existing views

        for (int i = 0; i < questions.size(); i++) {
            // Create a container for each question
            LinearLayout questionContainer = new LinearLayout(this);
            questionContainer.setOrientation(LinearLayout.VERTICAL);
            questionContainer.setPadding(16, 16, 16, 32);

            // Question number and text
            TextView quesText = new TextView(this);
            quesText.setText(getString(R.string.question) + (i + 1) + ":\n" + questions.get(i));
            quesText.setTextSize(16);
            quesText.setTextColor(Color.BLACK);
            quesText.setPadding(0, 0, 0, 16);
            questionContainer.addView(quesText);

            TextView answerText = new TextView(this);
            answerText.setText("Your Answer: " + userAnswers.get(i));
            answerText.setTextSize(16);
            answerText.setTextColor(Color.BLACK);
            answerText.setPadding(0, 0, 0, 16);
            questionContainer.addView(answerText);

            TextView correctAnswerText = new TextView(this);
            correctAnswerText.setText("Correct Answer: " + correctAnswers.get(i));
            correctAnswerText.setTextSize(16);
            correctAnswerText.setTextColor(Color.BLACK);
            correctAnswerText.setPadding(0, 0, 0, 16);
            questionContainer.addView(correctAnswerText);

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

    private void loadQuestionsFromFile() {
        try {
            // Read from assets folder
            InputStream is = getAssets().open("SavedData.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder fileContents = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                fileContents.append(line).append("\n");
            }

            br.close();
            parseQuestionsFromText(fileContents.toString());

            Toast.makeText(this, "Questions loaded successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseQuestionsFromText(String fileContents) {
        String[] lines = fileContents.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            // Split by | delimiter
            String[] parts = line.split("\\|");

            if (parts.length >= 6) {
                String answer = parts[0].trim();
                String questionText = parts[1].trim();
                String hint = parts[2].trim();
                String imagePath = parts[3].trim();
                String soundPath = parts[4].trim();
                String[] choicesArray = parts[5].split(",");

                // Convert choices array to ArrayList
                ArrayList<String> choices = new ArrayList<>();
                for (String choice : choicesArray) {
                    choices.add(choice.trim());
                }

                // Create and add the question
                questions.add(new Question(questionText, hint, answer, imagePath, soundPath, choices));
            }
        }
    }
}