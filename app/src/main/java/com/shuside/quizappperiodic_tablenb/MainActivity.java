package com.shuside.quizappperiodic_tablenb;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView question;
    TextView questionNum;
    Button Button1;
    Button Button2;
    Button Button3;
    Button Button4;
    Button previous;
    Button hintButton;
    ImageView image;
    Button nextButton;

    Toast popup;
    ArrayList<Question> questions = new ArrayList<Question>();
    MediaPlayer mediaPlayer;

    int qnum = 0;
    int numCorrect = 0;

    ArrayList<String> Answers = new ArrayList<>();  // Store correct answers as Strings
    ArrayList<String> UserAnswers = new ArrayList<>();  // Store user answers as Strings

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.Question);
        questionNum = findViewById(R.id.QuestionNumber);
        Button1 = findViewById(R.id.Button1);
        Button2 = findViewById(R.id.Button2);
        Button3 = findViewById(R.id.Button3);
        Button4 = findViewById(R.id.Button4);
        previous = findViewById(R.id.previousButton);
        hintButton = findViewById(R.id.hintButton);
        image = findViewById(R.id.imageView);
        popup = new Toast(this);
        nextButton = findViewById(R.id.nextButton);

        questions.add(new Question(
                getString(R.string.question_01),
                getString(R.string.hint_01),
                "Carbon",
                "img01",
                "sound01",
                new ArrayList<String>() {{
                    add("Hydrogen");
                    add("Magnesium");
                    add("Carbon");
                    add("Oxygen");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_02),
                getString(R.string.hint_02),
                "Nitrogen",
                "img02",
                "sound01",
                new ArrayList<String>() {{
                    add("Oxygen");
                    add("Hydrogen");
                    add("Argon");
                    add("Nitrogen");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_03),
                getString(R.string.hint_03),
                "Iron oxide",
                "img03",
                "sound01",
                new ArrayList<String>() {{
                    add("Ferrous oxide");
                    add("Iron dioxide");
                    add("Iron III oxide");
                    add("Iron oxide");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_04),
                getString(R.string.hint_04),
                "Alkali Metals",
                "img04",
                "sound01",
                new ArrayList<String>() {{
                    add("Halogens");
                    add("Alkali Metals");
                    add("Transition Metals");
                    add("Alkali Earth Metals");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_05),
                getString(R.string.hint_05),
                "Dimitri Mendelev",
                "img05",
                "sound01",
                new ArrayList<String>() {{
                    add("Charles Darwin");
                    add("John Green");
                    add("Dimitri Mendelev");
                    add("Mr. Massey");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_06),
                getString(R.string.hint_06),
                "Lead",
                "img06",
                "sound01",
                new ArrayList<String>() {{
                    add("Copper");
                    add("Silver");
                    add("Lead");
                    add("Yittrium");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_07),
                getString(R.string.hint_07),
                "False, Bromine is liquid at room temperature",
                "img07",
                "sound01",
                new ArrayList<String>() {{
                    add("False, Bromine is liquid at room temperature");
                    add("False, Mercury isnt a liquid at room temperature");
                    add("True");
                    add("Maybe?");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_08),
                getString(R.string.hint_08),
                "Nitrious Oxide",
                "img08",
                "sound01",
                new ArrayList<String>() {{
                    add("Nitrious Oxide");
                    add("Nitrogen Dioxide");
                    add("Cloroform");
                    add("Polytetrafluoroethylene");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_09),
                getString(R.string.hint_09),
                "J",
                "img09",
                "sound01",
                new ArrayList<String>() {{
                    add("J");
                    add("W");
                    add("Z");
                    add("V");
                }}
        ));
        questions.add(new Question(
                getString(R.string.question_10),
                getString(R.string.hint_10),
                "Quarks",
                "img10",
                "sound01",
                new ArrayList<String>() {{
                    add("Lambda's Particles");
                    add("Elements");
                    add("They are the simplest form of matter.");
                    add("Quarks");
                }}
        ));

        // Build correct answers list
        for (Question q : questions) {
            Answers.add(q.getAnswer());
        }

        // Initialize UserAnswers with null values (not answered yet)
        for (int i = 0; i < questions.size(); i++) {
            UserAnswers.add(null);
        }

        nextQuestion();

        // Button 1 Click Listener
        Button1.setOnClickListener(v -> handleAnswerClick(Button1.getText().toString()));

        // Button 2 Click Listener
        Button2.setOnClickListener(v -> handleAnswerClick(Button2.getText().toString()));

        // Button 3 Click Listener
        Button3.setOnClickListener(v -> handleAnswerClick(Button3.getText().toString()));

        // Button 4 Click Listener
        Button4.setOnClickListener(v -> handleAnswerClick(Button4.getText().toString()));

        nextButton.setOnClickListener(v -> {
            if (qnum + 1 < questions.size()) {
                qnum++;
                nextQuestion();
            } else {
                nextScene();
            }
        });

        previous.setOnClickListener(v -> {
            if (qnum > 0) {
                qnum--;
                nextQuestion();
                if (numCorrect > 0) {
                    numCorrect--;
                }
                Toast.makeText(getApplicationContext(), getString(R.string.previous_penalty), LENGTH_SHORT).show();
            }
        });

        hintButton.setOnClickListener(v -> {
            Intent hintIntent = new Intent(MainActivity.this, ViewHintActivity.class);
            hintIntent.putExtra("HINT", questions.get(qnum).getHint());
            startActivity(hintIntent);
        });
    }

    private void handleAnswerClick(String selectedAnswer) {
        Question ques = questions.get(qnum);

        if (ques.hasBeenAnswered()) {
            Toast.makeText(getApplicationContext(), getString(R.string.already_answered), LENGTH_SHORT).show();
            return;
        }

        ques.setHasBeenAnswered(true);
        UserAnswers.set(qnum, selectedAnswer);

        if (ques.getAnswer().equals(selectedAnswer)) {
            numCorrect++;
            Toast.makeText(getApplicationContext(), getString(R.string.correct_answer), LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_answer), LENGTH_SHORT).show();
        }

        if (qnum + 1 < questions.size()) {
            qnum++;
            nextQuestion();
        } else {
            nextScene();
        }
    }

    @SuppressLint("SetTextI18n")
    public void nextQuestion() {
        Question quesi = questions.get(qnum);
        if (qnum < questions.size()) {
            question.setText(quesi.getQuestionText());
            questionNum.setText("#" + (qnum + 1));

            // Set the button texts to the choices
            ArrayList<String> choices = quesi.getChoices();
            if (choices != null && choices.size() >= 4) {
                Button1.setText(choices.get(0));
                Button2.setText(choices.get(1));
                Button3.setText(choices.get(2));
                Button4.setText(choices.get(3));
            }

            String imgPath = quesi.getImageFilePath();
            if (imgPath != null && !imgPath.isEmpty()) {
                int resId = getResources().getIdentifier(imgPath, "drawable", getPackageName());
                if (resId != 0) {
                    image.setImageResource(resId);
                    image.setVisibility(VISIBLE);
                } else {
                    image.setVisibility(INVISIBLE);
                }
            } else {
                image.setVisibility(INVISIBLE);
            }
            playSound("sound01");
        }
    }

    private void playSound(String soundFileName) {
        if (soundFileName == null || soundFileName.isEmpty()) {
            return;
        }

        try {
            stopSound();
            int soundId = getResources().getIdentifier(soundFileName, "raw", getPackageName());

            if (soundId != 0) {
                mediaPlayer = MediaPlayer.create(this, soundId);
                mediaPlayer.setOnCompletionListener(mp -> stopSound());
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
    }

    public void nextScene() {
        stopSound();
        Intent myintent = new Intent(MainActivity.this, Scoree.class);
        myintent.putExtra("SCORE", numCorrect);
        myintent.putExtra("TOTAL", questions.size());
        myintent.putExtra("UserAnswers", UserAnswers);
        myintent.putExtra("Answers", Answers);
        startActivity(myintent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
}