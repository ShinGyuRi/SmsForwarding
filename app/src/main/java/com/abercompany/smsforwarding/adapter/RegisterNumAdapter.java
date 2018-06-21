package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewRegisterNumItemBinding;

import java.util.ArrayList;
import java.util.List;

public class RegisterNumAdapter extends RecyclerView.Adapter<RegisterNumAdapter.BindingHolder> {

    private Context context;
    private List<String> numbers = new ArrayList<>();

    private ItemClick itemClick;

    public interface ItemClick  {
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public class BindingHolder extends RecyclerView.ViewHolder{

        private ViewRegisterNumItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public RegisterNumAdapter(Context context, List<String> numbers) {
        this.context = context;
        this.numbers = numbers;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_register_num_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        holder.binding.tvRegisterNum.setText(numbers.get(position));

        holder.binding.viewItem.setOnClickListener(new View.OnClickListener()   {
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
        if (numbers == null) {
            return 0;
        }
        return numbers.size();
    }

    public void remove(int position) {
        numbers.remove(position);
    }

    public void add(String addItem) {
        numbers.add(addItem);
    }


}
