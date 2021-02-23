package com.halinhit.eduonline;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerClassAdapter extends FragmentStatePagerAdapter {


    private Task currentTask;
    public PagerClassAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("InformationTask", currentTask);
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new Frag_OverView();
                frag.setArguments(bundle);
                break;
            case 1:
                frag = new Frag_ScoreBoard();
                frag.setArguments(bundle);
                break;
            case 2:
                frag = new Frag_QuesParam();
                frag.setArguments(bundle);
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Tổng Quan";
                break;
            case 1:
                title = "Bảng điểm";
                break;
            case 2:
                title = "Thông số câu";
                break;
        }
        return title;
    }

    public void setArgunment(Task currentTask) {
        this.currentTask = currentTask;
    }

}
