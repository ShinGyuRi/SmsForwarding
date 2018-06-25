package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentDepositBinding;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.GetResidentResult;
import com.abercompany.smsforwarding.model.Resigent;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.UPDATE_TRIM_DATA.RESIDENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositFragment extends Fragment {

    private FragmentDepositBinding binding;
    private List<Deposit> deposits;
    private List<Resigent> residents;
    private DepositDataAdapter adapter;

    public DepositFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DepositFragment(List<Deposit> deposits) {
        this.deposits = deposits;
    }

    public static DepositFragment newInstance(List<Deposit> deposits) {
        DepositFragment fragment = new DepositFragment(deposits);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_deposit, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deposit, container, false);
        View view = binding.getRoot();

        getResident(deposits);

        return view;


    }





    private void getResident(final List<Deposit> deposits) {
        Call<GetResidentResult> getResidentResultCall = NetworkUtil.getInstace().getResident("");
        getResidentResultCall.enqueue(new Callback<GetResidentResult>() {
            @Override
            public void onResponse(Call<GetResidentResult> call, Response<GetResidentResult> response) {
                GetResidentResult getResidentResult = response.body();
                String result = getResidentResult.getResult();

                if ("success".equals(result)) {
                    residents = getResidentResult.getResigents();

                    setDepositAdapter(deposits, residents);
                }
            }

            @Override
            public void onFailure(Call<GetResidentResult> call, Throwable t) {

            }
        });
    }

    private void setDepositAdapter(final List<Deposit> deposits, List<Resigent> residents) {
        List<String> residentName = new ArrayList<>();
        for (int i=0; i<residents.size(); i++) {
            residentName.add(getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
        }
        adapter = new DepositDataAdapter(getContext(), deposits, residentName, RESIDENT);
        binding.rvDeposit.setAdapter(adapter);
        binding.rvDeposit.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }



}
