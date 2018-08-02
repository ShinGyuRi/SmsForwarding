package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityRegisterEtcNumBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterEtcNumActivity extends AppCompatActivity {

    private ActivityRegisterEtcNumBinding binding;

    private List<Resident> residents;
    private List<Broker> brokers;
    private List<String> residentName = new ArrayList<>();
    private List<String> brokerName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_etc_num);
        binding.setEtcNum(this);

        Intent intent = getIntent();
        residents = (List<Resident>) intent.getSerializableExtra("residents");
        brokers = (List<Broker>) intent.getSerializableExtra("brokers");

        residentName.add("입주자");
        for (int i = 0; i < residents.size(); i++) {
            residentName.add(getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
        }
        for (int i = 0; i < brokers.size(); i++) {
            brokerName.add(getString(R.string.str_deposit_realty, brokers.get(i).getName(), brokers.get(i).getRealtyName()));
        }

        binding.spResident.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, residentName));
//        binding.spBroker.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, brokerName));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (binding.spResident.getSelectedItemPosition() != 0 && !"".equals(binding.etRealtyName.getText().toString())) {
                    Debug.showToast(this, "입주자와 부동산 둘중 하나만 입력해주세요");
                } else {
                    if (binding.spResident.getSelectedItemPosition() != 0 && !"".equals(binding.etEtcNum.getText().toString())) {
                        insertEtcNum(binding.etEtcNum.getText().toString(), binding.spResident.getSelectedItem().toString());
                    } else if (!"".equals(binding.etRealtyName.getText().toString()) && !"".equals(binding.etEtcNum.getText().toString())) {
                        insertRealtyPhoneNum(binding.etEtcNum.getText().toString(), binding.etRealtyName.getText().toString());
                    }
                }
                break;
        }
    }

    private void insertEtcNum(String etcNum, String dstName) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertEtcNum(etcNum, dstName);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();

                    if (message) {
                        Debug.showToast(RegisterEtcNumActivity.this, "등록 되었습니다.");
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void insertRealtyPhoneNum(String phoneNum, String realtyName) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertRealtyPhoneNum(phoneNum, realtyName);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();

                    if (message) {
                        Debug.showToast(RegisterEtcNumActivity.this, "등록 되었습니다.");
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
