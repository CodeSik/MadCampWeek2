package com.example.project2.ui.randomgame;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.R;

public class RandomGameFragment extends Fragment {
    private RandomGameViewModel randomGameViewModel;
    private ImageView bottleImageView;
    private Button button;
    private Random rng = new Random();
    private float lastDegree;
    private String[] bottlePaths;
    private Map<String, Integer> bottleMap;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // randomGameViewModel =
        //        ViewModelProviders.of(this).get(RandomGameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_randomgame, container, false);

        recyclerView = root.findViewById(R.id.rg_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final AnimationDrawable popAnimation;

        try {
            bottlePaths = getActivity().getAssets().list("bottles");
        } catch (java.io.IOException e) {
            bottlePaths = new String[]{};
        }

        bottleMap = new HashMap<String, Integer>();
        for (Field f : com.example.project2.R.drawable.class.getFields()) {
            try {
                String fieldName = f.getName();
                if (!fieldName.startsWith("bottle_"))
                    continue;
                int resourceId = getResources().getIdentifier(fieldName, "drawable", getActivity().getPackageName());
                bottleMap.put(fieldName, resourceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mAdapter = new RGAdapter(bottlePaths, new RGAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageView imageView) {
                String tag = (String) imageView.getTag();
                String filename = tag.substring(0, tag.length() - 4);
                bottleImageView.setImageResource(bottleMap.get(filename));
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        bottleImageView = root.findViewById(R.id.text_randomgame);
        bottleImageView.setBackgroundResource(R.drawable.pop_animation);
        button = root.findViewById(R.id.spin_bottle);
        popAnimation=(AnimationDrawable) bottleImageView.getBackground();

        recyclerView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                ((AnimationDrawable)(bottleImageView).getBackground()).stop();
                bottleImageView.setBackgroundDrawable(null);
                bottleImageView.setBackgroundResource(R.drawable.pop_animation);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationSet animSet = new AnimationSet(true);
                animSet.setInterpolator(new AnticipateOvershootInterpolator(1f, 0.3f));
                animSet.setFillAfter(true);
                animSet.setFillEnabled(true);

                float randDegree = rng.nextInt(360);
                final RotateAnimation rotate = new RotateAnimation(
                        lastDegree,
                        lastDegree + 360 * 20 + randDegree,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );
                lastDegree = (lastDegree + randDegree) % 360;
                rotate.setDuration((rng.nextInt(5) + 5) * 1000);
                rotate.setFillAfter(true);
                animSet.addAnimation(rotate);
                bottleImageView.startAnimation(animSet);

                final CustomAnimationDrawableNew cad= new CustomAnimationDrawableNew((AnimationDrawable) getResources().getDrawable(R.drawable.pop_animation)) {
                    @Override
                    public void onAnimationFinish() {
                        /*((AnimationDrawable)(bottleImageView).getBackground()).stop();*/
                        /*bottleImageView.setBackgroundDrawable(null);*/
                        /*bottleImageView.setBackgroundResource(R.drawable.pop_animation);*/
                        bottleImageView.setBackgroundResource(R.drawable.pop_14);

                    }

                    @Override
                    public void onAnimationStart() {

                    }
                };



                /*bottleImageView.setBackground(cad);
                cad.start();*/




                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottleImageView.setBackground(cad);
                        cad.start();

                    }

                }, 1000);







            }
        });


        return root;
    }
}
