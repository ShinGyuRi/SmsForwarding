package com.abercompany.smsforwarding.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityMainBinding;
import com.abercompany.smsforwarding.fragment.DepositDataFragment;
import com.abercompany.smsforwarding.fragment.SearchRawDataFragment;
import com.abercompany.smsforwarding.model.Sms;
import com.abercompany.smsforwarding.network.QuestionsSpreadsheetWebService;
import com.abercompany.smsforwarding.service.SmsService;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;


    private List<Sms> lst;
    private QuestionsSpreadsheetWebService spreadsheetWebService;
    private List<String> nums = new ArrayList<String>();
    private List<Sms> lst2;
    private Cursor c;
    private Fragment searchRawDataFragment, depositDataFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMain(this);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                getMessage();
                Intent intent = new Intent(getApplicationContext(), SmsService.class);
                startService(intent);


                getNum("");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this servicePlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE)
                .check();

        setInitFrag();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
    }

    public void bottomBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_raw_data:
                initNaviButton(view);
                if (searchRawDataFragment == null) {
                    searchRawDataFragment = SearchRawDataFragment.newInstance(nums);
                }
                switchContent(searchRawDataFragment, "SEARCH_RAW_DATA");
                break;

            case R.id.btn_deposit_data:
                initNaviButton(view);
                if (depositDataFragment == null) {
                    depositDataFragment = DepositDataFragment.newInstance();
                }
                switchContent(depositDataFragment, "DEPOSIT_DATA");
                break;
        }
    }

    public void setInitFrag()  {
        searchRawDataFragment = SearchRawDataFragment.newInstance(nums);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, searchRawDataFragment, "SEARCH_RAW_DATA").addToBackStack("SEARCH_RAW_DATA").commit();
        binding.btnSearchRawData.setEnabled(false);
    }

    private void initNaviButton(View v) {
        binding.btnSearchRawData.setEnabled(true);
        binding.btnDepositData.setEnabled(true);
        v.setEnabled(false);
    }

    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();

        JSLog.D("back stack cnt:" + fm.getBackStackEntryCount(),new Throwable());

        boolean fragmentPopped = fm.popBackStackImmediate(tag, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(tag);
            ft.replace(R.id.container, fragment, tag).commit();
        }

    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                MainActivity.this.finishAffinity();
            }
            else
            {
                System.exit(0);
            }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().removeOnBackStackChangedListener(backStackChangedListener);
    }

    private FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            String currentTab = getSupportFragmentManager().findFragmentById(R.id.container).getTag();
            JSLog.D(currentTab, new Throwable());
            if(currentTab==null) return;
            switch(currentTab){
                case "SEARCH_RAW_DATA":
                    initNaviButton(binding.btnSearchRawData);
                    break;
                case "DEPOSIT_DATA":
                    initNaviButton(binding.btnDepositData);
                    break;
            }
        }
    };

    private void getNum(final String senderNum) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().getNum(senderNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Log.d(TAG, "message      ::      " + jsonObject.get("message").getAsJsonArray().toString());

                    JsonArray message = jsonObject.get("message").getAsJsonArray();
                    JsonObject messageObj;
                    for (int i=0; i<message.size(); i++) {
                        messageObj = message.get(i).getAsJsonObject();
                        if (DeviceUtil.getDevicePhoneNumber(MainActivity.this).equals(messageObj.get("phone_num").getAsString())) {
                            nums.add(messageObj.get("sender_num").getAsString());
                        }
                    }

//                    setRegisterNumAdapter(nums);


                    for (int i=0; i<nums.size(); i++) {
                        lst2 = getAllSms(nums.get(i));
                        Log.d(TAG, "lst2.size           :::     " + lst2.size());
                        uploadSmsBody(nums.get(i).toString(), lst2);
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    int i = 0;

    private void uploadSmsBody(final String senderNum, final List<Sms> lst) {
        String message = lst.get(i).getMsg();
        String phoneNum = DeviceUtil.getDevicePhoneNumber(this);
        String timeStamp = lst.get(i).getTime();
        Log.d(TAG, "phoneNum        :::     " + phoneNum);

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertSms(message, phoneNum, senderNum, timeStamp);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = null;
                result = jsonObject.get("result").getAsString();

                Log.d(TAG, "result          :::     " + result);

                if ("success".equals(result)) {
                    boolean success = jsonObject.get("message").getAsBoolean();
                    Log.d(TAG, "success          :::     " + success);
                    if (success) {
                        i++;
                        Log.d(TAG, "i           ::      " + i);
//                        Log.d(TAG, "lst.size            ::      " + lst.size());
                        if (i < lst.size()) {
                            uploadSmsBody(senderNum, lst);
                        }

                        if (i == lst.size()) {
                            i = 0;
                        }
                    } else {
                        i = 0;
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Fail", t);
            }
        });
    }


    private void uploadToSpreadSheet() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://docs.google.com/forms/d/e/")
                .build();
        spreadsheetWebService = retrofit.create(QuestionsSpreadsheetWebService.class);

        String smsMsg = lst.get(i).getMsg();
        String split[] = smsMsg.split("\n");
        String date, account, name, amount;
        if ("[Web발신]".equals(split[0])) {
            date = split[1];
            account = split[2];
            name = split[3];
            amount = split[5];
        } else {
            date = split[0];
            account = split[1];
            name = split[2];
            amount = split[4];
        }
        Log.d(TAG, "name            ::  " + name);
        Log.d(TAG, "account            ::  " + account);
        Log.d(TAG, "amount            ::  " + amount);
        Log.d(TAG, "date            ::  " + date);

        Call<Void> completeQuestionnaireCall = spreadsheetWebService.completeQuestionnaire(name, account, amount, date);
        completeQuestionnaireCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Submitted.      " + response);
                i++;
                if (i < lst.size()) {
                    uploadToSpreadSheet();
                }

                if (i == lst.size()) {
                    i = 0;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Faild", t);
            }
        });

    }


    public List<Sms> getAllSms(String phoneNum) {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        c = cr.query(message, null, null, null, null);
        this.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                if (phoneNum.equals(c.getString(c.getColumnIndexOrThrow("address")))) {
                    lstSms.add(objSms);
                }
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
//        c.close();

        return lstSms;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!c.isClosed()) {
            c.close();
        }
    }
}
