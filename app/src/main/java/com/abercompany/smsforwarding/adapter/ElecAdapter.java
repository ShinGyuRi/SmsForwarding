package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewInputElecItemBinding;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElecAdapter extends RecyclerView.Adapter<ElecAdapter.BindingHolder> {

    private Context context;
    private List<Defaulter> elec;

    private int itemCount = 1;

    public ElecAdapter(Context context, List<Defaulter> elec) {
        this.context = context;
        this.elec = elec;

        if (elec != null) {
            itemCount = elec.size();
        } else {
            itemCount = 0;
        }

        BusProvider.getInstance().register(this);
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_input_elec_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, int position) {
        if (elec != null) {
            holder.binding.etRoomNum.setText(elec.get(position).getRoomNum());
            holder.binding.etElecNum.setText(elec.get(position).getElecNum());
        }

    }

    @Override
    public int getItemCount() {
        return itemCount;
    }



    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewInputElecItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    @Subscribe
    public void Finished(OnClickEvent event) {
        if ("addItem".equals(event.getDep())) {
            itemCount++;
            notifyDataSetChanged();
        }
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        BusProvider.getInstance().unregister(this);
    }

}
