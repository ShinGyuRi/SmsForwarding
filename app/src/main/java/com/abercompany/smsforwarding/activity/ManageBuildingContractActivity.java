package com.abercompany.smsforwarding.activity;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ContactAdapter;
import com.abercompany.smsforwarding.databinding.ActivityManageBuildingContractBinding;
import com.abercompany.smsforwarding.databinding.DlgProgressCircleBinding;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Contact;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.SmsLib;

import java.util.ArrayList;
import java.util.List;

import static com.abercompany.smsforwarding.util.Definitions.CONTACT_TYPE.BROKER;
import static com.abercompany.smsforwarding.util.Definitions.CONTACT_TYPE.RESIDENT;

public class ManageBuildingContractActivity extends AppCompatActivity {

    private ActivityManageBuildingContractBinding binding;

    private List<String> etcPhoneNum = new ArrayList<>();
    private List<String> residentPhoneNum = new ArrayList<>();
    private List<String> brokerPhoneNum = new ArrayList<>();

    private List<String> buildingName = new ArrayList<>();
    private List<String> contactPhoneNum = new ArrayList<>();
    private List<String> contactRealtyPhoneNum = new ArrayList<>();
    private List<String> different = new ArrayList<>();
    private List<String> similar;

    private List<Building> buildings;

    private List<Resident> residents;
    private List<Broker> brokers;

    private List<Contact> residentContact = new ArrayList<>();
    private List<Contact> brokerContact = new ArrayList<>();
    private List<Contact> etcContact = new ArrayList<>();

    private String prefix = "";
    private String residentPrefix = "입주자";
    private String brokerPrefix = "부동산";

    private ContactAdapter adapter;

    private Handler progressHandler;

    private Dialog progressDialog;
    private DlgProgressCircleBinding progressBinding;

    private String prePhoneNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_building_contract);
        binding.setManageBuildingContact(this);

        binding.btnRefreshContacts.setEnabled(false);
        binding.btnEdit.setEnabled(false);

        Intent intent = getIntent();
        buildings = (List<Building>) intent.getSerializableExtra("building");
        residents = (List<Resident>) intent.getSerializableExtra("resident");
        brokers = (List<Broker>) intent.getSerializableExtra("broker");

        for (int i = 0; i < buildings.size(); i++) {
            buildingName.add(buildings.get(i).getName());
        }

        binding.spBuildingName.setAdapter(new ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                buildingName));

        binding.etContactNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        initPhoneNum();

        progressHandler = new Handler() {
            public void handleMessage(Message msg) {

                progressBinding.tvProgress.setText((String) msg.obj);
            }
        };


        binding.spBuildingName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.btnRefreshContacts.setEnabled(true);
                switch (position) {
                    case 0:
                        prefix = "JNK";
                        break;
                }

                showDlg();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getContactList();

                    }
                }).start();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initPhoneNum() {
        for (int i = 0; i < residents.size(); i++) {

            Contact contact = new Contact();
            contact.setName(residents.get(i).getName());
            contact.setPhoneNum(residents.get(i).getPhoneNum());

            residentPhoneNum.add(residents.get(i).getPhoneNum());
        }

        for (int i = 0; i < brokers.size(); i++) {
            Contact contact = new Contact();
            contact.setName(getString(R.string.str_deposit_realty,
                    brokers.get(i).getRealtyName(), brokers.get(i).getName()));
            contact.setPhoneNum(brokers.get(i).getPhoneNum());

            brokerPhoneNum.add(brokers.get(i).getPhoneNum());
        }
    }

    private void showDlg() {

        progressDialog = new Dialog(this, android.R.style.Theme_Black);

        progressBinding = DataBindingUtil
                .inflate(LayoutInflater.from(this), R.layout.dlg_progress_circle, null, false);

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        progressDialog.setContentView(progressBinding.getRoot());
        progressDialog.show();
    }

    public void onClick(View view) {

        String name = binding.etContactName.getText().toString();
        String newPhoneNum = binding.etContactNum.getText().toString();

        switch (view.getId()) {
            case R.id.btn_refresh_contacts:


                showDlg();

                similar = new ArrayList<>(contactPhoneNum);
                saveContract(residentPhoneNum, similar, RESIDENT, contactPhoneNum, prefix);

                similar = new ArrayList<>(contactRealtyPhoneNum);
                saveContract(brokerPhoneNum, similar, BROKER, contactRealtyPhoneNum, prefix);

                break;
            case R.id.btn_register:
                if (!"".equals(binding.etContactName.getText().toString()) &&
                        !"".equals(binding.etContactNum.getText().toString().replace("-", ""))) {
                    addContacts(prefix + binding.etContactName.getText().toString(),
                            binding.etContactNum.getText().toString());
                } else {
                    Debug.showToast(this, "이름과 번호를 입력해주세요");
                }
                break;

            case R.id.btn_edit:
                deleteContact(getContentResolver(), prePhoneNum);
                addContacts(name, newPhoneNum);
                Debug.showToast(this, "수정되었습니다");
                binding.etContactName.setFocusable(true);
                binding.etContactName.setFocusableInTouchMode(true);
                binding.etContactName.setText("");
                binding.etContactNum.setText("");
                break;

            case R.id.btn_add_etc_num:
                addEtcContacts(getContactID(getContentResolver(), prePhoneNum), newPhoneNum);
                Debug.showToast(this, "등록되었습니다");
                binding.etContactName.setFocusable(true);
                binding.etContactName.setFocusableInTouchMode(true);
                binding.etContactName.setText("");
                binding.etContactNum.setText("");
                break;
        }
    }

    private void saveContract(final List<String> residentPhoneNum, final List<String> similar, final String type, final List<String> contactPhoneNum, final String prefix) {

        new Thread(new Runnable() {
            @Override
            public void run() {
//                getContactList();

                if (contactPhoneNum.size() == 0) {

                    if (type.equals(RESIDENT)) {
                        for (int i = 0; i < residents.size(); i++) {
                            Message msg = progressHandler.obtainMessage();


                            JSLog.D("Add residentPhoneNum            :::     " + residents.get(i).getPhoneNum(), null);
                            addContacts(residentPrefix + prefix +
                                            residents.get(i).getHo() + residents.get(i).getName(),
                                    residents.get(i).getPhoneNum());


                            msg.obj = residentPrefix + prefix +
                                    residents.get(i).getHo() + residents.get(i).getName() + " 추가";
                            progressHandler.sendMessage(msg);
                        }
                    } else if (type.equals(BROKER)) {
                        for (int i = 0; i < brokers.size(); i++) {
                            Message msg = progressHandler.obtainMessage();


                            JSLog.D("Add brokerPhoneNum            :::     " + brokers.get(i).getPhoneNum(), null);
                            addContacts(getString(R.string.str_deposit_realty,
                                    brokers.get(i).getRealtyName(),
                                    brokers.get(i).getName()) + brokerPrefix + prefix,
                                    brokers.get(i).getPhoneNum());


                            msg.obj = getString(R.string.str_deposit_realty,
                                    brokers.get(i).getRealtyName(),
                                    brokers.get(i).getName()) + " 추가";
                            progressHandler.sendMessage(msg);
                        }
                    }


                    if (type.equals(BROKER)) {
                        progressDialog.dismiss();
                    }

                } else {
                    if (!residentPhoneNum.equals(contactPhoneNum)) {

                        different.addAll(residentPhoneNum);
                        different.addAll(contactPhoneNum);

                        similar.retainAll(contactPhoneNum);
                        different.removeAll(similar);

                        residentPhoneNum.removeAll(similar);


                        if (type.equals(RESIDENT)) {
                            for (int i = 0; i < residents.size(); i++) {
                                for (int j = 0; j < residentPhoneNum.size(); j++) {
                                    if (residentPhoneNum.get(j).equals(residents.get(i).getPhoneNum())) {
                                        Message msg = progressHandler.obtainMessage();

                                        JSLog.D("Add residentPhoneNum            :::     " + residentPhoneNum.get(j), null);
                                        addContacts(residentPrefix + prefix +
                                                        residents.get(i).getHo() + residents.get(i).getName(),
                                                residentPhoneNum.get(j));

                                        msg.obj = residentPrefix + prefix +
                                                residents.get(i).getHo() + residents.get(i).getName() + " 추가";
                                        progressHandler.sendMessage(msg);

                                    }
                                }
                            }
                        } else if (type.equals(BROKER)) {
                            for (int i = 0; i < brokers.size(); i++) {
                                for (int j = 0; j < brokerPhoneNum.size(); j++) {
                                    if (brokerPhoneNum.get(j).equals(brokers.get(i).getPhoneNum())) {
                                        Message msg = progressHandler.obtainMessage();

                                        JSLog.D("Add brokerPhoneNum            :::     " + brokerPhoneNum.get(j), null);
                                        addContacts(getString(R.string.str_deposit_realty,
                                                brokers.get(i).getRealtyName(),
                                                brokers.get(i).getName()) + brokerPrefix + prefix,
                                                brokerPhoneNum.get(j));

                                        msg.obj = getString(R.string.str_deposit_realty,
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
                            deleteContact(getContentResolver(), contactPhoneNum.get(i));

                            msg.obj = contactPhoneNum.get(i) + " 삭제";
                            progressHandler.sendMessage(msg);


                        }

                        if ("broker".equals(BROKER)) {
                            progressDialog.dismiss();
                        }
                    } else {
                        if (progressDialog.isShowing() &&
                                "broker".equals(BROKER)) {
                            progressDialog.dismiss();
                        }
                    }
                }
            }
        }).start();
    }

    private void setContactAdapter(final List<Contact> contact) {
        adapter = new ContactAdapter(this, contact);
        binding.rvContract.setAdapter(adapter);
        binding.rvContract.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

        adapter.setItemClick(new ContactAdapter.ItemClick() {
            @Override
            public void onClick(View view, final int position) {
                binding.btnEdit.setEnabled(true);
                binding.etContactName.setFocusable(false);

                switch (binding.spContactType.getSelectedItemPosition()) {
                    case 0:
                        break;
                    case 1:
                        binding.etContactName.setText(residentContact.get(position).getName());
                        binding.etContactNum.setText(residentContact.get(position).getPhoneNum());
                        prePhoneNum = residentContact.get(position).getPhoneNum();
                        break;
                    case 2:
                        binding.etContactName.setText(brokerContact.get(position).getName());
                        binding.etContactNum.setText(brokerContact.get(position).getPhoneNum());
                        prePhoneNum = brokerContact.get(position).getPhoneNum();
                        break;
                    case 3:
                        binding.etContactName.setText(etcContact.get(position).getName());
                        binding.etContactNum.setText(etcContact.get(position).getPhoneNum());
                        prePhoneNum = etcContact.get(position).getPhoneNum();
                        break;
                }
            }

            @Override
            public void onLongClick(View view, final int position) {
                SmsLib.getInstance().showSimplSelect2Dialog(ManageBuildingContractActivity.this,
                        "연락처를 삭제하시겠습니까?",
                        "예", "아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteContact(getContentResolver(), contact.get(position).getPhoneNum());
                                adapter.remove(position);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }
        });
    }

    private void updateContact (String contactId, String newNumber)
            throws RemoteException, OperationApplicationException{

        //ASSERT: @contactId alreay has a work phone number
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.RawContacts.Data.MIMETYPE + "='"  +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
        String[] phoneArgs = new String[]{contactId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)};
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                .build());
        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        Debug.showToast(this, "수정되었습니다");
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
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//            Toast.makeText(this, "등록되었습니다", Toast.LENGTH_SHORT).show();
//            binding.etContactName.setText("");
//            binding.etContactNum.setText("");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void addEtcContacts(long rawContactID, String phoneNum) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

//...
//add Phone to existiong Contact
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE).build());
//...
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            JSLog.D("update success", null);
        } catch (Exception e) {
            JSLog.D("update failed", null);
            e.printStackTrace();
        }
    }

    public void deleteContact(ContentResolver contactHelper, String number) {
        ArrayList<ContentProviderOperation> ops = new
                ArrayList<ContentProviderOperation>();

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

        Message msg = progressHandler.obtainMessage();
        msg.obj = "연락처를 가져오는 중입니다";
        progressHandler.sendMessage(msg);

        ContentResolver cr = getContentResolver();
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

                        if (binding.spBuildingName.getSelectedItemPosition() == 0) {
                            if (name.contains(prefix)) {
                                Contact contact = new Contact();

                                if (name.contains(residentPrefix)) {
                                    contact.setName(name);
                                    contact.setPhoneNum(phoneNo);

                                    JSLog.D("contactPhoneNum            :::     " + phoneNo, null);
                                    contactPhoneNum.add(phoneNo);
                                    residentContact.add(contact);

                                } else if (name.contains(brokerPrefix)) {

                                    contact.setName(name);
                                    contact.setPhoneNum(phoneNo);

                                    JSLog.D("contactRealtyPhoneNum            :::     " + phoneNo, null);
                                    contactRealtyPhoneNum.add(phoneNo);
                                    brokerContact.add(contact);
                                } else {
                                    contact.setName(name);
                                    contact.setPhoneNum(phoneNo);

                                    etcContact.add(contact);
                                }


                            }
                        }

                    }
                    pCur.close();



                    binding.spContactType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 1:
                                    setContactAdapter(residentContact);
                                    break;
                                case 2:
                                    setContactAdapter(brokerContact);
                                    break;
                                case 3:
                                    setContactAdapter(etcContact);
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }
        }
        if (cur != null) {
            progressDialog.dismiss();
            cur.close();
        }
    }
}
