package com.example.project2.ui.instamaterial.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.JsonTaskPost;
import com.example.project2.R;
import com.facebook.Profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.atomic.AtomicReference;

public class ContentActivity extends AppCompatActivity {
    private FloatingActionButton upload;
    private TextInputEditText contents_input;
    private ImageView imageView;
    private String feedContents;
    private String newPhotoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        upload = findViewById(R.id.uploadButton5);
        imageView = findViewById(R.id.PickedImage);
        contents_input = findViewById(R.id.InputHere);
        AtomicReference<String> body = new AtomicReference<>("");
        String id = String.valueOf(Profile.getCurrentProfile().getId());

        Bundle extras = getIntent().getExtras();
        newPhotoId = extras.getString("newPhotoId");


        //유저가 선택한 사진을 이전 Activity에서 받아와 띄우기
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bitmap);

        //contents 받기
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

        //upload
        upload.setOnClickListener(v -> {


            //upload
            String userid = id;
            String photoid = newPhotoId;
            String image = "http://192.249.19.244:1180/uploads/image" + photoid+".png";
            String contents = feedContents;
            int like = 0;
            body.set("id=" + userid + '&' + "photoid=" + photoid + '&' + "image=" + image + '&' + "contents=" + contents + '&' + "number=" + like);
            new JsonTaskPost().execute("http://192.249.19.244:1180/gallery", body.get());

        });

    }



}
