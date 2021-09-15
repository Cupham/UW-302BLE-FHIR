package com.example.toan;

import android.widget.ImageView;

import com.example.android.bluetoothlegatt.R;

public class DeviceManager
{
    public static DeviceType GetDeviceTypeByName(String s)
    {
        if(s.indexOf("UC-352BLE") >=0) return DeviceType.WEIGHTINGSCALE;
        if(s.indexOf("UT-201BLE") >=0 || s.indexOf("UT201BLE") >=0) return DeviceType.THERMOMETER;
        if(s.indexOf("UW-302BLE") >=0) return DeviceType.WATCH;
        if(s.indexOf("UA-651BLE") >=0|| s.indexOf("UA651BLE") >=0) return DeviceType.BLOODPRESSURE;
        return DeviceType.UNKNOWN;
    }
    public static int GetIconIdByName(String s)
    {
        DeviceType type = GetDeviceTypeByName(s);
        if(type == DeviceType.WEIGHTINGSCALE) return R.drawable.weighingscale;
        if(type == DeviceType.THERMOMETER) return R.drawable.thermometer;
        if(type == DeviceType.WATCH) return R.drawable.smartwatch;
        if(type == DeviceType.BLOODPRESSURE) return R.drawable.bloodpressure;
        return R.drawable.unknown;
    }
}

enum DeviceType
{
    THERMOMETER,
    WEIGHTINGSCALE,
    WATCH,
    BLOODPRESSURE,
    UNKNOWN
}