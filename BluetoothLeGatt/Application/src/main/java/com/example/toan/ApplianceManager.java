package com.example.toan;

import com.example.charts.BloodPressureChart;
import com.example.charts.BodyWeighingChart;
import com.example.charts.GeneralLightingChart;
import com.example.charts.HomeAirConditionerChart;
import com.example.charts.MyChart;
import com.example.charts.SwitchChart;
import com.example.charts.ThermoMeterChart;

public class ApplianceManager
{
    public  static ApplianceType GetApplianceTypeFromTypeString(String s)
    {
        switch (s)
        {
            case "generalLighting": return ApplianceType.GENERAL_LIGHTING;
            case "switch": return ApplianceType.SWITCH;
            case "bloodPressureMeter": return ApplianceType.BLOOD_PRESSURE_METER;
            case "bodyWeighingMachine": return ApplianceType.BODY_WEIGHING_MACHINE;
            case "clinicalThermometer": return ApplianceType.CHINICAL_THERMOMETER;
            case "homeAirConditioner": return ApplianceType.HOME_AIRCONDITIONER;
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
}
