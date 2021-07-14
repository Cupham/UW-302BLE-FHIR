package com.example.toan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class SavedUser
{
    public static ArrayList<String> sKey = new ArrayList<>();
    public static boolean saveArray(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (context);

        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putInt("SavedUser_size", sKey.size());

        for(int i=0;i<sKey.size();i++)
        {
            mEdit1.remove("SavedUser_" + i);
            mEdit1.putString("SavedUser_" + i, sKey.get(i));
        }

        return mEdit1.commit();
    }
    public static void loadArray(Context mContext)
    {
        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(mContext);
        sKey.clear();
        int size = mSharedPreference1.getInt("SavedUser_size", 0);

        for(int i=0;i<size;i++)
        {
            sKey.add(mSharedPreference1.getString("SavedUser_" + i, null));
        }

    }
    public static String getCURRENT_USER_ID(Context context)
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
    }
}
