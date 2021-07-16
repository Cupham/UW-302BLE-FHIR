package com.example.toan;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SavedData
{

    public static void SaveData(Context context, List<byte[]> DATA)
    {
        //Context context = getApplicationContext();
        File dir = new File(context.getFilesDir(), "mydir");
        if (!dir.exists())  dir.mkdir();
        try {
            File gpxfile = new File(dir, "DATA.txt");
            FileOutputStream fos = new FileOutputStream(gpxfile.getPath());
            for (int i = 0; i < DATA.size(); i++)
                fos.write(DATA.get(i));
            fos.close();
            Log.d("TOAN999","WRITE DATA TO FILE OK " + DATA.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static byte[] LoadData(Context context)
    {
        //Context context = getApplicationContext();
        File dir = new File(context.getFilesDir(), "mydir");
        if (!dir.exists())  dir.mkdir();
        try {
            File gpxfile = new File(dir, "DATA.txt");
            FileInputStream fos = new FileInputStream(gpxfile.getPath());
            long n = gpxfile.length();
            byte[] aa = new byte[(int) n];
            fos.read(aa);
            fos.close();
            Log.d("TOAN999","READ DATA TO FILE OK " + aa.length);
            return aa;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<byte[]> LoadAndSync(Context context)
    {
        List<byte[]> l = new ArrayList<>();
        byte[] bytes = LoadData(context);
        int n = bytes.length/256;
        for(int i =0; i < n; i ++)
        {
            int index = i*256;
            byte[] bytes2 = new byte[256];
            for(int j =0; j < 256; j++)
            {
                bytes2[j] =bytes[index+j];
            }
            l.add(bytes2);
        }
        return l;
    }
    public  static void ClearData(Context context)
    {
        List<byte[]> l = new ArrayList<>();
        SaveData(context, l);
    }
    public static Boolean TrytoAddToList(List<byte[]> original, byte[] newbytes)
    {
        original.add(newbytes);
        return true;
    }
    public static Boolean TrytoAddToList_old(List<byte[]> original, byte[] newbytes)
    {
        int n = original.size();
        Boolean is_have = false;
        for(int i =0; i < n; i++)
        {
            int j=0;
            byte[] bytesold = original.get(i);
            for(j =0; j < 256; j++)
            {
                if(bytesold[j] != newbytes[j]) break;
            }
            if(j >= 256)
            {
                is_have = true;
                break;
            }
        }
        if(is_have)
        {

        }
        else
        {
            original.add(newbytes);
        }
        return !is_have;
    }


}
