package com.example.project2.ui.instamaterial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.project2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class ContentActivity extends AppCompatActivity {
    private FloatingActionButton upload;
    private TextInputEditText contents_input;
    private String feedContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        upload = findViewById(R.id.uploadButton5);

        contents_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                feedContents = s.toString();
                //feedContents 가 유저가 입력하는 피드 내용. 이후에 uploadButton 을 누르면 서버에 전송하기.
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        upload.setOnClickListener(v -> {
            //upload
        });


    }
}