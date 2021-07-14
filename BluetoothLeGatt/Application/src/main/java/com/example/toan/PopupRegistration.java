package com.example.toan;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.android.bluetoothlegatt.R;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.w3c.dom.Text;

import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.gclient.IClientExecutable;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.util.BundleUtil;

public class PopupRegistration extends Activity
{
    public  static PopupRegistration I;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable android.os.Bundle savedInstanceState)
    {
        I = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_register);
    }

    public  void OnClickCancel(View view)
    {
        this.finish();
    }
    String name;
    String pass1, pass2;
    public  void OnClickOK(View view)
    {
        name = ((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString();
        pass1 = ((EditText) findViewById(R.id.editTextTextPassword)).getText().toString();
        pass2 = ((EditText) findViewById(R.id.editTextTextPassword2)).getText().toString();
        if(name.length()<=1) name = "androidusertest";
        if(pass1.length()<=1) pass1 = pass2 = "password";

        ((TextView)findViewById(R.id.textView_message)).setText(name + " " + pass1 + " " + pass2);
        Log.d("TOAN3556",name + " " + pass1 + " " + pass2);

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

        Patient patient = new Patient();
        patient.setId(name); //ID
        patient.addName().addGiven("CULOLO");
        patient.setActive(true);
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                Log.d("TOAN343", client.toString());
                MethodOutcome result= client.update().resource(patient).execute();

                 Log.d("TOAN343", result.getId().toString());
                SharedPreferences sharedPreferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
                SavedUser.loadArray(getApplicationContext());
                SavedUser.sKey.add(name + ":" + pass1);
                SavedUser.saveArray(getApplicationContext());
                Log.d("TOAN343","Saved user: " +name + ":" + pass1);
                SavedUser.setCURRENT_USER_ID(getApplicationContext(),name);
                PopupRegistration.I.finish();

            }
        };
        t.start();

        Log.d("TOAN3435","asd");


    }

    public class PatientFhirHelper
    {

        private IGenericClient client;
        private FhirContext ctx;

        public PatientFhirHelper() {
            ctx = FhirContext.forR4();
            client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        }

        public List<Patient> getPatients()
        {
            // Invoke the client
            Bundle bundle = client.search().forResource(Patient.class)
                    .where(new TokenClientParam("gender").exactly().code("unknown"))
                    .prettyPrint()
                    .returnBundle(Bundle.class)
                    .execute();
            return BundleUtil.toListOfResourcesOfType(ctx, bundle, Patient.class);
        }

        public IGenericClient getClient() {
            return client;
        }
    }


}
