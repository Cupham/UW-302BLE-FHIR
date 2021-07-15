package com.example.toan;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.android.bluetoothlegatt.DeviceControlActivity;
import com.example.android.bluetoothlegatt.R;
import com.example.cu.ActivityObj;
import com.example.cu.OneMinuteSummary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SendDataActivity extends Activity
{

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senddata);

        ((TextView)findViewById(R.id.textView_userinfo)).setText("User: " + SavedUser.getCURRENT_USER_ID(getApplicationContext()));

        OneMinuteSummary oms = null;
       /* if(DeviceControlActivity.DATA.size()>=1)
        {
            Log.d("TOAN34","Parse data from DATA");
            oms = new OneMinuteSummary(DeviceControlActivity.DATA.get(DeviceControlActivity.DATA.size()-1));

        }
        else*/
        {

            byte[] aa = SavedData.LoadData(getApplicationContext());
            int n = (aa.length-256)/256;

            oms = new OneMinuteSummary(aa);
            Log.d("TOAN34","Parse data from saved data: bytearraysize: " + aa.length + " expected: " + aa.length/256 + " got:" + oms.getActivities().size() );
        }
        ActivityObj ao = oms.getActivities().get(oms.getActivities().size()-1);


        ((TextView)findViewById(R.id.textView_userinfo_time)).setText(ao.getMeasureTime().toString());
        ((TextView)findViewById(R.id.textView_activity)).setText(ao.getCalories() + " " );
        ((TextView)findViewById(R.id.textView_step)).setText(ao.getStep() + "");
        ((TextView)findViewById(R.id.textView_distance)).setText("No info");
        ((TextView)findViewById(R.id.textView_bpm)).setText("No info");
        ((TextView)findViewById(R.id.textView_sleep)).setText(ao.getSleptHours()+":" + ao.getSleptMinutes());
        ((TextView)findViewById(R.id.textView_sleep_status)).setText(ao.getSleepStatus());

    }

    public  void OnClickCancel(View view)
    {
        this.finish();
    }

    public  void onClickSendData(View view)
    {
    }
}
