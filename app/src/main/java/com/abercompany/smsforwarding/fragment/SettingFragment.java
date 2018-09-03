package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.AddCashActivity;
import com.abercompany.smsforwarding.activity.ElecDefaulterActivity;
import com.abercompany.smsforwarding.activity.ElecStatusActivity;
import com.abercompany.smsforwarding.activity.ManageBuildingContractActivity;
import com.abercompany.smsforwarding.activity.RegisterEtcNumActivity;
import com.abercompany.smsforwarding.activity.RegisterPhoneNumActivity;
import com.abercompany.smsforwarding.activity.SearchDefaulterActivity;
import com.abercompany.smsforwarding.activity.SearchLeaveRoomActivity;
import com.abercompany.smsforwarding.databinding.DlgProgressCircleBinding;
import com.abercompany.smsforwarding.databinding.DlgProgressHorizontalBinding;
import com.abercompany.smsforwarding.databinding.FragmentSettingBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Contact;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.JSLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.abercompany.smsforwarding.util.Definitions.CONTACT_TYPE.BROKER;
import static com.abercompany.smsforwarding.util.Definitions.CONTACT_TYPE.RESIDENT;
import static com.abercompany.smsforwarding.util.Definitions.TRIM_DATA.EXISTING_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    private Context context;

    private List<Resident> residents;
    private List<Resident> inResidents;
    private List<Broker> brokers;
    private List<String> nums = new ArrayList<>();
    private List<Defaulter> defaulters;
    private List<Building> buildings;

    private List<String> residentContact = new ArrayList<>();
    private List<String> contactPhoneNum = new ArrayList<>();
    private List<String> contactRealtyPhoneNum = new ArrayList<>();
    private List<String> similar;
    private List<String> different = new ArrayList<>();
    private List<String> brokerContract = new ArrayList<>();

    private String buildingName = "";
    private String realty = "부동산";

    private Handler progressHandler;


    private Dialog progressDialog;
    private DlgProgressCircleBinding progressBinding;

    public SettingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SettingFragment(Context context, List<Resident> residents, List<Broker> brokers, List<String> nums, List<Defaulter> defaulters, List<Building> buildings) {
        this.context = context;
        this.residents = residents;
        this.inResidents = inResidents;
        this.brokers = brokers;
        this.nums = nums;
        this.defaulters = defaulters;
        this.buildings = buildings;


        for (int i = 0; i < residents.size(); i++) {
            if ("아인스빌".equals(residents.get(i).getBuildingName())) {
                buildingName = "입주자JNK";
            }

            Contact contact = new Contact();
            contact.setName(residents.get(i).getName());
            contact.setPhoneNum(residents.get(i).getPhoneNum());

            residentContact.add(residents.get(i).getPhoneNum());
        }

        for (int i = 0; i < brokers.size(); i++) {
            Contact contact = new Contact();
            contact.setName(context.getString(R.string.str_deposit_realty,
                    brokers.get(i).getRealtyName(), brokers.get(i).getName()));
            contact.setPhoneNum(brokers.get(i).getPhoneNum());

            brokerContract.add(brokers.get(i).getPhoneNum());
        }

    }

    public static SettingFragment newInstance(Context context, List<Resident> residents, List<Broker> brokers, List<String> nums, List<Defaulter> defaulters, List<Building> buildings) {
        SettingFragment fragment = new SettingFragment(context, residents, brokers, nums, defaulters, buildings);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_setting, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        View view = binding.getRoot();
        binding.setSetting(this);

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_last_rent:
                Intent intent = new Intent(getContext(), AddCashActivity.class);
                intent.putExtra("resident", (Serializable) inResidents);
                intent.putExtra("broker", (Serializable) brokers);
                intent.putExtra("dataType", EXISTING_DATA);
                intent.putExtra("building", (Serializable) buildings);
                startActivity(intent);
                break;

            case R.id.btn_register:
                Intent intent1 = new Intent(getContext(), RegisterPhoneNumActivity.class);
                intent1.putExtra("nums", (Serializable) nums);
                startActivity(intent1);

            case R.id.btn_check_defaulter:
                Intent intent2 = new Intent(getContext(), SearchDefaulterActivity.class);
                intent2.putExtra("defaulter", (Serializable) defaulters);
                startActivity(intent2);
                break;

            case R.id.btn_check_leave_room:
                Intent intent3 = new Intent(getContext(), SearchLeaveRoomActivity.class);
                startActivity(intent3);
                break;

            case R.id.btn_check_elec_defaulter:
                Intent intent4 = new Intent(getContext(), ElecDefaulterActivity.class);
                startActivity(intent4);
                break;

            case R.id.btn_register_etc_num:
                Intent intent5 = new Intent(getContext(), RegisterEtcNumActivity.class);
                intent5.putExtra("residents", (Serializable) inResidents);
                intent5.putExtra("brokers", (Serializable) brokers);
                startActivity(intent5);
                break;

            case R.id.btn_refresh_contacts:

                similar = new ArrayList<>(residentContact);

                saveContract(residentContact, similar, RESIDENT, contactPhoneNum);

                break;

            case R.id.btn_refresh_contacts_realty:

                similar = new ArrayList<>(brokerContract);

                saveContract(brokerContract, similar, BROKER, contactRealtyPhoneNum);


                break;

            case R.id.btn_manage_elec:
                Intent intent6 = new Intent(getContext(), ElecStatusActivity.class);
                startActivity(intent6);
                break;

            case R.id.btn_add_manage_building_contact:
                Intent intent7 = new Intent(getContext(), ManageBuildingContractActivity.class);
                startActivity(intent7);
                break;
        }
    }

    private void saveContract(final List<String> residentContact, final List<String> similar, final String type, final List<String> contactPhoneNum) {
        progressDialog = new Dialog(getContext(), android.R.style.Theme_Black);

        progressBinding = DataBindingUtil
                .inflate(LayoutInflater.from(getContext()), R.layout.dlg_progress_circle, null, false);

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        progressDialog.setContentView(progressBinding.getRoot());
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getContactList();

                if (contactPhoneNum.size() == 0) {

                    if (type.equals(RESIDENT)) {
                        for (int i = 0; i < residents.size(); i++) {
                            Message msg = progressHandler.obtainMessage();


                            JSLog.D("Add residentPhoneNum            :::     " + residents.get(i).getPhoneNum(), null);
                            addContacts(buildingName +
                                            residents.get(i).getHo() + residents.get(i).getName(),
                                    residents.get(i).getPhoneNum());


                            msg.obj = buildingName +
                                    residents.get(i).getHo() + residents.get(i).getName() + " 추가";
                            progressHandler.sendMessage(msg);
                        }
                    } else if (type.equals(BROKER)) {
                        for (int i = 0; i < brokers.size(); i++) {
                            Message msg = progressHandler.obtainMessage();


                            JSLog.D("Add residentPhoneNum            :::     " + brokers.get(i).getPhoneNum(), null);
                            addContacts(getContext().getString(R.string.str_deposit_realty,
                                    brokers.get(i).getRealtyName(),
                                    brokers.get(i).getName()) + realty,
                                    brokers.get(i).getPhoneNum());


                            msg.obj = getContext().getString(R.string.str_deposit_realty,
                                    brokers.get(i).getRealtyName(),
                                    brokers.get(i).getName()) + " 추가";
                            progressHandler.sendMessage(msg);
                        }
                    }


                    progressDialog.dismiss();

                } else {
                    if (!residentContact.equals(contactPhoneNum)) {

                        different.addAll(residentContact);
                        different.addAll(contactPhoneNum);

                        similar.retainAll(contactPhoneNum);
                        different.removeAll(similar);

                        residentContact.removeAll(similar);


                        if (type.equals(RESIDENT)) {
                            for (int i = 0; i < residents.size(); i++) {
                                for (int j = 0; j < residentContact.size(); j++) {
                                    if (residentContact.get(j).equals(residents.get(i).getPhoneNum())) {
                                        Message msg = progressHandler.obtainMessage();

                                        JSLog.D("Add residentPhoneNum            :::     " + residentContact.get(j), null);
                                        addContacts(buildingName +
                                                        residents.get(i).getHo() + residents.get(i).getName(),
                                                residentContact.get(j));

                                        msg.obj = buildingName +
                                                residents.get(i).getHo() + residents.get(i).getName() + " 추가";
                                        progressHandler.sendMessage(msg);

                                    }
                                }
                            }
                        } else if (type.equals(BROKER)) {
                            for (int i = 0; i < brokers.size(); i++) {
                                for (int j = 0; j < residentContact.size(); j++) {
                                    if (residentContact.get(j).equals(brokers.get(i).getPhoneNum())) {
                                        Message msg = progressHandler.obtainMessage();

                                        JSLog.D("Add residentPhoneNum            :::     " + residentContact.get(j), null);
                                        addContacts(getContext().getString(R.string.str_deposit_realty,
                                                brokers.get(i).getRealtyName(),
                                                brokers.get(i).getName()) + realty,
                                                residentContact.get(j));

                                        msg.obj = getContext().getString(R.string.str_deposit_realty,
                                                brokers.get(i).getRealtyName(),
                                                brokers.get(i).getName()) + " 추가";
                                        progressHandler.sendMessage(msg);

                                    }
                                }
                            }
                        }

                        contactPhoneNum.removeAll(similar);

                        for (int i = 0; i < contactPhoneNum.size(); i++) {
                            Message msg = progressHandler.obtainMessage();

                            JSLog.D("Delete contactPhoneNum            :::     " + contactPhoneNum.get(i), null);
                            deleteContact(getActivity().getContentResolver(), contactPhoneNum.get(i));

                            msg.obj = contactPhoneNum.get(i) + " 삭제";
                            progressHandler.sendMessage(msg);


                        }

                        progressDialog.dismiss();
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }
            }
        }).start();

        progressHandler = new Handler() {
            public void handleMessage(Message msg) {

                progressBinding.tvProgress.setText((String) msg.obj);
            }
        };
    }

    private void addContacts(String name, String phoneNum) {
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        if(mBitmap!=null){    // If an image is selected successfully
//            mBitmap.compress(Bitmap.CompressFormat.PNG , 75, stream);
//
//            // Adding insert operation to operations list
//            // to insert Photo in the table ContactsContract.Data
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,stream.toByteArray())
//                    .build());
//
//            try {
//                stream.flush();
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            // Executing all the insert operations as a single database transaction
            getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//            Toast.makeText(getActivity().getBaseContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void deleteContact(ContentResolver contactHelper, String number) {
        ArrayList<ContentProviderOperation> ops = new
                ArrayList<ContentProviderOperation>();
        JSLog.D("getContactID           :::     " + getContactID(contactHelper, number), null);
        String[] args = new String[]{String.valueOf(getContactID(contactHelper,
                number))};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
//            Toast.makeText(getActivity().getBaseContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private long getContactID(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        Cursor cursor = null;
        try {
            cursor = contactHelper.query(contactUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return -1;
    }

    private void getContactList() {

        progressBinding.tvProgress.setText("연락처를 가져오는 중입니다");

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        JSLog.I("Name: " + name, null);
                        JSLog.I("Phone Number: " + phoneNo, null);


                        if (name.contains(buildingName)) {
                            Contact resident = new Contact();
                            resident.setName(name);
                            resident.setPhoneNum(phoneNo);

                            JSLog.D("contactPhoneNum            :::     " + phoneNo, null);
                            contactPhoneNum.add(phoneNo);

                        }

                        if (name.contains(realty)) {

                            Contact resident = new Contact();
                            resident.setName(name);
                            resident.setPhoneNum(phoneNo);

                            JSLog.D("contactRealtyPhoneNum            :::     " + phoneNo, null);
                            contactRealtyPhoneNum.add(phoneNo);
                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }


}
