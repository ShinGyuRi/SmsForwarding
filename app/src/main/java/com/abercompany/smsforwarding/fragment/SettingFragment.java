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
import com.abercompany.smsforwarding.databinding.FragmentSettingBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Resident;

import java.io.Serializable;
import java.util.List;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    private List<Resident> residents;
    private List<Broker> brokers;

    public SettingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SettingFragment(List<Resident> residents, List<Broker> brokers) {
        this.residents = residents;
        this.brokers = brokers;
    }

    public static SettingFragment newInstance(List<Resident> residents, List<Broker> brokers) {
        SettingFragment fragment = new SettingFragment(residents, brokers);
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
        }
    }

}
