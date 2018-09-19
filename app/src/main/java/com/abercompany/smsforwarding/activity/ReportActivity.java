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
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.util.JSLog;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;

    private List<Deposit> trimmedData;
    private List<Deposit> filterData;
    private List<Building> buildings;
    private List<String> buildingName = new ArrayList<>();
    private List<String> account = new ArrayList<>();
    private List<Contract> contracts = new ArrayList<>();

    private ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        binding.setReport(this);

        Intent intent = getIntent();
        trimmedData = (List<Deposit>) intent.getSerializableExtra("trimmedData");
        buildings = (List<Building>) intent.getSerializableExtra("building");
        contracts = (List<Contract>) intent.getSerializableExtra("contract");

        setSpinner();
    }

    private void setSpinner() {
        for (int i = 0; i < buildings.size(); i++) {
            buildingName.add(buildings.get(i).getName());
        }
        for (int i = 0; i < trimmedData.size(); i++) {
            if (!account.contains(trimmedData.get(i).getAccount())) {
                account.add(trimmedData.get(i).getAccount());
            }
        }

        binding.spBuildingName.setAdapter(new ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, buildingName));
        binding.spAccount.setAdapter(new ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, account));
        binding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        binding.spDetailType.setAdapter(new ArrayAdapter(ReportActivity.this,
                                R.layout.support_simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.deposit)));
                        break;
                    case 2:
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
                String detailType = "";

                if (binding.spDetailType.getSelectedItem() != null) {
                    detailType = binding.spDetailType.getSelectedItem().toString();
                }

                setReportAdapter(binding.spBuildingName.getSelectedItem().toString(),
                        trimmedData, binding.spMonth.getSelectedItem().toString().replace("월", ""),
                        binding.spAccount.getSelectedItem().toString(),
                        binding.spType.getSelectedItem().toString(),
                        detailType);
                break;
        }
    }

    private void setReportAdapter(String buildingName, List<Deposit> trimmedData, String month, String account, String type, String detailType) {
        int incoming = 0;
        int outgoing = 0;
        int total = 0;

        filterData = new ArrayList<>();

        for (int i = 0; i < trimmedData.size(); i++) {

            String date = trimmedData.get(i).getDate().replace("[KB]", "");
            String dateMonth = date.split("/")[0];

            if (buildingName.equals(trimmedData.get(i).getBuildingName()) &&
                    dateMonth.contains(month) &&
                    trimmedData.get(i).getAccount().contains(account)) {

                if (binding.spType.getSelectedItemPosition() == 0) {
                    filterData.add(trimmedData.get(i));
                }else {
                    if (trimmedData.get(i).getMethod() != null) {
                        if (trimmedData.get(i).getMethod().contains(type)) {
                            if (binding.spDetailType.getSelectedItemPosition() == 0) {
                                filterData.add(trimmedData.get(i));
                            } else {
                                if ("선택".equals(detailType)) {
                                    filterData.add(trimmedData.get(i));
                                }else if (trimmedData.get(i).getType() != null &&
                                        detailType.equals(trimmedData.get(i).getType())) {
                                    filterData.add(trimmedData.get(i));
                                }
                            }
                        }
                    }
                }
            }

        }
        adapter = new ReportAdapter(this, filterData, contracts);
        binding.rvReport.setAdapter(adapter);
        binding.rvReport.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

        for (int j = 0; j < filterData.size(); j++) {
            if (filterData.get(j).getMethod().contains("입금")) {
                incoming += Integer.parseInt(filterData.get(j).getAmount().replace(",", ""));
            } else if (filterData.get(j).getMethod().contains("출금")) {
                outgoing += Integer.parseInt(filterData.get(j).getAmount().replace(",", ""));
            }
        }

        binding.tvSumIncoming.setText(getString(R.string.str_incoming, incoming));
        binding.tvSumOutgoing.setText(getString(R.string.str_outgoing, outgoing));
        binding.tvTotal.setText(getString(R.string.str_total, (incoming - outgoing)));
    }

}
