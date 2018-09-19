package com.abercompany.smsforwarding.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivitySelectLeaveDayBinding;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.PrefUtil;

public class SelectLeaveDayActivity extends AppCompatActivity {

    private ActivitySelectLeaveDayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_leave_day);
        binding.setSelectLeaveDay(this);

        String leaveDay = PrefUtil.getInstance().getStringPreference("leaveDay");
        if (!"".equals(leaveDay)) {
            binding.etDay.setText(leaveDay);
        }

        String upperRealtyFees = PrefUtil.getInstance().getStringPreference("upperRealtyFees");
        if (!"".equals(upperRealtyFees)) {
            binding.etUpperFloorRealtyFees.setText(upperRealtyFees);
        }

        String underRealtyFees = PrefUtil.getInstance().getStringPreference("underRealtyFees");
        if (!"".equals(underRealtyFees)) {
            binding.etUnderFloorRealtyFees.setText(underRealtyFees);
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (!"".equals(binding.etDay.getText().toString())) {
                    PrefUtil.getInstance().putPreference("leaveDay", binding.etDay.getText().toString());
                    Debug.showToast(this, "등록되었습니다");
                }
                break;

            case R.id.btn_register_upper_realty_fees:
                if (!"".equals(binding.etUpperFloorRealtyFees.getText().toString())) {
                    PrefUtil.getInstance().putPreference("upperRealtyFees", Integer.parseInt(binding.etDay.getText().toString()));
                    Debug.showToast(this, "등록되었습니다");
                }
                break;

            case R.id.btn_register_under_realty_fees:
                if (!"".equals(binding.etUnderFloorRealtyFees.getText().toString())) {
                    PrefUtil.getInstance().putPreference("underRealtyFees", Integer.parseInt(binding.etDay.getText().toString()));
                    Debug.showToast(this, "등록되었습니다");
                }
                break;
        }
    }
}
