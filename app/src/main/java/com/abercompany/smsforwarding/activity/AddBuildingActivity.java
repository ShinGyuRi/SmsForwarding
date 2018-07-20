package com.abercompany.smsforwarding.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityAddBuildingBinding;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBuildingActivity extends AppCompatActivity {

    private ActivityAddBuildingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_building);
        binding.setAddBuilding(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                insertBuilding(binding.etBuildingName.getText().toString(),
                        binding.etTotalRoomNum.getText().toString());
                break;
        }
    }

    private void insertBuilding(String name, String totalRoomNum) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertBuilding(name, totalRoomNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        Debug.showToast(AddBuildingActivity.this, "등록되었습니다.");
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
