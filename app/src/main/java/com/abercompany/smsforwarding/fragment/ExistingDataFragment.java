package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentExistingDataBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.SelectedSpinnerEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExistingDataFragment extends Fragment {

    private FragmentExistingDataBinding binding;
    private List<Deposit> existingDatas;
    private List<Broker> brokers;
    private List<Resident> residents;
    private DepositDataAdapter adapter;


    public ExistingDataFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ExistingDataFragment(List<Deposit> existingDatas, List<Resident> residents, List<Broker> brokers) {
        this.existingDatas = existingDatas;
        this.residents = residents;
        this.brokers = brokers;
    }

    public static ExistingDataFragment newInstance(List<Deposit> existingDatas, List<Resident> residents, List<Broker> brokers) {
        ExistingDataFragment fragment = new ExistingDataFragment(existingDatas, residents, brokers);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_existing_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_existing_data, container, false);
        View view = binding.getRoot();
        binding.setExistingData(this);

        setDepositAdapter(existingDatas, residents, brokers);

        return view;
    }


    private void setDepositAdapter(List<Deposit> existingDatas ,List<Resident> residents, List<Broker> brokers) {
        adapter = new DepositDataAdapter(getActivity(), getContext(), existingDatas, residents, brokers, EXISTING_DATA);
        binding.rvWithdraw.setAdapter(adapter);
        binding.rvWithdraw.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

}
