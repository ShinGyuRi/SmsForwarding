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
import com.abercompany.smsforwarding.databinding.DlgCheckOutListBinding;

public class CheckOutListDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private DlgCheckOutListBinding binding;

    private Context context;

    public CheckOutListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
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

        binding.checkElecGas.setOnCheckedChangeListener(this);
        binding.checkInputOutDate.setOnCheckedChangeListener(this);
        binding.checkRemoteCon.setOnCheckedChangeListener(this);
        binding.checkAccount.setOnCheckedChangeListener(this);
        binding.checkKatok.setOnCheckedChangeListener(this);
        binding.checkTv.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_elec_gas:
                break;

            case R.id.check_input_out_date:
                break;

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
}
