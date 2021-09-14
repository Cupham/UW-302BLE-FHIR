package com.example.uichart.ui.json;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class JSONDevices
{
    public ArrayList<JSONDevice> devices = new ArrayList<>();
    public ArrayAdapter<String> GetAdapter(Context context)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        for(int i=0; i < devices.size(); i++)
        {
            adapter.add(devices.get(i).id);
        }
        return adapter;
    }
    public  String[] GetIDSStrings()
    {
        List<String> aa = new ArrayList<>();
        for(int i=0; i < devices.size(); i++)
        {
            aa.add(devices.get(i).id);
        }
        return aa.toArray(new String[0]);
    }
}
