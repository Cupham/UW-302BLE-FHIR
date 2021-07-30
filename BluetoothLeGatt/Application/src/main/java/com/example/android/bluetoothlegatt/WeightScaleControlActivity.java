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

import com.example.cu.OneMinuteSummary;
import com.example.toan.SavedData;
import com.example.toan.SendDataActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class WeightScaleControlActivity extends Activity {
    private final static String TAG = WeightScaleControlActivity.class.getSimpleName();
    public  static WeightScaleControlActivity I;
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
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                ((Button)findViewById(R.id.button_connect)).setText("Conn");
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                String s = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                TextView console = findViewById(R.id.textView_console);
                //console.setText(console.getText()+"\n"+"Receiced(" + (++count_receive) + ") len=" + s.length() + ": " + s)
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
        setContentView(R.layout.activity_weightscale);

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
        DATA = SavedData.LoadAndSync(getApplicationContext());
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    String Byearrytointstring(byte[] s) { String ss = "";for (int i = 0; i < s.length; i++) { ss+= Byte.toUnsignedInt(s[i]) + " "; }return ss; }

    BluetoothGattCharacteristic WRITABLE_OBJ;
    BluetoothGattCharacteristic NOTIFY_OBJ;
    String NOTIFY_CHARACTER = "1a0934f1-b364-11e4-ab27-0800200c9a66";
    String NOTIFY_SERVICE = "1a0934f0-b364-11e4-ab27-0800200c9a66";
    String WRITABLE_CHARACTER = "11127001-b364-11e4-ab27-0800200c9a66";
    String WRITABLE_SERVICE = "11127000-b364-11e4-ab27-0800200c9a66";
    public static List<byte[]> DATA = new ArrayList<byte[]>();
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
    private void MysetText(final String value, final TextView textview)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                textview.setText(value);;
            }
        });
    }

    int ID_MAX =-1;
    int ID_MIN =-1;
    int ID_CURRENT =-1;
    Boolean processed_first_package = false;
    int GOT_MESSAGE =0;
    Boolean is_auto_get_10 = true;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void OnClickGetDataAll(View button)
    {
        SavedData.ClearData(getApplicationContext());
        GOT_MESSAGE=0;
        processed_first_package = false;

        TextView console = findViewById(R.id.textView_console);
        console.setText("");
        is_auto_get_10 = true;
        Get10Packages(true);
    }
    public void OnClickSync(View button)
    {
        GOT_MESSAGE=0;
        processed_first_package = false;

        TextView console = findViewById(R.id.textView_console);
        console.setText("");
        is_auto_get_10 = true;
        Get10Packages(false);
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
    public void OnClickGetSendData(View button)
    {
        Log.d("TOAN123","onClickRegistration");
        Intent intent = new Intent(this, SendDataActivity.class);
        startActivity(intent);
    }
    int count_message=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void MyOnRecieve(BluetoothGattCharacteristic charac)
    {
        //TextView console = findViewById(R.id.textView_console);
        //Log.d("TOAN123",""+ charac.getValue().length);
        if ( WRITABLE_OBJ== charac)
        {
            TextView status = findViewById(R.id.textView_toan);
            byte[] b = charac.getValue();
            if(processed_first_package == false)
            {

                if(b.length < 8)
                {
                    Log.d("TOAN324: ","len:"+b.length + " data= " + Byearrytointstring(b));
                    processed_first_package = true;
                }
                else
                {
                    ID_MAX = (Byte.toUnsignedInt(b[6])) * 256 + (Byte.toUnsignedInt(b[7]));
                    String s = "data= " + Byearrytointstring(b);
                    Log.d("TOAN324", s);
                    ID_MIN = (Byte.toUnsignedInt(b[4])) * 256 + (Byte.toUnsignedInt(b[5]));
                    ID_CURRENT = (Byte.toUnsignedInt(b[4])) * 256 + (Byte.toUnsignedInt(b[5]));
                    //MysetText("Downloading " + ID_CURRENT + "/" + ID_MAX);
                    processed_first_package = true;

                    MysetText("Downloading: " + ID_CURRENT + "/" + ID_MAX,status );
                }
            }
            else
            {
                if(b.length >=6)
                {
                    Log.d("TOAN324", "len=" + b.length);
                    ID_CURRENT = (Byte.toUnsignedInt(b[4])) * 256 + (Byte.toUnsignedInt(b[5]));
                    MysetText("Downloading: " + ID_CURRENT + "/" + ID_MAX,status);
                    //MysetText("Downloading " + ID_CURRENT + "/" + ID_MAX);
                }

            }
        }
        else if ( NOTIFY_OBJ== charac)
        {
            byte[] b = charac.getValue();
            int len = b.length;
            int index = 20*count_message;
            for(int i =index; i < index + len; i++)  data_tmp[i] = b[i-index];

            if(count_message==12)
            {
                byte[] data_tmp_copy = java.util.Arrays.copyOf(data_tmp,256);

                //DATA.add(data_tmp_copy);
                Boolean is_need_to_write = SavedData.TrytoAddToList(DATA,data_tmp_copy);
                if(!is_need_to_write) {
                    MysetText("SAVED 256 bytes; current size: " + DATA.size() + "x256");
                    SavedData.SaveData(getApplicationContext(), DATA);
                }
                else MysetText("Skipped to save : " + DATA.size() + "x256");

                OneMinuteSummary a = new OneMinuteSummary(data_tmp_copy);
                TextView aaa = findViewById(R.id.textView_show);
                aaa.setText("DATA:" + a.getActivities().toString());
            }

            count_message++;
            Log.d("TOAN678","count_message: "+ count_message + " len:" + b.length );
            MysetText("Receieve package " + count_message + "/13/ (" + ID_CURRENT  + ")"  );
            if(count_message==10 && is_auto_get_10)
            {
                Get3Packages();
            }
            else if(count_message==13)
            {
                if(ID_MAX <=1)
                {
                    MysetText("FINISHED get " + DATA.size() + "x256 bytes");

                    //Toast toast = Toast.makeText(getApplicationContext() , ("Finished get " + DATA.size() + "x256 bytes"),Toast.LENGTH_LONG);
                    //toast.show();

                    byte[] new_datarequest = {(byte) 0x03, (byte) 0x01, (byte) 0x58, (byte) 0x03};
                    WriteToWriteCharacteric(new_datarequest);
                }
                else
                {
                    GOT_MESSAGE++;
                    MysetText("Finished 13 packages");
                    processed_first_package = false;
                    count_message = 0;
                    Get10Packages(false);
                }
            }
        }
    }
    void ProcessObjects()
    {
        BluetoothGatt mBluetoothGatt = mBluetoothLeService.GetmBluetoothGatt();
        WRITABLE_OBJ = mBluetoothGatt.getService(UUID.fromString(WRITABLE_SERVICE)).getCharacteristic(UUID.fromString(WRITABLE_CHARACTER));
        NOTIFY_OBJ = mBluetoothGatt.getService(UUID.fromString(NOTIFY_SERVICE)).getCharacteristic(UUID.fromString(NOTIFY_CHARACTER));
        if(GOT_MESSAGE==0)
        {
            if (null != WRITABLE_OBJ) MysetText("Check WRITABLE_OBJ OK");
            else MysetText("Check WRITABLE_OBJ FAIL");
            if (null != NOTIFY_OBJ) MysetText("Check NOTIFY_OBJ OK");
            else MysetText("Check NOTIFY_OBJ FAIL");
        }
    }
    void Subcribe()
    {
        BluetoothGatt mBluetoothGatt = mBluetoothLeService.GetmBluetoothGatt();
        MysetText("<< BEGIN SUBCRIBE >>");
        UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        mBluetoothGatt.setCharacteristicNotification(WRITABLE_OBJ,true);
        BluetoothGattDescriptor descriptor = WRITABLE_OBJ.getDescriptor(uuid);//UUID.fromString(WRITABLE_CHARACTER));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        mBluetoothGatt.setCharacteristicNotification(NOTIFY_OBJ,true);
        BluetoothGattDescriptor descriptor2 = WRITABLE_OBJ.getDescriptor(uuid);//UUID.fromString(WRITABLE_CHARACTER));
        descriptor2.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor2);
        MysetText("<< FINISH SUBCRIBE >>");
    }
    byte[] data_tmp = new byte[256];
    @RequiresApi(api = Build.VERSION_CODES.O)
    void Get10Packages(Boolean is_all )
    {
        ProcessObjects();
        count_message=0;
        BluetoothGatt mBluetoothGatt = mBluetoothLeService.GetmBluetoothGatt();
        BluetoothGattService Service = WRITABLE_OBJ.getService();
        if (Service == null)  Log.e("TOAN", "service not found!");
        UUID uuid_write = UUID.fromString(WRITABLE_CHARACTER);
        BluetoothGattCharacteristic charac = Service.getCharacteristic(uuid_write);

        if (charac == null) Log.e("TOAN", "char not found!");

        if(GOT_MESSAGE==0)
        {
            byte[] new_datarequest = {(byte) 0x04, (byte) 0x01, (byte) 0x58, (byte) GOT_MESSAGE, (byte) 0x00};
            if (is_all)  new_datarequest[4] = (byte)1;
            MysetText("WRITING: " + Byearrytointstring(new_datarequest));
            WriteToWriteCharacteric(new_datarequest);
        }
        else
        {
            byte[] new_datarequest = {(byte) 0x03, (byte) 0x01, (byte) 0x58, (byte) 01};
            MysetText("WRITING: " + Byearrytointstring(new_datarequest));
            WriteToWriteCharacteric(new_datarequest);
        }

        if(GOT_MESSAGE==0)
        Subcribe();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void Get3Packages()
    {
        byte[] new_datarequest = {(byte) 0x03, (byte) 0x01, (byte) 0x58, (byte) 0x01};
        WriteToWriteCharacteric(new_datarequest);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void WriteToWriteCharacteric(byte[] new_datarequest)
    {
        BluetoothGatt mBluetoothGatt = mBluetoothLeService.GetmBluetoothGatt();
        BluetoothGattService Service = WRITABLE_OBJ.getService();
        if (Service == null)  Log.e("TOAN", "service not found!");
        UUID uuid_write = UUID.fromString(WRITABLE_CHARACTER);
        BluetoothGattCharacteristic charac = Service.getCharacteristic(uuid_write);
        if (charac == null) Log.e("TOAN", "char not found!");
        //byte[] new_datarequest = {(byte) 0x03, (byte) 0x01, (byte) 0x58, (byte) 0x01};
        charac.setValue(new_datarequest);

        boolean status = mBluetoothGatt.writeCharacteristic(charac);
        if(status==true) MysetText("WRITE OK OK : " + Byearrytointstring(new_datarequest));
        else MysetText("WRITE FAIL");
    }



}


































