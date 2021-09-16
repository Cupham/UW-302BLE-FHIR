package com.example.uichart.ui.json;

import com.example.toan.ApplianceType;

import java.util.ArrayList;

public class JSONDevice
{
    public String deviceType;
    public String id;
    public  JSONManufacturer manufacturer;
    public  JSONProtocol protocol;
    public String installationLocation;

    //Extras by toanstt
    public ArrayList<String> merged_ids;
    public ArrayList<String> merged_names;
    public ArrayList<ApplianceType>  merged_types;
}
