package com.example.project2.ui.pictures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project2.R;

//imageclick 했을 때 fullimageactivity
public class FullImageActivity extends Activity implements PreviewAdapter.OnListItemSelectedInterface{
    ViewPager2 viewPager2;
    UserAdapter userAdapter;
    PreviewAdapter previewAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);


        //intent를 이용해 picturefragment에서 특정 사진의 position 값 저장
        Intent i = getIntent();

        int position=i.getExtras().getInt("id");


        viewPager2=findViewById(R.id.viewPager2);
        Integer[] icons={
                R.drawable.pic_1,
                R.drawable.pic_2,
                R.drawable.pic_3,
                R.drawable.pic_4,
                R.drawable.pic_5,
                R.drawable.pic_6,
                R.drawable.pic_7,
                R.drawable.pic_8,
                R.drawable.pic_9,
                R.drawable.pic_10,
                R.drawable.pic_1,
                R.drawable.pic_2,
                R.drawable.pic_3,
                R.drawable.pic_4,
                R.drawable.pic_5,
                R.drawable.pic_6,
                R.drawable.pic_7,
                R.drawable.pic_8,
                R.drawable.pic_9,
                R.drawable.pic_10
        };

        //adapter 적용
        userAdapter=new UserAdapter(this, icons);
        viewPager2.setAdapter(userAdapter);
        viewPager2.setCurrentItem(position, false);

        RecyclerView recyclerView = findViewById(R.id.image_preview);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(FullImageActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontalLayoutManager.scrollToPosition(position-1);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        previewAdapter = new PreviewAdapter(this, icons, this);
        recyclerView.setAdapter(previewAdapter);


    }


    @Override
    public void onItemSelected(View v, int position) {
        RecyclerView recyclerView = findViewById(R.id.image_preview);
        PreviewAdapter.ViewHolder viewHolder = (PreviewAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        recyclerView.scrollToPosition(position+1);
        viewPager2.setCurrentItem(position);
    }
}
