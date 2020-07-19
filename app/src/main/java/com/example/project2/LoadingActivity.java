package com.example.project2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;


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
                String id = String.valueOf(Profile.getCurrentProfile().getId());
                String name= String.valueOf(Profile.getCurrentProfile().getName());

                String body = "id=" + id + '&' + "name=" + name;
                new JsonTaskPost().execute("http://192.249.19.244:1180/users", body);
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