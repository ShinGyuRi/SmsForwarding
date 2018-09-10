package com.abercompany.smsforwarding.activity;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.ContactAdapter;
import com.abercompany.smsforwarding.databinding.ActivityManageBuildingContractBinding;
import com.abercompany.smsforwarding.model.Contact;
import com.abercompany.smsforwarding.util.Debug;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.SmsLib;

import java.util.ArrayList;
import java.util.List;

public class ManageBuildingContractActivity extends AppCompatActivity {

    private ActivityManageBuildingContractBinding binding;

    private List<Contact> contact = new ArrayList<>();

    private String prefix = "JNK";

    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_building_contract);
        binding.setManageBuildingContact(this);

        binding.etContactNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        getContactList();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (!"".equals(binding.etContactName.getText().toString()) &&
                        !"".equals(binding.etContactNum.getText().toString().replace("-", ""))) {
                    addContacts(prefix + binding.etContactName.getText().toString(),
                            binding.etContactNum.getText().toString());
                } else {
                    Debug.showToast(this, "이름과 번호를 입력해주세요");
                }
                break;
        }
    }

    private void setContactAdapter(final List<Contact> contact) {
        adapter = new ContactAdapter(this, contact);
        binding.rvContract.setAdapter(adapter);
        binding.rvContract.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

        adapter.setItemClick(new ContactAdapter.ItemClick() {
            @Override
            public void onClick(View view, final int position) {
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
            Toast.makeText(this, "등록되었습니다", Toast.LENGTH_SHORT).show();
            binding.etContactName.setText("");
            binding.etContactNum.setText("");
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


                        if (name.contains(prefix)) {
                            Contact resident = new Contact();
                            resident.setName(name);
                            resident.setPhoneNum(phoneNo);

                            JSLog.D("contactPhoneNum            :::     " + phoneNo, null);
                            contact.add(resident);

                        }
                    }
                    pCur.close();

                    setContactAdapter(contact);
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }
}
