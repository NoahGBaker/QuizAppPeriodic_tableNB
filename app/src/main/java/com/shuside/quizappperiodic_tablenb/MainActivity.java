package com.shuside.quizappperiodic_tablenb;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    Toast popup;
    ArrayList<Object[]> questions = new ArrayList<Object[]>();

    int qnum = 0;
    int numCorrect = 0;


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
        popup = new Toast(this);

        questions.add(new Object[]{"The most abundant element in plastics is Carbon", true});
        questions.add(new Object[]{"The air is made up mostly of Oxygen", false});
        questions.add(new Object[]{"All elements on the periodic table can be found in nature.", false});
        questions.add(new Object[]{"The one of most reactive groups on the periodic table are Alkali metals.", true});
        questions.add(new Object[]{"Mendelevium also known as Element 101 was discovered by Dimitri Mendelev the man who invented the periodic table.", false});
        questions.add(new Object[]{"Uranium eventually becomes lead if you give it time.", true});
        questions.add(new Object[]{"Mercury is the only element liquid at room temperature.", false});
        questions.add(new Object[]{"According to scientists we are technically made of stardust.", true});
        questions.add(new Object[]{"The letter J appears only once in the periodic table.", false});
        questions.add(new Object[]{"Protons, Neutrons and Electrons are the simplest form of matter", false});
        nextQuestion();
        trueButton.setOnClickListener(v -> {
            Object[] ques = questions.get(qnum);
            if (ques[1].equals(true)) {
                numCorrect++;
                Toast.makeText(getApplicationContext(), "Correct!", LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong.", LENGTH_SHORT).show();
            }

            if (qnum+1 < questions.size()) {
                qnum++;
                nextQuestion();
            } else {
                nextScene();
            }
        });

        falseButton.setOnClickListener(v -> {
            Object[] quest = questions.get(qnum);
            if (quest[1].equals(false)) {
                numCorrect++;
                Toast.makeText(getApplicationContext(), "Correct!", LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong.", LENGTH_SHORT).show();
            }

            if (qnum+1 < questions.size()) {
                qnum++;
                nextQuestion();
            } else {
                nextScene();
            }
        });
        previous.setOnClickListener(v -> {
            if (qnum < questions.size() && qnum != 0) {
                qnum--;
                nextQuestion();
                numCorrect--;
            }
        });
    }


    public void nextQuestion() {
        Object[] quesi = questions.get(qnum);
        if (qnum < questions.size()) {
            question.setText((String) quesi[0]);
            questionNum.setText("#" + (qnum+1));
        }
    }

    public void nextScene() {
        Intent myintent = new Intent(MainActivity.this, Scoree.class);
        myintent.putExtra("SCORE",numCorrect);
        myintent.putExtra("TOTAL",questions.size());
        startActivity(myintent);
        finish();
    }
}