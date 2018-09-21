package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.CheckOutActivity;
import com.abercompany.smsforwarding.databinding.FragmentSearchCheckoutBinding;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Checkout;
import com.abercompany.smsforwarding.model.GetCheckoutResult;
import com.abercompany.smsforwarding.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCheckoutFragment extends Fragment {

    private FragmentSearchCheckoutBinding binding;

    private List<Building> buildings;
    private List<String> buildingName = new ArrayList<>();
    private Checkout checkout;

    public SearchCheckoutFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SearchCheckoutFragment(List<Building> buildings) {
        this.buildings = buildings;

        for (int i = 0; i < buildings.size(); i++) {
            buildingName.add(buildings.get(i).getName());
        }
    }

    public static SearchCheckoutFragment newInstance(List<Building> buildings) {
        SearchCheckoutFragment fragment = new SearchCheckoutFragment(buildings);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search_checkout, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_checkout, container, false);
        View view = binding.getRoot();
        binding.setSearchCheckout(this);

        initViews();

        return view;
    }

    private void initViews() {
        binding.spBuildingName.setAdapter(new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, buildingName));
    }

    public void onClick(View view) {

        String roomNum = binding.etRoomNum.getText().toString();
        String name = binding.etName.getText().toString();
        String buildingName = binding.spBuildingName.getSelectedItem().toString();

        switch (view.getId()) {

            case R.id.btn_search:

                getCheckout(roomNum, name, buildingName);
                break;

            case R.id.btn_edit:
                String deposit = binding.etDeposit.getText().toString();
                String rent = binding.etRent.getText().toString();
                String manageFee = binding.etManageFee.getText().toString();
                String elecNum = binding.etElecNum.getText().toString();
                String elecAmount = binding.etElecAmount.getText().toString();
                String gasNum = binding.etGasNum.getText().toString();
                String gasAmount = binding.etGasAmount.getText().toString();
                String checkoutFee = binding.etCheckoutFee.getText().toString();
                String realtyFees = binding.etRealtyFees.getText().toString();
                String penalty = binding.etPenalty.getText().toString();
                String discount = binding.etDiscount.getText().toString();
                String total = binding.tvTotal.getText().toString();
                String account = binding.etAccount.getText().toString();
                String bank = binding.etBank.getText().toString();
                String date = binding.etCheckoutDate.getText().toString();
                String usageFee = binding.etUsageFee.getText().toString();

                ((CheckOutActivity) getActivity()).insertCheckout(roomNum, name, deposit, rent, manageFee, elecNum, elecAmount,
                        gasNum, gasAmount, checkoutFee, realtyFees, penalty, discount,
                        total, account, bank, buildingName, date, usageFee);
                break;
        }
    }

    private void getCheckout(String roomNum, String name, String buildingName) {
        Call<GetCheckoutResult> getCheckoutResultCall = NetworkUtil.getInstace().getCheckout(roomNum, name, buildingName);
        getCheckoutResultCall.enqueue(new Callback<GetCheckoutResult>() {
            @Override
            public void onResponse(Call<GetCheckoutResult> call, Response<GetCheckoutResult> response) {
                GetCheckoutResult getCheckoutResult = response.body();
                String result = getCheckoutResult.getResult();

                if ("success".equals(result)) {
                    checkout = getCheckoutResult.getCheckout();

                    setCheckoutValue(checkout);
                }
            }

            @Override
            public void onFailure(Call<GetCheckoutResult> call, Throwable t) {

            }
        });
    }

    private void setCheckoutValue(Checkout checkout) {
        binding.etCheckoutDate.setText(checkout.getDate());
        binding.etDeposit.setText(checkout.getDeposit());
        binding.etUsageFee.setText(checkout.getUsageFee());
        binding.etRent.setText(checkout.getRent());
        binding.etManageFee.setText(checkout.getManageFee());
        binding.etElecNum.setText(checkout.getElecNum());
        binding.etElecAmount.setText(checkout.getElecAmount());
        binding.etGasNum.setText(checkout.getGasNum());
        binding.etGasAmount.setText(checkout.getGasAmount());
        binding.etCheckoutFee.setText(checkout.getCheckoutFee());
        binding.etRealtyFees.setText(checkout.getRealtyFees());
        binding.etPenalty.setText(checkout.getPenalty());
        binding.etDiscount.setText(checkout.getDiscount());
        binding.tvTotal.setText("정산금액: " + checkout.getTotal());
        binding.etBank.setText(checkout.getBank());
        binding.etAccount.setText(checkout.getAccount());
    }

}
