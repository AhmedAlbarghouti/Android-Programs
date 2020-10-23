package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String email;
    private EditText emailText;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginButton = findViewById(R.id.login);
        EditText emailText = findViewById(R.id.emailText);

        SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String saved = prefs.getString(email, "");
        emailText.setText(saved);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savedEmail = emailText.getText().toString();
                saveSharedPrefs(savedEmail);
                Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
                goToProfile.putExtra("EMAIL",savedEmail);
                startActivity(goToProfile);
            }
        });



    }



    private void saveSharedPrefs(String saved){
        SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(email,saved);
        edit.commit();
    }

}