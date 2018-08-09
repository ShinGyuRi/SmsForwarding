package com.abercompany.smsforwarding.activity;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;

import java.util.ArrayList;

public class AddContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

    }
}
