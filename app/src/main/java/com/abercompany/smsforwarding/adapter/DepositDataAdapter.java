package com.abercompany.smsforwarding.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ViewDepositItemBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.model.SpinnerItem;
import com.abercompany.smsforwarding.model.StateVO;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;
import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.NEW_DATA;

public class DepositDataAdapter extends RecyclerView.Adapter<DepositDataAdapter.BindingHolder> {

    private Activity activity;
    private Context context;
    private List<Deposit> deposits;
    private List<Resident> residents;
    private List<Broker> brokers;
    private List<Deposit> editDeposits = new ArrayList<>();
    private List<Room> rooms;
    private List<Room> checkOutRooms = new ArrayList<>();
    private List<Building> buildings;

    private List<String> residentName = new ArrayList<>();
    private List<String> brokerName = new ArrayList<>();
    private List<String> buildingName = new ArrayList<>();

    private ArrayList<StateVO> listVOs = new ArrayList<>();

    private CheckboxSpinnerAdapter checkboxSpinnerAdapter;

    private boolean selectedFlag = false;
    private String type = "";
    private StringBuilder sumNote = new StringBuilder();

    private BindingHolder bindingHolder;


    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }


    public class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDepositItemBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.setDepositItem(this);
        }

        public ViewDepositItemBinding getBinding() {
            return binding;
        }
    }

    public DepositDataAdapter(Activity activity, Context context, List<Deposit> deposits, List<Resident> residents, List<Broker> brokers, String type, List<Room> rooms, List<Building> buildings) {
        this.activity = activity;
        this.context = context;
        this.deposits = deposits;
        this.residents = residents;
        this.brokers = brokers;
        this.type = type;
        this.rooms = rooms;
        this.buildings = buildings;

        residentName.add("입주자");
        brokerName.add("중개인");
        buildingName.add("건물");

        for (int i=0; i<rooms.size(); i++) {
            if ("퇴실".equals(rooms.get(i).getActive())) {
                checkOutRooms.add(rooms.get(i));
            }
        }
        for (int i = 0; i < checkOutRooms.size(); i++) {
            Resident resident = new Resident();
            resident.setHo(checkOutRooms.get(i).getRoomNum());
            resident.setName(checkOutRooms.get(i).getBuildingName());
            residents.add(resident);
        }

        for (int i = 0; i < brokers.size(); i++) {
            brokerName.add(context.getString(R.string.str_deposit_realty, brokers.get(i).getName(), brokers.get(i).getRealtyName()));
        }
        for (int i = 0; i < buildings.size(); i++) {
            buildingName.add(buildings.get(i).getName());
        }



        BusProvider.getInstance().register(this);
    }

    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_deposit_item, parent, false);

        bindingHolder = new BindingHolder(view);


        return bindingHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, final int position) {
//        holder.setIsRecyclable(false);

        if (deposits.get(position).getMethod().contains("입금")) {
            holder.binding.spCategory.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.deposit)));
        } else if (deposits.get(position).getMethod().contains("출금") ||
                deposits.get(position).getMethod().contains("공동CMS출")) {
            holder.binding.spCategory.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.withdraw)));
        }

        holder.binding.spBuildingName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, buildingName));

        holder.binding.tvMessage.setText(context.getString(R.string.str_deposit_message,
                deposits.get(position).getName(),
                deposits.get(position).getAmount(),
                deposits.get(position).getDate(),
                deposits.get(position).getMethod()));

        holder.binding.spBuildingName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                residentName.clear();
                residentName.add("입주자");
                holder.binding.spToName.setSelection(0);
                for (int i = 0; i < residents.size(); i++) {
                    if (holder.binding.spBuildingName.getSelectedItem().toString().equals(residents.get(i).getBuildingName())) {
                        if (!residentName.contains(context.getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()))) {
                            residentName.add(context.getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (EXISTING_DATA.equals(type)) {
            JSLog.D("getSelectedTypePosition        :::     " + getSelectedTypePosition(position), null);
            holder.binding.spBuildingName.setSelection(getSelectedBuildingPosition(position, buildingName));

            holder.binding.spCategory.setSelection(getSelectedTypePosition(position));
            if ("출금-수수료".equals(holder.binding.spCategory.getSelectedItem().toString())) {
                JSLog.D("broker getSelectedPosition        :::     " + getSelectedPosition(position, brokerName), null);
                holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, brokerName));
                holder.binding.spToName.setSelection(getSelectedPosition(position, brokerName));
            } else {
                JSLog.D("resident getSelectedPosition        :::     " + getSelectedPosition(position, residentName), null);

                holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residentName));
                holder.binding.spToName.setSelection(getSelectedPosition(position, residentName));
            }


        }

        if (holder.binding.spCategory.getTag() != null) {
            holder.binding.spCategory.setSelection((Integer) holder.binding.spCategory.getTag());
        }

        holder.binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {

                holder.binding.spCategory.setTag(itemPosition);


                    if (deposits.get(position).getMethod().contains("입금")) {
                        switch (itemPosition) {
                            case 0:
                                for (int i = 0; i < editDeposits.size(); i++) {
                                    if (deposits.get(position).getIndex().equals(editDeposits.get(i).getIndex())) {
                                        JSLog.D("remove editDeposits position               :::         " + i, null);
                                        editDeposits.remove(i);
                                    }
                                }
                            case 7:
                            case 8:
                                holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item));
                                if (!"".equals(holder.binding.spCategory.getSelectedItem().toString()) && holder.binding.spCategory.getSelectedItemPosition() != 0) {
                                    Deposit deposit = new Deposit();
                                    deposit.setName(deposits.get(position).getName());
                                    deposit.setDate(deposits.get(position).getDate());
                                    deposit.setDestinationName("");
                                    deposit.setType(holder.binding.spCategory.getSelectedItem().toString());
                                    deposit.setIndex(deposits.get(position).getIndex());
                                    deposit.setViewPosition(position);
                                    deposit.setBuildingName(holder.binding.spBuildingName.getSelectedItem().toString());

                                    for (int i = 0; i < editDeposits.size(); i++) {
                                        if (deposits.get(position).getIndex().equals(editDeposits.get(i).getIndex())) {
                                            JSLog.D("remove editDeposits position               :::         " + i, null);
                                            editDeposits.remove(i);
                                        }
                                    }
                                    JSLog.D("add editDeposits position              :::         " + deposit.getViewPosition(), null);
                                    editDeposits.add(deposit);
                                }
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                if (NEW_DATA.equals(type)) {
                                    JSLog.D("residentName size              :::     " + residentName.size(), null);
                                    holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residentName));
                                }
                                selectedFlag = false;
                                break;
                        }
                    } else if (deposits.get(position).getMethod().contains("출금") ||
                            deposits.get(position).getMethod().contains("공동CMS출")) {
                        switch (itemPosition) {
                            case 0:
                                for (int i = 0; i < editDeposits.size(); i++) {
                                    if (deposits.get(position).getIndex().equals(editDeposits.get(i).getIndex())) {
                                        JSLog.D("remove editDeposits position               :::         " + i, null);
                                        editDeposits.remove(i);
                                    }
                                }
                            case 3:
                            case 4:
                            case 5:
                                holder.binding.spExtraToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item));
                                holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item));
                                if (!"".equals(holder.binding.spCategory.getSelectedItem().toString()) && holder.binding.spCategory.getSelectedItemPosition() != 0) {
                                    Deposit deposit = new Deposit();
                                    deposit.setName(deposits.get(position).getName());
                                    deposit.setDate(deposits.get(position).getDate());
                                    deposit.setDestinationName("");
                                    deposit.setType(holder.binding.spCategory.getSelectedItem().toString());
                                    deposit.setIndex(deposits.get(position).getIndex());
                                    deposit.setViewPosition(position);
                                    deposit.setBuildingName(holder.binding.spBuildingName.getSelectedItem().toString());

                                    for (int i = 0; i < editDeposits.size(); i++) {
                                        if (deposits.get(position).getIndex().equals(editDeposits.get(i).getIndex())) {
                                            JSLog.D("remove editDeposits position               :::         " + i, null);
                                            editDeposits.remove(i);
                                        }
                                    }
                                    JSLog.D("add editDeposits position              :::         " + deposit.getViewPosition(), null);
                                    editDeposits.add(deposit);
                                }
                                break;
                            case 2:
                                if (NEW_DATA.equals(type)) {
                                    JSLog.D("residentName size              :::     " + residentName.size(), null);
                                    holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, residentName));
                                }
                                selectedFlag = false;
                                break;
                            case 1:
                                if (NEW_DATA.equals(type)) {
                                    holder.binding.spToName.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, brokerName));

                                    for (int i = 0; i < residentName.size(); i++) {
                                        StateVO stateVO = new StateVO();
                                        stateVO.setTitle(residentName.get(i));
                                        stateVO.setSelected(false);
                                        listVOs.add(stateVO);
                                    }

                                    checkboxSpinnerAdapter = new CheckboxSpinnerAdapter(context, 0, listVOs, holder.binding);
                                    holder.binding.spExtraToName.setAdapter(checkboxSpinnerAdapter);
                                }
                                selectedFlag = false;
                                break;
                        }
                    }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.binding.spToName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPosition, long id) {
                if (selectedFlag && itemPosition != 0) {
                    Deposit deposit = new Deposit();
                    deposit.setName(deposits.get(position).getName());
                    deposit.setDate(deposits.get(position).getDate());
                    deposit.setDestinationName(holder.binding.spToName.getSelectedItem().toString());
                    deposit.setType(holder.binding.spCategory.getSelectedItem().toString());
                    deposit.setIndex(deposits.get(position).getIndex());
                    for (int i = 0; i < residentName.size(); i++) {
                        if (holder.binding.spCategory.getSelectedItem().toString().contains("수수료") &&
                                checkboxSpinnerAdapter.getListState().get(i).isSelected()) {
                            sumNote.append(checkboxSpinnerAdapter.getListState().get(i).getTitle());
                        }
                    }
                    deposit.setNote(sumNote.toString());
                    deposit.setViewPosition(position);
                    deposit.setBuildingName(holder.binding.spBuildingName.getSelectedItem().toString());

                    for (int i = 0; i < editDeposits.size(); i++) {
                        if (deposits.get(position).getIndex().equals(editDeposits.get(i).getIndex())) {
                            JSLog.D("remove editDeposits position               :::         " + i, null);
                            editDeposits.remove(i);
                        }
                    }
                    JSLog.D("add editDeposits position              :::         " + deposit.getViewPosition(), null);
                    editDeposits.add(deposit);
                }

                selectedFlag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
        if (deposits.size() == 0) {
            return 0;
        }
        if (deposits.size() < 10) {
            return deposits.size();
        }
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        BusProvider.getInstance().unregister(this);
        super.onDetachedFromRecyclerView(recyclerView);
    }

    private int i = 0;

    @Subscribe
    public void FinishLoad(OnClickEvent event) {

        JSLog.D("editDeposits size              :::         " + editDeposits.size(), null);
        if (i < editDeposits.size()) {
            if (bindingHolder.binding.spCategory.getSelectedItemPosition() == bindingHolder.binding.spCategory.getLastVisiblePosition()) {
                updateTrimmedData(editDeposits.get(i).getName(), editDeposits.get(i).getDate(), editDeposits.get(i).getDestinationName(), editDeposits.get(i).getType(), editDeposits.get(i).getNote(), editDeposits.get(i).getBuildingName());
            } else {
                if (bindingHolder.binding.spToName.getSelectedItemPosition() != 0) {
                    updateTrimmedData(editDeposits.get(i).getName(), editDeposits.get(i).getDate(), editDeposits.get(i).getDestinationName(), editDeposits.get(i).getType(), editDeposits.get(i).getNote(), editDeposits.get(i).getBuildingName());
                }
            }
        }

    }


    private void updateTrimmedData(String name, String date, String objectName, final String type, String note, String buildingName) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().updateTrimmedData(name, date, objectName, type, DeviceUtil.getDevicePhoneNumber(context), NEW_DATA, "", "", note,
                buildingName);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();


                JSLog.D("updateTrimmedData get result               :::     " + result, null);
                if ("success".equals(result)) {
                    String message = jsonObject.get("message").getAsString();

                    JSLog.D("updateTrimmedData get message               :::     " + message, null);
                    if ("success".equals(message)) {
                        Debug.showToast(context, "등록되었습니다");
                        JSLog.D("type           :::     " + DepositDataAdapter.this.type, null);
                        if (NEW_DATA.equals(DepositDataAdapter.this.type)) {
//                            deposits.remove(position);

                            i++;
                            if (i < editDeposits.size()) {
                                updateTrimmedData(editDeposits.get(i).getName(), editDeposits.get(i).getDate(), editDeposits.get(i).getDestinationName(), editDeposits.get(i).getType(),
                                        editDeposits.get(i).getNote(), editDeposits.get(i).getBuildingName());
                            } else {
                                for (int i = 0; i < editDeposits.size(); i++) {
                                    deposits.remove(editDeposits.get(i).getViewPosition());
                                }
                                notifyDataSetChanged();

                                i = 0;
                                editDeposits.clear();

                                BusProvider.getInstance().post(new OnClickEvent());
                            }
                        }
                    } else {
                        i = 0;
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private int getSelectedPosition(int position, List<String> compareList) {
        for (int j = 0; j < compareList.size(); j++) {

            JSLog.D("dstName            :::     " + deposits.get(position).getDestinationName(), null);
            JSLog.D("compareList            :::     " + compareList.get(j), null);
            if ((deposits.get(position).getDestinationName()).equals(compareList.get(j))) {
                return j;
            }
        }
        return 0;
    }

    private int getSelectedTypePosition(int position) {
        for (int j = 0; j < residents.size(); j++) {

            if (deposits.get(position).getMethod().contains("입금")) {
                if ((deposits.get(position).getType()).equals(context.getResources().getStringArray(R.array.deposit)[j])) {
                    return j;
                }
            } else if (deposits.get(position).getMethod().contains("출금")) {
                if ((deposits.get(position).getType()).equals(context.getResources().getStringArray(R.array.withdraw)[j])) {
                    return j;
                }
            }
        }
        return 0;
    }

    private int getSelectedBuildingPosition(int position, List<String> compareList) {
        for (int i = 0; i < deposits.size(); i++) {
            if (deposits.get(position).getBuildingName() != null) {
                if (deposits.get(position).getBuildingName().equals(compareList.get(i))) {
                    return i;
                }
            }
        }
        return 0;
    }

    public BindingHolder getBindingHolder() {
        return bindingHolder;
    }
}
