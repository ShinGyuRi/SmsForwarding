package com.abercompany.smsforwarding.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.FragmentDepositDataBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositDataFragment extends Fragment {

    private FragmentDepositDataBinding binding;

    public DepositDataFragment() {
        // Required empty public constructor
    }

    public static DepositDataFragment newInstance() {
        DepositDataFragment fragment = new DepositDataFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_deposit_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deposit_data, container, false);
        View view = binding.getRoot();

        return view;


    }

}
