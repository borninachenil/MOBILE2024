package com.example.projetimage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button validateButton = findViewById(R.id.valider_button);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirection vers PremiumMembershipActivity
                Intent intent = new Intent(LoginActivity.this, PremiumMembershipActivity.class);
                startActivity(intent);
            }
        });
    }
}