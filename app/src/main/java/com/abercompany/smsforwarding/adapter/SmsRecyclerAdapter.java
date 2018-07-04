package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.Sms;
import com.abercompany.smsforwarding.databinding.ViewSmsItemBinding;

import java.util.ArrayList;
import java.util.List;

public class SmsRecyclerAdapter extends RecyclerView.Adapter<SmsRecyclerAdapter.BindingHolder> {

    private Context context;
    private List<Defaulter> defaulters = new ArrayList<>();

    public class BindingHolder extends RecyclerView.ViewHolder  {

        private ViewSmsItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public SmsRecyclerAdapter(Context context, List<Defaulter> defaulters) {
        this.context = context;
        this.defaulters = defaulters;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_sms_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        holder.binding.tvSmsBody.setText(defaulters.get(position).getDstName());
    }

    @Override
    public int getItemCount() {
        if (defaulters == null) {
            return 0;
        }
        return defaulters.size();
    }
}
