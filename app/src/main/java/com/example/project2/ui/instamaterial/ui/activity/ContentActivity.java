package com.example.project2.ui.instamaterial.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import java.util.concurrent.atomic.AtomicReference;

public class ContentActivity extends AppCompatActivity {
    private FloatingActionButton upload;
    private TextInputEditText contents_input;
    private ImageView imageView;
    private String feedContents;
    private Integer newphotoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        upload = findViewById(R.id.uploadButton5);
        imageView = findViewById(R.id.PickedImage);
        contents_input = findViewById(R.id.InputHere);
        AtomicReference<String> body = new AtomicReference<>("");
        String id = String.valueOf(Profile.getCurrentProfile().getId());

        new JsonTaskGetFeeds().execute("http://192.249.19.244:1180/gallery");


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
            String photoid = newphotoid.toString();
            String image = "http://192.249.19.244:1180/uploads/image" + photoid+".png";
            String contents = feedContents;
            int like = 0;
            body.set("id=" + userid + '&' + "photoid=" + photoid + '&' + "image=" + image + '&' + "contents=" + contents + '&' + "number=" + like);
            new JsonTaskPost().execute("http://192.249.19.244:1180/gallery", body.get());

        });

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


//    public class JsonTaskGetWriter extends AsyncTask<String, String, String>
//    {
//        @Override
//        protected String doInBackground(String... urls) {
//
//            try {
//                HttpURLConnection con = null;
//                BufferedReader reader = null;
//                try{
//                    URL url = new URL(urls[0]);
//                    //연결을 함
//                    con = (HttpURLConnection) url.openConnection();
//
//                    con.connect();
//
//                    InputStream stream = con.getInputStream();
//                    reader = new BufferedReader(new InputStreamReader(stream));
//
//                    //실제 데이터를 받는곳
//                    StringBuffer buffer = new StringBuffer();
//                    //line별 스트링을 받기 위한 temp 변수
//                    String line = "";
//                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
//                    while((line = reader.readLine()) != null){
//                        buffer.append(line);
//                    }
//                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String… urls) 니까
//                    return buffer.toString();
//                    //아래는 예외처리 부분이다.
//
//                } catch (MalformedURLException e){
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if(con != null){
//                        con.disconnect();
//                    }
//                    try {
//                        if(reader != null){
//                            reader.close();//버퍼를 닫아줌
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                JSONArray jarray = new JSONArray(result);
//
//                for (int i = 0; i < jarray.length(); i++) {
//                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
//                    String id = jObject.getString("id");
//                    String name = jObject.getString("name");
//                    String number = jObject.getString("number");
//                    String follow = jObject.getString("follow");
//                    String state = jObject.getString("state");
//                    String photo = jObject.getString("photo");
//                    profileInfo = new ProfileData(id, name, number, follow, state, photo);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//
//    }

    }
}