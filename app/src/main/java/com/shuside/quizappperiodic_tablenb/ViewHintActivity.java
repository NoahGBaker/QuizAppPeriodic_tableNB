package com.shuside.quizappperiodic_tablenb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewHintActivity extends AppCompatActivity {

    TextView hintTextView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hint);

        hintTextView = findViewById(R.id.hintTextView);
        backButton = findViewById(R.id.backToQuizButton);

        // Get hint from intent
        String hint = getIntent().getStringExtra("HINT");
        if (hint != null && !hint.isEmpty()) {
            hintTextView.setText(hint);
        } else {
            hintTextView.setText(getString(R.string.no_hint_available));
        }

        backButton.setOnClickListener(v -> finish());
    }
}