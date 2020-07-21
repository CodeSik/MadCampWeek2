package com.example.project2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
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
import java.util.Locale;

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

        loginButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                if (isLoggedIn) {
                    LoginManager.getInstance().logInWithReadPermissions(LoadingActivity.this, Arrays.asList("public_profile","email"));
                    String id = String.valueOf(Profile.getCurrentProfile().getId());
                    String name= String.valueOf(Profile.getCurrentProfile().getName());
                    String number = getPhoneNumber();
                    String follow = " ";
                    String state = " ";
                    String photo = "http://192.249.19.244:1180/uploads/ic_user_location.png";
                    String body = "id=" + id + '&' + "name=" + name+ '&' +"number="+number+ '&' +"follow="+follow+'&'+"state="+state+ '&' +"photo="+photo;
                    new JsonTaskPost().execute("http://192.249.19.244:1180/users", body);
                }
            }
        });




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

    @SuppressLint("MissingPermission")
    public String getPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = "";
        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            } else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (phoneNumber.startsWith("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0"); // +8210xxxxyyyy 로 시작되는 번호

        }
        //phoneNumber = phoneNumber.substring(phoneNumber.length()-10,phoneNumber.length());
        //phoneNumber="0"+phoneNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
        } else {
            phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber);
        }
        return phoneNumber;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

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