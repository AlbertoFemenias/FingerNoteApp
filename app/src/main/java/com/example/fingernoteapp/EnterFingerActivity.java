package com.example.fingernoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import android.os.Bundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;


public class EnterFingerActivity extends AppCompatActivity {

    Button button;

    private static final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_finger);

        button = (Button) findViewById(R.id.button);


        //Create a thread pool with a single thread//
        Executor newExecutor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = this;



        //Start listening for authentication events//
        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            //onAuthenticationError is called when a fatal error occurrs//
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                } else {
                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }
           //onAuthenticationSucceeded is called when a fingerprint is matched successfully//
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Fingerprint recognised successfully");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
            //onAuthenticationFailed is called when the fingerprint doesn’t match//
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Fingerprint not recognised");
            }
        });





        //Create the BiometricPrompt instance//
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                //Add some text to the dialog//
                .setTitle("Fingerprint identification")
                //.setSubtitle("Subtitle goes here")
                .setDescription("To enter the app you must validate using the fingerprint scanner")
                .setNegativeButtonText("Cancel")
                //Build the dialog//
                .build();




        //Assign an onClickListener to the app’s “Authentication” button//
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBiometricPrompt.authenticate(promptInfo);
            }
        });

    }

}
