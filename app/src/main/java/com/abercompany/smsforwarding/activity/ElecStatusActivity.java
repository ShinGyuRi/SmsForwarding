package com.abercompany.smsforwarding.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.SmsRecyclerAdapter;
import com.abercompany.smsforwarding.databinding.ActivityElecStatusBinding;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.GetElecDefaulter;
import com.abercompany.smsforwarding.util.NetworkUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElecStatusActivity extends AppCompatActivity {

    private ActivityElecStatusBinding binding;

    private List<Defaulter> elecStatus;
    private SmsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_elec_status);
        binding.setElecStatus(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                getElecStatus();
                break;
        }
    }

    private void getElecStatus() {
        Call<GetElecDefaulter> getElecDefaulterCall = NetworkUtil.getInstace().getElecStatus("");
        getElecDefaulterCall.enqueue(new Callback<GetElecDefaulter>() {
            @Override
            public void onResponse(Call<GetElecDefaulter> call, Response<GetElecDefaulter> response) {
                GetElecDefaulter getElecDefaulter = response.body();
                String result = getElecDefaulter.getResult();

                if ("success".equals(result)) {
                    elecStatus = getElecDefaulter.getElecDefaulterList();

                    setElecDefaulterAdapter(elecStatus);
                }

            }

            @Override
            public void onFailure(Call<GetElecDefaulter> call, Throwable t) {

            }
        });
    }

    private void setElecDefaulterAdapter(List<Defaulter> elecStatus) {
        adapter = new SmsRecyclerAdapter(this, elecStatus);
        binding.rvElecStats.setAdapter(adapter);
        binding.rvElecStats.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

    }
}
