package com.example.charts;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.toan.ApplianceManager;
import com.example.toan.ApplianceType;
import com.example.uichart.ui.json.JSONDevice;

public class MyChart
{
    public TextView TextView_Mode=null;
    public MyChart(Context context, LinearLayout rl, JSONDevice myJSONdevice, String url, RequestQueue queue)
    {

    }
    public void GetData(RequestQueue queue)
    {

    }
    final static public MyChart NewChartClass(Context context, LinearLayout view, JSONDevice myJSONdevice, String URL, RequestQueue queue)
    {
        ApplianceType type = ApplianceManager.GetApplianceTypeFromTypeString(myJSONdevice.deviceType, myJSONdevice.installationLocation);
        MyChart chart;


        switch (type)
        {
            case GENERAL_LIGHTING:
                chart = new GeneralLightingChart(context, view,myJSONdevice,URL,queue);
                break;
            case SWITCH:
                chart = new SwitchChart(context, view,myJSONdevice,URL,queue);
                break;
            case BLOOD_PRESSURE_METER:
                chart = new BloodPressureChart(context, view,myJSONdevice,URL,queue);
                break;
            case BODY_WEIGHING_MACHINE:
                chart = new BodyWeighingChart(context, view,myJSONdevice,URL,queue);
                break;
            case CHINICAL_THERMOMETER:
                chart = new ThermoMeterChart(context, view,myJSONdevice,URL,queue);
                break;
            case HOME_AIRCONDITIONER:
                chart = new HomeAirConditionerChart(context, view,myJSONdevice,URL,queue);
                break;
            default:
                chart = new MyChart(context, view,myJSONdevice,URL,queue);
                break;
        }
        return chart;
    }
}

