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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.toan.SavedDevices;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivityToan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicescan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navmenu);
        navigationView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();


        //new

        if(getActionBar()!=null)
            getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        checkPermissionREAD_EXTERNAL_STORAGE(this);
        checkPermissionWRITE_EXTERNAL_STORAGE(this);
        //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //toanstt

        ArrayList<SavedDevices.DeviceInfo> deviceInfos = SavedDevices.loadArray(getApplicationContext());
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i =0; i < deviceInfos.size(); i++)
            arrayList.add(deviceInfos.get(i).Name);
        Log.d("TOAN234", "LOADED: " + deviceInfos.size() + " devices" );
        ListView listKnownDevices = (ListView)findViewById(R.id.listview_knowndevices);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listKnownDevices.setAdapter(adapter);
        listKnownDevices.setOnItemClickListener((parent, view, position, id) ->{
            Log.d("TOAN23", "ON CLICKING " + position);
            ProcessOnClickKnownDevice(deviceInfos.get(position));
        });


    }
    void ProcessOnClickKnownDevice(SavedDevices.DeviceInfo deviceInfo)
    {

        if(deviceInfo.Name.indexOf("UC-352BLE") >=0)
        {
            BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UC_352;
            final Intent intent = new Intent(this, UC_352WeightScaleControlActivity.class);
            intent.putExtra(UC_352WeightScaleControlActivity.EXTRAS_DEVICE_NAME, deviceInfo.Name);
            intent.putExtra(UC_352WeightScaleControlActivity.EXTRAS_DEVICE_ADDRESS,  deviceInfo.Address);
            if (mScanning)
            {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
        else if(deviceInfo.Name.indexOf("UT-201BLE") >=0 || deviceInfo.Name.indexOf("UT201BLE") >=0)
        {
            BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UT_201;
            final Intent intent = new Intent(this, UT_201ThermoMeterControlActivity.class);
            intent.putExtra(UT_201ThermoMeterControlActivity.EXTRAS_DEVICE_NAME, deviceInfo.Name);
            intent.putExtra(UT_201ThermoMeterControlActivity.EXTRAS_DEVICE_ADDRESS, deviceInfo.Address);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
        else if(deviceInfo.Name.indexOf("UW-302BLE") >=0)
        {
            BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UW_302;
            final Intent intent = new Intent(this, UW_302ActivityTrackerControlActivity.class);
            intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_NAME, deviceInfo.Name);
            intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_ADDRESS, deviceInfo.Address);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
        else if(deviceInfo.Name.indexOf("UA-651BLE") >=0|| deviceInfo.Name.indexOf("UA651BLE") >=0)
        {
            BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UA_651;
            final Intent intent = new Intent(this, UA_651BloodPressureControlActivity.class);
            intent.putExtra(UA_651BloodPressureControlActivity.EXTRAS_DEVICE_NAME, deviceInfo.Name);
            intent.putExtra(UA_651BloodPressureControlActivity.EXTRAS_DEVICE_ADDRESS, deviceInfo.Address);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
        else //if(device.getName().indexOf("BL") >=0)
        {
            BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UNKNOWN;
            final Intent intent = new Intent(this, UW_302ActivityTrackerControlActivity.class);
            intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_NAME, deviceInfo.Name);
            intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_ADDRESS, deviceInfo.Address);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }


    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id=item.getItemId();

        switch (id){
            case R.id.nav_home:
                Intent h= new Intent(DeviceScanActivityToan.this,MainActivity.class);
                h.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(h);
                break;
            case R.id.nav_contact:
                Intent g= new Intent(DeviceScanActivityToan.this,UserActivity.class);
                g.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(g);
                break;

        }



        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private LeDeviceListAdapter mLeDeviceListAdapter=null;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[],  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, yay! Start the Bluetooth device scan.
                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }

        }
    }
    public static boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    //showDialog("External storage", context,Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    101);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public static boolean checkPermissionWRITE_EXTERNAL_STORAGE(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    //showDialog("External storage", context,Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    102);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        ListView lv = findViewById(R.id.listview_scan);
        {
            // Initializes list view adapter.
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            //setListAdapter(mLeDeviceListAdapter);
            lv.setAdapter(mLeDeviceListAdapter);

            lv.setOnItemClickListener((parent, view, position, id) ->
            {

                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;


                Log.d("TOAN234","on clidk: " + device.getName());
                if(device.getName().indexOf("UC-352BLE") >=0)
                {
                    BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UC_352;
                    SaveDevice(device);
                    final Intent intent = new Intent(this, UC_352WeightScaleControlActivity.class);
                    intent.putExtra(UC_352WeightScaleControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                    intent.putExtra(UC_352WeightScaleControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                    if (mScanning)
                    {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }
                    startActivity(intent);
                }
                else if(device.getName().indexOf("UT-201BLE") >=0 || device.getName().indexOf("UT201BLE") >=0)
                {
                    BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UT_201;
                    Log.d("TOAN234", "clicking UT-201BLE");
                    SaveDevice(device);
                    final Intent intent = new Intent(this, UT_201ThermoMeterControlActivity.class);
                    intent.putExtra(UT_201ThermoMeterControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                    intent.putExtra(UT_201ThermoMeterControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                    if (mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }
                    startActivity(intent);
                }
                else if(device.getName().indexOf("UW-302BLE") >=0)
                {
                    BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UW_302;
                    //SaveDevice(device);
                    final Intent intent = new Intent(this, UW_302ActivityTrackerControlActivity.class);
                    intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                    intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                    if (mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }
                    startActivity(intent);
                }
                else if(device.getName().indexOf("UA-651BLE") >=0|| device.getName().indexOf("UA651BLE") >=0)
                {
                    BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UA_651;
                    SaveDevice(device);
                    final Intent intent = new Intent(this, UA_651BloodPressureControlActivity.class);
                    intent.putExtra(UA_651BloodPressureControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                    intent.putExtra(UA_651BloodPressureControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                    if (mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }
                    startActivity(intent);
                }
                else //if(device.getName().indexOf("BL") >=0)
                {
                    BluetoothLeService.deviceType = BluetoothLeService.AandDDeviceType.UNKNOWN;
                    final Intent intent = new Intent(this, UW_302ActivityTrackerControlActivity.class);
                    intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                    intent.putExtra(UW_302ActivityTrackerControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                    if (mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }
                    startActivity(intent);
                }

                if(BluetoothLeService.deviceType!= BluetoothLeService.AandDDeviceType.UNKNOWN)
                {

                }
            });
            scanLeDevice(true);
        }
    }
    void SaveDevice(BluetoothDevice device )
    {
        SavedDevices.DeviceInfo df = new SavedDevices.DeviceInfo();
        df.Address = device.getAddress();
        df.Name = device.getName();
        df.Type = device.getType();

        SavedDevices.tryToAddAndSave(df, getApplicationContext());
        Log.d("TOAN234", "SAVING A DEVICE");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }*/

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
    @Override
    public void onBackPressed()
    {
        Intent h= new Intent(DeviceScanActivityToan.this,MainActivity.class);
        h.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(h);
    }
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivityToan.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
