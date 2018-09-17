package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewRoomNumItemBinding;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.BindingHolder> {

    private Context context;
    private List<Room> rooms;
    private List<Contract> contracts;
    private List<Defaulter> defaulters;
    private String buildingName = "";

    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public class BindingHolder extends RecyclerView.ViewHolder {
        private ViewRoomNumItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    public RoomAdapter(Context context, List<Room> rooms, List<Contract> contracts, List<Defaulter> defaulters, String buildingName) {
        this.context = context;
        this.rooms = rooms;
        this.contracts = contracts;
        this.defaulters = defaulters;
        this.buildingName = buildingName;
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_room_num_item, parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingHolder holder, final int position) {
        holder.binding.tvRoomNum.setText(rooms.get(position).getRoomNum());
        holder.binding.tvStatus.setVisibility(View.INVISIBLE);
        holder.binding.tvName.setVisibility(View.INVISIBLE);
        holder.binding.tvCharges.setVisibility(View.INVISIBLE);
        holder.binding.tvLeaveDay.setVisibility(View.INVISIBLE);
        holder.binding.tvTerm.setVisibility(View.INVISIBLE);

        for (int j = 0; j < contracts.size(); j++) {
            if (rooms.get(position).getRoomNum().equals(contracts.get(j).getRoomNum())) {
                if ("재실".equals(contracts.get(j).getActive())) {
                    holder.binding.tvStatus.setVisibility(View.VISIBLE);
                    holder.binding.tvName.setVisibility(View.VISIBLE);
                    holder.binding.tvCharges.setVisibility(View.VISIBLE);
                    holder.binding.tvLeaveDay.setVisibility(View.VISIBLE);
                }

                if (!"퇴실".equals(contracts.get(j).getActive())) {
                    holder.binding.tvTerm.setVisibility(View.VISIBLE);
                    holder.binding.tvTerm.setText(contracts.get(j).getTerm() + "개월");
                    if ("재실".equals(contracts.get(j).getActive())) {

                        holder.binding.tvStatus.setText(contracts.get(j).getActive());
                        holder.binding.tvName.setText(contracts.get(j).getName());
                        holder.binding.tvCharges.setText(context.getString(R.string.str_charges,
                                contracts.get(j).getDeposit().replace("0000", ""),
                                contracts.get(j).getRent().replace("0000", ""),
                                contracts.get(j).getManageFee().replace("0000", "")));
                        holder.binding.tvLeaveDay.setText(context.getString(R.string.str_leave_info2, contracts.get(j).getEndDate()));
                    } else if ("계약".equals(contracts.get(j).getActive())) {
                        holder.binding.tvStatus.setText(contracts.get(j).getActive());
                        holder.binding.tvName.setText(contracts.get(j).getName());
                        holder.binding.tvCharges.setText(contracts.get(j).getDownPayment());
                        holder.binding.tvStatus.setVisibility(View.VISIBLE);
                        holder.binding.tvName.setVisibility(View.VISIBLE);
                        holder.binding.tvCharges.setVisibility(View.VISIBLE);
                    }
                }



            }
        }




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
        if (rooms.size() == 0) {
            return 0;
        }
        return rooms.size();
    }
}
