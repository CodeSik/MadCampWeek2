package com.example.project2.ui.Gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.ui.phonebook.ProfileData;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<GalleryData> listViewItemList;
    private ProfileData profileData;
    private Context context;

    public GalleryAdapter(ArrayList<GalleryData> items, Context context) {
        this.listViewItemList = items;
        this.profileData = new ProfileData();
        this.context = context;
    }

    public ArrayList<GalleryData> getListViewItemList() {
        return listViewItemList;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profile;
        private TextView name;
        private ImageView image;
        private Button likeList;


        public ViewHolder(View itemView) {
            super(itemView) ;

            profile = itemView.findViewById(R.id.profile_feed);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            likeList = itemView.findViewById(R.id.like_list_Button);
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.fragment_gallery_listview, parent, false) ;
        GalleryAdapter.ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
        final GalleryData item = listViewItemList.get(position);
        final ProfileData profileItem = profileData;
        Glide.with(context).load(item.getImage()).into(holder.image);
        Glide.with(context).load(profileItem.getPhoto()).into(holder.profile);
        holder.name.setText(profileItem.getName());
        holder.likeList.setText("좋아요" + item.getLike() + "개");
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        if (listViewItemList == null) {
            return 0;
        } else {
            return listViewItemList.size();
        }
    }
    public void updateItems(ArrayList<GalleryData> items) {
        listViewItemList.clear();
        if (items != null)
            listViewItemList.addAll(items);
        notifyDataSetChanged();
    }

    public void updateProfile(ProfileData item) {
        profileData = item;
        notifyDataSetChanged();
    }
}



