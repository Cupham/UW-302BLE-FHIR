package com.example.charts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android.bluetoothlegatt.R;
import com.example.uichart.ui.json.JSONDevice;
import com.example.uichart.ui.json.JSONDevices;

public class ApplicanceAdapter extends ArrayAdapter<String> {

    Activity context;
    JSONDevices JSONdevices;
    public ApplicanceAdapter(Activity context, JSONDevices JSONdevice)
    {
        super(context, R.layout.item_applicance);
        this.JSONdevices = JSONdevices;
        this.context = context;
    }


    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        android.view.View rowView = inflater.inflate(R.layout.item_applicance, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        titleText.setText("ok123");


        return rowView;

    }
}

