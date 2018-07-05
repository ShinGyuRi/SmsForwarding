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
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;
import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.NEW_DATA;

public class DepositDataAdapter extends RecyclerView.Adapter<DepositDataAdapter.BindingHolder> {

    private Activity activity;
    private Context context;
    private List<Deposit> deposits;
    private List<Resident> residents;
    private List<Broker> brokers;

    private List<String> residentName = new ArrayList<>();
    private List<String> brokerName = new ArrayList<>();


    private boolean selectedFlag = false;
    private String type = "";


    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDepositItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.setDepositItem(this);
        }
    }

    public DepositDataAdapter(Activity activity, Context context, List<Deposit> deposits, List<Resident> residents, List<Broker> brokers, String type) {
        this.activity = activity;
        this.context = context;
        this.deposits = deposits;
        this.residents = residents;
        this.brokers = brokers;
        this.type = type;


        for (int i = 1; i < residents.size(); i++) {
            if (i == 0) {
                residentName.add("");
            }
            residentName.add(context.getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
        }
        for (int i = 0; i < brokers.size(); i++) {
            if (i == 0) {
                brokerName.add("");
            }
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

        if (EXISTING_DATA.equals(type)) {
            JSLog.D("getSelectedTypePosition        :::     " + getSelectedTypePosition(position), null);

            holder.binding.spCategory.setSelection(getSelectedTypePosition(position));
            if ("출금-수수료".equals(holder.binding.spCategory.getSelectedItem().toString())) {
                JSLog.D("getSelectedPosition        :::     " + getSelectedPosition(position, brokerName), null);
                holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, brokerName));
                holder.binding.spToName.setSelection(getSelectedPosition(position, brokerName));
            } else {
                JSLog.D("getSelectedPosition        :::     " + getSelectedPosition(position, residentName), null);
                holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residentName));
                holder.binding.spToName.setSelection(getSelectedPosition(position, residentName));
            }
        }
        holder.binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {

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
                        if (NEW_DATA.equals(type)) {
                            holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residentName));
                        }
                        selectedFlag = false;
                        break;
                    case 7:
                        if (NEW_DATA.equals(type)) {
                            holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, brokerName));
                        }
                        selectedFlag = false;
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
                JSLog.D("selectedFlag           :::     " + selectedFlag, null);
                if (selectedFlag) {
                    updateTrimmedData(deposits.get(position).getName(), deposits.get(position).getDate(), holder.binding.spToName.getSelectedItem().toString(), holder.binding.spCategory.getSelectedItem().toString(), position);
                }

                selectedFlag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    private void updateTrimmedData(String name, String date, String objectName, final String type, final int position) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().updateTrimmedData(name, date, objectName, type);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Debug.showToast(context, "등록되었습니다");
                    JSLog.D("type           :::     " + DepositDataAdapter.this.type, null);
                    if (NEW_DATA.equals(DepositDataAdapter.this.type)) {
                        deposits.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private int getSelectedPosition(int position, List<String> compareList) {
        for (int j = 0; j < compareList.size(); j++) {

            if ((deposits.get(position).getDestinationName()).equals(compareList.get(j))) {
                return j;
            }
        }
        return 0;
    }

    private int getSelectedTypePosition(int position) {
        for (int j = 0; j < residents.size(); j++) {

            if ((deposits.get(position).getType()).equals(context.getResources().getStringArray(R.array.category)[j])) {
                return j;
            }
        }
        return 0;
    }

}
