package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewDepositLogItemBinding;
import com.abercompany.smsforwarding.model.DepositLog;

import java.util.List;

public class DepositLogAdapter extends RecyclerView.Adapter<DepositLogAdapter.BindingHolder> {

    private Context context;
    private List<DepositLog> depositLogs;

    public class BindingHolder extends RecyclerView.ViewHolder  {

        private ViewDepositLogItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public DepositLogAdapter(Context context, List<DepositLog> depositLogs) {
        this.context = context;
        this.depositLogs = depositLogs;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_deposit_log_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        holder.binding.tvPeriod.setText(context.getString(R.string.str_period, depositLogs.get(position).getStartDate(), depositLogs.get(position).getEndDate()));
        holder.binding.tvDepositDate.setText(depositLogs.get(position).getDepositDate());
        holder.binding.tvDepositAmount.setText(depositLogs.get(position).getAmount());

    }

    @Override
    public int getItemCount() {
        if (depositLogs.size() == 0) {
            return 0;
        }
        return depositLogs.size();
    }


}
