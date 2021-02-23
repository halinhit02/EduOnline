package com.halinhit.eduonline;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private Classroom classroom;
    PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("informationClass", classroom);
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new Frag_Task();
                frag.setArguments(bundle);
                break;
            case 1:
                frag = new Frag_Member();
                frag.setArguments(bundle);
                break;
            case 2:
                frag = new Frag_Library();
                frag.setArguments(bundle);
                break;
            case 3:
                frag = new Frag_Notification();
                frag.setArguments(bundle);
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Bài tập";
                break;
            case 1:
                title = "Thành viên";
                break;
            case 2:
                title = "Thư viện";
                break;
            case 3:
                title = "Thông báo";
        }
        return title;
    }

    public void setArgunment(Classroom classroom) {
        this.classroom = classroom;
    }
}
