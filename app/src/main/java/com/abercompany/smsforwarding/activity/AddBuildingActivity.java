package com.abercompany.smsforwarding.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.AddBuildingAdapter;
import com.abercompany.smsforwarding.databinding.ActivityAddBuildingBinding;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBuildingActivity extends AppCompatActivity {

    private ActivityAddBuildingBinding binding;

    private AddBuildingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_building);
        binding.setAddBuilding(this);

        setRoomInfoAdapter();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                JSLog.D("rvRoomInfo getChildCount               :::         " + binding.rvRoomInfo.getChildCount(), null);
                JSLog.D("adapter getItemCount               :::         " + adapter.getItemCount(), null);
                insertBuilding(binding.etBuildingName.getText().toString(),
                        String.valueOf(adapter.getItemCount()));
                break;

            case R.id.btn_add_room_info:
                BusProvider.getInstance().post(new OnClickEvent());
                break;
        }
    }

    private void setRoomInfoAdapter() {

        adapter = new AddBuildingAdapter(this, this, binding.etBuildingName.getText().toString());
        binding.rvRoomInfo.setAdapter(adapter);
        binding.rvRoomInfo.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }

    private void insertBuilding(final String name, String totalRoomNum) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertBuilding(name, totalRoomNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        for (int i = 0; i < adapter.getItemCount(); i++) {

                            String roomNum = ((EditText) binding.rvRoomInfo.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.et_room_num)).getText().toString();
                            String price = ((EditText) binding.rvRoomInfo.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.et_room_price)).getText().toString();
                            insertRoomInfo(name, roomNum, price);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void insertRoomInfo(String buildingName, String roomNum, String price) {
        final Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertRoomInfo(buildingName, roomNum, price);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        Debug.showToast(AddBuildingActivity.this, "등록되었습니다.");
                        AddBuildingActivity.this.finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
