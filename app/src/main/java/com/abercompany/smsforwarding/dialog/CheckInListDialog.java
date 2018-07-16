package com.abercompany.smsforwarding.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.CompoundButton;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.DlgCheckInListBinding;

public class CheckInListDialog extends Dialog implements CompoundButton.OnCheckedChangeListener{

    private DlgCheckInListBinding binding;

    private Context context;

    public CheckInListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public DlgCheckInListBinding getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dlg_check_in_list, null, false);
        setContentView(binding.getRoot());

        binding.checkIdNum.setOnCheckedChangeListener(this);
        binding.checkInputEmerNum.setOnCheckedChangeListener(this);
        binding.checkAmount.setOnCheckedChangeListener(this);
        binding.checkElecGas.setOnCheckedChangeListener(this);
        binding.checkCondition.setOnCheckedChangeListener(this);
        binding.checkRealty.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_id_num:
                break;

            case R.id.check_input_emer_num:
                break;

            case R.id.check_amount:
                break;

            case R.id.check_elec_gas:
                break;

            case R.id.check_condition:
                break;

            case R.id.check_realty:
                break;
        }
    }
}
