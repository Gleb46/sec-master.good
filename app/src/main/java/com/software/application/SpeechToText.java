package com.software.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class SpeechToText extends AppCompatActivity {

    private  static  final  String TAG = "logs";
    private EditText editText;


    private Button btnStart;
    private Button btnLap;
    private Button btnStop;
    private TextView tvtime;

    private int mLaps=1;

    private Context mContext;
    private Chronometer mChronometer;
    private Thread mThresdChrono;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_to_text);

        mContext=this;

        btnStart = (Button) findViewById(R.id.button_start);
        btnLap = (Button) findViewById(R.id.button_lap);
        btnStop = (Button) findViewById(R.id.button_stop);
        tvtime = (TextView) findViewById(R.id.textView_tv_time);





        editText = (EditText) findViewById(R.id.editText);


        dbHelper = new DBHelper(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChronometer==null){
                    mChronometer=new Chronometer(mContext);
                    mThresdChrono=new Thread(mChronometer);
                    mThresdChrono.start();
                    mChronometer.start();

                    mLaps=1;

                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChronometer!=null){
                    mChronometer.stop();
                    mThresdChrono.interrupt();
                    mThresdChrono=null;
                    mChronometer=null;
                }

            }
        });
        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChronometer==null){
                    return;
                }
                editText.clearComposingText();
                editText.append(mLaps+") "+
                        String.valueOf(tvtime.getText() )+"\n");
                //String.valueOf(editText.getText())+" "+
                mLaps++;


            }
        });


    }
    public void updateTimeText(final String time){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvtime.setText(time);
            }
        });
    }

    public String SelectNameFromDb()
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_INTERVIEWER_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        int Name = cursor.getColumnIndex(DBHelper.KEY_NAMEINTERVIEWER);
        return cursor.getString(Name);
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSpeechInput2(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 11);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }
    public void SaveToDB(View view) {
        Calendar c = Calendar.getInstance();//date
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String date = formattedDate;
        String text = editText.getText().toString();


        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(DBHelper.KEY_DATE, date);
        contentValues.put(DBHelper.KEY_DATA, text);

        database.insert(DBHelper.TABLE_INTERVIEW,null,contentValues);
        dbHelper.close();
        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();


    }





}
