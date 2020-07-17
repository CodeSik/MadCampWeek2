package com.example.project2.ui.pictures;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project2.R;

//처음 gallery 화면 - grid view
public class PicturesFragment extends Fragment {

    private PicturesViewModel picturesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //gird view 생성
        View root = inflater.inflate(R.layout.fragment_pictures, container, false);
        GridView gridView = (GridView) root.findViewById(R.id.grid_view);
        gridView.setAdapter(new picturesAdapter(getActivity()));

        //grid view의 picture click 했을 때, 사진의 position 값 받아서 FullImageActivity 에 전달, 실행
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getActivity(), FullImageActivity.class);
                i.putExtra("id", position);
                startActivity(i);
            }
        });
        return root;
    }
}