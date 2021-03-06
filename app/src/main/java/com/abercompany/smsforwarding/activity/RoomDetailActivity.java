package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityRoomDetailBinding;
import com.abercompany.smsforwarding.dialog.CheckInListDialog;
import com.abercompany.smsforwarding.dialog.CheckOutListDialog;
import com.abercompany.smsforwarding.model.CheckIn;
import com.abercompany.smsforwarding.model.CheckOut;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.GetCheckInListResult;
import com.abercompany.smsforwarding.model.GetCheckOutListResult;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.REQUEST_REALTY;

public class RoomDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

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
    private String buildingName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_detail);
        binding.setRoomDetail(this);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");
        contract = (Contract) intent.getSerializableExtra("contract");
        buildingName = intent.getStringExtra("buildingName");

        setInitView(room, contract);
    }

    private void setInitView(Room room, Contract contract) {
        binding.tvRoomNum.setText(room.getRoomNum());

        if ("재실".equals(room.getActive())) {
            binding.rbCheckIn.setChecked(true);
            rbCheckOutSetOnClick();
        } else {
            binding.rbCheckOut.setChecked(true);
            rbCheckInSetOnClick();
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
                if (!"".equals(binding.etName.getText().toString())) {
                    if (checkedId == R.id.rb_check_in) {
                        rbCheckOutSetOnClick();
                    } else if (checkedId == R.id.rb_check_out) {
                        rbCheckInSetOnClick();
                    }
                } else {
                    Debug.showToast(RoomDetailActivity.this, "이름을 입력해주세요");
                }
            }
        });


    }

    private void rbCheckInSetOnClick() {

        binding.rbCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rb_check_in:
                        getCheckInList(binding.tvRoomNum.getText().toString(), binding.etName.getText().toString());
                        active = "재실";
                        break;
                }
            }
        });
    }

    private void rbCheckOutSetOnClick() {
        binding.rbCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rb_check_out:
                        getCheckOutList(binding.tvRoomNum.getText().toString(), binding.etName.getText().toString());
                        active = "퇴실";
                        break;
                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                uploadContract(buildingName);
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

    private void getCheckInList(final String roomNum, final String name) {
        Call<GetCheckInListResult> getCheckInListResultCall = NetworkUtil.getInstace().getCheckInList(roomNum, name);
        getCheckInListResultCall.enqueue(new Callback<GetCheckInListResult>() {
            @Override
            public void onResponse(Call<GetCheckInListResult> call, Response<GetCheckInListResult> response) {
                GetCheckInListResult getCheckInListResult = response.body();
                String result = getCheckInListResult.getResult();

                if ("success".equals(result)) {
                    CheckIn checkIn = getCheckInListResult.getCheckIns();


                    final CheckInListDialog checkInListDialog = new CheckInListDialog(RoomDetailActivity.this);
                    checkInListDialog.show();


                    if (checkIn.isIdNum() != null) {

                        checkInListDialog.getBinding().checkIdNum.setChecked(checkIn.isIdNum());
                        checkInListDialog.getBinding().checkInputEmerNum.setChecked(checkIn.isEmerNum());
                        checkInListDialog.getBinding().checkAmount.setChecked(checkIn.isAmount());
                        checkInListDialog.getBinding().checkElecGas.setChecked(checkIn.isElecGas());
                        checkInListDialog.getBinding().checkCondition.setChecked(checkIn.isCondition());
                        checkInListDialog.getBinding().checkRealty.setChecked(checkIn.isRealty());

                    }

                    checkInListDialog.getBinding().btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            insertCheckInList(checkInListDialog, roomNum, name,
                                    checkInListDialog.getBinding().checkIdNum.isChecked(),
                                    checkInListDialog.getBinding().checkInputEmerNum.isChecked(),
                                    checkInListDialog.getBinding().checkAmount.isChecked(),
                                    checkInListDialog.getBinding().checkElecGas.isChecked(),
                                    checkInListDialog.getBinding().checkCondition.isChecked(),
                                    checkInListDialog.getBinding().checkRealty.isChecked());

                            if (!checkInListDialog.getBinding().checkIdNum.isChecked() ||
                                    !checkInListDialog.getBinding().checkInputEmerNum.isChecked() ||
                                    !checkInListDialog.getBinding().checkAmount.isChecked() ||
                                    !checkInListDialog.getBinding().checkElecGas.isChecked() ||
                                    !checkInListDialog.getBinding().checkCondition.isChecked() ||
                                    !checkInListDialog.getBinding().checkRealty.isChecked()) {
                                binding.rbCheckOut.setChecked(true);
                            }
                        }
                    });
                    checkInListDialog.getBinding().btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.rbCheckOut.setChecked(true);
                            checkInListDialog.cancel();
                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<GetCheckInListResult> call, Throwable t) {

            }
        });
    }

    private void insertCheckInList(final CheckInListDialog dialog, String roomNum, String name, boolean idNum, boolean emerNum, boolean amount, boolean elecGas, boolean condition, boolean realty) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertCheckInList(roomNum, name, idNum, emerNum, amount, elecGas, condition, realty);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        JSLog.D("insert CheckIn List Success            !!!     ", null);
                        dialog.dismiss();

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getCheckOutList(final String roomNum, final String name) {
        Call<GetCheckOutListResult> getCheckOutListResultCall = NetworkUtil.getInstace().getCheckOutList(roomNum, name);
        getCheckOutListResultCall.enqueue(new Callback<GetCheckOutListResult>() {
            @Override
            public void onResponse(Call<GetCheckOutListResult> call, Response<GetCheckOutListResult> response) {
                GetCheckOutListResult checkOutListResult = response.body();
                String result = checkOutListResult.getResult();

                if ("success".equals(result)) {
                    CheckOut checkOut = checkOutListResult.getCheckOut();

                    final CheckOutListDialog checkoutListDialog = new CheckOutListDialog(RoomDetailActivity.this, RoomDetailActivity.this);
                    checkoutListDialog.show();


                    if (checkOut.getAccount() != null) {

                        checkoutListDialog.getBinding().etElecAmount.setText(String.valueOf(checkOut.getElecAmount()));
                        checkoutListDialog.getBinding().etGasAmount.setText(String.valueOf(checkOut.getGasAmount()));
                        checkoutListDialog.getBinding().etInputOutDate.setText(checkOut.getOutDate());
                        checkoutListDialog.getBinding().checkRemoteCon.setChecked(checkOut.getRemoteCon());
                        checkoutListDialog.getBinding().checkAccount.setChecked(checkOut.getAccount());
                        checkoutListDialog.getBinding().checkKatok.setChecked(checkOut.getKatok());
                        checkoutListDialog.getBinding().checkTv.setChecked(checkOut.getTv());
                        checkoutListDialog.getBinding().tvAdjustAmount.setText(getString(R.string.str_adjustment_amount,
                                String.valueOf(checkOut.getDeposit() - (checkOut.getElecAmount() + checkOut.getGasAmount() + 70000 + checkOut.getDayAmount()))));
                    }


                    checkoutListDialog.getBinding().btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String elecAmount = checkoutListDialog.getBinding().etElecAmount.getText().toString();
                            String gasAmount = checkoutListDialog.getBinding().etGasAmount.getText().toString();
                            String outDate = checkoutListDialog.getBinding().etInputOutDate.getText().toString();

                            if ("".equals(elecAmount)) {
                                elecAmount = "0";
                            }
                            if ("".equals(gasAmount)) {
                                gasAmount = "0";
                            }

                            insertCheckOutList(checkoutListDialog, roomNum, name,
                                    Integer.parseInt(elecAmount),
                                    Integer.parseInt(gasAmount),
                                    outDate,
                                    checkoutListDialog.getBinding().checkRemoteCon.isChecked(),
                                    checkoutListDialog.getBinding().checkAccount.isChecked(),
                                    checkoutListDialog.getBinding().checkKatok.isChecked(),
                                    checkoutListDialog.getBinding().checkTv.isChecked());

                            if ("".equals(elecAmount) ||
                                    "".equals(gasAmount) ||
                                    "".equals(outDate) ||
                                    !checkoutListDialog.getBinding().checkRemoteCon.isChecked() ||
                                    !checkoutListDialog.getBinding().checkAccount.isChecked() ||
                                    !checkoutListDialog.getBinding().checkKatok.isChecked() ||
                                    !checkoutListDialog.getBinding().checkTv.isChecked()) {
                                binding.rbCheckIn.setChecked(true);
                            }
                        }
                    });
                    checkoutListDialog.getBinding().btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.rbCheckIn.setChecked(true);
                            checkoutListDialog.cancel();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetCheckOutListResult> call, Throwable t) {

            }
        });
    }

    private void insertCheckOutList(final CheckOutListDialog dialog, String roomNum, String name, int elecAmount, int gasAmount, String outDate, boolean remoteCon, boolean account, boolean katok, boolean tv) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertCheckOutList(roomNum, name, elecAmount, gasAmount, outDate, remoteCon, account, katok, tv);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        JSLog.D("insert CheckOut List Success            !!!     ", null);
                        dialog.dismiss();

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void uploadContract(String buildingName) {
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
        JSLog.D("active         :::     " + active, null);

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertContract(
                roomNum, name, phoneNum, idNum, etcNum, address, emerNum, emerName,
                downPayment, deposit, rent, manageFee, startDate, endDate, elecNum, gasNum, active,
                realtyName, account, brokerName, brokerPhoneNum, buildingName);

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
        } else {
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
