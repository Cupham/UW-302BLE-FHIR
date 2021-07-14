package com.example.toan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.android.bluetoothlegatt.R;

import java.util.ArrayList;

public class PopupLogin extends Activity
{
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_login);
    }

    public  void OnClickCancel(View view)
    {
        this.finish();
    }

    public  void OnClickOK(View view)
    {
        String name = ((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString();
        String pass1 = ((EditText) findViewById(R.id.editTextTextPassword)).getText().toString();
        if(name.length()<=1) name = "androidusertest";
        if(pass1.length()<=1) pass1  = "password";

        ((TextView)findViewById(R.id.textView_message)).setText(name + " " + pass1 + " " );
        Log.d("TOAN3556",name + " " + pass1 + " " );

        SavedUser.loadArray(getApplicationContext());
        ArrayList<String> mylist = SavedUser.sKey;
        Boolean is_found= false;

        for( int i =0; i < mylist.size(); i++)
        {
            String userdatastring = mylist.get(i);
            String[] ss = userdatastring.split(":");
            if(name.equals((ss[0])))
            {

                is_found = true;
                break;
            }
        }
        if(is_found)
        {
            SavedUser.setCURRENT_USER_ID(getApplicationContext(), name);
            finish();
        }
        else
        {
            ((TextView)findViewById(R.id.textView_message)).setText("User not found, please register a new one" );
        }

    }

}
