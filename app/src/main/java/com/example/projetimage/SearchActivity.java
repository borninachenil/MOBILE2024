package com.example.projetimage;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView textViewDrawings = findViewById(R.id.text_drawings);

        SharedPreferences sharedPreferences = getSharedPreferences("drawings", MODE_PRIVATE);
        String savedDrawings = sharedPreferences.getString("drawings", "");

        if (!savedDrawings.isEmpty()) {
            textViewDrawings.setText(savedDrawings);
        } else {
            textViewDrawings.setText("Aucun dessin enregistré.");
        }
    }
}