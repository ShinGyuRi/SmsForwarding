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
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Deposit;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.BindingHolder> {

    private Context context;
    private List<Deposit> trimmedData;
    private List<String> roomNums = new ArrayList<>();
    private List<String> names = new ArrayList<>();

    private List<Contract> contracts;


    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewReportItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public ReportAdapter(Context context, List<Deposit> trimmedData, List<Contract> contracts) {
        this.context = context;
        this.trimmedData = trimmedData;
        this.contracts = contracts;

        for (int i = 0; i < trimmedData.size(); i++) {
            String dstName = trimmedData.get(i).getDestinationName();
            String name = "";
            String roomNum = "";

            if (!"".equals(dstName)) {
                name = dstName.substring(0, dstName.indexOf("("));
                roomNum = dstName.substring(dstName.indexOf("(") + 1, dstName.indexOf(")"));
            }

            roomNums.add(roomNum);
            names.add(name);
        }
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
        holder.binding.tvRoomNum.setText(roomNums.get(position));
        holder.binding.tvName.setText(names.get(position));
        holder.binding.tvType.setText(trimmedData.get(position).getType());
        holder.binding.tvDate.setText(trimmedData.get(position).getDate().replace("[KB]", ""));
        holder.binding.tvAmount.setText(trimmedData.get(position).getAmount());

        for (int i = 0; i < contracts.size(); i++) {
            if (roomNums.get(position).equals(contracts.get(i).getRoomNum()) &&
                    names.get(position).equals(contracts.get(i).getName()) &&
                    trimmedData.get(position).getType().contains("월세")) {
                holder.binding.tvManageFee.setText(contracts.get(i).getManageFee());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (trimmedData.size() == 0) {
            return 0;
        }
        return trimmedData.size();
    }
}
