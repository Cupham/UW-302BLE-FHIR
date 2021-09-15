package com.example.android.bluetoothlegatt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.toan.DeviceManager;
import com.example.toan.SavedDevices;

import java.util.ArrayList;

public class SavedDeviceAdapter extends ArrayAdapter<String>
{
    ArrayList<String> maintitle;
    Context context;
    ArrayList<SavedDevices.DeviceInfo> deviceInfos;
    RequestQueue queue;
    View[] views;

    public SavedDeviceAdapter(Context context,ArrayList<String> maintitle,ArrayList<SavedDevices.DeviceInfo> deviceInfos )
    {
        super(context, R.layout.item_appliance,maintitle);
        this.maintitle = maintitle;
        this.deviceInfos = deviceInfos;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        views= new View[maintitle.size()];
    }
    public View getView(int position, View view, ViewGroup parent)
    {
        if(views[position]==null)
            views[position] =  CreateView(position,view,parent);
        return views[position];

    }
    View CreateView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.item_saveddevice, null, true);
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        imageView.setImageResource(DeviceManager.GetIconIdByName(maintitle.get(position)));
        titleText.setText(maintitle.get(position));
        return rowView;
    }
}

