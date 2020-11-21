package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {
    private ArrayList<Message> elements = new ArrayList<>();
    myListAdapter listAdapter = new myListAdapter();
    Button sbt;
    Button rbt;
    SQLiteDatabase db;
    EditText et;
    private AppCompatActivity parentActivity;
    public static final String ID = "ID";
    public static final String MESSAGE = "MESSAGE";
    public static final String SENDER = "SENDER";
    Bundle dataToPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ListView chatList = findViewById(R.id.myListView);
        chatList.setAdapter(listAdapter);
        sbt = findViewById(R.id.mySButton);
        rbt = findViewById(R.id.myRButton);
        et = findViewById(R.id.txtmsg);
        boolean isTablet = findViewById(R.id.fLayout) != null; //check if the FrameLayout is loaded

        loadDataFromDatabase();
        MyOpener dbHelper = new MyOpener(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        chatList.setOnItemClickListener((list, item,position,id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(MESSAGE,elements.get(position).getText());
            dataToPass.putBoolean(SENDER, elements.get(position).getcond());
            dataToPass.putLong(ID, id);


            if(isTablet)
            {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fLayout, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });
        sbt.setOnClickListener(click ->
             {


                 String TextMessage = et.getEditableText().toString();
                 ContentValues newRowValues = new ContentValues();
                 newRowValues.put(MyOpener.COLUMN_M,TextMessage);
                 newRowValues.put(MyOpener.COLUMN_User,1);

                 long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);
                 Message message = new Message(et.getEditableText().toString(),true,newId);
                 elements.add(message);
                et.getEditableText().clear();
                listAdapter.notifyDataSetChanged();

                 String [] columns = {MyOpener.COLUMN_ID, MyOpener.COLUMN_M, MyOpener.COLUMN_User};
                 Cursor checker = db.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);
                 printCursor(checker,db.getVersion());
            });


        rbt.setOnClickListener(click ->
        {

            String TextMessage = et.getEditableText().toString();
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyOpener.COLUMN_M,TextMessage);
            newRowValues.put(MyOpener.COLUMN_User,0);

            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

            Message message = new Message(et.getEditableText().toString(),false,newId);
            elements.add(message);
            et.getEditableText().clear();
            listAdapter.notifyDataSetChanged();

            String [] columns = {MyOpener.COLUMN_ID, MyOpener.COLUMN_M, MyOpener.COLUMN_User};
            Cursor checker = db.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);
            printCursor(checker,db.getVersion());
            });


        chatList.setOnItemLongClickListener((parent, view, pos, id) -> {
            showMessage(pos);
            return false;
        });






    }
    private void printCursor (Cursor c, int version){

        MyOpener dbHelper = new MyOpener(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int idColIndex = c.getColumnIndex(MyOpener.COLUMN_ID);
        int textColIndex = c.getColumnIndex(MyOpener.COLUMN_M);
        int senderColIndex = c.getColumnIndex(MyOpener.COLUMN_User);

       Log.i("Database Version ", String.valueOf(db.getVersion()));
       Log.i("Number of Columns ", String.valueOf(c.getColumnCount()));
       Log.i("Name of Columns", Arrays.toString(c.getColumnNames()));
       Log.i("Number of Rows", String.valueOf(c.getCount()));
       while(c.moveToNext()){

           long id = c.getLong(idColIndex);
           String text = c.getString(textColIndex);
           int sender = c.getInt(senderColIndex);
           Log.i("Result of Each Rows", " " + id + " " + text + " " + sender);


       }
       c.moveToPosition(0);
    }
    private void showMessage(int pos){

        Message selectedMessage = elements.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete")
                .setMessage("The selected row is "+(listAdapter.getItemId(pos))+" The database id: "+listAdapter.getItemId(pos))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(selectedMessage);
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
    }

    private void deleteMessage(Message msg){
        MyOpener dbHelper = new MyOpener(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MyOpener.TABLE_NAME, MyOpener.COLUMN_ID + "= ?", new String[] {Long.toString(msg.getId())});
        String [] columns = {MyOpener.COLUMN_ID, MyOpener.COLUMN_M, MyOpener.COLUMN_User};
        Cursor checker = db.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);

        printCursor(checker,db.getVersion());
    }
    private void loadDataFromDatabase() {

        MyOpener dbHelper = new MyOpener(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String [] columns = {MyOpener.COLUMN_ID, MyOpener.COLUMN_M, MyOpener.COLUMN_User};

        Cursor results = db.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);

        int idColIndex = results.getColumnIndex(MyOpener.COLUMN_ID);
        int textColIndex = results.getColumnIndex(MyOpener.COLUMN_M);
        int senderColIndex = results.getColumnIndex(MyOpener.COLUMN_User);
        Cursor checker = db.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);
       printCursor(checker,db.getVersion());

        while(results.moveToNext())
        {
            long id = results.getLong(idColIndex);
            String text = results.getString(textColIndex);
            int sender = results.getInt(senderColIndex);
          if(sender==1){
              elements.add(new Message(text,true,id));
          }else{
              elements.add(new Message(text,false,id));
          }


        }

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
            return elements.get(position).getId();
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
        private long id;

        public Message(String text, boolean cond, long id) {
            this.text = text;
            this.cond = cond;
            this.id = id;
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

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }


    }

    public class MyOpener extends SQLiteOpenHelper {
        private final static String DATABASE_NAME = "Chat_HistoryDB";
        private final static int VERSION_NUM = 1;
        private final static String TABLE_NAME= "Chat_History";
        private final static String COLUMN_M = "Message";
        private final static String COLUMN_ID = "_id";
        private final static String COLUMN_User = "Sent";

        public MyOpener(Context ctx){
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TABLE_NAME + "("+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,  "
             +
                     COLUMN_M + " text,"
                    +COLUMN_User+ " INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
             db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
             onCreate(db);
        }
    }
}