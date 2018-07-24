package com.abercompany.smsforwarding.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.DlgCheckOutListBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class CheckOutListDialog extends Dialog implements CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener {

    private DlgCheckOutListBinding binding;

    private Context context;
    private Activity activity;

    public CheckOutListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CheckOutListDialog(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    public DlgCheckOutListBinding getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dlg_check_out_list, null, false);
        setContentView(binding.getRoot());
        binding.setCheckOutList(this);

        binding.checkRemoteCon.setOnCheckedChangeListener(this);
        binding.checkAccount.setOnCheckedChangeListener(this);
        binding.checkKatok.setOnCheckedChangeListener(this);
        binding.checkTv.setOnCheckedChangeListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_input_out_date:
                Calendar endDate = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        endDate.get(Calendar.YEAR),
                        endDate.get(Calendar.MONTH),
                        endDate.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(activity.getFragmentManager(), "outDate");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.check_remote_con:
                break;

            case R.id.check_account:
                break;

            case R.id.check_katok:
                break;

            case R.id.check_tv:
                break;
        }
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

        String date = year + "-" + monthResult + "-" + dayResult;
        binding.etInputOutDate.setText(date);
    }
}
