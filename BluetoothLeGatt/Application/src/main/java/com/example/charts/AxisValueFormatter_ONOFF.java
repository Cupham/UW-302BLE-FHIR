package com.example.charts;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

public class AxisValueFormatter_ONOFF extends IndexAxisValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        if (value == 0.0) return "OFF";
        if (value == 1.0) return "ON";
        return "";
    }

}
