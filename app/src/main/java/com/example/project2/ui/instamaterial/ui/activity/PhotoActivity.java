package com.example.project2.ui.instamaterial.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.project2.R;
import com.example.project2.ui.phonebook.CameraActivity;
import com.example.project2.ui.phonebook.GalleryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class PhotoActivity extends AppCompatActivity {
    private Boolean isFabOpen = true;
    private Animation fab_open, fab_close;
    private FloatingActionButton camera, gallery, upload;
    private Integer newphotoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        camera = findViewById(R.id.cameraButton);
        gallery = findViewById(R.id.galleryButton);
        upload = findViewById(R.id.uploadButton4);


        new JsonTaskGetFeeds().execute("http://192.249.19.244:1180/gallery");

        upload.setOnClickListener(v -> {
            anim();
        });

        camera.setOnClickListener(v -> {
            anim();
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("newPhotoId", newphotoid.toString());
            startActivity(intent);
        });

        gallery.setOnClickListener(v -> {
            anim();
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            intent.putExtra("newPhotoId", newphotoid.toString());
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


    public class JsonTaskGetFeeds extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.connect();

                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();
                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";
                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String… urls) 니까
                    return buffer.toString();
                    //아래는 예외처리 부분이다.

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            ArrayList<Integer> photoidList = new ArrayList<>();
            try {
                JSONArray jarray = new JSONArray(result);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                    int photoid = Integer.parseInt(jObject.getString("photoid"));
                    photoidList.add(photoid);
//                    String id = jObject.getString("id");
//                    String name = jObject.getString("name");
//                    String number = jObject.getString("number");
//                    JsonData data = new JsonData(id, name, number, photo);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            newphotoid = Collections.max(photoidList);
            newphotoid += 1;
        }
    }
}