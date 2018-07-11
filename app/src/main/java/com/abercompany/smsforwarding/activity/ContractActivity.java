package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ViewPagerAdapter;
import com.abercompany.smsforwarding.databinding.ActivityContractBinding;
import com.abercompany.smsforwarding.fragment.ExpireContractFragment;
import com.abercompany.smsforwarding.fragment.RoomFragment;
import com.abercompany.smsforwarding.model.Defaulter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ContractActivity extends AppCompatActivity {

    private ActivityContractBinding binding;

    private HashSet<Contract> contractHashSet = new HashSet<>();

    public enum Contract {
        Room(),
        Expire();
    }

    private List<Defaulter> defaulters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contract);

        Intent intent = getIntent();
        defaulters = (ArrayList<Defaulter>) intent.getSerializableExtra("defaulter");

        initViews();
    }

    private void initViews() {
        contractHashSet.add(Contract.Room);
        contractHashSet.add(Contract.Expire);

        final ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getListFragment());
        binding.viewPager.setAdapter(pagerAdapter);

        binding.tabLayout.addOnTabSelectedListener(getViewPagerOnTabSelectedListener());
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
    }

    private TabLayout.ViewPagerOnTabSelectedListener getViewPagerOnTabSelectedListener() {
        return new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager)  {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        };
    }

    private ArrayList<Fragment> getListFragment() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        if (contractHashSet.contains(Contract.Room)) {
            fragments.add(RoomFragment.newInstance(defaulters));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ROOM"));
        }

        if (contractHashSet.contains(Contract.Expire)) {
            fragments.add(ExpireContractFragment.newInstance());
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("EXPIRE"));
        }

        return fragments;
    }
}
