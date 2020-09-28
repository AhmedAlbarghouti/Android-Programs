package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

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
        setContentView(R.layout.activity_main_linear);
        TextView tv = findViewById(R.id.tv);
        Button btn = findViewById(R.id.button);
        CheckBox cb = findViewById(R.id.cb);
        Switch swi = findViewById(R.id.sw);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,getResources().getString(R.string.info),Toast.LENGTH_LONG).show();
            }
        });

      swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton cb, boolean b) {
           if(b){ Snackbar.make(swi,getResources().getString(R.string.confirm),Snackbar.LENGTH_SHORT)
                    .setAction(getResources().getString(R.string.undo),click-> cb.setChecked(!b)).show();}
           else if(!b){
               Snackbar.make(swi,getResources().getString(R.string.deny),Snackbar.LENGTH_SHORT)
                       .setAction(getResources().getString(R.string.undo),click-> cb.setChecked(!b)).show();
           }
          }
      });



    }
}