package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.AddBuildingActivity;
import com.abercompany.smsforwarding.adapter.BuildingAdapter;
import com.abercompany.smsforwarding.databinding.FragmentBuildingBinding;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Room;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuildingFragment extends Fragment {

    private FragmentBuildingBinding binding;

    private List<Building> buildings;
    private List<Room> rooms;

    private BuildingAdapter adapter;

    public BuildingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BuildingFragment(List<Room> rooms) {
        this.rooms = rooms;
    }

    public static BuildingFragment newInstance(List<Room> rooms) {
        BuildingFragment fragment = new BuildingFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_building, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_building, container, false);
        View view = binding.getRoot();

        binding.setBuilding(this);

        return view;
    }

    public FragmentBuildingBinding getBinding() {
        return binding;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_building:
                Intent intent = new Intent(getContext(), AddBuildingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
