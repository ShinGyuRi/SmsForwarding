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

/**
 * A simple {@link Fragment} subclass.
 */
public class ExistingDataFragment extends Fragment {

    private FragmentExistingDataBinding binding;
    private List<Deposit> existingDatas;
    private List<Broker> brokers;
    private List<Resident> residents;
    private DepositDataAdapter adapter;

    private List<String> objectName, name, date, type;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_existing_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_existing_data, container, false);
        View view = binding.getRoot();
        binding.setExistingData(this);

        setDepositAdapter(existingDatas, brokers);

        return view;
    }

    public void upload(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                for (int i = 0; i < name.size(); i++) {
                    updateTrimmedData(name.get(i), date.get(i), objectName.get(i), type.get(i));
                }
                break;
        }
    }

    private void updateTrimmedData(String name, String date, String objectName, String type) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().updateTrimmedData(name, date, objectName, type);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Debug.showToast(getContext(), "등록되었습니다");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void FinishLoad(SelectedSpinnerEvent event) {
        JSLog.D("Event          !!!     ", null);
        name = event.getName();
        date = event.getDate();
        objectName = event.getObjectName();
        type = event.getType();

        binding.btnUpload.setEnabled(true);
    }

    private void setDepositAdapter(List<Deposit> existingDatas, List<Broker> brokers) {
        List<String> residentName = new ArrayList<>();
        for (int i = 0; i < residents.size(); i++) {
            residentName.add(getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
        }
        List<String> brokerName = new ArrayList<>();
        for (int i = 0; i < brokers.size(); i++) {
            brokerName.add(getString(R.string.str_deposit_realty, brokers.get(i).getName(), brokers.get(i).getRealtyName()));
        }
        adapter = new DepositDataAdapter(getContext(), existingDatas, residentName, brokerName);
        binding.rvWithdraw.setAdapter(adapter);
        binding.rvWithdraw.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }
}
