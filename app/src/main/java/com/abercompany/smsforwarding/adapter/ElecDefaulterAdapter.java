package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewElecDefaulterItemBinding;
import com.abercompany.smsforwarding.model.ElecDefaulter;

import java.util.List;

public class ElecDefaulterAdapter extends RecyclerView.Adapter<ElecDefaulterAdapter.BindingHolder> {

    private Context context;
    private List<ElecDefaulter> elecDefaulters;

    public class BindingHolder extends RecyclerView.ViewHolder  {

        private ViewElecDefaulterItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public ElecDefaulterAdapter(Context context, List<ElecDefaulter> elecDefaulters) {
        this.context = context;
        this.elecDefaulters = elecDefaulters;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_elec_defaulter_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        holder.binding.tvElecDefaulter.setText(elecDefaulters.get(position).getRoomNum() +
        elecDefaulters.get(position).getName() +
        elecDefaulters.get(position).getCheckDate());
    }

    @Override
    public int getItemCount() {
        if (elecDefaulters == null) {
            return 0;
        }
        return elecDefaulters.size();
    }
}
