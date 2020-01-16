package com.example.uninacorse;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private int livelloBatteria, accIniziale;
    private TextView textView,txtAcc,txtTemp;
    private LocationManager locationManager;
    private Location location;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.txtView);
        txtAcc=findViewById(R.id.txtAcc);
        txtTemp=findViewById(R.id.txtTemp);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        /*AggionaPosizione aggionaPosizione=new AggionaPosizione();
        aggionaPosizione.start();*/

        livelloBatteria=100;
        accIniziale=0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        sendBatteryData(database);
        readBatteryData(database);

        sendAccData(database);
        readAccData(database);

    }

    private void readAccData(FirebaseDatabase database) {

        DatabaseReference myRef=database.getReference("Accelerazione");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                txtAcc.setText("Accelerazione: "+value+"%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this,"Error"+error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendAccData(final FirebaseDatabase database)
    {

        if(accIniziale!=200)
            accIniziale++;
        else
            accIniziale=60;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                DatabaseReference myRef=database.getReference();

                myRef.child("Accelerazione").setValue(accIniziale+"");

                sendAccData(database);
            }
        },2000);
    }

    private void sendBatteryData(final FirebaseDatabase database)
    {
        final boolean loop=true;
        if(livelloBatteria!=0)
            livelloBatteria-=1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write a message to the database
                DatabaseReference ref;
                ref = database.getReference();

                if(livelloBatteria==0)
                {
                    ref.child("Batteria").setValue("Ricarica");
                    livelloBatteria=100;
                }

                ref.child("Batteria").setValue(livelloBatteria+"");

                if(loop)
                    sendBatteryData(database);

            }
        },1000);

    }

    private void readBatteryData(FirebaseDatabase database) {

        DatabaseReference myRef=database.getReference("Batteria");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textView.setText("Batteria: "+value+"%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this,"Error"+error,Toast.LENGTH_LONG).show();
            }
        });

    }

    public class AggionaPosizione extends Thread{

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            while(true)
            {
                getLocation(database);
            }

        }
    }

    public void getLocation(FirebaseDatabase database)
    {
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            OnGps();
        }
        else
        {
            if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                Location locationGPS=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location locationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location locationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if(locationGPS!=null)
                {
                    double lat=locationGPS.getLatitude();
                    double longi=locationGPS.getLongitude();

                    String latitude=String.valueOf(lat);
                    //textView2.setText(latitude);
                    Log.d("MainActivity",latitude);
                    DatabaseReference myRef;
                    myRef=database.getReference();
                    myRef.child("Latitudine").setValue(latitude);
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    },2000);*/
                }
                else
                if(locationNetwork!=null)
                {
                    double lat=locationNetwork.getLatitude();
                    double longi=locationNetwork.getLongitude();

                    String latitude=String.valueOf(lat);
                }
                else
                if(locationPassive!=null)
                {
                    double lat=locationPassive.getLatitude();
                    double longi=locationPassive.getLongitude();

                    String latitude=String.valueOf(lat);
                }
                else
                {
                    Toast.makeText(this,"No pos",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void OnGps() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


}