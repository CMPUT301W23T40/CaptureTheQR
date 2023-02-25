package com.cmput301w23t40.capturetheqr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * If the program detects this device is new, the user needs to sign up with
 * a unique username and a phone number
 */
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

    /**
     * Take the username and phone number the user entered and try to find the username
     * in the database. If the username is unique and has proper length, save the info in
     * the database; if not, prompt the user to input again.
     */
    public void createNewUser(){
        while(editTextUsername.toString().length() < 6 ||
                !DB.UsernameIsNew(editTextUsername.toString())){
            // FIXME need to let the user know the reason of failure
            // FIXME prompt to input another username
        }
        DB.saveUser(editTextUsername.toString(), editTextContactInfo.toString(), getDeviceID(this));
    }



    /**
     * Get ANDROID_ID as the unique device ID. According to the android documentation:
     * "The value of ANDROID_ID is unique for each combination of app-signing key, user, and device."
     * "The value of ANDROID_ID does not change on package uninstall or reinstall, as long as the
     * signing key is the same." "The value may change if a factory reset is performed on the device ..."
     * More about ANDROID_ID is available at https://developer.android.com/reference/android/provider/Settings.Secure#ANDROID_ID
     * and https://developer.android.com/about/versions/oreo/android-8.0-changes#privacy-all
     * @param context the context from which to get the ANDROID_ID
     * @return ANDROID_ID
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context){
        /*
        Adapted code from the following resource for getting ANDROID_ID:
        author: https://stackoverflow.com/users/4360419/arsal-imam
        url: https://stackoverflow.com/questions/59640456/how-to-get-device-unique-id-in-my-android-application
        last updated: Jan 8, 2020
        licence: CC BY-SA 4.0, https://creativecommons.org/licenses/by-sa/4.0/deed.en
        */
        return Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}