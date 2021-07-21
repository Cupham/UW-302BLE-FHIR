package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cu.ActivityObj;
import com.example.cu.BloodPressureObj;
import com.example.cu.OneMinuteSummary;
import com.example.cu.WeightObj;
import com.example.toan.PopupLogin;
import com.example.toan.PopupRegistration;
import com.example.toan.SavedData;
import com.example.toan.SavedUser;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        CheckLogedin();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navmenu);
        navigationView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();



    }
    void CheckLogedin()
    {
        String username = SavedUser.getCURRENT_USER_ID(getApplicationContext());
        TextView tv = findViewById(R.id.textView_user_infor);
        if(!username.equals(""))
        {

            tv.setText("Welcome: " + username);
            findViewById(R.id.button_login).setVisibility(View.GONE);
            findViewById(R.id.button_register).setVisibility(View.GONE);
            findViewById(R.id.button_logout).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_userinfo).setVisibility(View.VISIBLE);
            FillData();

        }
        else
        {
            tv.setText("Not login");
            findViewById(R.id.button_login).setVisibility(View.VISIBLE);
            findViewById(R.id.button_register).setVisibility(View.VISIBLE);
            findViewById(R.id.button_logout).setVisibility(View.GONE);
            findViewById(R.id.layout_userinfo).setVisibility(View.GONE);
        }

    }
    void FillData()
    {
        //toanstt
        OneMinuteSummary oms = null;
        byte[] aa = SavedData.LoadData(getApplicationContext());
        if (aa.length>=256) {
            int n = (aa.length - 256);
            byte[] aa2;
            n = aa.length / 256;
           /* for (int i =0; i < n; i++)
            {
                aa2 = Arrays.copyOfRange(aa, i*256,i*256+256);
                oms = new OneMinuteSummary(aa2);
                Log.d("TOAN12",oms.getActivities().get(0).toString());
            }*/

            aa2 = Arrays.copyOfRange(aa, aa.length - 256, aa.length);
            oms = new OneMinuteSummary(aa2);
            Log.d("TOAN34", "Parse data from saved data: bytearraysize: " + aa.length + " expected: " + aa.length / 256 + " got:" + oms.getActivities().size());

            ActivityObj ao = oms.getActivities().get(oms.getActivities().size() - 1);


            ((TextView) findViewById(R.id.textView_userinfo_time)).setText(ao.getMeasureTime().toString());
            ((TextView) findViewById(R.id.textView_activity)).setText(ao.getCalories() + " ");
            ((TextView) findViewById(R.id.textView_step)).setText(ao.getStep() + "");
            ((TextView) findViewById(R.id.textView_distance)).setText("NA");
            ((TextView) findViewById(R.id.textView_bpm)).setText("NA");
            ((TextView) findViewById(R.id.textView_sleep)).setText(ao.getSleptHours() + ":" + ao.getSleptMinutes());
            ((TextView) findViewById(R.id.textView_sleep_status)).setText(ao.getSleepStatus());


            WeightObj wo = oms.getWeight();
            if (wo != null) {
                ((TextView) findViewById(R.id.textView_weight)).setText(wo.getWeight() + "");
                ((TextView) findViewById(R.id.textView_bmi)).setText("NA");
                ((TextView) findViewById(R.id.textView_weight_time)).setText(wo.getMeasureTime() + "");
            }


            BloodPressureObj bo = oms.getBloodPressure();
            if (bo != null) {
                ((TextView) findViewById(R.id.textView_DIA)).setText(bo.getDIA() + "");
                ((TextView) findViewById(R.id.textView_MAP)).setText(bo.getMAP() + "");
                ((TextView) findViewById(R.id.textView_PUL)).setText(bo.getPUL() + "");
                ((TextView) findViewById(R.id.textView_SYS)).setText(bo.getSYS() + "");
                ((TextView) findViewById(R.id.textView_weight_time)).setText(bo.getMeasureTime() + "");
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id=item.getItemId();

        switch (id){

            case R.id.nav_home:
                Log.d("TOAN234","onNavigationItemSelected" + "home"  );
                Intent h= new Intent(UserActivity.this,MainActivity.class);
                h.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(h);
                break;
            case R.id.nav_about:
                Log.d("TOAN234","onNavigationItemSelected" + "devide"  );
                Intent i= new Intent(UserActivity.this, DeviceScanActivityToan.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
            case R.id.nav_contact:
                Log.d("TOAN234","onNavigationItemSelected" + "user"  );
                Intent g= new Intent(UserActivity.this, UserActivity.class);
                g.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(g);
                break;

        }



        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed()
    {

    }
    public  void onClickRegistration(View view)
    {
        Log.d("TOAN123","onClickRegistration");
        Intent intent = new Intent(this, PopupRegistration.class);
        startActivity(intent);
    }
    public  void onClickLogin(View view)
    {
        Log.d("TOAN123","onClickLogin");
        Intent intent = new Intent(this, PopupLogin.class);
        startActivity(intent);
    }
    public  void onClickLogout(View view)
    {
        SavedUser.setCURRENT_USER_ID(getApplicationContext(), "");
        CheckLogedin();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("TOAN233","UserActivity onResume");
        CheckLogedin();
    }
}