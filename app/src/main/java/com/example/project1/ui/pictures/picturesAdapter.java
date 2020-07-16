package com.example.project1.ui.pictures;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.project1.R;

//picture fragment의 adapter
public class picturesAdapter extends BaseAdapter {
    private Context mContext;

    public Integer[] Thumbs={
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

    public picturesAdapter(Context c){
        mContext=c;
    }


    @Override
    public int getCount() {
        return Thumbs.length;
    }

    @Override
    public Object getItem(int i) {
        return Thumbs[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    //imageview 생성한다음 gridview layout에 적용
    @Override
    public View getView(int i, View convertview, ViewGroup parent) {
        ImageView imageView=new ImageView(mContext);
        imageView.setImageResource(Thumbs[i]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(350,350));

        return imageView;

    }
}
