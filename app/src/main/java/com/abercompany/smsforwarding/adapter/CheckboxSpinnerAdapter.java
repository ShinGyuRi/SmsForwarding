package com.abercompany.smsforwarding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.CheckboxSpinnerItemBinding;
import com.abercompany.smsforwarding.databinding.ViewDepositItemBinding;
import com.abercompany.smsforwarding.model.StateVO;
import com.abercompany.smsforwarding.util.JSLog;

import java.util.ArrayList;
import java.util.List;

public class CheckboxSpinnerAdapter extends ArrayAdapter<StateVO> {

    private Context mContext;
    private ArrayList<StateVO> listState;
    private CheckboxSpinnerAdapter myAdapter;
    private boolean isFromView = false;
    private ViewHolder holder;
    private ViewDepositItemBinding depositItemBinding;

    public CheckboxSpinnerAdapter(Context context, int resource, List<StateVO> objects, ViewDepositItemBinding depositItemBinding) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<StateVO>) objects;
        this.myAdapter = this;
        this.depositItemBinding = depositItemBinding;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        holder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        holder.binding = DataBindingUtil.inflate(inflater,
                R.layout.checkbox_spinner_item, null, false);


        holder.binding.tvRoomNum.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.binding.cbRoomNum.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.binding.cbRoomNum.setVisibility(View.INVISIBLE);
        } else {
            holder.binding.cbRoomNum.setVisibility(View.VISIBLE);
        }
        holder.binding.cbRoomNum.setTag(position);
        holder.binding.cbRoomNum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);

                    if (depositItemBinding.spCategory.getSelectedItem().toString().contains("수수료")) {
                        JSLog.D("chRoomNum checked changed          !!!     ", null);
                        depositItemBinding.spToName.setSelection(0);
                    }
                }
            }
        });
        return holder.binding.getRoot();
    }

    public class ViewHolder {
        public CheckboxSpinnerItemBinding binding;
    }

    public ArrayList<StateVO> getListState() {
        return listState;
    }

    public ViewHolder getHolder() {
        return holder;
    }
}
