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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElecStatusActivity extends AppCompatActivity {

    private ActivityElecStatusBinding binding;

    private List<Defaulter> elecStatus;
    private List<Defaulter> filterElec;
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
                filterElec = new ArrayList<>();
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
                    String keyword = binding.etKeyword.getText().toString();

                    if ("".equals(keyword)) {
                        filterElec = elecStatus;
                    } else {
                        for (int i = 0; i < elecStatus.size(); i++) {
                            if (elecStatus.get(i).getEndDate().contains(keyword) ||
                                    elecStatus.get(i).getDstName().contains(keyword) ||
                                    elecStatus.get(i).getName().contains(keyword) ||
                                    elecStatus.get(i).getRoomNum().contains(keyword)) {
                                filterElec.add(elecStatus.get(i));
                            }
                        }
                    }

                    setElecDefaulterAdapter(filterElec);
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

    private final Comparator<Defaulter> sortByDate = new Comparator<Defaulter>() {
        @Override
        public int compare(Defaulter o1, Defaulter o2) {
            return Collator.getInstance().compare(o1.getEndDate(), o2.getEndDate());
        }
    };

}
