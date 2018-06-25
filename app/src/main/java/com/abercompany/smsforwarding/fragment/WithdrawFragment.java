package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentWithdrawBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.GetBrokerResult;
import com.abercompany.smsforwarding.model.Resigent;
import com.abercompany.smsforwarding.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.UPDATE_TRIM_DATA.BROKER;

/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawFragment extends Fragment {

    private FragmentWithdrawBinding binding;
    private List<Deposit> withdraws;
    private List<Broker> brokers;
    private DepositDataAdapter adapter;

    public WithdrawFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public WithdrawFragment(List<Deposit> withdraws) {
        this.withdraws = withdraws;
    }

    public static WithdrawFragment newInstance(List<Deposit> withdraws) {
        WithdrawFragment fragment = new WithdrawFragment(withdraws);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_withdraw, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw, container, false);
        View view = binding.getRoot();

        getBroker(withdraws);

        return view;
    }

    private void getBroker(final List<Deposit> withdraws) {
        Call<GetBrokerResult> getBrokerResultCall = NetworkUtil.getInstace().getBroker("");
        getBrokerResultCall.enqueue(new Callback<GetBrokerResult>() {
            @Override
            public void onResponse(Call<GetBrokerResult> call, Response<GetBrokerResult> response) {
                GetBrokerResult getBrokerResult = response.body();
                String result = getBrokerResult.getResult();

                if ("success".equals(result)) {
                    brokers = getBrokerResult.getBrokers();

                    setDepositAdapter(withdraws, brokers);
                }
            }

            @Override
            public void onFailure(Call<GetBrokerResult> call, Throwable t) {

            }
        });
    }

    private void setDepositAdapter(List<Deposit> withdraws, List<Broker> brokers) {
        List<String> residentName = new ArrayList<>();
        for (int i=0; i<brokers.size(); i++) {
            residentName.add(getString(R.string.str_deposit_realty, brokers.get(i).getName(), brokers.get(i).getRealtyName()));
        }
        adapter = new DepositDataAdapter(getContext(), withdraws, residentName, BROKER);
        binding.rvWithdraw.setAdapter(adapter);
        binding.rvWithdraw.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }
}
