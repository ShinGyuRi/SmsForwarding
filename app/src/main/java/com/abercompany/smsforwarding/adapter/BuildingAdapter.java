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
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.util.JSLog;

import java.util.ArrayList;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BindingHolder> {

    private Context context;
    private List<Building> buildings = new ArrayList<>();
    private List<Room> rooms;

    private StringBuilder emptyRoomBuilder = new StringBuilder();
    private StringBuilder underContractRoomBuilder = new StringBuilder();

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

    public BuildingAdapter(Context context, List<Building> buildingList, List<Room> rooms) {
        this.context = context;
        this.buildings = buildingList;
        this.rooms = rooms;

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

        for (int i = 0; i < rooms.size(); i++) {
            if (buildings.get(position).getName().equals(rooms.get(i).getBuildingName())) {
                if ("퇴실".equals(rooms.get(i).getActive()) &&
                        holder.binding.tvBuildingName.getText().toString().equals(rooms.get(i).getBuildingName())) {
                    emptyRoomBuilder.append(context.getString(R.string.str_room_info, rooms.get(i).getRoomNum(), rooms.get(i).getPrice()));
                } else if ("계약".equals(rooms.get(i).getActive()) &&
                        holder.binding.tvBuildingName.getText().toString().equals(rooms.get(i).getBuildingName())) {
                    underContractRoomBuilder.append(context.getString(R.string.str_room_info, rooms.get(i).getRoomNum(), rooms.get(i).getPrice()));
                }
            }

        }

        holder.binding.tvEmptyRoomInfo.setText(emptyRoomBuilder.toString());
        holder.binding.tvUnderContractRoom.setText("계약중:    " + underContractRoomBuilder.toString());
        emptyRoomBuilder.setLength(0);
        underContractRoomBuilder.setLength(0);

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
        if (buildings == null || buildings.size() == 0) {
            return 0;
        }
        JSLog.D("buildings size         :::     " + buildings.size(), null);
        return buildings.size();
    }
}
