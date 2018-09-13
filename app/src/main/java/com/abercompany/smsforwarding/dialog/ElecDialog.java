package com.abercompany.smsforwarding.dialog;

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

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.DlgElecBinding;
import com.abercompany.smsforwarding.model.Defaulter;

public class ElecDialog extends Dialog {

    private DlgElecBinding binding;

    private Context context;
    private Defaulter elec;

    public ElecDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ElecDialog(Context context, Defaulter elec) {
        super(context);
        this.context = context;
        this.elec = elec;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dlg_elec,
                null, false);
        binding.setDlgElec(this);

        initView();
    }

    private void initView() {
        if (elec != null) {

            if (!"".equals(elec.getRoomNum())) {
                binding.etRoomNum.setText(elec.getRoomNum());
            }

            if (!"".equals(elec.getName())) {
                binding.etName.setText(elec.getName());
            }

            if (!"".equals(elec.getEndDate())) {
                binding.etCheckDate.setText(elec.getEndDate());
            }

            if (!"".equals(elec.getElecNum())) {
                binding.etElecNum.setText(elec.getElecNum());
            }

            if (!"".equals(elec.getAmount())) {
                binding.etAmount.setText(elec.getAmount());
            }

            if (!"".equals(elec.getStatus())) {
                binding.etStatus.setText(elec.getStatus());
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                break;

            case R.id.btn_ok:
                break;
        }
    }
}
