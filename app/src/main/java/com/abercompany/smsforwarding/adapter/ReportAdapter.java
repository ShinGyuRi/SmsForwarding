package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewReportItemBinding;
import com.abercompany.smsforwarding.model.Deposit;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.BindingHolder> {

    private Context context;
    private List<Deposit> trimmedData;


    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewReportItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public ReportAdapter(Context context, List<Deposit> trimmedData) {
        this.context = context;
        this.trimmedData = trimmedData;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_report_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        holder.binding.tvReportMessage.setText(trimmedData.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        if (trimmedData.size() == 0) {
            return 0;
        }
        return trimmedData.size();
    }
}
