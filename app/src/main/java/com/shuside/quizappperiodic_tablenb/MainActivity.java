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
    Button trueButton;
    Button falseButton;
    Button previous;
    Button hintButton;
    ImageView image;
    Button nextButton;

    Toast popup;
    ArrayList<Question> questions = new ArrayList<Question>();
    MediaPlayer mediaPlayer;

    int qnum = 0;
    int numCorrect = 0;

    ArrayList<Boolean> Answers = new ArrayList<>();  // FIXED: Initialize here
    ArrayList<Boolean> UserAnswers = new ArrayList<>();  // FIXED: Initialize here

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.Question);
        questionNum = findViewById(R.id.QuestionNumber);
        trueButton = findViewById(R.id.True);
        falseButton = findViewById(R.id.False);
        previous = findViewById(R.id.previousButton);
        hintButton = findViewById(R.id.hintButton);
        image = findViewById(R.id.imageView);
        popup = new Toast(this);
        nextButton = findViewById(R.id.nextButton);

        questions.add(new Question(
                getString(R.string.question_01),
                getString(R.string.hint_01),
                "True",
                "img01",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_02),
                getString(R.string.hint_02),
                "False",
                "img02",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_03),
                getString(R.string.hint_03),
                "False",
                "img03",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_04),
                getString(R.string.hint_04),
                "True",
                "img04",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_05),
                getString(R.string.hint_05),
                "False",
                "img05",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_06),
                getString(R.string.hint_06),
                "True",
                "img06",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_07),
                getString(R.string.hint_07),
                "False",
                "img07",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_08),
                getString(R.string.hint_08),
                "True",
                "img08",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_09),
                getString(R.string.hint_09),
                "False",
                "img09",
                "sound01"
        ));
        questions.add(new Question(
                getString(R.string.question_10),
                getString(R.string.hint_10),
                "False",
                "img10",
                "sound01"
        ));

        // Build correct answers list
        for (Question q : questions) {
            String answer = q.getAnswer();
            if (answer.equals("True")) {
                Answers.add(true);
            } else if (answer.equals("False")) {
                Answers.add(false);
            } else {
                Answers.add(null);
            }
        }

        // Initialize UserAnswers with null values (not answered yet)
        for (int i = 0; i < questions.size(); i++) {
            UserAnswers.add(null);
        }

        nextQuestion();

        nextButton.setOnClickListener(v -> {
            if (qnum + 1 < questions.size()) {
                qnum++;
                nextQuestion();
            } else {
                nextScene();
            }
        });

        trueButton.setOnClickListener(v -> {
            Question ques = questions.get(qnum);

            if (ques.hasBeenAnswered()) {
                Toast.makeText(getApplicationContext(), getString(R.string.already_answered), LENGTH_SHORT).show();
                return;
            }

            ques.setHasBeenAnswered(true);
            UserAnswers.set(qnum, true);  // FIXED: Use set() instead of add()

            if (ques.getAnswer().equals("True")) {
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
        });

        falseButton.setOnClickListener(v -> {
            Question quest = questions.get(qnum);

            if (quest.hasBeenAnswered()) {
                Toast.makeText(getApplicationContext(), getString(R.string.already_answered), LENGTH_SHORT).show();
                return;
            }

            quest.setHasBeenAnswered(true);
            UserAnswers.set(qnum, false);  // FIXED: Use set() instead of add()

            if (quest.getAnswer().equals("False")) {
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

    @SuppressLint("SetTextI18n")
    public void nextQuestion() {
        Question quesi = questions.get(qnum);
        if (qnum < questions.size()) {
            question.setText(quesi.getQuestionText());
            questionNum.setText("#" + (qnum + 1));

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
        myintent.putExtra("UserAnswers", UserAnswers);  // Pass as Serializable
        myintent.putExtra("Answers", Answers);  // Pass as Serializable
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