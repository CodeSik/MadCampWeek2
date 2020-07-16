package com.example.project1.ui.gamepager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project1.ui.argame.ARGameFragment;
import com.example.project1.ui.randomgame.RandomGameFragment;


public class GamePagerAdapter extends FragmentStateAdapter {
    private final Fragment[] fragments = new Fragment[] {
        new RandomGameFragment(),
        new ARGameFragment()
    };

    private final String[] fragmentNames = new String[] {
        "Random Game",
        "Random Game AR"
    };

    public GamePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public String getFragmentName(int position) {
        return this.fragmentNames[position];
    }
}
