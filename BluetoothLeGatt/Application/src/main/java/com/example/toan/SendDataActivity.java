package com.example.toan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

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
            int n = (aa.length-256);
            byte[] aa2;
            n = aa.length/256;
           /* for (int i =0; i < n; i++)
            {
                aa2 = Arrays.copyOfRange(aa, i*256,i*256+256);
                oms = new OneMinuteSummary(aa2);
                Log.d("TOAN12",oms.getActivities().get(0).toString());
            }*/

            aa2 = Arrays.copyOfRange(aa, aa.length-256,aa.length);
            oms = new OneMinuteSummary(aa2);
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
        byte[] aa = SavedData.LoadData(getApplicationContext());
        int n = (aa.length-256);
        byte[] aa2;
        aa2 = Arrays.copyOfRange(aa, aa.length-256,aa.length);
        final OneMinuteSummary oms = new OneMinuteSummary(aa2);
        final String username = SavedUser.getCURRENT_USER_ID(getApplicationContext());
        //ActivityObj ao = oms.getActivities().get(oms.getActivities().size()-1);
       /* int n = aa.length/256;
        OneMinuteSummary oms = null;
        for (int i =0; i < n; i++)
        {
            byte[] aa2;
            aa2 = Arrays.copyOfRange(aa, i*256,i*256+256);
            oms = new OneMinuteSummary(aa2);
            Log.d("TOAN12",oms.getActivities().get(0).toString());
        }*/



        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                sendToFHIRServer(username,oms);
            }
        };
        t.start();

    }


    public boolean sendToFHIRServer(String user_ID, OneMinuteSummary data){
        Observation obs = new Observation();
        obs.getCode().addCoding().setSystem("http://www.acme.org/nutritionorders")
                .setCode("123")
                .setDisplay("Consumed calories in kCal");
        obs.setValue( new Quantity().setValue(data.getActivities().get(data.getActivities().size() -1).getCalories())
                    .setUnit("kCal").setSystem("http://unitsofmeasure.org").setCode("aaa")
        );
        obs.setSubject(new Reference(user_ID));
        org.hl7.fhir.r4.model.Bundle b = new org.hl7.fhir.r4.model.Bundle();
        b.setType( org.hl7.fhir.r4.model.Bundle.BundleType.TRANSACTION);
        b.addEntry()
                .setResource(obs)
                .getRequest()
                .setUrl("Observation")
                .setMethod( org.hl7.fhir.r4.model.Bundle.HTTPVerb.POST);
        FhirContext ctx = FhirContext.forR4();
        System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(b));

// Create a client and post the transaction to the server
        IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        org.hl7.fhir.r4.model.Bundle resp = client.transaction().withBundle(b).execute();
        Log.d("TOAN3434",resp.toString());
        return true;
    }
}










































