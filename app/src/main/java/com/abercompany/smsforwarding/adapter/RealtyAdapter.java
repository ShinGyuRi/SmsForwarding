package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewRealtyItemBinding;
import com.abercompany.smsforwarding.model.Realty;

import java.util.List;

public class RealtyAdapter extends RecyclerView.Adapter<RealtyAdapter.BindingHolder>{

    private Context context;
    private List<Realty> realties;

    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public class BindingHolder extends RecyclerView.ViewHolder {
        private ViewRealtyItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public RealtyAdapter(Context context, List<Realty> realties) {
        this.context = context;
        this.realties = realties;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_realty_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        holder.binding.tvRealtyName.setText(realties.get(position).getRealtyName());
        holder.binding.tvRealtyAccount.setText(realties.get(position).getRealtyAccount());
        holder.binding.tvBrokerName.setText(realties.get(position).getRealtyBrokerName());
        holder.binding.tvBrokerPhoneNum.setText(realties.get(position).getRealtyBrokerPhoneNum());

        holder.binding.viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.onClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (realties.size() == 0) {
            return 0;
        }
        return realties.size();
    }
}
