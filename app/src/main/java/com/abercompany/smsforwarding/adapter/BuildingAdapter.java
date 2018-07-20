package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewBuildingItemBinding;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.util.JSLog;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BindingHolder> {

    private Context context;
    private List<Building> buildings;

    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewBuildingItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public BuildingAdapter(Context context, List<Building> buildingList) {
        this.context = context;
        this.buildings = buildingList;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_building_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        holder.binding.tvBuildingName.setText(buildings.get(position).getName());
        holder.binding.tvCountEmptyRoom.setText("" + (buildings.get(position).getCountTotalRoom() - buildings.get(position).getCountUseRoom()));

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
        if (buildings.size() == 0) {
            return 0;
        }
        JSLog.D("buildings size         :::     " + buildings.size(), null);
        return buildings.size();
    }
}
