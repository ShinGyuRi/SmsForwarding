package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.AddCashActivity;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentExistingDataBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.model.SelectedSpinnerEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;
import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.NEW_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExistingDataFragment extends Fragment {

    private FragmentExistingDataBinding binding;
    private List<Deposit> existingDatas;
    private List<Broker> brokers;
    private List<Resident> residents;
    private List<Room> rooms;
    private List<Building> buildings;
    private List<Deposit> searchDatas;
    private DepositDataAdapter adapter;


    public ExistingDataFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ExistingDataFragment(List<Deposit> existingDatas, List<Resident> residents, List<Broker> brokers, List<Room> rooms, List<Building> buildings) {
        this.existingDatas = existingDatas;
        this.residents = residents;
        this.brokers = brokers;
        this.rooms = rooms;
        this.buildings = buildings;
    }

    public static ExistingDataFragment newInstance(List<Deposit> existingDatas, List<Resident> residents, List<Broker> brokers, List<Room> rooms, List<Building> buildings) {
        ExistingDataFragment fragment = new ExistingDataFragment(existingDatas, residents, brokers, rooms, buildings);
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_cash:
                Intent intent = new Intent(getContext(), AddCashActivity.class);
                intent.putExtra("resident", (Serializable) residents);
                intent.putExtra("broker", (Serializable) brokers);
                intent.putExtra("dataType", EXISTING_DATA);
                intent.putExtra("building", (Serializable) buildings);
                startActivity(intent);
                break;

            case R.id.btn_upload:
                BusProvider.getInstance().post(new OnClickEvent());
                break;

            case R.id.btn_search:
                searchDatas = new ArrayList<>();
                if (binding.spMonth.getSelectedItemPosition() != 0) {
                    searchData(existingDatas, binding.spMonth.getSelectedItem().toString().replace("월", ""));
                }
                break;
        }
    }

    private void setDepositAdapter(List<Deposit> existingDatas ,List<Resident> residents, List<Broker> brokers) {
        adapter = new DepositDataAdapter(getActivity(), getContext(), existingDatas, residents, brokers, EXISTING_DATA, rooms, buildings);
        binding.rvDeposit.setAdapter(adapter);
        binding.rvDeposit.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    private void searchData(List<Deposit> existingDatas, String month) {
        for (int i = 0; i < existingDatas.size(); i++) {
            String date = existingDatas.get(i).getDate().replace("[KB]", "");
            String dateMonth = date.split("/")[0];

            if (dateMonth.contains(month)) {
                searchDatas.add(existingDatas.get(i));
            }
        }

        setDepositAdapter(searchDatas, residents, brokers);
    }
}
