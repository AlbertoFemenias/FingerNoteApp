package com.example.fingernoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";

    EditText mEditText;
    String password;


    //PREVENT APP RUNNING IN BACKGROUND FOR SAFETY
    @Override
    public void onPause(){
        super.onPause();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        password = getIntent().getStringExtra("PLAINPASS");
        mEditText = findViewById(R.id.edit_text);
    }

    public void save(View v) {
        String text = mEditText.getText().toString();
        String encryptedText;
        FileOutputStream fos = null;

        if (text.equals("")){  //IF THERE IS NO INPUT, JUST DONT SAVE
            return;
        }

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE); //OPEN FILE
            //encryptedText = encrypt(text, password);       //ENCRYPT INPUT
            fos.write(text.getBytes());           //SAVE ENCRYPTED INPUT

            mEditText.getText().clear();
            //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(View v) {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text, decryptedText;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            //decryptedText = decrypt(sb.toString(), password);

            //mEditText.setText();
            mEditText.setText(sb.toString());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}