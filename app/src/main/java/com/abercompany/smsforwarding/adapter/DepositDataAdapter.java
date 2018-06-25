package com.abercompany.smsforwarding.adapter;

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
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.UPDATE_TRIM_DATA.RESIDENT;

public class DepositDataAdapter extends RecyclerView.Adapter<DepositDataAdapter.BindingHolder> {

    private Context context;
    private List<Deposit> deposits;
    private List<String> residents;
    private String name, date, objectName;
    private String type;

    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDepositItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public DepositDataAdapter(Context context, List<Deposit> deposits, List<String> residents, String type) {
        this.context = context;
        this.deposits = deposits;
        this.residents = residents;
        this.type = type;
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
        holder.binding.btnUpload.setEnabled(false);
        holder.binding.tvMessage.setText(context.getString(R.string.str_deposit_message,
                deposits.get(position).getName(),
                deposits.get(position).getAmount(),
                deposits.get(position).getDate(),
                deposits.get(position).getMethod()));

//        holder.binding.spBroker.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, brokers));
        holder.binding.spHo.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residents));

        holder.binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = deposits.get(position).getName();
                date = deposits.get(position).getDate();

                updateTrimmedData(name, date, objectName, type);
            }
        });

        holder.binding.spHo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                objectName = holder.binding.spHo.getItemAtPosition(position).toString();
                holder.binding.btnUpload.setEnabled(true);
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


    private void updateTrimmedData(String name, String date, String objectName, String type) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().updateTrimmedData(name, date, objectName, type);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Debug.showToast(context, "등록되었습니다");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
