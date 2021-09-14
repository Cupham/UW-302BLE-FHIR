package com.example.charts;

import android.content.Context;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.example.uichart.ui.json.JSONDevice;

public class MyChart
{
    public MyChart(Context context, LinearLayout rl, JSONDevice myJSONdevice, String url, RequestQueue queue)
    {

    }
    public void GetData(RequestQueue queue)
    {

    }
    final static public MyChart NewChartClass(Context context, LinearLayout view, JSONDevice myJSONdevice, String URL, RequestQueue queue)
    {
        MyChart chart;
        switch (myJSONdevice.deviceType)
        {
            case "generalLighting":
                chart = new GeneralLightingChart(context, view,myJSONdevice,URL,queue);
                break;
            case "switch":
                chart = new SwitchChart(context, view,myJSONdevice,URL,queue);
                break;
            case "bloodPressureMeter":
                chart = new BloodPressureChart(context, view,myJSONdevice,URL,queue);
                break;
            case "bodyWeighingMachine":
                chart = new BodyWeighingChart(context, view,myJSONdevice,URL,queue);
                break;
            case "clinicalThermometer":
                chart = new ThermoMeterChart(context, view,myJSONdevice,URL,queue);
                break;
            case "homeAirConditioner":
                chart = new HomeAirConditionerChart(context, view,myJSONdevice,URL,queue);
                break;
            default:
                chart = new MyChart(context, view,myJSONdevice,URL,queue);
                break;
        }
        return chart;
    }
}

