package com.cmput301w23t40.capturetheqr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirstTimeLogInActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextContactInfo;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_log_in);
        editTextUsername = findViewById(R.id.edtxt_username);
        editTextContactInfo = findViewById(R.id.edtxt_contactInfo);
        buttonSubmit = findViewById(R.id.btn_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });
    }

    public void createNewUser(){
        while(editTextUsername.toString().length() < 6 ||
                whetherUsernameExists(editTextUsername.toString())){
            // FIXME need to let the user know the reason of failure
            // FIXME prompt to input another username
        }
        saveUser(editTextUsername.toString(), editTextContactInfo.toString(), getUserIdentifier(this));
    }

    public boolean whetherUsernameExists(String username){
        // FIXME DB queries
        return false;
    }

    public void saveUser(String username, String contactInfo, String deviceID){
        // FIXME DB queries
    }

    // FIXME citation
    // https://stackoverflow.com/questions/59640456/how-to-get-device-unique-id-in-my-android-application
    // https://developer.android.com/reference/android/provider/Settings.Secure#ANDROID_ID
    // https://developer.android.com/about/versions/oreo/android-8.0-changes#privacy-all
    @SuppressLint("HardwareIds")
    public static String getUserIdentifier(Context context){
        return Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}