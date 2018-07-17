package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewLeaveRoomItemBinding;
import com.abercompany.smsforwarding.model.Contract;

import java.util.List;

public class LeaveRoomAdapter extends RecyclerView.Adapter<LeaveRoomAdapter.BindingHolder> {

    private Context context;
    private List<Contract> contracts;

    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewLeaveRoomItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public LeaveRoomAdapter(Context context, List<Contract> contracts) {
        this.context = context;
        this.contracts = contracts;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_leave_room_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        holder.binding.tvRoomNum.setText(contracts.get(position).getRoomNum());
        holder.binding.tvName.setText(contracts.get(position).getName());
        holder.binding.tvStartDate.setText(contracts.get(position).getStartDate());
        holder.binding.tvEndDate.setText(contracts.get(position).getEndDate());
    }

    @Override
    public int getItemCount() {
        if (contracts.size() == 0) {
            return 0;
        }
        return contracts.size();
    }
}
