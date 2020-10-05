package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginButton =findViewById(R.id.login);

        loginButton.setOnClickListener(bt -> {sa(String s)});


    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("Login Information", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        String savedString = prefs.getString(Reserver)
        edit.putString("")

    }
    private void saveSharedPrefs(String stringtoSave){



}
}
