package com.sitp.prototype;

import android.content.SharedPreferences;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity
    implements EventListener, DeviceDialog.DeviceDialogListener {

    private List<Device> devices = new ArrayList<>();
    public static final String PREFS_NAME = "PrefsFile";
    private RvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFab();
        initRv();
    }

    private void initFab()
    {
        // speak button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_speak);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Voice Recognition to be Added.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // add button
        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDevice(2, "balcony window", 1);

                Snackbar.make(view, "Default Device Added.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initRv()
    {
        // init recycler view
        RecyclerView rv = findViewById(R.id.rv_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);

        // addDevice(1, "bedroom ac", 2);
        // addDevice(0, "main kitchen", 0);
        // addDevice(2, "balcony window", 1);
        getDevices();
        adapter = new RvAdapter(this, devices);
        rv.setAdapter(adapter);

        adapter.setRvListener(new RvAdapter.RvListener() {
            @Override
            public void onItemClicked(int position) {
                // open dialog
                DialogFragment newFragment = new DeviceDialog();
                Bundle args = new Bundle();
                args.putInt("devicePosition", position);
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "device_dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // get devices from local data
    private boolean getDevices()
    {
        SharedPreferences sharedPreferences=getSharedPreferences(PREFS_NAME, 0);
        String temp = sharedPreferences.getString("deviceList", "");
        ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            devices = (List<Device>)ois.readObject();
        }
        catch (IOException e)
        {
            return false;
        }
        catch(ClassNotFoundException e1)
        {
            return false;
        }

        return true;
    }

    private boolean addDevice(int model, String name, int status)
    {
        try
        {
            Device device = new Device(model, name, status);
            devices.add(device);

            SharedPreferences deviceList = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = deviceList.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(devices);//把对象写到流里
                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                editor.putString("deviceList", temp);
                editor.commit();

                // refresh data in adapter
                adapter.notifyDataSetChanged();
                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        catch (Exception e) {
            // construction fails
            return false;
        }
    }

    @Override
    public void onDialogPositiveClick(DeviceDialog dialog)
    {
        // refresh data
        initRv();

        Snackbar.make(findViewById(R.id.app_bar), "Device Name Saved.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onDialogNegativeClick(DeviceDialog dialog)
    {
        Snackbar.make(findViewById(R.id.app_bar), "Device Name Not Saved.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
