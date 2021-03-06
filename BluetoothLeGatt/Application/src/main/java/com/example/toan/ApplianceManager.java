package com.example.toan;

import com.example.android.bluetoothlegatt.R;
import com.example.charts.BloodPressureChart;
import com.example.charts.BodyWeighingChart;
import com.example.charts.GeneralLightingChart;
import com.example.charts.HomeAirConditionerChart;
import com.example.charts.MyChart;
import com.example.charts.SwitchChart;
import com.example.charts.ThermoMeterChart;

public class ApplianceManager
{
    public  static ApplianceType GetApplianceTypeFromTypeString(String s, String installationLocation)
    {
        switch (s)
        {
            case "generalLighting": return ApplianceType.GENERAL_LIGHTING;
            case "switch": return ApplianceType.SWITCH;
            case "bloodPressureMeter": return ApplianceType.BLOOD_PRESSURE_METER;
            case "bodyWeighingMachine": return ApplianceType.BODY_WEIGHING_MACHINE;
            case "clinicalThermometer": return ApplianceType.CHINICAL_THERMOMETER;
            case "homeAirConditioner": return ApplianceType.HOME_AIRCONDITIONER;
            case "mergedtemperature": return ApplianceType.MERGED_TEMPETATURE;
            case "temperatureSensor":
            {
                if(installationLocation=="livingRoom")
                    return ApplianceType.TEMPERATURE_SENSOR_INSIDE;
                else return ApplianceType.TEMPERATURE_SENSOR_OUTSIDE;
            }
            //case "homeAirConditioner": return ApplianceType.HOME_AIRCONDITIONER;
            case "illuminanceSensor": return ApplianceType.ILLUMINANCE_SENSOR;
            default: return ApplianceType.UNKNOWN;
        }
        //return ApplianceType.UNKNOWN;
    }
    public  static boolean IsSupportONOFF(ApplianceType t)
    {
        if(t == ApplianceType.GENERAL_LIGHTING || t == ApplianceType.SWITCH || t == ApplianceType.HOME_AIRCONDITIONER) return true;
        return false;
    }
    public  static boolean IsSupportMODE(ApplianceType t)
    {
        if( t == ApplianceType.HOME_AIRCONDITIONER) return true;
        return false;
    }
    public static int GetIconIdByType(ApplianceType t)
    {
        if(t==ApplianceType.GENERAL_LIGHTING) return R.drawable.lightbulb;
        if(t==ApplianceType.SWITCH) return R.drawable.switch_;
        if(t==ApplianceType.BLOOD_PRESSURE_METER) return R.drawable.bloodpressure;
        if(t==ApplianceType.BODY_WEIGHING_MACHINE) return R.drawable.weighingscale;
        if(t==ApplianceType.CHINICAL_THERMOMETER) return R.drawable.thermometer;
        if(t==ApplianceType.HOME_AIRCONDITIONER) return R.drawable.airconditioner;
        if(t==ApplianceType.MERGED_TEMPETATURE) return R.drawable.temperature;
        if(t==ApplianceType.ILLUMINANCE_SENSOR) return R.drawable.illuminated;

        return R.drawable.unknown;

    }
    public static String GetShortName(ApplianceType t)
    {
        if(t == ApplianceType.TEMPERATURE_SENSOR_INSIDE) return "Inside";
        if(t == ApplianceType.TEMPERATURE_SENSOR_OUTSIDE) return "Outside";
        if(t == ApplianceType.CHINICAL_THERMOMETER) return "Thermometer";
        return "unknow_shortname";

    }
}
