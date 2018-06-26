package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.RegisterNumAdapter;
import com.abercompany.smsforwarding.adapter.SmsRecyclerAdapter;
import com.abercompany.smsforwarding.databinding.FragmentSearchRawDataBinding;
import com.abercompany.smsforwarding.model.Sms;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRawDataFragment extends Fragment {

    private FragmentSearchRawDataBinding binding;

    private String TAG = SearchRawDataFragment.class.getSimpleName();

    private SmsRecyclerAdapter smsRecyclerAdapter;
    private RegisterNumAdapter registerNumAdapter;
    private List<Sms> lst;
    private List<String> nums = new ArrayList<String>();
    private Cursor c;

    public SearchRawDataFragment() {

    }

    @SuppressLint("ValidFragment")
    public SearchRawDataFragment(List<String> nums) {
        // Required empty public constructor
        this.nums = nums;
    }

    public static SearchRawDataFragment newInstance(List<String> nums) {
        SearchRawDataFragment fragment = new SearchRawDataFragment(nums);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search_raw_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_raw_data, container, false);
        View view = binding.getRoot();
        binding.setSearchRawData(this);


//        Log.e(TAG, "Address     :::     " + getAllSms().get(0).getAddress());
//        Log.e(TAG, "ID     :::     " + getAllSms().get(0).getId());
//        Log.e(TAG, "FolderName     :::     " + getAllSms().get(0).getFolderName());
//        Log.e(TAG, "Time     :::     " + getAllSms().get(0).getTime());
//        Log.e(TAG, "State     :::     " + getAllSms().get(0).getReadState());

        binding.btnUpload.setEnabled(false);

        setRegisterNumAdapter(nums);


        return view;

    }

    public void search(View view) {
        String senderNum = binding.etPhoneNum.getText().toString().replace("-", "");
        switch (view.getId()) {
            case R.id.btn_search:
                if (lst != null) {
                    lst.clear();
                }
                lst = getAllSms(senderNum);
                setSmsAdapter(lst);
                binding.btnUpload.setEnabled(true);
                break;

            case R.id.btn_upload:
                uploadSmsBody(senderNum, lst);
                break;

            case R.id.btn_register:
                registerNum(senderNum, DeviceUtil.getDevicePhoneNumber(getContext()));
                registerNumAdapter.add(senderNum);
                registerNumAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void registerNum(String senderNum, String phoneNum) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().registerNum(senderNum, phoneNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean success = jsonObject.get("message").getAsBoolean();
                    if (success) {
                        Toast.makeText(getContext(), "번호가 등록되었습니다", Toast.LENGTH_SHORT).show();
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
        String phoneNum = DeviceUtil.getDevicePhoneNumber(getContext());
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

    private void setSmsAdapter(List<Sms> lst) {
        smsRecyclerAdapter = new SmsRecyclerAdapter(getContext(), lst);
        binding.rvSms.setAdapter(smsRecyclerAdapter);
        binding.rvSms.setLayoutManager(new LinearLayoutManager(getContext()));
        smsRecyclerAdapter.notifyDataSetChanged();
    }

    private void setRegisterNumAdapter(final List<String> numbers) {
        registerNumAdapter = new RegisterNumAdapter(getContext(), numbers);
        binding.rvRegisterNum.setAdapter(registerNumAdapter);
        binding.rvRegisterNum.setLayoutManager(new LinearLayoutManager(getContext()));
        registerNumAdapter.notifyDataSetChanged();
        registerNumAdapter.setItemClick(new RegisterNumAdapter.ItemClick() {
            @Override
            public void onClick(View view, final int position) {
                showSimplSelect2Dialog(getContext(), "등록된 번호를 삭제하시겠습니까?", "예", "아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestDeleteNum(numbers.get(position), position);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }
        });
    }

    private void requestDeleteNum(String senderNum, final int position) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().deleteNum(senderNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();
                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();
                    if (message) {
                        registerNumAdapter.remove(position);
                        registerNumAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void showSimplSelect2Dialog(Context context, String message, String yesBtn, String noBtn, DialogInterface.OnClickListener yesBtnListener, DialogInterface.OnClickListener noBtnListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(yesBtn,yesBtnListener);
        alertBuilder.setNegativeButton(noBtn,noBtnListener);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    public List<Sms> getAllSms(String phoneNum) {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContext().getContentResolver();

        c = cr.query(message, null, null, null, null);
        getActivity().startManagingCursor(c);
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
        c.close();

        return lstSms;
    }


}
