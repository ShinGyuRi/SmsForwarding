package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ElecAdapter;
import com.abercompany.smsforwarding.databinding.FragmentInputElecBinding;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
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
public class InputElecFragment extends Fragment {

    private FragmentInputElecBinding binding;

    private List<Defaulter> elec;
    private List<Defaulter> filterData = new ArrayList<>();
    private List<Building> buildings;
    private List<String> checkDate = new ArrayList<>();

    private ElecAdapter adapter;

    public InputElecFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public InputElecFragment(List<Defaulter> elec, List<Building> buildings) {
        this.elec = elec;
        this.buildings = buildings;
    }

    public static InputElecFragment newInstance(List<Defaulter> elec, List<Building> buildings) {
        InputElecFragment fragment = new InputElecFragment(elec, buildings);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_input_elec, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_elec, container, false);
        View view = binding.getRoot();
        binding.setInputElec(this);

        setDate(elec);

        return view;
    }

    public void onClick(View view) {
        OnClickEvent event = new OnClickEvent();

        switch (view.getId()) {
            case R.id.btn_add_item:
                event.setDep("addItem");
                BusProvider.getInstance().post(event);
                binding.btnRegister.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_register:

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    String roomNum = ((EditText) binding.rvElec.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.et_room_num)).getText().toString();
                    String elecNum = ((EditText) binding.rvElec.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.et_elec_num)).getText().toString();
                    String checkDate = binding.datePicker.getYear() + "-" + binding.datePicker.getMonth() + "-" + binding.datePicker.getDayOfMonth();

                    insertElec(roomNum, elecNum, checkDate);
                }

                Debug.showToast(getContext(), "등록되었습니다");
                getActivity().finish();
                break;
        }
    }

    private void setElecAdapter(List<Defaulter> elec) {
        adapter = new ElecAdapter(getContext(), elec);
        binding.rvElec.setAdapter(adapter);
        binding.rvElec.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    private void setDate(final List<Defaulter> elec) {
        for (int i = 0; i < elec.size(); i++) {
            if (!checkDate.contains(elec.get(i).getEndDate())) {
                checkDate.add(elec.get(i).getEndDate());
            }
        }
        int firstCheckYear = Integer.parseInt(checkDate.get(0).split("-")[0]);
        int firstCheckMonth = Integer.parseInt(checkDate.get(0).split("-")[1]);
        int firstCheckDays = Integer.parseInt(checkDate.get(0).split("-")[2]);

        for (int j = 0; j < elec.size(); j++) {
            if (checkDate.get(0).equals(elec.get(j).getEndDate())) {
                filterData.add(elec.get(j));
            }
        }
        setElecAdapter(filterData);

        binding.datePicker.init(firstCheckYear, firstCheckMonth - 1, firstCheckDays,
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        JSLog.D("DateChanged                !!!     ", null);

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

                        filterData = new ArrayList<>();
                        for (int i = 0; i < checkDate.size(); i++) {
                            if (String.valueOf(year).equals(checkDate.get(i).split("-")[0]) &&
                                    monthResult.equals(checkDate.get(i).split("-")[1])) {
                                binding.datePicker.updateDate(year, monthOfYear,
                                        Integer.parseInt(checkDate.get(i).split("-")[2]));

                                for (int j = 0; j < elec.size(); j++) {
                                    if (checkDate.get(i).equals(elec.get(j).getEndDate())) {
                                        if (!filterData.contains(elec.get(j))) {
                                            filterData.add(elec.get(j));
                                        }
                                    }
                                }

                            }
                        }
                        if (filterData.size() == 0) {
                            setElecAdapter(null);
                        } else {
                            setElecAdapter(filterData);
                        }
                    }
                });
    }

    private void insertElec(String roomNum, String elecNum, String checkDate) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertElec(roomNum, elecNum, checkDate);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    boolean message = jsonObject.get("message").getAsBoolean();

                    if (message) {
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
