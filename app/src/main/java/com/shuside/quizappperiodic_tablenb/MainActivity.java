package com.shuside.quizappperiodic_tablenb;

import static android.app.PendingIntent.getActivity;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    String savedInitials;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    Toast popup;
    ArrayList<Question> questions = new ArrayList<Question>();
    MediaPlayer mediaPlayer;

    //SharedPref
    SharedPreferences sharedPref;
    int defaultValue;
    int highScore;

    int qnum = 0;
    int numCorrect = 0;

    int score = 0;

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
        //SharedPref
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        defaultValue = getResources().getInteger(R.integer.saved_high_score_default_key);
        highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), defaultValue);

        // Check if user has entered initials before
        savedInitials = sharedPref.getString("user_initials", null);
        ref.setValue(savedInitials);

        if (savedInitials == null || savedInitials.isEmpty()) {
            // First time user - show dialog
            showInitialsDialog();
        } else {
            // User already has initials saved - start quiz directly
            Toast.makeText(this, "Welcome back, " + savedInitials + "!", Toast.LENGTH_SHORT).show();
            startQuiz();
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("Data Change:", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Data Change:", "Failed to read value.", error.toException());
            }
        });


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
            if (score > highScore) {
                editSharedPref(numCorrect);
                ref.setValue(highScore);
            }
            Toast.makeText(getApplicationContext(), getString(R.string.correct_answer), LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_answer), LENGTH_SHORT).show();
            score = 0;
        }

        if (qnum + 1 < questions.size()) {
            qnum++;
            nextQuestion();
        } else {
            nextScene();
        }
    }
    private void startQuiz() {
        // Load questions from file
        loadQuestionsFromFile();

        // If no questions were loaded, add default ones
        if (questions.isEmpty()) {
            addDefaultQuestions();
        }

        // Build correct answers list
        for (Question q : questions) {
            Answers.add(q.getAnswer());
        }

        // Initialize UserAnswers
        for (int i = 0; i < questions.size(); i++) {
            UserAnswers.add(null);
        }

        nextQuestion();
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

    private void editSharedPref(int newHighScore) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_high_score_key), newHighScore);
        editor.apply();

    }

    private void saveQuestionsToFile() {
        String filename = "SavedData.txt";
        StringBuilder fileContents = new StringBuilder();

        // Build the content from your questions
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            fileContents.append("Question ").append(i + 1).append(": ")
                    .append(q.getQuestionText()).append("\n")
                    .append("Answer: ").append(q.getAnswer()).append("\n\n");
        }

        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.toString().getBytes());
            Toast.makeText(this, "Questions saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving questions", Toast.LENGTH_SHORT).show();
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

    private void addDefaultQuestions() {
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

    private void showInitialsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Initials");
        builder.setMessage("Please enter your initials (2-3 letters):");

        // Create an EditText for user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        input.setHint("ABC");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String initials = input.getText().toString().trim().toUpperCase();

                // Validate initials (2-3 characters)
                if (initials.length() >= 2 && initials.length() <= 3) {
                    // Save to SharedPreferences
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_initials", initials);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Welcome, " + initials + "!", Toast.LENGTH_SHORT).show();

                    // Now start the quiz
                    startQuiz();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter 2-3 letters", Toast.LENGTH_SHORT).show();
                    showInitialsDialog(); // Show dialog again if invalid
                }
            }
        });

        builder.setCancelable(false); // User must enter initials
        builder.show();
    }
}

