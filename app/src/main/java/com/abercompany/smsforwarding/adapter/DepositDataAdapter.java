package com.abercompany.smsforwarding.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewDepositItemBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.SelectedSpinnerEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.JSLog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DepositDataAdapter extends RecyclerView.Adapter<DepositDataAdapter.BindingHolder> implements DatePickerDialog.OnDateSetListener {

    private Activity activity;
    private Context context;
    private List<Deposit> deposits;
    private List<Resident> residents;
    private List<Broker> brokers;
    private List<String> name = new ArrayList<>();
    private List<String> date = new ArrayList<>();
    private List<String> objectName = new ArrayList<>();
    private List<String> type = new ArrayList<>();
    private List<String> startDate = new ArrayList<>();
    private List<String> endDate = new ArrayList<>();

    private List<String> residentName = new ArrayList<>();
    private List<String> brokerName = new ArrayList<>();

    private List<Integer> positions = new ArrayList<>();
    private DatePickerDialog startDpd, endDpd;


    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDepositItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.setDepositItem(this);
        }
    }

    public DepositDataAdapter(Activity activity, Context context, List<Deposit> deposits, List<Resident> residents, List<Broker> brokers) {
        this.activity = activity;
        this.context = context;
        this.deposits = deposits;
        this.residents = residents;
        this.brokers = brokers;


        for (int i = 0; i < residents.size(); i++) {
            residentName.add(context.getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
        }
        for (int i = 0; i < brokers.size(); i++) {
            brokerName.add(context.getString(R.string.str_deposit_realty, brokers.get(i).getName(), brokers.get(i).getRealtyName()));
        }
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_deposit_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, final int position) {
        holder.binding.tvMessage.setText(context.getString(R.string.str_deposit_message,
                deposits.get(position).getName(),
                deposits.get(position).getAmount(),
                deposits.get(position).getDate(),
                deposits.get(position).getMethod()));

        holder.binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {
                JSLog.D("View.getID()       ::      " + view.getId(), null);
                JSLog.D("View.getID()       ::      " + holder.binding.spCategory, null);
                JSLog.D("spCategory position        :::     " + position, null);
                type.add(holder.binding.spCategory.getItemAtPosition(itemPosition).toString());
                switch (itemPosition) {
                    case 0:
                        holder.binding.spToName.setAdapter(null);
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residentName));
                        positions.add(position);
                        JSLog.D("onItemSelected position            :::     " + position, null);
                        break;
                    case 8:
                        holder.binding.spToName.setAdapter(null);
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.binding.spToName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {
                JSLog.D("spToName position        :::     " + position, null);

                if (!checkSameValue(name, residents.get(itemPosition).getName()) && !checkSameValue(date, deposits.get(position).getDate()) &&
                        !checkSameValue(objectName, holder.binding.spToName.getItemAtPosition(itemPosition).toString())) {
                    objectName.add(holder.binding.spToName.getItemAtPosition(itemPosition).toString());

                    JSLog.D("resident.getName()         :::" + residents.get(itemPosition).getName(), null);
                    name.add(residents.get(itemPosition).getName());
                    date.add(deposits.get(position).getDate());
                }

                BusProvider.getInstance().post(new SelectedSpinnerEvent(name, date, objectName, type, startDate, endDate, positions));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.binding.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                startDpd = DatePickerDialog.newInstance(
                        DepositDataAdapter.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                startDpd.setTitle("Start Date");
                startDpd.show(activity.getFragmentManager(), "startDate" + position);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (deposits.size() == 0) {
            return 0;
        }
        return deposits.size();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag().contains("startDate")) {
            int position = Integer.parseInt(view.getTag().replace("startDate", ""));
            JSLog.D("onDateSet position         :::     " + position, null);
            startDate.add(position, year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

            Calendar now = Calendar.getInstance();
            endDpd = DatePickerDialog.newInstance(
                    DepositDataAdapter.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            endDpd.setTitle("End Date");
            endDpd.show(activity.getFragmentManager(), "endDate" + position);
        } else if (view.getTag().contains("endDate")) {
            int position = Integer.parseInt(view.getTag().replace("endDate", ""));
            JSLog.D("onDateSet position         :::     " + position, null);
            endDate.add(position, year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            BusProvider.getInstance().post(new SelectedSpinnerEvent(name, date, objectName, type, startDate, endDate, positions));
        }
    }

    private boolean checkSameValue(List<String> lst, String compare) {
        for (int i = 0; i < lst.size(); i++) {
            if (compare.equals(lst.get(i))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void remove(int position, int i) {
        deposits.remove(position);
        name.remove(i);
        date.remove(i);
        objectName.remove(i);
        type.remove(i);
//        name.clear();
//        date.clear();
//        objectName.clear();
//        type.clear();
//        positions.clear();

        notifyDataSetChanged();
    }


}
