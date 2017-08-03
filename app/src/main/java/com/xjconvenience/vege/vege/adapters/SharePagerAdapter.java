package com.xjconvenience.vege.vege.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xjconvenience.vege.vege.modules.share.FollowFragment;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

public class SharePagerAdapter extends FragmentPagerAdapter {

    public SharePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private String src;

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FollowFragment.newInstance(0, "");
            case 1:
                return FollowFragment.newInstance(1, src);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "关注公众号";
            case 1:
                return "领取代金券";
            default:
                return "";
        }
    }
}
