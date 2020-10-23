package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {
    private ArrayList<Message> elements = new ArrayList<>();
    myListAdapter listAdapter = new myListAdapter();
    Button sbt;
    Button rbt;

    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ListView chatList = findViewById(R.id.myListView);
        chatList.setAdapter(listAdapter);
        sbt = findViewById(R.id.mySButton);
        rbt = findViewById(R.id.myRButton);
        et = findViewById(R.id.txtmsg);


        sbt.setOnClickListener(click ->
             {
                Message message = new Message(et.getEditableText().toString(),true);
                elements.add(message);
                et.getEditableText().clear();
                listAdapter.notifyDataSetChanged();
            });


        rbt.setOnClickListener(click ->
        {
                Message message = new Message(et.getEditableText().toString(),false);
                elements.add(message);
                et.getEditableText().clear();
                listAdapter.notifyDataSetChanged();
            });


        chatList.setOnItemLongClickListener((parent, view, pos, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to delete")
                    .setMessage("The selected row is "+(listAdapter.getItemId(pos)+1)+" The database id: "+listAdapter.getItemId(pos))
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            elements.remove(pos);
                            listAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        });





    }
    
    class myListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            Message msg = elements.get(position);
            if(msg.getcond()){
                View sView = inflater.inflate(R.layout.send_row_layout,parent,false);
                TextView sText = sView.findViewById(R.id.sendText);
                sText.setText(msg.getText());
                return sView;

            } if (!msg.getcond()){
                View rView = inflater.inflate(R.layout.recieve_row_layout, parent,false);
                TextView rText = rView.findViewById(R.id.receiveText);
                rText.setText(msg.getText());
                return rView;
            }
            return null;

        }
    }
    private class Message {

        private String text; // message string
        private boolean cond;        // true for message from sender and false for message from receiver

        public Message(String text, boolean cond) {
            this.text = text;
            this.cond = cond;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean getcond() {
            return cond;
        }

        public void setSender(boolean sender) {
            this.cond = cond;
        }


    }
}