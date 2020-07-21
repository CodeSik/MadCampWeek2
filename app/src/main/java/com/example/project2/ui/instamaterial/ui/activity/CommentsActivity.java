package com.example.project2.ui.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;

import com.example.project2.JsonTaskPost;
import com.example.project2.R;
import com.example.project2.ui.instamaterial.Utils;
import com.example.project2.ui.instamaterial.ui.adapter.CommentItem;
import com.example.project2.ui.instamaterial.ui.adapter.CommentsAdapter;
import com.example.project2.ui.instamaterial.ui.adapter.FeedItem;
import com.example.project2.ui.instamaterial.ui.view.SendCommentButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentsActivity extends BaseDrawerActivity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    private ArrayList<CommentItem> recvitems;
    private CommentItem senditem;
    private String photoid;
    private String comment;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupComments();
        setupSendCommentButton();
        recvitems = new ArrayList<CommentItem>();
        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        photoid = getIntent().getStringExtra("photoid");
        name = getIntent().getStringExtra("name");

        //new JsonTaskCommentGet().execute("http://192.249.19.244:1180/comment", body);
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                comment = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().hide();
        } else if (Build.VERSION.SDK_INT < 21) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {

            senditem = new CommentItem(photoid,name,comment);
            String body = " ";
            String sdphotoid = senditem.getPhotoid();
            String sdname = senditem.getName();
            String sdcomment = senditem.getComment();
            body = "photoid=" + sdphotoid + '&' + "name=" + sdname + '&' + "comment=" + sdcomment;
            new JsonTaskCommentPost().execute("http://192.249.19.244:1180/comment", body);

            new JsonTaskCommentGet().execute("http://192.249.19.244:1180/comment/photoid");

            commentsAdapter.setAnimationsLocked(false);
            commentsAdapter.setDelayEnterAnimation(false);
            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);

        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }


    public class JsonTaskCommentPost extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... urls) {

            try {
                String body = urls[1];
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    //URL url = new URL("http://192.249.19.244:1180/phonebook");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//application JSON 형식으로 전송
                    // con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    // con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    //Log.d("josn",jsonObject.toString());
                    con.connect();
                    Log.d("josn", body);
                    //서버로 보내기위해서 스트림 만듬

                    OutputStream outStream = con.getOutputStream();

                    //버퍼를 생성하고 넣음

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                    writer.write(body);
                    //Log.d("josn123232323",jsonObject.toString());
                    writer.flush();

                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {

                        buffer.append(line);

                    }
                    Log.d("output buffer", buffer.toString());
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

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
            //Toast.makeText(this,result,0);
        }

    }

    private class JsonTaskCommentGet extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        protected void onPreExecute() {

            super.onPreExecute();

            dialog = new ProgressDialog(CommentsActivity.this);

            //dialog.setCancelable(false);

            dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{

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

                    while((line = reader.readLine()) != null){

                        buffer.append(line);
                    }
                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String… urls) 니까
                    return buffer.toString();
                    //아래는 예외처리 부분이다.


                } catch (MalformedURLException e){

                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){

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
            dialog.dismiss();
            //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            recvitems.clear();
            commentsAdapter.getCommentItems().clear();
            try {
                JSONArray jarray = new JSONArray(result);

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                    String photoid = jObject.getString("id");
                    String name = jObject.getString("name");
                    String comment = jObject.getString("comment");
                    CommentItem data = new CommentItem(photoid,name,comment);
                    recvitems.add(data);
                }


            }catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commentsAdapter.addItem(recvitems);
                    commentsAdapter.notifyDataSetChanged();
                }
            });
            Log.d("printget",result);
        }
    }
}
