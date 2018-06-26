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
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.SelectedSpinnerEvent;
import com.abercompany.smsforwarding.provider.BusProvider;

import java.util.ArrayList;
import java.util.List;

public class DepositDataAdapter extends RecyclerView.Adapter<DepositDataAdapter.BindingHolder> {

    private Context context;
    private List<Deposit> deposits;
    private List<String> residents;
    private List<String> brokers;
    private List<String> name = new ArrayList<>();
    private List<String> date = new ArrayList<>();
    private List<String> objectName = new ArrayList<>();
    private List<String> type = new ArrayList<>();


    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDepositItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public DepositDataAdapter(Context context, List<Deposit> deposits, List<String> residents, List<String> brokers) {
        this.context = context;
        this.deposits = deposits;
        this.residents = residents;
        this.brokers = brokers;
        for (int i = 0; i < deposits.size(); i++) {
            this.objectName.add("");
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

        name.add(position, deposits.get(position).getName());
        date.add(position, deposits.get(position).getDate());

        holder.binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {
                type.add(position, holder.binding.spCategory.getItemAtPosition(itemPosition).toString());
                switch (itemPosition) {
                    case 0:
                        holder.binding.spToName.setAdapter(null);
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 7:
                        holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residents));
                        break;
                    case 6:
                        holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, brokers));
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
                objectName.add(position, holder.binding.spToName.getItemAtPosition(itemPosition).toString());

                BusProvider.getInstance().post(new SelectedSpinnerEvent(name, date, objectName, type));
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


}
