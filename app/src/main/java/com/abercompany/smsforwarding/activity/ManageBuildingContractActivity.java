package com.abercompany.smsforwarding.activity;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.databinding.DataBindingUtil;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityManageBuildingContractBinding;
import com.abercompany.smsforwarding.util.Debug;

import java.util.ArrayList;

public class ManageBuildingContractActivity extends AppCompatActivity {

    private ActivityManageBuildingContractBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_building_contract);
        binding.setManageBuildingContact(this);

        binding.etContactNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (!"".equals(binding.etContactName.getText().toString()) &&
                        !"".equals(binding.etContactNum.getText().toString().replace("-", ""))) {
                    addContacts(binding.etContactName.getText().toString(),
                            binding.etContactNum.getText().toString());
                } else {
                    Debug.showToast(this, "이름과 번호를 입력해주세요");
                }
                break;
        }
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
}
