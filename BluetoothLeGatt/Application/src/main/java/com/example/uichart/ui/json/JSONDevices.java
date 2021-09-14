package com.example.uichart.ui.json;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

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
}
