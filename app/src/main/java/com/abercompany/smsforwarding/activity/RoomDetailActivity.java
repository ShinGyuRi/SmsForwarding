package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.RadioGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.DepositLogAdapter;
import com.abercompany.smsforwarding.databinding.ActivityRoomDetailBinding;
import com.abercompany.smsforwarding.dialog.CheckInListDialog;
import com.abercompany.smsforwarding.dialog.CheckOutListDialog;
import com.abercompany.smsforwarding.model.CheckInList;
import com.abercompany.smsforwarding.model.CheckOutList;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.DepositLog;
import com.abercompany.smsforwarding.model.GetCheckInListResult;
import com.abercompany.smsforwarding.model.GetCheckOutListResult;
import com.abercompany.smsforwarding.model.GetDepositLogResult;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.DEP_TYPE.ROOM_DETAIL;
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

    private List<DepositLog> depositLogs;

    private DepositLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_detail);
        binding.setRoomDetail(this);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");
        contract = (Contract) intent.getSerializableExtra("contract");
        buildingName = intent.getStringExtra("buildingName");
        JSLog.D("buildingName           :::     " + buildingName, new Throwable());

        setInitView(room, contract);
    }

    private void setInitContent(boolean flag) {

        binding.etName.setFocusable(flag);
        binding.etName.setFocusableInTouchMode(flag);

        binding.etPhoneNum.setFocusable(flag);
        binding.etPhoneNum.setFocusableInTouchMode(flag);

        binding.etIdNum.setFocusable(flag);
        binding.etIdNum.setFocusableInTouchMode(flag);

        binding.etEtcNum.setFocusable(flag);
        binding.etEtcNum.setFocusableInTouchMode(flag);

        binding.etAddress.setFocusable(flag);
        binding.etAddress.setFocusableInTouchMode(flag);

        binding.etEmerNum.setFocusable(flag);
        binding.etEmerNum.setFocusableInTouchMode(flag);

        binding.etEmerName.setFocusable(flag);
        binding.etEmerName.setFocusableInTouchMode(flag);

        binding.etDownPayment.setFocusable(flag);
        binding.etDownPayment.setFocusableInTouchMode(flag);

        binding.etDeposit.setFocusable(flag);
        binding.etDeposit.setFocusableInTouchMode(flag);

        binding.etRent.setFocusable(flag);
        binding.etRent.setFocusableInTouchMode(flag);

        binding.etManageFee.setFocusable(flag);
        binding.etManageFee.setFocusableInTouchMode(flag);

        binding.etElecNum.setFocusable(flag);
        binding.etElecNum.setFocusableInTouchMode(flag);

        binding.etGasNum.setFocusable(flag);
        binding.etGasNum.setFocusableInTouchMode(flag);

        binding.tvTerm.setEnabled(flag);
        binding.btnRealty.setEnabled(flag);

        if (!flag) {
            binding.layoutRoomDetail.setBackgroundColor(getResources().getColor(R.color.transparent));
        } else {
            binding.layoutRoomDetail.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    private void setInitView(Room room, Contract contract) {
        setInitContent(false);

        binding.tvRoomNum.setText(room.getRoomNum());
        binding.etPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        binding.etEtcNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        binding.etEmerNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        if ("재실".equals(room.getActive())) {
            binding.rbCheckIn.setChecked(true);
            rbCheckOutSetOnClick();
        } else if("퇴실".equals(room.getActive())){
            binding.rbCheckOut.setChecked(true);
            rbCheckInSetOnClick();
        } else if ("계약".equals(room.getActive())) {
            binding.rbUnderContract.setChecked(true);
        }

        if (contract != null) {
            getDepositLog(room.getRoomNum(), contract.getName());

            JSLog.D("contract.getDownPayment            :::     " + contract.getDownPayment(), null);
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
                dpd.setTitle("입실일");
                dpd.show(getFragmentManager(), "startDate");
                break;

            case R.id.btn_realty:
                Intent intent = new Intent(this, RealtyActivity.class);
                intent.putExtra("dep", ROOM_DETAIL);
                intent.putExtra("realtyName", realtyName);
                intent.putExtra("realtyAccount", account);
                intent.putExtra("realtyBrokerName", brokerName);
                intent.putExtra("realtyBrokerPhoneNum", brokerPhoneNum);
                startActivityForResult(intent, REQUEST_REALTY);
                break;

            case R.id.btn_add_zero_deposit:
                binding.etDeposit.append("0000");
                break;
            case R.id.btn_add_zero_down_payment:
                binding.etDownPayment.append("0000");
                break;
            case R.id.btn_add_zero_manage_fee:
                binding.etManageFee.append("0000");
                break;
            case R.id.btn_add_zero_rent:
                binding.etRent.append("0000");
                break;

            case R.id.btn_add_zero_discount:
                binding.etDiscount.append("0000");
                break;

            case R.id.btn_edit:
                JSLog.D("EDIT           !!!!    ", null);
                setInitContent(true);
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
                    CheckInList checkInList = getCheckInListResult.getCheckInsList();


                    final CheckInListDialog checkInListDialog = new CheckInListDialog(RoomDetailActivity.this);
                    checkInListDialog.show();


                    if (checkInList.isIdNum() != null) {

                        checkInListDialog.getBinding().checkIdNum.setChecked(checkInList.isIdNum());
                        checkInListDialog.getBinding().checkInputEmerNum.setChecked(checkInList.isEmerNum());
                        checkInListDialog.getBinding().checkAmount.setChecked(checkInList.isAmount());
                        checkInListDialog.getBinding().checkElecGas.setChecked(checkInList.isElecGas());
                        checkInListDialog.getBinding().checkCondition.setChecked(checkInList.isCondition());
                        checkInListDialog.getBinding().checkRealty.setChecked(checkInList.isRealty());

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
                        JSLog.D("insert CheckInList List Success            !!!     ", null);
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
                    CheckOutList checkOutList = checkOutListResult.getCheckOutList();

                    final CheckOutListDialog checkoutListDialog = new CheckOutListDialog(RoomDetailActivity.this, RoomDetailActivity.this);
                    checkoutListDialog.show();


                    if (checkOutList.getAccount() != null) {

                        checkoutListDialog.getBinding().etElecAmount.setText(String.valueOf(checkOutList.getElecAmount()));
                        checkoutListDialog.getBinding().etGasAmount.setText(String.valueOf(checkOutList.getGasAmount()));
                        checkoutListDialog.getBinding().etInputOutDate.setText(checkOutList.getOutDate());
                        checkoutListDialog.getBinding().checkRemoteCon.setChecked(checkOutList.getRemoteCon());
                        checkoutListDialog.getBinding().checkAccount.setChecked(checkOutList.getAccount());
                        checkoutListDialog.getBinding().checkKatok.setChecked(checkOutList.getKatok());
                        checkoutListDialog.getBinding().checkTv.setChecked(checkOutList.getTv());
                        checkoutListDialog.getBinding().tvAdjustAmount.setText(getString(R.string.str_adjustment_amount,
                                String.valueOf(checkOutList.getDeposit() - (checkOutList.getElecAmount() + checkOutList.getGasAmount() + 70000 + checkOutList.getDayAmount()))));
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
                        JSLog.D("insert CheckOutList List Success            !!!     ", null);
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
        String phoneNum = binding.etPhoneNum.getText().toString().replace("-", "");
        String idNum = binding.etIdNum.getText().toString();
        String etcNum = binding.etEtcNum.getText().toString().replace("-", "");
        String address = binding.etAddress.getText().toString();
        String emerNum = binding.etEmerNum.getText().toString().replace("-", "");
        String emerName = binding.etEmerName.getText().toString();
        String downPayment = binding.etDownPayment.getText().toString();
        String deposit = binding.etDeposit.getText().toString();
        String rent = binding.etRent.getText().toString();
        String manageFee = binding.etManageFee.getText().toString();
        String elecNum = binding.etElecNum.getText().toString();
        String gasNum = binding.etGasNum.getText().toString();
        String active = "";
        String discount = binding.etDiscount.getText().toString();

        if (binding.rbCheckOut.isChecked()) {
            active = "퇴실";
        } else if (binding.rbCheckIn.isChecked()) {
            active = "재실";
        } else if (binding.rbUnderContract.isChecked()) {
            active = "계약";
        }
        JSLog.D("active         :::     " + active, null);

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertContract(
                roomNum, name, phoneNum, idNum, etcNum, address, emerNum, emerName,
                downPayment, deposit, rent, manageFee, startDate, endDate, elecNum, gasNum, active,
                realtyName, account, brokerName, brokerPhoneNum, buildingName, discount);

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

    private void getDepositLog(String roomNum, String name) {
        Call<GetDepositLogResult> getDepositLogResultCall = NetworkUtil.getInstace().getDepositLog(roomNum, name);
        getDepositLogResultCall.enqueue(new Callback<GetDepositLogResult>() {
            @Override
            public void onResponse(Call<GetDepositLogResult> call, Response<GetDepositLogResult> response) {
                GetDepositLogResult getDepositLogResult = response.body();
                String result = getDepositLogResult.getResult();

                if ("success".equals(result)) {
                    depositLogs = getDepositLogResult.getDepositLogs();

                    setDepositLogAdapter(depositLogs);
                }
            }

            @Override
            public void onFailure(Call<GetDepositLogResult> call, Throwable t) {

            }
        });
    }

    private void setDepositLogAdapter(List<DepositLog> depositLogs) {
        adapter = new DepositLogAdapter(this, depositLogs);
        binding.rvDepositLog.setAdapter(adapter);
        binding.rvDepositLog.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
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
            dpd.setTitle("퇴실일");
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
