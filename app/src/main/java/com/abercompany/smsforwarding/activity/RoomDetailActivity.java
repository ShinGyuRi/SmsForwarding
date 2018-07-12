package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityRoomDetailBinding;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.REQUEST_REALTY;

public class RoomDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private ActivityRoomDetailBinding binding;

    private Room room;
    private Contract contract;

    private String startDate = "";
    private String endDate = "";
    private String active = "";
    private String realtyName = "";
    private String account = "";
    private String brokerName = "";
    private String brokerPhoneNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_detail);
        binding.setRoomDetail(this);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");
        contract = (Contract) intent.getSerializableExtra("contract");

        setInitView(room, contract);
    }

    private void setInitView(Room room, Contract contract) {
        binding.tvRoomNum.setText(room.getRoomNum());

        if ("재실".equals(room.getActive())) {
            binding.rbCheckIn.setChecked(true);
        } else {
            binding.rbCheckOut.setChecked(true);
        }

        if (contract != null) {
            binding.etName.setText(contract.getName());
            binding.etPhoneNum.setText(contract.getPhoneNum());
            binding.etIdNum.setText(contract.getIdNum());
            binding.etEtcNum.setText(contract.getEtcNum());
            binding.etAddress.setText(contract.getAddress());
            binding.etEmerNum.setText(contract.getEmerNum());
            binding.etEmerName.setText(contract.getEmerName());
            binding.etDownPayment.setText(contract.getDownPayment());
            binding.etDeposit.setText(contract.getDeposit());
            binding.etRent.setText(contract.getRent());
            binding.etManageFee.setText(contract.getManageFee());
            binding.tvTerm.setText(getString(R.string.str_term, contract.getStartDate(), contract.getEndDate()));
            binding.etElecNum.setText(contract.getElecNum());
            binding.etGasNum.setText(contract.getGasNum());
            startDate = contract.getStartDate();
            endDate = contract.getEndDate();
            binding.tvRealty.setText(getString(R.string.str_realty_info,
                    contract.getRealtyName(),
                    contract.getRealtyAccount(),
                    contract.getRealtyBrokerName(),
                    contract.getRealtyBrokerPhoneNum()));


            realtyName = contract.getRealtyName();
            account = contract.getRealtyAccount();
            brokerName = contract.getRealtyBrokerName();
            brokerPhoneNum = contract.getRealtyBrokerPhoneNum();
        }

        binding.rgActive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_check_in:
                        active = "재실";
                        break;

                    case R.id.rb_check_out:
                        active = "퇴실";
                        break;
                }
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                uploadContract();
                break;

            case R.id.tv_term:
                Calendar startDate = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "startDate");
                break;

            case R.id.tv_realty:
                Intent intent = new Intent(this, RealtyActivity.class);
                intent.putExtra("realtyName", realtyName);
                intent.putExtra("realtyAccount", account);
                intent.putExtra("realtyBrokerName", brokerName);
                intent.putExtra("realtyBrokerPhoneNum", brokerPhoneNum);
                startActivityForResult(intent, REQUEST_REALTY);
                break;
        }
    }

    private void uploadContract() {
        String roomNum = binding.tvRoomNum.getText().toString();
        String name = binding.etName.getText().toString();
        String phoneNum = binding.etPhoneNum.getText().toString();
        String idNum = binding.etIdNum.getText().toString();
        String etcNum = binding.etEtcNum.getText().toString();
        String address = binding.etAddress.getText().toString();
        String emerNum = binding.etEmerNum.getText().toString();
        String emerName = binding.etEmerName.getText().toString();
        String downPayment = binding.etDownPayment.getText().toString();
        String deposit = binding.etDeposit.getText().toString();
        String rent = binding.etRent.getText().toString();
        String manageFee = binding.etManageFee.getText().toString();
        String elecNum = binding.etElecNum.getText().toString();
        String gasNum = binding.etGasNum.getText().toString();
        String active = "";

        if (binding.rbCheckOut.isChecked()) {
            active = "퇴실";
        } else if (binding.rbCheckIn.isChecked()) {
            active = "재실";
        }

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertContract(
                roomNum, name, phoneNum, idNum, etcNum, address, emerNum, emerName,
                downPayment, deposit, rent, manageFee, startDate, endDate, elecNum, gasNum, active,
                realtyName, account, brokerName, brokerPhoneNum);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        Debug.showToast(RoomDetailActivity.this, "등록되었습니다");
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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

        if ("startDate".equals(view.getTag())) {
            startDate = year + "-" + monthResult + "-" + dayResult;


            Calendar endDate = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), "endDate");
        } else if ("endDate".equals(view.getTag())) {
            endDate = year + "-" + monthResult + "-" + dayResult;

            binding.tvTerm.setText(getString(R.string.str_term, startDate, endDate));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_REALTY) {
            realtyName = data.getStringExtra("realtyName");
            account = data.getStringExtra("realtyAccount");
            brokerName = data.getStringExtra("realtyBrokerName");
            brokerPhoneNum = data.getStringExtra("realtyBrokerPhoneNum");

            binding.tvRealty.setText(getString(R.string.str_realty_info,
                    realtyName,
                    account,
                    brokerName,
                    brokerPhoneNum));
        }
    }
}
