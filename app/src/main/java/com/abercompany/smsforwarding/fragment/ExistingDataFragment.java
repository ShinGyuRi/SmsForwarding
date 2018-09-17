package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.AddCashActivity;
import com.abercompany.smsforwarding.adapter.DepositDataAdapter;
import com.abercompany.smsforwarding.databinding.FragmentExistingDataBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.model.SelectedSpinnerEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;
import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.NEW_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExistingDataFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FragmentExistingDataBinding binding;
    private List<Deposit> existingDatas;
    private List<Broker> brokers;
    private List<Resident> residents;
    private List<Room> rooms;
    private List<Building> buildings;
    private List<Deposit> searchDatas;
    private DepositDataAdapter adapter;


    private String startDate = "";
    private String endDate = "";

    public ExistingDataFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ExistingDataFragment(List<Deposit> existingDatas, List<Resident> residents, List<Broker> brokers, List<Room> rooms, List<Building> buildings) {
        this.existingDatas = existingDatas;
        this.residents = residents;
        this.brokers = brokers;
        this.rooms = rooms;
        this.buildings = buildings;
    }

    public static ExistingDataFragment newInstance(List<Deposit> existingDatas, List<Resident> residents, List<Broker> brokers, List<Room> rooms, List<Building> buildings) {
        ExistingDataFragment fragment = new ExistingDataFragment(existingDatas, residents, brokers, rooms, buildings);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_existing_data, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_existing_data, container, false);
        View view = binding.getRoot();
        binding.setExistingData(this);

        setDepositAdapter(existingDatas, residents, brokers);

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_cash:
                Intent intent = new Intent(getContext(), AddCashActivity.class);
                intent.putExtra("resident", (Serializable) residents);
                intent.putExtra("broker", (Serializable) brokers);
                intent.putExtra("dataType", EXISTING_DATA);
                intent.putExtra("building", (Serializable) buildings);
                startActivity(intent);
                break;

            case R.id.btn_upload:
                BusProvider.getInstance().post(new OnClickEvent());
                break;

            case R.id.tv_search_date:
                searchDatas = new ArrayList<>();

                Calendar startDate = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle("시작일");
                dpd.show(getActivity().getFragmentManager(), "startDate");
                break;

            case R.id.btn_search:
                if (!"".equals(this.startDate) &&
                        !"".equals(this.endDate)) {
                }
                searchData(existingDatas, this.startDate, this.endDate);
                break;
        }
    }

    private void setDepositAdapter(List<Deposit> existingDatas ,List<Resident> residents, List<Broker> brokers) {
        adapter = new DepositDataAdapter(getActivity(), getContext(), existingDatas, residents, brokers, EXISTING_DATA, rooms, buildings);
        binding.rvDeposit.setAdapter(adapter);
        binding.rvDeposit.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    private void searchData(List<Deposit> existingDatas, String startDate, String endDate) {
        DateFormat dateFormat1 = new SimpleDateFormat("MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd kk:mm");
        Date startDt = null;
        Date endDt = null;
        Date compareDt = null;
        Calendar calendar = null;

        for (int i = 0; i < existingDatas.size(); i++) {
            String date = existingDatas.get(i).getDate().replace("[KB]", "");
            JSLog.D("date               :::     " + date, null);
            String dateMonth = date.split("/")[0];
            String dateDay = date.split("/")[1];

            try {

                startDt = dateFormat1.parse(startDate);
                endDt = dateFormat1.parse(endDate);

                calendar = Calendar.getInstance();
                calendar.setTime(endDt);
                calendar.add(Calendar.DATE, 1);

                compareDt = dateFormat2.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (startDt.before(compareDt) &&
                    calendar.getTime().after(compareDt)) {
                searchDatas.add(existingDatas.get(i));
            }


        }

        setDepositAdapter(searchDatas, residents, brokers);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
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

        if ("startDate".equals(view.getTag())) {
            startDate = monthResult + "-" + dayResult;


            Calendar endDate = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setTitle("끝일");
            dpd.show(getActivity().getFragmentManager(), "endDate");
        } else if ("endDate".equals(view.getTag())) {
            endDate = monthResult + "-" + dayResult;

            binding.tvSearchDate.setText(getString(R.string.str_period, startDate, endDate));
        }
    }
}
