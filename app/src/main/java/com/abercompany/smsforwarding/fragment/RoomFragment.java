package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.RoomAdapter;
import com.abercompany.smsforwarding.databinding.FragmentRoomBinding;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.GetContractResult;
import com.abercompany.smsforwarding.model.GetRoomResult;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.util.NetworkUtil;

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

    public RoomFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public RoomFragment(List<Defaulter> defaulters) {
        this.defaulters = defaulters;
    }

    public static RoomFragment newInstance(List<Defaulter> defaulters)   {
        RoomFragment fragment = new RoomFragment(defaulters);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_room, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false);
        View view = binding.getRoot();

        getRoom();

        return view;
    }

    private void setRoomAdapter(List<Room> rooms, List<Contract> contracts, List<Defaulter> defaulters)    {
        adapter = new RoomAdapter(getContext(), rooms, contracts, defaulters);
        binding.rvRoomNum.setAdapter(adapter);
        binding.rvRoomNum.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter.notifyDataSetChanged();
        adapter.setItemClick(new RoomAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {

            }
        });
    }

    private void getRoom()  {
        Call<GetRoomResult> getRoomResultCall = NetworkUtil.getInstace().getRoom("");
        getRoomResultCall.enqueue(new Callback<GetRoomResult>() {
            @Override
            public void onResponse(Call<GetRoomResult> call, Response<GetRoomResult> response) {
                GetRoomResult getRoomResult = response.body();
                String result = getRoomResult.getResult();

                if ("success".equals(result)) {
                    rooms = getRoomResult.getRooms();

                    getContract(rooms);
                }
            }

            @Override
            public void onFailure(Call<GetRoomResult> call, Throwable t) {

            }
        });
    }

    private void getContract(final List<Room> rooms)  {
        Call<GetContractResult> getContractResultCall = NetworkUtil.getInstace().getContract("");
        getContractResultCall.enqueue(new Callback<GetContractResult>() {
            @Override
            public void onResponse(Call<GetContractResult> call, Response<GetContractResult> response) {
                GetContractResult getContractResult = response.body();
                String result = getContractResult.getResult();

                if ("success".equals(result)) {
                    contracts = getContractResult.getContracts();

                    setRoomAdapter(rooms, contracts, defaulters);
                }
            }

            @Override
            public void onFailure(Call<GetContractResult> call, Throwable t) {

            }
        });
    }

}
