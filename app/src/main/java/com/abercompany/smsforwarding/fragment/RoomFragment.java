package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.AddBuildingActivity;
import com.abercompany.smsforwarding.activity.MainActivity;
import com.abercompany.smsforwarding.activity.RoomDetailActivity;
import com.abercompany.smsforwarding.adapter.RoomAdapter;
import com.abercompany.smsforwarding.databinding.FragmentRoomBinding;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.GetContractResult;
import com.abercompany.smsforwarding.model.GetRoomResult;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomFragment extends Fragment {

    private FragmentRoomBinding binding;

    private RoomAdapter adapter;
    private List<Room> rooms;
    private List<Contract> contracts;
    private List<Defaulter> defaulters;
    private String buildingName = "";

    public RoomFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public RoomFragment(List<Defaulter> defaulters, String buildingName, List<Room> rooms) {
        this.defaulters = defaulters;
        this.buildingName = buildingName;
        this.rooms = rooms;

        JSLog.D("buildingName           :::     " + this.buildingName, null);
    }

    public static RoomFragment newInstance(List<Defaulter> defaulters, String buildingName, List<Room> rooms) {
        RoomFragment fragment = new RoomFragment(defaulters, buildingName, rooms);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_room, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false);
        View view = binding.getRoot();
        binding.setRoom(this);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getContract(rooms, buildingName);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_building:
                Intent intent = new Intent(getContext(), AddBuildingActivity.class);
                intent.putExtra("room", (Serializable) rooms);
                intent.putExtra("buildingName", buildingName);
                startActivity(intent);
                break;
        }
    }

    private void setRoomAdapter(final List<Room> rooms, final List<Contract> contracts, List<Defaulter> defaulters, final String buildingName) {
        adapter = new RoomAdapter(getContext(), rooms, contracts, defaulters, buildingName);
        binding.rvRoomNum.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 8) {
                    return 4;
                }
                return 1;
            }
        });
        binding.rvRoomNum.setLayoutManager(manager);
        adapter.notifyDataSetChanged();
        adapter.setItemClick(new RoomAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), RoomDetailActivity.class);
                intent.putExtra("room", rooms.get(position));
                intent.putExtra("buildingName", buildingName);
                for (int i = 0; i < contracts.size(); i++) {
                    if (rooms.get(position).getRoomNum().equals(contracts.get(i).getRoomNum()) &&
                            !"퇴실".equals(contracts.get(i).getActive())) {
                        intent.putExtra("contract", contracts.get(i));
                    }
                }
                startActivity(intent);
            }
        });
    }


    private void getContract(final List<Room> rooms, final String buildingName) {
        Call<GetContractResult> getContractResultCall = NetworkUtil.getInstace().getContract(buildingName);
        getContractResultCall.enqueue(new Callback<GetContractResult>() {
            @Override
            public void onResponse(Call<GetContractResult> call, Response<GetContractResult> response) {
                GetContractResult getContractResult = response.body();
                String result = getContractResult.getResult();

                if ("success".equals(result)) {
                    contracts = getContractResult.getContracts();

                    setRoomAdapter(rooms, contracts, defaulters, buildingName);
                }
            }

            @Override
            public void onFailure(Call<GetContractResult> call, Throwable t) {

            }
        });
    }

}
