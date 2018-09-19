package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ViewPagerAdapter;
import com.abercompany.smsforwarding.databinding.ActivityElecStatusBinding;
import com.abercompany.smsforwarding.fragment.InputElecFragment;
import com.abercompany.smsforwarding.fragment.SearchElecFragment;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.GetElecDefaulter;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.abercompany.smsforwarding.util.ui.JinViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElecStatusActivity extends AppCompatActivity {

    private ActivityElecStatusBinding binding;

    private List<Building> buildings;
    private List<String> buildingName = new ArrayList<>();

    private HashSet<Elec> elecHashSet = new HashSet<>();

    private ViewPagerAdapter pagerAdapter;

    public enum Elec {
        InputElec(),
        SearchElec();
    }

    private List<Defaulter> elec = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_elec_status);

        Intent intent = getIntent();
        buildings = (List<Building>) intent.getSerializableExtra("building");

        for (int i = 0; i < buildings.size(); i++) {
            buildingName.add(buildings.get(i).getName());
        }

        initViews();



    }

    private void initViews() {
        elecHashSet.add(Elec.InputElec);
        elecHashSet.add(Elec.SearchElec);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getListFragment(elec));
        binding.viewPager.setAdapter(pagerAdapter);

        binding.tabLayout.addOnTabSelectedListener(getViewPagerOnTabSelectedListener());
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.spBuildingName.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, buildingName));
        binding.spBuildingName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getElecStatus(buildingName.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private TabLayout.ViewPagerOnTabSelectedListener getViewPagerOnTabSelectedListener() {
        return new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager)  {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        };
    }

    private ArrayList<Fragment> getListFragment(List<Defaulter> elec) {
        ArrayList<Fragment> fragments = new ArrayList<>();

        if (elecHashSet.contains(Elec.InputElec)) {
            fragments.add(InputElecFragment.newInstance(elec, buildings));
            if (binding.tabLayout.getTabCount() < 2) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("INPUT"));
            }
        }

        if (elecHashSet.contains(Elec.SearchElec)) {
            fragments.add(SearchElecFragment.newInstance(elec));
            if (binding.tabLayout.getTabCount() < 2) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("SEARCH"));
            }
        }

        return fragments;
    }

    private void getElecStatus(String buildingName) {
        Call<GetElecDefaulter> getElecDefaulterCall = NetworkUtil.getInstace().getElecStatus(buildingName);
        getElecDefaulterCall.enqueue(new Callback<GetElecDefaulter>() {
            @Override
            public void onResponse(Call<GetElecDefaulter> call, Response<GetElecDefaulter> response) {
                GetElecDefaulter getElecDefaulter = response.body();
                String result = getElecDefaulter.getResult();

                if ("success".equals(result)) {
                    elec = getElecDefaulter.getElecDefaulterList();


                    pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getListFragment(elec));
                    binding.viewPager.setAdapter(pagerAdapter);
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<GetElecDefaulter> call, Throwable t) {

            }
        });
    }
}
