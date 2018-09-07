package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ReportAdapter;
import com.abercompany.smsforwarding.databinding.ActivityReportBinding;
import com.abercompany.smsforwarding.model.Deposit;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;

    private List<Deposit> trimmedData;
    private List<Deposit> filterData;
    private List<String> account = new ArrayList<>();

    private ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        binding.setReport(this);

        Intent intent = getIntent();
        trimmedData = (List<Deposit>) intent.getSerializableExtra("trimmedData");

        setSpinner();
    }

    private void setSpinner() {
        for (int i = 0; i < trimmedData.size(); i++) {
            if (!account.contains(trimmedData.get(i).getAccount())) {
                account.add(trimmedData.get(i).getAccount());
            }
        }

        binding.spAccount.setAdapter(new ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, account));
        binding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        binding.spDetailType.setAdapter(new ArrayAdapter(ReportActivity.this,
                                R.layout.support_simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.deposit)));
                        break;
                    case 1:
                        binding.spDetailType.setAdapter(new ArrayAdapter(ReportActivity.this,
                                R.layout.support_simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.withdraw)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                setReportAdapter(trimmedData, binding.spMonth.getSelectedItem().toString().replace("월", ""),
                        binding.spAccount.getSelectedItem().toString(),
                        binding.spType.getSelectedItem().toString(),
                        binding.spDetailType.getSelectedItem().toString());
                break;
        }
    }

    private void setReportAdapter(List<Deposit> trimmedData, String month, String account, String type, String detailType) {
        filterData = new ArrayList<>();

        for (int i = 0; i < trimmedData.size(); i++) {

            String date = trimmedData.get(i).getDate().replace("[KB]", "");
            String dateMonth = date.split("/")[0];

            if (dateMonth.contains(month) &&
                    trimmedData.get(i).getAccount().contains(account) &&
                    trimmedData.get(i).getMethod().contains(type)) {

                if ("선택".equals(detailType)) {
                    filterData.add(trimmedData.get(i));
                }else if (detailType.equals(trimmedData.get(i).getType())) {
                    filterData.add(trimmedData.get(i));
                }
            }

        }
        adapter = new ReportAdapter(this, filterData);
        binding.rvReport.setAdapter(adapter);
        binding.rvReport.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }

}
