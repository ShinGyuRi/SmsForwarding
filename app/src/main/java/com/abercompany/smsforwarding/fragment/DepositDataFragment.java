package com.abercompany.smsforwarding.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentDepositDataBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.GetBrokerResult;
import com.abercompany.smsforwarding.model.GetDepositResult;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositDataFragment extends Fragment {

    private FragmentDepositDataBinding binding;
    private List<Deposit> deposits;
    private List<Broker> brokers;
    private DepositDataAdapter adapter;

    public DepositDataFragment() {
        // Required empty public constructor
    }

    public static DepositDataFragment newInstance() {
        DepositDataFragment fragment = new DepositDataFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_deposit_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deposit_data, container, false);
        View view = binding.getRoot();

        getBroker();

        return view;


    }

    private void getDeposit(String phoneNum, final List<Broker> brokers) {
        Call<GetDepositResult> jsonObjectCall = NetworkUtil.getInstace().getDeposit(phoneNum);
        jsonObjectCall.enqueue(new Callback<GetDepositResult>() {
            @Override
            public void onResponse(Call<GetDepositResult> call, Response<GetDepositResult> response) {
                GetDepositResult getDepositResult = response.body();
                String result = getDepositResult.getResult();

                if ("success".equals(result)) {
                    deposits = getDepositResult.getMessage();
                    if (deposits.size() != 0) {
                        setDepositAdapter(deposits, brokers);
                    }

                }
            }

            @Override
            public void onFailure(Call<GetDepositResult> call, Throwable t) {

            }
        });
    }

    private void getBroker() {
        Call<GetBrokerResult> getBrokerResultCall = NetworkUtil.getInstace().getBroker("");
        getBrokerResultCall.enqueue(new Callback<GetBrokerResult>() {
            @Override
            public void onResponse(Call<GetBrokerResult> call, Response<GetBrokerResult> response) {
                GetBrokerResult getBrokerResult = response.body();
                String result = getBrokerResult.getResult();

                if ("success".equals(result)) {
                    brokers = getBrokerResult.getBrokers();
                    getDeposit(DeviceUtil.getDevicePhoneNumber(getContext()), brokers);
                }
            }

            @Override
            public void onFailure(Call<GetBrokerResult> call, Throwable t) {

            }
        });
    }

    private void setDepositAdapter(List<Deposit> deposits, List<Broker> brokers) {
        adapter = new DepositDataAdapter(getContext(), deposits, brokers);
        binding.rvDeposit.setAdapter(adapter);
        binding.rvDeposit.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

}
