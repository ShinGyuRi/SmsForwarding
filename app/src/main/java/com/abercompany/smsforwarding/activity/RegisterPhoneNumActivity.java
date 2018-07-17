package com.abercompany.smsforwarding.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.RegisterNumAdapter;
import com.abercompany.smsforwarding.databinding.ActivityRegisterPhoneNumBinding;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPhoneNumActivity extends AppCompatActivity {

    private ActivityRegisterPhoneNumBinding binding;

    private RegisterNumAdapter registerNumAdapter;
    private List<String> nums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_phone_num);
        binding.setRegisterPhoneNum(this);

        Intent intent = getIntent();
        nums = (ArrayList<String>) intent.getSerializableExtra("nums");

        setRegisterNumAdapter(nums);
    }

    public void onClick(View view) {
        String senderNum = binding.etPhoneNum.getText().toString().replace("-", "");
        switch (view.getId()) {
            case R.id.btn_register:
                registerNum(senderNum, DeviceUtil.getDevicePhoneNumber(this));
                registerNumAdapter.add(senderNum);
                registerNumAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void registerNum(String senderNum, String phoneNum) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().registerNum(senderNum, phoneNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean success = jsonObject.get("message").getAsBoolean();
                    if (success) {
                        Toast.makeText(RegisterPhoneNumActivity.this, "번호가 등록되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void setRegisterNumAdapter(final List<String> numbers) {
        registerNumAdapter = new RegisterNumAdapter(RegisterPhoneNumActivity.this, numbers);
        binding.rvRegisterNum.setAdapter(registerNumAdapter);
        binding.rvRegisterNum.setLayoutManager(new LinearLayoutManager(RegisterPhoneNumActivity.this));
        registerNumAdapter.notifyDataSetChanged();
        registerNumAdapter.setItemClick(new RegisterNumAdapter.ItemClick() {
            @Override
            public void onClick(View view, final int position) {
                showSimplSelect2Dialog(RegisterPhoneNumActivity.this, "등록된 번호를 삭제하시겠습니까?", "예", "아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestDeleteNum(numbers.get(position), position);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }
        });
    }

    private void requestDeleteNum(String senderNum, final int position) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().deleteNum(senderNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();
                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        registerNumAdapter.remove(position);
                        registerNumAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void showSimplSelect2Dialog(Context context, String message, String yesBtn, String noBtn, DialogInterface.OnClickListener yesBtnListener, DialogInterface.OnClickListener noBtnListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(yesBtn,yesBtnListener);
        alertBuilder.setNegativeButton(noBtn,noBtnListener);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }
}
