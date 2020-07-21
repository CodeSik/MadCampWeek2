package com.example.project2.ui.instamaterial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.project2.R;
import com.example.project2.ui.phonebook.CameraActivity;
import com.example.project2.ui.phonebook.GalleryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PhotoActivity extends AppCompatActivity {
    private Boolean isFabOpen = true;
    private Animation fab_open, fab_close;
    private FloatingActionButton camera, gallery, upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        camera =  findViewById(R.id.cameraButton);
        gallery =  findViewById(R.id.galleryButton);
        upload = findViewById(R.id.uploadButton4);

        upload.setOnClickListener(v -> {
            anim();
        });

        camera.setOnClickListener(v -> {
            anim();
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(intent);
        });

        gallery.setOnClickListener(v -> {
            anim();
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);

        });

        //finish(); //upload 다 되면?

    }

    public void anim() {

        if (isFabOpen) {
            camera.startAnimation(fab_close);
            gallery.startAnimation(fab_close);
            camera.setClickable(false);
            gallery.setClickable(false);
            isFabOpen = false;
        } else {
            camera.startAnimation(fab_open);
            gallery.startAnimation(fab_open);
            camera.setClickable(true);
            gallery.setClickable(true);
            isFabOpen = true;
        }
    }

}