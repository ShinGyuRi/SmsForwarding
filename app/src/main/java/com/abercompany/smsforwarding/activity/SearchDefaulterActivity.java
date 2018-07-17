package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.SmsRecyclerAdapter;
import com.abercompany.smsforwarding.databinding.ActivitySearchDefaulterBinding;
import com.abercompany.smsforwarding.model.Defaulter;

import java.util.ArrayList;
import java.util.List;

public class SearchDefaulterActivity extends AppCompatActivity {

    private ActivitySearchDefaulterBinding binding;

    private List<Defaulter> defaulters;
    private SmsRecyclerAdapter smsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_defaulter);

        Intent intent = getIntent();
        defaulters = (ArrayList<Defaulter>) intent.getSerializableExtra("defaulter");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                setSmsAdapter(defaulters);
                break;
        }
    }

    private void setSmsAdapter(List<Defaulter> defaulters) {
        smsRecyclerAdapter = new SmsRecyclerAdapter(this, defaulters);
        binding.rvSms.setAdapter(smsRecyclerAdapter);
        binding.rvSms.setLayoutManager(new LinearLayoutManager(this));
        smsRecyclerAdapter.notifyDataSetChanged();
    }
}
