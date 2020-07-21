package com.example.project2.ui.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.ui.instamaterial.ui.adapter.FeedItem;
import com.example.project2.ui.instamaterial.ui.adapter.UserProfileAdapter;
import com.example.project2.ui.instamaterial.ui.utils.CircleTransformation;
import com.example.project2.ui.instamaterial.ui.view.RevealBackgroundView;
import com.example.project2.ui.phonebook.PhoneBookFragment;
import com.facebook.Profile;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

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

import butterknife.BindView;


/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private ArrayList<String> photoList;
    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.vUserStats)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    @BindView(R.id.posts_numbers)
    TextView postNum;
    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;


    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);

        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        String id = String.valueOf(Profile.getCurrentProfile().getId());
        photoList = new ArrayList<>();
        new JsonTaskGetfeed().execute("http://192.249.19.244:1180/gallery/"+id);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = "http://192.249.19.244:1180/uploads/image"+id+".png";
       // Glide.with(getApplicationContext()).load(profilePhoto).into(ivUserProfilePhoto);

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);



        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().hide();
        } else if (Build.VERSION.SDK_INT < 21) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }


    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));

    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this,photoList);
            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
            postNum.setText(Integer.toString(photoList.size()));
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());
           vUserStats.setAlpha(0);

           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
           vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }


    private class JsonTaskGetfeed extends AsyncTask<String, String, String> {


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

            //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();

            try {
                JSONArray jarray = new JSONArray(result);

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                    String id = jObject.getString("id");
                    String image = jObject.getString("image");
                    String name = jObject.getString("name");
                    String photoid = jObject.getString("photoid");
                    String contents=jObject.getString("contents");
                    int like = jObject.getInt("like");
                    photoList.add(image);
                }


            }catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
            Log.d("printget",result);
        }


    }
}
