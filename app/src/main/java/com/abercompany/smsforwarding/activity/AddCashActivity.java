package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.ActivityAddCashBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;
import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.NEW_DATA;

public class AddCashActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ActivityAddCashBinding binding;

    private List<Resident> residents;
    private List<Broker> brokers;
    private List<String> residentName = new ArrayList<>();
    private List<String> brokerName = new ArrayList<>();

    private String date = "";
    private DatePickerDialog dpd;


    private String startDate = "";
    private String endDate = "";
    private String dataType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_cash);
        binding.setAddCash(this);

        setResidentBrokerName();

        binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {
                switch (itemPosition) {
                    case 0:
                    case 7:
                    case 10:
                    case 11:
                        binding.spToName.setAdapter(new ArrayAdapter(AddCashActivity.this, R.layout.support_simple_spinner_dropdown_item));
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 9:
                        binding.spToName.setAdapter(new ArrayAdapter(AddCashActivity.this, R.layout.support_simple_spinner_dropdown_item, residentName));
                        break;
                    case 8:
                        binding.spToName.setAdapter(new ArrayAdapter(AddCashActivity.this, R.layout.support_simple_spinner_dropdown_item, brokerName));
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setResidentBrokerName() {
        Intent intent = getIntent();
        residents = (ArrayList<Resident>) intent.getSerializableExtra("resident");
        brokers = (ArrayList<Broker>) intent.getSerializableExtra("broker");
        dataType = intent.getStringExtra("dataType");

        for (int i = 0; i < residents.size(); i++) {
            residentName.add(getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
        }
        for (int i = 0; i < brokers.size(); i++) {
            brokerName.add(getString(R.string.str_deposit_realty, brokers.get(i).getName(), brokers.get(i).getRealtyName()));
        }

        if (EXISTING_DATA.equals(dataType)) {
            binding.tvLastTerm.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                String name = binding.etName.getText().toString();
                String objectName = "";
                String type = "";
                String amount = binding.etAmount.getText().toString();

                if (!"".equals(binding.spToName.getSelectedView().toString())) {
                    objectName = binding.spToName.getSelectedItem().toString();
                }
                if (!"".equals(binding.spCategory.getSelectedItem().toString())) {
                    type = binding.spCategory.getSelectedItem().toString();
                }
                if (!"".equals(name) &&
                        !"".equals(type) &&
                        !"".equals(date) &&
                        !"".equals(amount)) {
                    insertCash(name, amount, objectName, type);
                }
                break;

            case R.id.et_date:

                Calendar cal = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        this,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "date");
                break;

            case R.id.tv_last_term:
                Calendar startDate = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        this,
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "startDate");
                break;
        }
    }

    private void insertCash(final String name, String amount, final String objectName, final String type) {
        String message = getString(R.string.str_sms_message_cash, date, "", name, "현금", amount);
        JSLog.D("sms message            :::     " + message, null);

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertSms(message, "", "", getTimeStamp());
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = null;
                result = jsonObject.get("result").getAsString();

                JSLog.D("result          :::     " + result, null);

                if ("success".equals(result)) {
                    boolean success = jsonObject.get("message").getAsBoolean();
                    JSLog.D("success          :::     " + success, null);
                    if (success) {
                        updateTrimmedData(name, date, objectName, type, dataType);
                    } else {
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                JSLog.E("Fail", t);
            }
        });
    }

    private void updateTrimmedData(String name, String date, String objectName, final String type, String dataType) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().updateTrimmedData(name, date, objectName, type, DeviceUtil.getDevicePhoneNumber(this), dataType, startDate, endDate);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Debug.showToast(AddCashActivity.this, "등록되었습니다");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private String getTimeStamp() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        return ts;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String monthResult = "";
        String dayResult = "";
        if (String.valueOf(monthOfYear + 1).length() == 1) {
            monthResult = "0" + (monthOfYear + 1);
        } else {
            monthResult = String.valueOf(monthOfYear + 1);
        }
        if (String.valueOf(dayOfMonth).length() == 1) {
            dayResult = "0" + dayOfMonth;
        }else {
            dayResult = String.valueOf(dayOfMonth);
        }

        if ("date".equals(view.getTag())) {

            date = monthResult + "/" + dayResult;
            binding.etDate.setText(date);
        } else if ("startDate".equals(view.getTag())) {
            startDate = year + "-" + monthResult + "-" + dayResult;


            Calendar endDate = Calendar.getInstance();
            dpd = DatePickerDialog.newInstance(
                    this,
                    endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), "endDate");
        } else if ("endDate".equals(view.getTag())) {
            endDate = year + "-" + monthResult + "-" + dayResult;

            binding.tvLastTerm.setText(getString(R.string.str_term, startDate, endDate));
        }
    }
}
