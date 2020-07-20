package com.example.project2.ui.phonebook;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class StateActivity extends AppCompatActivity {
    private TextInputEditText editText;
    private TextView textView;
    private TextView statusMessage;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        this.getViewObject();

//        statusMessage = findViewById(R.id.state_profile);
//        textView.setText(statusMessage.getText());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FloatingActionButton fab = findViewById(R.id.uploadButton4);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //send contents
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
            }
        });
    }

    private void getViewObject()
    {
        editText = findViewById(R.id.STATEinput);
        textView = findViewById(R.id.STATUS);
    }
}

