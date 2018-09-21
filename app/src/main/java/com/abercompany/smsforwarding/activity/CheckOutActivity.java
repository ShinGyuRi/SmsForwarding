package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ViewPagerAdapter;
import com.abercompany.smsforwarding.databinding.ActivityCheckOutBinding;
import com.abercompany.smsforwarding.fragment.ExpireContractFragment;
import com.abercompany.smsforwarding.fragment.RegisterCheckoutFragment;
import com.abercompany.smsforwarding.fragment.SearchCheckoutFragment;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends AppCompatActivity  {

    private ActivityCheckOutBinding binding;


    private HashSet<Checkout> checkout = new HashSet<>();

    public enum Checkout {
        Register(),
        Search();
    }


    private List<Contract> contracts;
    private List<Resident> residents;
    private List<Deposit> trimmedData;
    private List<Building> buildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out);

        Intent intent = getIntent();

        buildings = (List<Building>) intent.getSerializableExtra("building");
        contracts = (List<Contract>) intent.getSerializableExtra("contract");
        residents = (List<Resident>) intent.getSerializableExtra("resident");
        trimmedData = (List<Deposit>) intent.getSerializableExtra("trimmedData");


        initViews();
    }

    private void initViews() {
        checkout.add(Checkout.Register);
        checkout.add(Checkout.Search);

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

        if (checkout.contains(Checkout.Register)) {
            fragments.add(RegisterCheckoutFragment.newInstance(contracts, residents, buildings, trimmedData));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("REGISTER"));
        }

        if (checkout.contains(Checkout.Search)) {
            fragments.add(SearchCheckoutFragment.newInstance(buildings));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("SEARCH"));
        }

        return fragments;
    }

    public void insertCheckout(String roomNum, String name, String deposit, String rent, String manageFee,
                               String elecNum, String elecAmount, String gasNum, String gasAmount, String checkoutFee,
                               String realtyFee, String penalty, String discount, String total, String account,
                               String bank, String buildingName, String date, String usageFee) {

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertCheckout(roomNum, name, deposit,
                rent, manageFee, elecNum, elecAmount, gasNum, gasAmount, checkoutFee, realtyFee, penalty,
                discount, total, account, bank, buildingName, date, usageFee);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        Debug.showToast(CheckOutActivity.this, "등록되었습니다.");
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


}
