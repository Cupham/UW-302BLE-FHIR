package com.example.toan;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.preference.PreferenceManager;

import java.util.ArrayList;


public class SavedDevices
{
    static public class DeviceInfo
    {
        public String Address="noaddress";
        public String Name="noname";
        public int Type=0;
        public String User_ID="";
        public DeviceInfo()
        {

        }

        public DeviceInfo(String string)
        {
            String[] strings = string.split(";");
            if(strings.length>=1) Address = strings[0];
            if(strings.length>=2) Name = strings[1];
            if(strings.length>=3) Type = Integer.parseInt(strings[2]);
            if(strings.length>=4) User_ID = strings[3];
        }
        public  String toString()
        {
            return Address + ";" + Name + ";" + Type + ";" + User_ID;
        }
    }

    public static ArrayList<DeviceInfo> devices = new ArrayList<DeviceInfo>();
    public static boolean saveArray(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (context);

        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putInt("SavedDevices_size", devices.size());

        for(int i=0;i<devices.size();i++)
        {
            mEdit1.remove("SavedDevices_" + i);
            mEdit1.putString("SavedDevices_" + i, devices.get(i).toString());
        }

        return mEdit1.commit();
    }
    public static ArrayList<DeviceInfo> loadArray(Context mContext)
    {
        SharedPreferences mSharedPreference1 =  PreferenceManager.getDefaultSharedPreferences(mContext);
        devices.clear();
        int size = mSharedPreference1.getInt("SavedDevices_size", 0);

        for(int i=0;i<size;i++)
        {
            devices.add(new DeviceInfo(mSharedPreference1.getString("SavedDevices_" + i, null)));
        }
        return devices;
    }
    public  static boolean tryToAddAndSave(DeviceInfo deviceInfo, Context context)
    {
        boolean is_have = false;
        for(int i=0;i < devices.size(); i++)
        {
            if ( IsSame(devices.get(i),deviceInfo )) {
                is_have = true;
                break;
            }
        }
        if(!is_have)
        {
            devices.add(deviceInfo);
            saveArray(context);
        }
        return !is_have;
    }
    static public boolean IsSame(DeviceInfo df1, DeviceInfo df2)
    {
        if(!df1.Address.equals(df2.Address)) return false;
        if(!df1.User_ID .equals(df2.User_ID)) return false;
        return true;
    }


    public static String getCURRENT_DEVICE_ID(Context context)
    {
        SharedPreferences aa = context.getSharedPreferences("CURRENT_DEVICE_ADDRESS",Context.MODE_PRIVATE);
        return aa.getString("CURRENT_DEVICE_ID","" );
    }
    public  static  void setCURRENT_DEVICE_ID(Context context, String ss)
    {
        SharedPreferences aa = context.getSharedPreferences("CURRENT_DEVICE_ADDRESS",Context.MODE_PRIVATE);
        SharedPreferences.Editor edi = aa.edit();
        edi.putString("CURRENT_DEVICE_ID", ss);
        edi.commit();
    }



}
