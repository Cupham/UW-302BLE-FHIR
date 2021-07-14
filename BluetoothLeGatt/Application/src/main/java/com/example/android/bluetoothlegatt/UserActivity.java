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

import com.example.toan.PopupLogin;
import com.example.toan.PopupRegistration;
import com.example.toan.SavedUser;
import com.google.android.material.navigation.NavigationView;

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
            findViewById(R.id.button_login).setVisibility(View.INVISIBLE);
            findViewById(R.id.button_register).setVisibility(View.INVISIBLE);
            findViewById(R.id.button_logout).setVisibility(View.VISIBLE);
        }
        else
        {
            tv.setText("Not login");
            findViewById(R.id.button_login).setVisibility(View.VISIBLE);
            findViewById(R.id.button_register).setVisibility(View.VISIBLE);
            findViewById(R.id.button_logout).setVisibility(View.INVISIBLE);
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