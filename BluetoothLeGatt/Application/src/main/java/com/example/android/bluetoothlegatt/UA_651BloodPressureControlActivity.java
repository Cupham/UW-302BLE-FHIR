/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.cu.MyFHIRClient;
import com.example.cu.UA_651Obj;
import com.example.toan.SavedUser;
import com.example.toan.SendDataActivity;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ca.uhn.fhir.rest.api.MethodOutcome;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class UA_651BloodPressureControlActivity extends Activity {
    private final static String TAG = UA_651BloodPressureControlActivity.class.getSimpleName();
    public  static UA_651BloodPressureControlActivity I;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    //int count_receive =0;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))
            {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                ((Button)findViewById(R.id.button_connect)).setText("Dis");
                MysetText("Connected to the weight scale");

            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                ((Button)findViewById(R.id.button_connect)).setText("Conn");
                MysetText("Disconnected");
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                Subcribe();
            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                String s = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                //TextView console = findViewById(R.id.textView_console);
                //console.setText("KHUE:" + console.getText()+"\n"+"Receiced(" + (++count_receive) + ") len=" + s.length() + ": " + s)
                //BluetoothGattCharacteristic aa =((BluetoothGattCharacteristic)intent.getExtras().get("com.example.bluetooth.le.EXTRA_DATA"));
                //MysetText("KHUE1: get read data " + intent.getDataString() + aa.getValue().toString());
                //MyOnRecieveRead(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            }

            //Log.d("TOAN:Received", intent.toString());
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    private void clearUI()
    {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        I = this;
        setContentView(R.layout.activity_ua_651);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value2);

        if(getActionBar()!=null) {
            getActionBar().setTitle(mDeviceName);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        MysetText("test my consle");
        //DATA = SavedData.LoadAndSync(getApplicationContext());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private void MysetText(final String value)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                TextView console = findViewById(R.id.textView_console);
                CharSequence tt = console.getText();
                if (tt.length()>1024)
                    tt = tt.subSequence(0,1023);
                console.setText(value + "\n" + tt);;
            }
        });
    }



    public void OnClickConnect(View button)
    {
        if(mConnected == true)
        {
            mBluetoothLeService.disconnect();
        }
        else
        {
            mBluetoothLeService.connect(mDeviceAddress);
        }
    }
    public static UA_651Obj UA_651 = null;
    public void OnClickGetSendData(View button)
    {
        MysetText("Sending to FHIR");
        Thread createObs = new Thread() {
            @Override
            public void run() {
                String id = SavedUser.getCURRENT_USER_ID(getApplicationContext());
                Patient patient = MyFHIRClient.getClient().read().resource(Patient.class).withId(id).execute();
                MethodOutcome outcome =  MyFHIRClient.getClient().create()
                        .resource(UA_651.toObservation(patient))
                        .prettyPrint().encodedJson().execute();
                String createdID =outcome.getId().getBaseUrl() + "/" + outcome.getId().getIdPart();
                MysetText("Created Observation ID " + createdID);
                SavedUser.setCURRENT_OBSERVATION_UA_ID(I.getApplicationContext(),createdID);
            }
        };
        Thread updateObs = new Thread() {
            @Override
            public void run() {
                String id = SavedUser.getCURRENT_USER_ID(getApplicationContext());
                Patient patient = MyFHIRClient.getClient().read().resource(Patient.class).withId(id).execute();
                String obsID = SavedUser.getCURRENT_OBSERVATION_UA_ID(I.getApplicationContext());

                Observation obs = UA_651.toObservation(patient);
                obs.setId(obsID);
                MethodOutcome outcome =  MyFHIRClient.getClient().update()
                        .resource(obs)
                        .prettyPrint().encodedJson().execute();
                String createdID = outcome.getId().getIdPart();
                MysetText("Updated Observation ID " + createdID);
            }
        };
        if(SavedUser.getCURRENT_OBSERVATION_UA_ID(I.getApplicationContext()) != "") {
            updateObs.start();
        } else {
            createObs.start();
        }
    }
    int count_message=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void MyOnRecieve(BluetoothGattCharacteristic charac)
    {
        if(charac ==bloodpressureChar )
        {
            MysetText("RE: bloodpressureChar: " + charac.getValue().toString() + " " +charac.getValue().length);


            UA_651Obj ua = new UA_651Obj(charac.getValue());

            ((TextView)findViewById(R.id.textView_blood_time)).setText(ua.getMeasureTime() + "");
            ((TextView)findViewById(R.id.textView_DIA)).setText(ua.getDIA() + "");
            ((TextView)findViewById(R.id.textView_MAP)).setText(ua.getMAP() + "");
            ((TextView)findViewById(R.id.textView_PUL)).setText(ua.getPUL() + "");
            ((TextView)findViewById(R.id.textView_SYS)).setText(ua.getSYS() + "");

            UA_651 = ua;
        }
        else MysetText("RE: UN: " + charac.getValue().toString() + " " +charac.getValue().length);
    }
    public  void MyOnRecieveRead(BluetoothGattCharacteristic characteristic)
    {
        MysetText("READ: bloodpressureChar "+ characteristic.getValue().toString());
    }
    BluetoothGattCharacteristic bloodpressureChar = null;
    BluetoothGattCharacteristic bloodpressureFeatureChar = null;
    void Subcribe()
    {
        //mBluetoothLeService.getSupportedGattServices()
        MysetText("<< BEGIN SUBCRIBE >>");
        BluetoothGatt mBluetoothGatt = mBluetoothLeService.GetmBluetoothGatt();
        List<BluetoothGattService> listServices = mBluetoothGatt.getServices();

        for(int i =0; i < listServices.size(); i++)
        {
            List<BluetoothGattCharacteristic> listChas= listServices.get(i).getCharacteristics();
            Log.d("TOAN12345SERVICE",listServices.get(i).getUuid().toString() );
            for(int j =0; j < listChas.size(); j++) {
                Log.d("TOAN12345CHAR",listChas.get(j).getUuid().toString() );
                if (listChas.get(j).getUuid().toString().indexOf("00002a49") == 0)
                {
                    //MysetText("Weight scale service found: " + listServices.get(i).getUuid().toString()); //1809
                    MysetText("bloodpressureFeatureChar found: " + listChas.get(j).getUuid().toString());
                    bloodpressureFeatureChar = listChas.get(j);
                }
                if(listChas.get(j).getUuid().toString().indexOf("00002a35")==0)
                {
                    //MysetText("Weight scale service found: " + listServices.get(i).getUuid().toString()); //1809
                    MysetText("bloodpressureChar found: " + listChas.get(j).getUuid().toString());
                    bloodpressureChar =listChas.get(j);
                }
            }
        }

        if(bloodpressureChar!=null)
        {
            MysetText("<< BEGIN SUBCRIBE >>");


            mBluetoothGatt.setCharacteristicNotification(bloodpressureChar,true);
            UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
            BluetoothGattDescriptor descriptor= bloodpressureChar.getDescriptor(uuid);


            MysetText("Setting descriptor: " + descriptor.getUuid().toString());
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);



            //mBluetoothGatt.readCharacteristic(bloodpressureFeatureChar);


            MysetText("<< FINISH SUBCRIBE >>");
        }



        //weightscalechar uuid = 00002a9d
        //mBluetoothGatt.setCharacteristicNotification(weightscalechar,true);
        //UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        //BluetoothGattDescriptor descriptor= weightscalechar.getDescriptor(uuid);
        //MysetText("Setting descriptor: " + descriptor.getUuid().toString());
        //descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        //mBluetoothGatt.writeDescriptor(descriptor);




    }



}


































