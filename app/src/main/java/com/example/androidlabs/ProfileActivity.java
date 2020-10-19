package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ProfileActivity extends AppCompatActivity {
    Button cbt;
    ImageButton bt;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String ACTIVITY_NAME = "ProfileActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setContentView(R.layout.activity_profile);
        super.onCreate(savedInstanceState);
        Log.e(ACTIVITY_NAME, "In onCreate");
        EditText myemail = findViewById(R.id.myemail);
        Intent fromMain = getIntent();
        String emailText = fromMain.getStringExtra("EMAIL");
        myemail.setText(emailText);
        ImageButton image = findViewById(R.id.imagecap);
        bt = image;
        Button chatbt = findViewById(R.id.chatButton);
        cbt = chatbt;
        image.setOnClickListener(bt -> dispatchTakePictureIntent());

        chatbt.setOnClickListener(cbt-> startActivity(new Intent(ProfileActivity.this, ChatRoomActivity.class)));
    }

    private void dispatchTakePictureIntent(){
        Log.e(ACTIVITY_NAME,"In dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.e(ACTIVITY_NAME,"In onActivityResult");
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        Bundle extras = data.getExtras();
        Bitmap imageBitMap = (Bitmap) extras.get("data");
        bt.setImageBitmap(imageBitMap);
    }
}

}