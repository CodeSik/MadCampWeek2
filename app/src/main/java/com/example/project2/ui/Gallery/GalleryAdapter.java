package com.example.project2.ui.Gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project2.R;


import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<Bitmap> mData;


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView name;
        private TextView number;
        private TextView email;
        private ImageView profile;

        public ViewHolder(View itemView) {
            super(itemView) ;
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            email = itemView.findViewById(R.id.email);
            profile = itemView.findViewById(R.id.profile);
            photo = itemView.findViewById(R.id.photo);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    GalleryAdapter(ArrayList<Bitmap> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.fragment_gallery_listview, parent, false) ;
        GalleryAdapter.ViewHolder vh = new ViewHolder(view);

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = mData.get(position);
        holder.photo.setImageBitmap(bitmap);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.size();
        }
    }
}



