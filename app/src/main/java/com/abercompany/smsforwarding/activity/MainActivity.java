package com.abercompany.smsforwarding.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.adapter.BuildingAdapter;
import com.abercompany.smsforwarding.databinding.ActivityMainBinding;
import com.abercompany.smsforwarding.fragment.BuildingFragment;
import com.abercompany.smsforwarding.fragment.ExistingDataFragment;
import com.abercompany.smsforwarding.fragment.NewDataFragment;
import com.abercompany.smsforwarding.fragment.RoomFragment;
import com.abercompany.smsforwarding.fragment.SettingFragment;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Building;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Defaulter;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.GetBrokerResult;
import com.abercompany.smsforwarding.model.GetBuildingResult;
import com.abercompany.smsforwarding.model.GetContractResult;
import com.abercompany.smsforwarding.model.GetDefaulterResult;
import com.abercompany.smsforwarding.model.GetDepositResult;
import com.abercompany.smsforwarding.model.GetResidentResult;
import com.abercompany.smsforwarding.model.GetRoomResult;
import com.abercompany.smsforwarding.model.OnClickEvent;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.model.Room;
import com.abercompany.smsforwarding.model.Sms;
import com.abercompany.smsforwarding.network.QuestionsSpreadsheetWebService;
import com.abercompany.smsforwarding.provider.BusProvider;
import com.abercompany.smsforwarding.service.SmsService;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.abercompany.smsforwarding.util.PrefUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;


    private List<Sms> lst;
    private QuestionsSpreadsheetWebService spreadsheetWebService;
    private List<String> nums = new ArrayList<String>();
    private List<Sms> lst2;
    private Cursor c;
    private Fragment buildingFragment, newDataFragment, existingDataFragment, settingFragment, roomFragment;
    private List<Deposit> trimmedData;
    private List<Deposit> newDatas = new ArrayList<>();
    private List<Deposit> existingDatas = new ArrayList<>();
    private List<Resident> residents;
    private List<Broker> brokers;
    private List<Defaulter> defaulters = new ArrayList<>();
    private List<Building> buildings;
    private List<Room> rooms;
    private List<Room> splitRooms;
    private List<Contract> contracts;
    private List<Contract> leaveContracts;

    private BuildingAdapter buildingAdapter;

    private Menu menu;
    private MenuItem navBuilding, navNewData, navExistingData, navSetting, navRealty, navReport;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        BusProvider.getInstance().register(this);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                getMessage();
                Intent intent = new Intent(getApplicationContext(), SmsService.class);
                startService(intent);


                getNum("");
                getTrimmedData(DeviceUtil.getDevicePhoneNumber(MainActivity.this));
                getBroker();
                getResident();
                getRoom();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this servicePlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
                .check();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
    }


    private void initNav() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        menu = navigationView.getMenu();
        navBuilding = menu.findItem(R.id.nav_building);
        navNewData = menu.findItem(R.id.nav_new_data);
        navExistingData = menu.findItem(R.id.nav_existing_data);
        navSetting = menu.findItem(R.id.nav_setting);
        navRealty = menu.findItem(R.id.nav_realty);
        navReport = menu.findItem(R.id.nav_report);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void setInitFrag() {
        buildingFragment = BuildingFragment.newInstance(rooms);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, buildingFragment, "BUILDING").addToBackStack("BUILDING").commitAllowingStateLoss();
        navBuilding.setEnabled(false);

        getBuilding();
    }

    private void initNaviButton(MenuItem menu, DrawerLayout drawer) {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        navBuilding.setEnabled(true);
        navNewData.setEnabled(true);
        navExistingData.setEnabled(true);
        navSetting.setEnabled(true);
        navRealty.setEnabled(true);
        navReport.setEnabled(true);
        menu.setEnabled(false);
    }

    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();

        JSLog.D("back stack cnt:" + fm.getBackStackEntryCount(), new Throwable());

        boolean fragmentPopped = fm.popBackStackImmediate(tag, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(tag);
            ft.replace(R.id.container, fragment, tag).commit();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    MainActivity.this.finishAffinity();
                } else {
                    System.exit(0);
                }
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().removeOnBackStackChangedListener(backStackChangedListener);
    }

    private FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            String currentTab = getSupportFragmentManager().findFragmentById(R.id.container).getTag();
            JSLog.D(currentTab, new Throwable());

//            getBroker();
//            getResident();
//            getDefaulter();

            if (currentTab == null) return;
            switch (currentTab) {
                case "BUILDING":
                    initNaviButton(navBuilding, drawer);
                    setBuildingAdapter(buildings, rooms, contracts, defaulters, leaveContracts);
                    break;
                case "NEW_DATA":
                    initNaviButton(navNewData, drawer);
                    break;
                case "EXISTING_DATA":
                    initNaviButton(navExistingData, drawer);
                    break;
                case "SETTING":
                    initNaviButton(navSetting, drawer);
                    break;
            }
        }
    };

    private void getBroker() {
        Call<GetBrokerResult> getBrokerResultCall = NetworkUtil.getInstace().getBroker("");
        getBrokerResultCall.enqueue(new Callback<GetBrokerResult>() {
            @Override
            public void onResponse(Call<GetBrokerResult> call, Response<GetBrokerResult> response) {
                GetBrokerResult getBrokerResult = response.body();
                String result = getBrokerResult.getResult();

                if ("success".equals(result)) {
                    brokers = getBrokerResult.getBrokers();

                    PrefUtil.getInstance().putPreference(brokers, "broker");
                }
            }

            @Override
            public void onFailure(Call<GetBrokerResult> call, Throwable t) {

            }
        });
    }

    private void getResident() {
        Call<GetResidentResult> getResidentResultCall = NetworkUtil.getInstace().getResident("");
        getResidentResultCall.enqueue(new Callback<GetResidentResult>() {
            @Override
            public void onResponse(Call<GetResidentResult> call, Response<GetResidentResult> response) {
                GetResidentResult getResidentResult = response.body();
                String result = getResidentResult.getResult();

                if ("success".equals(result)) {
                    residents = getResidentResult.getResidents();;

                }
            }

            @Override
            public void onFailure(Call<GetResidentResult> call, Throwable t) {

            }
        });
    }

    private void getNum(final String senderNum) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().getNum(senderNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Log.d(TAG, "message      ::      " + jsonObject.get("message").getAsJsonArray().toString());

                    JsonArray message = jsonObject.get("message").getAsJsonArray();
                    JsonObject messageObj;
                    for (int i = 0; i < message.size(); i++) {
                        messageObj = message.get(i).getAsJsonObject();
                        if (DeviceUtil.getDevicePhoneNumber(MainActivity.this).equals(messageObj.get("phone_num").getAsString())) {
                            nums.add(messageObj.get("sender_num").getAsString());
                        }
                    }

//                    setRegisterNumAdapter(nums);


                    for (int i = 0; i < nums.size(); i++) {
                        lst2 = getAllSms(nums.get(i));
                        Log.d(TAG, "lst2.size           :::     " + lst2.size());
                        if (lst2.size() > 0) {
                            uploadSmsBody(nums.get(i).toString(), lst2);
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    int i = 0;

    private void uploadSmsBody(final String senderNum, final List<Sms> lst) {
        String message = lst.get(i).getMsg();
        String phoneNum = DeviceUtil.getDevicePhoneNumber(this);
        String timeStamp = lst.get(i).getTime();
        Log.d(TAG, "phoneNum        :::     " + phoneNum);
        JSLog.D("sms Message            :::     " + message, null);

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertSms(message, phoneNum, senderNum, timeStamp);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = null;
                result = jsonObject.get("result").getAsString();

                Log.d(TAG, "result          :::     " + result);

                if ("success".equals(result)) {
                    boolean success = jsonObject.get("message").getAsBoolean();
                    Log.d(TAG, "success          :::     " + success);
                    if (success) {
                        i++;
                        Log.d(TAG, "i           ::      " + i);
//                        Log.d(TAG, "lst.size            ::      " + lst.size());
                        if (i < lst.size()) {
                            uploadSmsBody(senderNum, lst);
                        }

                        if (i == lst.size()) {
                            i = 0;
                        }
                    } else {
                        i = 0;
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Fail", t);
            }
        });
    }

    public void getTrimmedData(String phoneNum) {
        Call<GetDepositResult> jsonObjectCall = NetworkUtil.getInstace().getDeposit(phoneNum);
        jsonObjectCall.enqueue(new Callback<GetDepositResult>() {
            @Override
            public void onResponse(Call<GetDepositResult> call, Response<GetDepositResult> response) {
                GetDepositResult getDepositResult = response.body();
                String result = getDepositResult.getResult();

                if ("success".equals(result)) {
                    trimmedData = getDepositResult.getMessage();
                    if (trimmedData != null) {
                        for (int i = 0; i < trimmedData.size(); i++) {
                            if (trimmedData.get(i).getType() == null || trimmedData.get(i).getDestinationName() == null) {
                                newDatas.add(trimmedData.get(i));
                            } else if (trimmedData.get(i).getType() != null && trimmedData.get(i).getDestinationName() != null) {
                                existingDatas.add(trimmedData.get(i));
                            }
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<GetDepositResult> call, Throwable t) {

            }
        });
    }

    private void getBuilding() {
        Call<GetBuildingResult> getBuildingResultCall = NetworkUtil.getInstace().getBuilding("");
        getBuildingResultCall.enqueue(new Callback<GetBuildingResult>() {
            @Override
            public void onResponse(Call<GetBuildingResult> call, Response<GetBuildingResult> response) {
                GetBuildingResult getBuildingResult = response.body();
                String result = getBuildingResult.getResult();

                if ("success".equals(result)) {
                    buildings = getBuildingResult.getBuildings();

                    getContract();
                }
            }

            @Override
            public void onFailure(Call<GetBuildingResult> call, Throwable t) {

            }
        });
    }

    private void setBuildingAdapter(final List<Building> buildings, final List<Room> rooms, List<Contract> contracts, final List<Defaulter> defaulters,
                                    List<Contract> leaveContracts) {

        JSLog.D("rooms.size             :::     " + rooms.size(), null);
        buildingAdapter = new BuildingAdapter(buildingFragment.getContext(), buildings, rooms, contracts, defaulters, leaveContracts);
        ((BuildingFragment) buildingFragment).getBinding().rvBuilding.setAdapter(buildingAdapter);
        ((BuildingFragment) buildingFragment).getBinding().rvBuilding.setLayoutManager(new LinearLayoutManager(buildingFragment.getContext()));
        buildingAdapter.notifyDataSetChanged();
        buildingAdapter.setItemClick(new BuildingAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                splitRooms = new ArrayList<>();
                for (int i = 0; i < rooms.size(); i++) {
                    if (buildings.get(position).getName().equals(rooms.get(i).getBuildingName())) {
                        splitRooms.add(rooms.get(i));
                    }
                }

                if (view.getId() == R.id.view_item) {
                    roomFragment = RoomFragment.newInstance(defaulters, buildings.get(position).getName(), splitRooms);
                    switchContent(roomFragment, "ROOM");
                }
            }
        });
    }

    private void getRoom() {
        Call<GetRoomResult> getRoomResultCall = NetworkUtil.getInstace().getRoom("");
        getRoomResultCall.enqueue(new Callback<GetRoomResult>() {
            @Override
            public void onResponse(Call<GetRoomResult> call, Response<GetRoomResult> response) {
                GetRoomResult getRoomResult = response.body();
                String result = getRoomResult.getResult();

                if ("success".equals(result)) {
                    rooms = getRoomResult.getRooms();


                    getDefaulter();

                }
            }

            @Override
            public void onFailure(Call<GetRoomResult> call, Throwable t) {

            }
        });
    }

    private void getContract() {
        Call<GetContractResult> getContractResultCall = NetworkUtil.getInstace().getContract("");
        getContractResultCall.enqueue(new Callback<GetContractResult>() {
            @Override
            public void onResponse(Call<GetContractResult> call, Response<GetContractResult> response) {
                GetContractResult getContractResult = response.body();
                String result = getContractResult.getResult();

                if ("success".equals(result)) {
                    contracts = getContractResult.getContracts();


                    setBuildingAdapter(buildings, rooms, contracts, defaulters, leaveContracts);
                }
            }

            @Override
            public void onFailure(Call<GetContractResult> call, Throwable t) {

            }
        });
    }

    private void uploadToSpreadSheet() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://docs.google.com/forms/d/e/")
                .build();
        spreadsheetWebService = retrofit.create(QuestionsSpreadsheetWebService.class);

        String smsMsg = lst.get(i).getMsg();
        String split[] = smsMsg.split("\n");
        String date, account, name, amount;
        if ("[Web발신]".equals(split[0])) {
            date = split[1];
            account = split[2];
            name = split[3];
            amount = split[5];
        } else {
            date = split[0];
            account = split[1];
            name = split[2];
            amount = split[4];
        }
        Log.d(TAG, "name            ::  " + name);
        Log.d(TAG, "account            ::  " + account);
        Log.d(TAG, "amount            ::  " + amount);
        Log.d(TAG, "date            ::  " + date);

        Call<Void> completeQuestionnaireCall = spreadsheetWebService.completeQuestionnaire(name, account, amount, date);
        completeQuestionnaireCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Submitted.      " + response);
                i++;
                if (i < lst.size()) {
                    uploadToSpreadSheet();
                }

                if (i == lst.size()) {
                    i = 0;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Faild", t);
            }
        });

    }

    public void getDefaulter() {
        Call<GetDefaulterResult> getDefaulterResultCall = NetworkUtil.getInstace().getDefaulter("");
        getDefaulterResultCall.enqueue(new Callback<GetDefaulterResult>() {
            @Override
            public void onResponse(Call<GetDefaulterResult> call, Response<GetDefaulterResult> response) {
                GetDefaulterResult getDefaulterResult = response.body();
                String result = getDefaulterResult.getResult();

                if ("success".equals(result)) {
                    defaulters = getDefaulterResult.getDefaulters();

                    getLeaveRoom();

                }
            }

            @Override
            public void onFailure(Call<GetDefaulterResult> call, Throwable t) {
            }
        });

    }

    private void getLeaveRoom() {
        Call<GetContractResult> getContractResultCall = NetworkUtil.getInstace().getLeaveRoom("");
        getContractResultCall.enqueue(new Callback<GetContractResult>() {
            @Override
            public void onResponse(Call<GetContractResult> call, Response<GetContractResult> response) {
                GetContractResult getContractResult = response.body();
                String result = getContractResult.getResult();

                if ("success".equals(result)) {
                    leaveContracts = getContractResult.getContracts();

                    initNav();
                    setInitFrag();
                }
            }

            @Override
            public void onFailure(Call<GetContractResult> call, Throwable t) {

            }
        });
    }

    public List<Sms> getAllSms(String phoneNum) {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        c = cr.query(message, null, "address='" + phoneNum + "'", null, null);
        this.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                if (phoneNum.equals(c.getString(c.getColumnIndexOrThrow("address")))) {
                    lstSms.add(objSms);
                }
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
//        c.close();

        JSLog.D("lstSms.size            :::     " + lstSms.size(), null);
        return lstSms;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        if (c != null) {
            if (!c.isClosed()) {
                c.close();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_building) {
            initNaviButton(item, drawer);
            if (buildingFragment == null) {
                buildingFragment = BuildingFragment.newInstance(rooms);
                getBuilding();
            }
            switchContent(buildingFragment, "BUILDING");
        } else if (id == R.id.nav_new_data) {
            initNaviButton(item, drawer);
            if (newDataFragment == null) {
                newDataFragment = NewDataFragment.newInstance(newDatas, residents, brokers, rooms, buildings);
            }
            switchContent(newDataFragment, "NEW_DATA");
        } else if (id == R.id.nav_existing_data) {
            initNaviButton(item, drawer);
            if (existingDataFragment == null) {
                existingDataFragment = ExistingDataFragment.newInstance(existingDatas, residents, brokers, rooms, buildings);
            }
            switchContent(existingDataFragment, "EXISTING_DATA");
        } else if(id == R.id.nav_realty)    {
            initNaviButton(item, drawer);
            Intent intent = new Intent(this, RealtyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            initNaviButton(item, drawer);
            if (settingFragment == null) {
                settingFragment = SettingFragment.newInstance(this, residents, brokers, nums, defaulters, buildings);
            }
            switchContent(settingFragment, "SETTING");
        } else if (id == R.id.nav_report) {
            initNaviButton(item, drawer);
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("trimmedData", (Serializable) trimmedData);
            startActivity(intent);
        }


        return true;
    }

    @Subscribe
    public void FinishLoad(OnClickEvent event) {

        getTrimmedData(DeviceUtil.getDevicePhoneNumber(this));
        getDefaulter();

    }
}
