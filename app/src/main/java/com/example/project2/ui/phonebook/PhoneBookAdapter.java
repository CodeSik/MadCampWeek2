package com.example.project2.ui.phonebook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.R;

import java.util.ArrayList;

public class PhoneBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<JsonData> listViewItemList;
    private Context context;
    public static final int PROFILE_CONTENT = 0;
    public static final int UNFOLLOW_CONTENT = 1;
    public static final int FOLLOW_CONTENT = 2;

    public PhoneBookAdapter(ArrayList<JsonData> items, Context context) {
        this.listViewItemList = items;
        this.context = context;
    }

    public ArrayList<JsonData> getListViewItemList() {
        return listViewItemList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return PROFILE_CONTENT;
        else
            return UNFOLLOW_CONTENT;
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder{

        public ProfileViewHolder(@NonNull View ProfileView) {
            super(ProfileView);
        }
    }

    public class PhoneBookViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView name;
        private TextView number;
        private TextView email;
        private ImageButton callButton;
        private ImageButton smsButton;
        private Button followButton;
        private View expandableList;

        public PhoneBookViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            photo = itemView.findViewById(R.id.photo);
            expandableList = itemView.findViewById(R.id.expandable_list);
            callButton = itemView.findViewById(R.id.call_button);
            smsButton = itemView.findViewById(R.id.sms_button);
            followButton = itemView.findViewById(R.id.follow_button);
        }

        public void bind(final JsonData item) {
            boolean expanded = item.getExpanded();

            expandableList.setVisibility(expanded ? View.VISIBLE : View.GONE);
            name.setText(item.getName());
            number.setText(item.getNumber());
            Glide.with(context).load(item.getPhoto()).into(photo);

            callButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(PhoneBookAdapter.this.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity)PhoneBookAdapter.this.context, new String[]{ Manifest.permission.CALL_PHONE }, PhoneBookFragment.PERMISSIONS_CALL_PHONE);
                    else {
                        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumber()));
                        context.startActivity(call);
                    }
                }
            });

            smsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(PhoneBookAdapter.this.context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity)PhoneBookAdapter.this.context, new String[]{ Manifest.permission.SEND_SMS }, PhoneBookFragment.PERMISSIONS_REQUEST_SEND_SMS);
                    else {
                        Intent send = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + item.getNumber()));
                        context.startActivity(send);
                    }
                }
            });

            followButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //follow action
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder holder;
        View view;
        if (viewType == PROFILE_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_phonebook_profileview, parent, false);
            holder = new ProfileViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_phonebook_listview, parent, false);
            holder = new PhoneBookViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof PhoneBookViewHolder) {
            final JsonData item = listViewItemList.get(position);
            PhoneBookViewHolder phoneBookViewHolder = (PhoneBookViewHolder)holder;
            phoneBookViewHolder.bind(item);
            phoneBookViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setExpanded(!item.getExpanded());
                    notifyItemChanged(position);
                }
            });
        }
        else if(holder instanceof ProfileViewHolder)
        {
            //TODO: 프로필에 바인드 함수 구현 &
            ProfileViewHolder profileViewHolder = (ProfileViewHolder)holder;
        }
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public void updateItems(ArrayList<JsonData> items) {
        listViewItemList.clear();
        if (items != null)
            listViewItemList.addAll(items);
        notifyDataSetChanged();
    }


    public void fillter(String searchText, ArrayList<JsonData> backupList){

        listViewItemList.clear();

        for( JsonData item : backupList)
        {
            if(item.getName().toUpperCase().contains(searchText.toUpperCase()))
            {
                listViewItemList.add(item);
            }
        }

        notifyDataSetChanged();

    }
}
