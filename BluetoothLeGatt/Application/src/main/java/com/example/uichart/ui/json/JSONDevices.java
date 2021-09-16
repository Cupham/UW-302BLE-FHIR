package com.example.uichart.ui.json;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.toan.ApplianceManager;
import com.example.toan.ApplianceType;

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

   public static JSONDevices GetAppliances(JSONDevices devices )
    {
        JSONDevices t = new JSONDevices();
        for(int i =0; i < devices.devices.size(); i++)
        {
            ApplianceType type = ApplianceManager.GetApplianceTypeFromTypeString(devices.devices.get(i).deviceType, devices.devices.get(i).installationLocation );
            if(type == ApplianceType.HOME_AIRCONDITIONER || type == ApplianceType.GENERAL_LIGHTING)
                t.devices.add(devices.devices.get(i));
        }
        return t;
    }
    public JSONDevices MergeThermoDevices()
    {
        JSONDevices t = new JSONDevices();
        JSONDevice merged_temperatures = new JSONDevice();
        merged_temperatures.deviceType="mergedtemperature";
        merged_temperatures.merged_ids = new ArrayList<>();
        merged_temperatures.merged_names = new ArrayList<>();
        merged_temperatures.merged_types = new ArrayList<>();

        t.devices.add(merged_temperatures);
        for(int i =0; i < devices.size(); i++)
        {
            JSONDevice current_device = devices.get(i);
            ApplianceType type = ApplianceManager.GetApplianceTypeFromTypeString(current_device.deviceType, current_device.installationLocation );
            if(type == ApplianceType.CHINICAL_THERMOMETER || type == ApplianceType.TEMPERATURE_SENSOR_INSIDE || type == ApplianceType.TEMPERATURE_SENSOR_OUTSIDE)
            {
                merged_temperatures.merged_ids.add(current_device.id);
                merged_temperatures.merged_names.add(current_device.installationLocation);
                merged_temperatures.merged_types.add(type);
            }
            else t.devices.add(current_device);
        }
        return t;
    }

}
