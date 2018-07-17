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
import com.abercompany.smsforwarding.activity.AddCashActivity;
import com.abercompany.smsforwarding.activity.RegisterPhoneNumActivity;
import com.abercompany.smsforwarding.activity.SearchDefaulterActivity;
import com.abercompany.smsforwarding.activity.SearchLeaveRoomActivity;
import com.abercompany.smsforwarding.databinding.FragmentSettingBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.Resident;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    private List<Resident> residents;
    private List<Broker> brokers;
    private List<String> nums = new ArrayList<>();
    private List<Defaulter> defaulters;

    public SettingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SettingFragment(List<Resident> residents, List<Broker> brokers, List<String> nums, List<Defaulter> defaulters) {
        this.residents = residents;
        this.brokers = brokers;
        this.nums = nums;
        this.defaulters = defaulters;
    }

    public static SettingFragment newInstance(List<Resident> residents, List<Broker> brokers, List<String> nums, List<Defaulter> defaulters) {
        SettingFragment fragment = new SettingFragment(residents, brokers, nums, defaulters);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_setting, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        View view = binding.getRoot();
        binding.setSetting(this);

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_last_rent:
                Intent intent = new Intent(getContext(), AddCashActivity.class);
                intent.putExtra("resident", (Serializable) residents);
                intent.putExtra("broker", (Serializable) brokers);
                intent.putExtra("dataType", EXISTING_DATA);
                startActivity(intent);
                break;

            case R.id.btn_register:
                Intent intent1 = new Intent(getContext(), RegisterPhoneNumActivity.class);
                intent1.putExtra("nums", (Serializable) nums);
                startActivity(intent1);

            case R.id.btn_check_defaulter:
                Intent intent2 = new Intent(getContext(), SearchDefaulterActivity.class);
                intent2.putExtra("defaulter", (Serializable) defaulters);
                startActivity(intent2);
                break;

            case R.id.btn_check_leave_room:
                Intent intent3 = new Intent(getContext(), SearchLeaveRoomActivity.class);
                startActivity(intent3);
                break;

        }
    }

}
