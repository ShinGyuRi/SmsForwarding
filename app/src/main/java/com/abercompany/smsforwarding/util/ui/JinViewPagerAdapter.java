package com.abercompany.smsforwarding.util.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.abercompany.smsforwarding.adapter.ViewPagerAdapter;
import com.abercompany.smsforwarding.fragment.InputElecFragment;

import java.util.ArrayList;

public class JinViewPagerAdapter extends ViewPagerAdapter {
    public JinViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm, fragments);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        if (object instanceof InputElecFragment) {
            // Create a new method notifyUpdate() in your fragment
            // it will get call when you invoke
            // notifyDatasetChaged();
            ((InputElecFragment) object).notifyUpdate();
        }

        return super.getItemPosition(object);
    }
}
