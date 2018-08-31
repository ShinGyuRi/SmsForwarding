package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.AddCashActivity;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentNewDataBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.abercompany.smsforwarding.util.SmsLib;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.NEW_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewDataFragment extends Fragment {

    private FragmentNewDataBinding binding;
    private List<Deposit> newDatas;
    private List<Resident> residents;
    private List<Broker> brokers;
    private List<Room> rooms;
    private DepositDataAdapter adapter;


    public NewDataFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NewDataFragment(List<Deposit> newDatas, List<Resident> residents, List<Broker> brokers, List<Room> rooms) {
        this.newDatas = newDatas;
        this.residents = residents;
        this.brokers = brokers;
        this.rooms = rooms;
    }

    public static NewDataFragment newInstance(List<Deposit> newDatas, List<Resident> residents, List<Broker> brokers, List<Room> rooms) {
        NewDataFragment fragment = new NewDataFragment(newDatas, residents, brokers, rooms);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_new_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_data, container, false);
        View view = binding.getRoot();
        binding.setNewData(this);

        setDepositAdapter(newDatas, residents, brokers);
        return view;


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_cash:
                Intent intent = new Intent(getContext(), AddCashActivity.class);
                intent.putExtra("resident", (Serializable) residents);
                intent.putExtra("broker", (Serializable) brokers);
                intent.putExtra("dataType", NEW_DATA);
                startActivity(intent);
                break;

            case R.id.btn_upload:
                BusProvider.getInstance().post(new OnClickEvent());
                break;
        }
    }

    private void setDepositAdapter(final List<Deposit> newDatas, List<Resident> residents, List<Broker> brokers) {
        adapter = new DepositDataAdapter(getActivity(), getContext(), newDatas, residents, brokers, NEW_DATA, rooms);
        binding.rvDeposit.setAdapter(adapter);
        binding.rvDeposit.setHasFixedSize(true);
        binding.rvDeposit.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDeposit.getRecycledViewPool().setMaxRecycledViews(1, 0);
        adapter.notifyDataSetChanged();
        adapter.setItemClick(new DepositDataAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                getRawMessage(newDatas.get(position).getTimeStamp());
            }
        });
    }

    private void getRawMessage(String timeStamp) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().getRawMessage(timeStamp);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    String message = jsonObject.get("message").getAsString();
                    SmsLib.getInstance().showSimpleDialog(getContext(), message, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
