package com.example.project1.ui.randomgame;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;

import java.io.IOException;
import java.io.InputStream;

public class RGAdapter extends RecyclerView.Adapter<RGAdapter.MyViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(ImageView imageView);
    }

    private String[] imageDataset;
    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public MyViewHolder(ImageView v) {
            super(v);
            imageView = v;
        }

        public void bind(final ImageView imageView, final OnItemClickListener listener) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(imageView);
                }
            });
        }
    }

    public RGAdapter(String[] dataset, OnItemClickListener listener) {
        this.imageDataset = dataset;
        this.listener = listener;
    }

    @Override
    public RGAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InputStream inputStream;
        String path = "bottles/" + imageDataset[position];
        try {
            inputStream = holder.imageView.getContext().getAssets().open(path);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            holder.imageView.setImageDrawable(drawable);
        } catch (IOException e) {}
        holder.imageView.setTag(imageDataset[position]);
        holder.bind(holder.imageView, listener);
    }

    @Override
    public int getItemCount() {
        return imageDataset.length;
    }
}

