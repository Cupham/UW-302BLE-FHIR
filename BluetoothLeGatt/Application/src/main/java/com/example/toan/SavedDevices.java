package com.example.toan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;


public class SavedDevices
{
    static public class DeviceInfo
    {
        public String Address;
        public String Name;
        public int Type;
        public DeviceInfo()
        {

        }

        public DeviceInfo(String string)
        {
            String[] strings = string.split(";");
            Address = strings[0];
            Name = strings[1];
            Type = Integer.parseInt(strings[2]);
        }
        public  String toString()
        {
            return Address + ";" + Name + ";" + Type;
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
        if(devices.get(i).Address.equals(deviceInfo.Address))
        {
            is_have = true;
            break;
        }
        if(!is_have)
        {
            devices.add(deviceInfo);
            saveArray(context);
        }
        return !is_have;
    }
   /* public static String getCURRENT_USER_ID(Context context)
    {
        SharedPreferences aa = context.getSharedPreferences("toanstt",Context.MODE_PRIVATE);
        return aa.getString("CURRENT_USER_ID","" );
    }
    public  static  void setCURRENT_USER_ID(Context context, String ss)
    {
        SharedPreferences aa = context.getSharedPreferences("toanstt",Context.MODE_PRIVATE);
        SharedPreferences.Editor edi = aa.edit();
        edi.putString("CURRENT_USER_ID", ss);
        edi.commit();
    }*/
}
