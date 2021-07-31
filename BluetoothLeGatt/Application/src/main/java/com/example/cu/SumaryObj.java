package com.example.cu;

import java.util.Arrays;
import java.util.Date;

public class SumaryObj
{
    public float total_calories=0;
    public int BLOOD_SYS=-1;
    public Date BLOOD_DAY;
    public double WEIGHT_WEIGHT=-1;
    public Date WEIGHT_DAY;
    public  SumaryObj(byte[] data)
    {
        //data n*256
        int n = data.length/256;
        int current_day=-1;
        for(int i =n-1; i >=0 ; i--)
        {
            byte[] aa2;
            aa2 = Arrays.copyOfRange(data, i*256, i*256+256);
            UW302Object ms = new UW302Object(aa2);
            if(ms.getActivities()!=null && ms.getActivities().size()>0)
            {
                if (current_day == -1)
                {
                    current_day = ms.getActivities().get(0).getMeasureTime().getDay();
                    ExtractData(ms);
                }
                else
                {
                    int my_day = ms.getActivities().get(0).getMeasureTime().getDay();
                    if (my_day == current_day) ExtractData(ms);
                }
            }
            if(ms.getBloodPressure()!=null && BLOOD_SYS==-1)
            {
                BLOOD_SYS = ms.getBloodPressure().getSYS();
                BLOOD_DAY = ms.getBloodPressure().getMeasureTime();
            }
            if(ms.getWeight()!=null && WEIGHT_WEIGHT==-1)
            {
                WEIGHT_WEIGHT = ms.getWeight().getWeight();
                WEIGHT_DAY = ms.getWeight().getMeasureTime();
            }

        }

    }
    void ExtractData(UW302Object ms)
    {
        for(int j =0; j < ms.getActivities().size(); j++)
        {
            total_calories+= ms.getActivities().get(j).getCalories();
        }
    }
}
