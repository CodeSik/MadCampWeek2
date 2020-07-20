package com.example.project2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

public class LoadingActivity extends Activity {
    private CallbackManager callbackManager;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    EditText email_login;
    EditText pass_login;
    TextInputLayout mInputLayoutEmailForgotPassword;
    TextInputLayout mInputLayoutFirstNameSignup;
    TextInputLayout mInputLayoutLastNameSignup;

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int PERMISSIONS_CALL_PHONE = 2;
    private static final int PERMISSIONS_REQUEST_ALL = 3;
    private static String[] requiredPermissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestRequiredPermissions();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("public_profile");


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
            String id = String.valueOf(Profile.getCurrentProfile().getId());
            String name= String.valueOf(Profile.getCurrentProfile().getName());
            String follow = " ";
            String state = " ";
            String photo = "http://192.249.19.244:1180/uploads/ic_user_location.png";
            String body = "id=" + id + '&' + "name=" + name+ '&' +"follow="+follow+'&'+"state="+state+ '&' +"photo="+photo;
            new JsonTaskPost().execute("http://192.249.19.244:1180/users", body);
        }


// Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code



            }

            @Override
            public void onCancel() {
                // App code
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                Toast.makeText( getApplicationContext() , "페이스북 로그인을 취소하셨습니다." , Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText( getApplicationContext() , exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
    private void requestRequiredPermissions() {
        boolean allGranted = true;
        for (String permission : this.requiredPermissions) {
            boolean granted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
            allGranted = allGranted && granted;
        }
        if (!allGranted)
            requestPermissions(requiredPermissions, PERMISSIONS_REQUEST_ALL);
    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(this::finish, 1000);
    }
}