package com.example.project2.ui.instamaterial.ui.adapter;

import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.ui.instamaterial.ui.activity.InstaActivity;
import com.example.project2.ui.instamaterial.ui.activity.UserProfileActivity;
import com.example.project2.ui.instamaterial.ui.view.LoadingFeedItemView;
import com.facebook.Profile;


/**
 * Created by froger_mcs on 05.11.14.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    private final List<FeedItem> feedItems = new ArrayList<>();

    private static Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = true;



    public FeedAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }

        return null;
    }

    private void updatelike(int like, int i){

            String id = feedItems.get(i).getId();
            String image= feedItems.get(i).getImage();
            String photoid = feedItems.get(i).getPhotoid();
            String name = feedItems.get(i).getName();
            String contents = feedItems.get(i).getContents();
            int likecount = like;
            String body = "id=" + id + '&' + "image="+image+ '&' +"photoid="+photoid+ '&' +"name="+name+ '&' +"contents="+contents+ '&' +"like=" + likecount;

            new JsonTaskPutFeedForLike().execute("http://192.249.19.244:1180/gallery/"+photoid, body);
        }




    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                int likesCount = feedItems.get(adapterPosition).getLike();
                if(feedItems.get(adapterPosition).isLiked) {
                    feedItems.get(adapterPosition).setLike(likesCount-1);
                    feedItems.get(adapterPosition).isLiked = false;
                    updatelike(feedItems.get(adapterPosition).getLike(),adapterPosition);
                    notifyItemChanged(adapterPosition);
                }
                else {
                    feedItems.get(adapterPosition).setLike(likesCount+1);
                    feedItems.get(adapterPosition).isLiked=true;
                    updatelike(feedItems.get(adapterPosition).getLike(),adapterPosition);
                    notifyItemChanged(adapterPosition);
                }
                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                if (context instanceof InstaActivity) {
                    if (feedItems.get(adapterPosition).isLiked)
                        ((InstaActivity) context).showLikedSnackbar();
                    else
                        ((InstaActivity) context).showUnLikedSnackbar();
                }
            }
        });

        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                int likesCount = feedItems.get(adapterPosition).getLike();
                if(feedItems.get(adapterPosition).isLiked) {
                    feedItems.get(adapterPosition).setLike(likesCount-1);
                    feedItems.get(adapterPosition).isLiked = false;
                    updatelike(feedItems.get(adapterPosition).getLike(),adapterPosition);
                    notifyItemChanged(adapterPosition);
                }
                else {
                    feedItems.get(adapterPosition).setLike(likesCount+1);
                    feedItems.get(adapterPosition).isLiked=true;
                    updatelike(feedItems.get(adapterPosition).getLike(),adapterPosition);
                    notifyItemChanged(adapterPosition);
                }
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                if (context instanceof InstaActivity) {
                    if (feedItems.get(adapterPosition).isLiked)
                        ((InstaActivity) context).showLikedSnackbar();
                    else
                        ((InstaActivity) context).showUnLikedSnackbar();

                }
            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onFeedItemClickListener.onProfileClick(view);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(feedItems.get(position));

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }

    }

    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    public List<FeedItem> getFeedItems() {
        return feedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public void updateItems(boolean animated, ArrayList<FeedItem> feeditems) {
        feedItems.clear();
        feedItems.addAll(feeditems);
        Collections.reverse(feeditems);
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }
    }
    public void updateItems(boolean animated) {

        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @BindView(R.id.ivFeedBottom)
        ImageView ivFeedBottom;
        @BindView(R.id.btnComments)
        ImageButton btnComments;
        @BindView(R.id.btnLike)
        ImageButton btnLike;
        @BindView(R.id.btnMore)
        ImageButton btnMore;
        @BindView(R.id.vBgLike)
        View vBgLike;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @BindView(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @BindView(R.id.vImageRoot)
        FrameLayout vImageRoot;

        FeedItem feedItem;



        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindView(FeedItem feedItem) {
            this.feedItem = feedItem;
            int adapterPosition = getAdapterPosition();
            String id = String.valueOf(Profile.getCurrentProfile().getId());
            //프사 설정
            Glide.with(context).load("http://192.249.19.244:1180/uploads/image"+id+".png").into(ivUserProfile);
            //이름 설정
            username.setText(feedItem.getName());
            //center가 photo
            Glide.with(context).load(feedItem.getImage()).into(ivFeedCenter);

            //bottom이 contents
            TextView content = itemView.findViewById(R.id.contentsView);
            content.setText(feedItem.getContents());

            TextView name = itemView.findViewById(R.id.nameView);
            name.setText(feedItem.getName());

            btnLike.setImageResource(feedItem.isLiked ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
            tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                    R.plurals.likes_count, feedItem.getLike(), feedItem.getLike()
            ));
        }

        public FeedItem getFeedItem() {
            return feedItem;
        }
    }

    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(FeedItem feedItem) {
            super.bindView(feedItem);
        }
    }



    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }

    private class JsonTaskPutFeedForLike extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                String body= urls[1];
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.249.19.244:1180/phonebook");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("PUT");
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//application JSON 형식으로 전송
                    // con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    // con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    //Log.d("josn",jsonObject.toString());
                    con.connect();
                    Log.d("josn",body);
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

                    while((line = reader.readLine()) != null){

                        buffer.append(line);

                    }
                    Log.d("output buffer", buffer.toString());
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

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
            //Toast.makeText(this,result,0);
        }


    }
}
