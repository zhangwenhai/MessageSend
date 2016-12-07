package com.theone.sns.ui.mblog;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;

public class FragmentsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentManager fm;

    public FragmentsPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.fm = fm;
        fragments = data;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return (fragments == null) ? 0 : fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void setFragments(ArrayList fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }
}
