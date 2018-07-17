package com.abercompany.smsforwarding.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.LeaveRoomAdapter;
import com.abercompany.smsforwarding.databinding.ActivitySearchDefaulterBinding;
import com.abercompany.smsforwarding.databinding.ActivitySearchLeaveRoomBinding;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.GetContractResult;
import com.abercompany.smsforwarding.util.NetworkUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchLeaveRoomActivity extends AppCompatActivity {

    private ActivitySearchLeaveRoomBinding binding;

    private List<Contract> contracts;
    private LeaveRoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_leave_room);
        binding.setSearchLeaveRoom(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                getLeaveRoom();
                break;
        }
    }

    private void getLeaveRoom() {
        Call<GetContractResult> getContractResultCall = NetworkUtil.getInstace().getLeaveRoom("");
        getContractResultCall.enqueue(new Callback<GetContractResult>() {
            @Override
            public void onResponse(Call<GetContractResult> call, Response<GetContractResult> response) {
                GetContractResult getContractResult = response.body();
                String result = getContractResult.getResult();

                if ("success".equals(result)) {
                    contracts = getContractResult.getContracts();

                    setLeaveRoomAdapter(contracts);
                }
            }

            @Override
            public void onFailure(Call<GetContractResult> call, Throwable t) {

            }
        });
    }

    private void setLeaveRoomAdapter(List<Contract> contracts)  {
        adapter = new LeaveRoomAdapter(this, contracts);
        binding.rvLeaveRoom.setAdapter(adapter);
        binding.rvLeaveRoom.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }
}
