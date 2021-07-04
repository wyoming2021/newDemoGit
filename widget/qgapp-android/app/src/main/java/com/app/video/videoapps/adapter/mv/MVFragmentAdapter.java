package com.app.video.videoapps.adapter.mv;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.app.video.videoapps.bean.TypeBean;
import com.app.video.videoapps.fragment.mv.MVChildFragment;

import java.util.List;

public class MVFragmentAdapter extends FragmentPagerAdapter {


    private List<TypeBean> mTitles;

    public MVFragmentAdapter(FragmentManager fm, List<TypeBean> mTitles) {
        super(fm);
        this.mTitles = mTitles;
    }


    @Override
    public Fragment getItem(int position) {
        return MVChildFragment.newInstance(mTitles.get(position).getOid(), mTitles.get(position));
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position).getOtypename();
    }
}