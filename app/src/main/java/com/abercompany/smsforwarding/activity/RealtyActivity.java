package com.abercompany.smsforwarding.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.RealtyAdapter;
import com.abercompany.smsforwarding.databinding.ActivityRealtyBinding;
import com.abercompany.smsforwarding.model.GetRealtyResult;
import com.abercompany.smsforwarding.model.Realty;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.abercompany.smsforwarding.util.SmsLib;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealtyActivity extends AppCompatActivity {

    private ActivityRealtyBinding binding;

    private List<Realty> realties;
    private RealtyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_realty);
        binding.setRealty(this);

        Intent intent = getIntent();
        binding.etRealtyName.setText(intent.getStringExtra("realtyName"));
        binding.etRealtyAccount.setText(intent.getStringExtra("realtyAccount"));
        binding.etRealtyBrokerName.setText(intent.getStringExtra("realtyBrokerName"));
        binding.etBrokerPhoneNum.setText(intent.getStringExtra("realtyBrokerPhoneNum"));
        getRealty();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                Intent intent = new Intent();
                intent.putExtra("realtyName", binding.etRealtyName.getText().toString());
                intent.putExtra("realtyBrokerName", binding.etRealtyBrokerName.getText().toString());
                intent.putExtra("realtyBrokerPhoneNum", binding.etBrokerPhoneNum.getText().toString());
                intent.putExtra("realtyAccount", binding.etRealtyAccount.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void getRealty() {
        Call<GetRealtyResult> getRealtyResultCall = NetworkUtil.getInstace().getRealty("");
        getRealtyResultCall.enqueue(new Callback<GetRealtyResult>() {
            @Override
            public void onResponse(Call<GetRealtyResult> call, Response<GetRealtyResult> response) {
                GetRealtyResult getRealtyResult = response.body();
                String result = getRealtyResult.getResult();

                if ("success".equals(result)) {
                    realties = getRealtyResult.getRealties();

                    setRealtyAdapter(realties);
                }
            }

            @Override
            public void onFailure(Call<GetRealtyResult> call, Throwable t) {

            }
        });
    }

    private void setRealtyAdapter(final List<Realty> realties) {
        adapter = new RealtyAdapter(this, realties);
        binding.rcRealty.setAdapter(adapter);
        binding.rcRealty.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        adapter.setItemClick(new RealtyAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                binding.etRealtyName.setText(realties.get(position).getRealtyName());
                binding.etRealtyAccount.setText(realties.get(position).getRealtyAccount());
                binding.etRealtyBrokerName.setText(realties.get(position).getRealtyBrokerName());
                binding.etBrokerPhoneNum.setText(realties.get(position).getRealtyBrokerPhoneNum());
            }

            @Override
            public void onLongClick(View view, final int position) {
                SmsLib.getInstance().showSimplSelect2Dialog(RealtyActivity.this,
                        "삭제하시겠습니까?",
                        "예", "아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRealty(realties.get(position).getRealtyName(),
                                        realties.get(position).getRealtyBrokerName(), adapter,
                                        position);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }
        });
    }

    private void deleteRealty(String realtyName, String brokerName, final RealtyAdapter adapter, final int position) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().deleteRealty(realtyName, brokerName);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();

                    if (message) {
                        Debug.showToast(RealtyActivity.this, "삭제되었습니다");
                        adapter.remove(position);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
