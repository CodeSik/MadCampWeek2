package com.example.project2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
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
import java.util.Arrays;

public class LoadingActivity extends Activity {
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("public_profile");

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
        }

// Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("Success", String.valueOf(loginResult.getAccessToken()));
                Log.d("Success", String.valueOf(Profile.getCurrentProfile().getId()));
                Log.d("Success", String.valueOf(Profile.getCurrentProfile().getName()));
                Toast.makeText(getApplicationContext(), String.valueOf(Profile.getCurrentProfile().getName()) , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText( getApplicationContext() , "페이스북 로그인을 취소하셨습니다." , Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText( getApplicationContext() , exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


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