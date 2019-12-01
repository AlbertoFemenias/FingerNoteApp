package com.example.fingernoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SAMPLE_ALIAS = "MYALIAS";

    private EnCryptor encryptor;
    private DeCryptor decryptor;
    private byte[] ivGeneral;

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


        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        String stringArray = settings.getString("myIV", null);
        if (stringArray != null) {
           /* String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
            ivGeneral = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                ivGeneral[i] = Byte.parseByte(split[i]);
            }*/
           ivGeneral = Base64.decode(stringArray, Base64.DEFAULT);

            Log.d(TAG,"Se cargo el iv:"+ ivGeneral.toString());

        }



        encryptor = new EnCryptor();


        try {
            decryptor = new DeCryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }
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
            //encryptedText = encryptText(text);       //ENCRYPT INPUT
            fos.write(encryptText(text));           //SAVE ENCRYPTED INPUT

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
        byte[] buffer = null;

        try {
            fis = openFileInput(FILE_NAME);
            buffer =   new byte[(int) fis.getChannel().size()];
            fis.read(buffer);
            /*InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text, decryptedText;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }*/


            String decryptedText = decryptText(buffer);

            mEditText.setText(decryptedText);
            //mEditText.setText(sb.toString());


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


    private String decryptText(byte[] encryptedData) {
        String decrypted = null;
        try {
            decrypted = decryptor.decryptData(SAMPLE_ALIAS, encryptedData, ivGeneral);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            Log.e(TAG, "decryptData() called with: " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    private byte [] encryptText(String toEncrypt) {
        byte[] encrypted = null;
        try {
            encrypted = encryptor.encryptText(SAMPLE_ALIAS, toEncrypt);
            ivGeneral = encryptor.getIv();
            SharedPreferences settings = getSharedPreferences("PREFS", 0);
            SharedPreferences.Editor editor = settings.edit();
            String saveThis = Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT);
            editor.putString("myIV", saveThis);
            editor.apply();

            Log.d(TAG,"Se guarda el iv:"+ saveThis);


        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG, "onClick() called with: " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return encrypted;
    }


}