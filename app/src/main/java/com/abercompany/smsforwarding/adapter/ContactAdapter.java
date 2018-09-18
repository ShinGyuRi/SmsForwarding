package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewContactItemBinding;
import com.abercompany.smsforwarding.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.BindingHolder> {

    private Context context;
    private List<Contact> contact;


    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_contact_item, parent, false);

        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        holder.binding.tvContactName.setText(contact.get(position).getName());
        holder.binding.tvContactNum.setText(contact.get(position).getPhoneNum());

        holder.binding.viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.onClick(v, position);
                }
            }
        });

        holder.binding.viewItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemClick != null) {
                    itemClick.onLongClick(v, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contact.size() == 0) {
            return 0;
        }
        return contact.size();
    }

    public void remove(int index) {
        contact.remove(index);
        notifyDataSetChanged();
    }

    public ContactAdapter(Context context, List<Contact> contact) {
        this.context = context;
        this.contact = contact;
    }

    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewContactItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}

