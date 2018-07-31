package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityIncomingCallBinding;

public class IncomingCallActivity extends AppCompatActivity {

    private ActivityIncomingCallBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_call);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        binding.tvMessage.setText(name + "\n전화가 왔습니다");
    }
}
