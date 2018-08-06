package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityIncomingCallBinding;
import com.abercompany.smsforwarding.model.Resident;

public class IncomingCallActivity extends AppCompatActivity {

    private ActivityIncomingCallBinding binding;

    private String content = "";
    private String phoneNum = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_call);

        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        phoneNum = intent.getStringExtra("phoneNum");

        binding.tvMessage.setText(content + "\n전화가 왔습니다");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                break;
        }
    }

    private void registerPhoneNum(String phoneNum) {

    }
}
