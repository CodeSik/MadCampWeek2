package com.example.project2.ui.pictures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.R;


//fullimageactivity의 adapter
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    Context mContext;


    public UserAdapter(Context mContext, Integer[] userList){
        this.mContext=mContext;
    }

    public Integer[] icons={
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

    @NonNull
    @Override
    //user_item layout의 view 생성
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.icon.setImageResource(icons[position]);
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.icon);
        }
    }
}
