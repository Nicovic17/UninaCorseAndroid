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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private int livelloBatteria, accIniziale, temp, speed;
    private TextView textView, txtAcc, txtTemp, txtSpeed, txtThrottle, txtBreak;
    private LocationManager locationManager;
    private SeekBar speedBar,throttleBar,breakBar;
    private Location location;
    private Button incTempButt,decTempButt,eqTempButt,incBatteryButt,decBatteryButt,eqBatteryButt, testButton;

    //Rotazione volante
    private ImageView wheel;
    private double mCurrAngle=0;
    private double mPrevAngle=0;
    private FirebaseDatabase firebaseDatabase;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Rotazione volante
        wheel=findViewById(R.id.wheel);

        wheel.setOnTouchListener(this);

        //Rotazione volante


        speedBar = findViewById(R.id.speedbar);
        throttleBar = findViewById(R.id.throttlebar);
        breakBar = findViewById(R.id.breakbar);
        txtSpeed = findViewById(R.id.speedTxt);
        txtThrottle = findViewById(R.id.throttleTxt);
        txtBreak = findViewById(R.id.breakTxt);

        incTempButt = findViewById(R.id.tempPlusButt);
        decTempButt = findViewById(R.id.tempMinButt);
        eqTempButt = findViewById(R.id.tempEqButt);
        testButton=findViewById(R.id.test);




        incBatteryButt=findViewById(R.id.batteryPlusButt);
        decBatteryButt=findViewById(R.id.batteryMinButt);
        eqBatteryButt=findViewById(R.id.batteryEqButt);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        /*AggionaPosizione aggionaPosizione=new AggionaPosizione();
        aggionaPosizione.start();*/

        livelloBatteria = 0;
        accIniziale = 0;
        temp = 20;
        speed = 100;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseDatabase=database;
        final DatabaseReference myRef = database.getReference();

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeData(database);
            }
        });

        speedBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        txtSpeed.setText("speed: "+i+"km/h");
                        myRef.child("Speed").setValue(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        txtSpeed.setText("speed: "+seekBar.getProgress()+"km/h");
                        myRef.child("Speed").setValue(seekBar.getProgress());
                    }
                }
        );

        throttleBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        txtThrottle.setText("throttle: "+i+"%");
                        myRef.child("Throttle").setValue(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        txtThrottle.setText("throttle: "+seekBar.getProgress()+"%");
                        myRef.child("Throttle").setValue(seekBar.getProgress());
                    }
                }
        );

        breakBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtBreak.setText("break: "+i+"%");
                myRef.child("Break").setValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtBreak.setText("break: "+seekBar.getProgress()+"%");
                myRef.child("Break").setValue(seekBar.getProgress());
            }
        });

        incTempButt.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                temp++;
                myRef.child("Temp").setValue(temp);
            }
        });
        decTempButt.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                temp--;
                myRef.child("Temp").setValue(temp);
            }
        });
        eqTempButt.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {

                myRef.child("Temp").setValue(temp);
            }
        });

        incBatteryButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(livelloBatteria!=100)
                {
                    livelloBatteria++;
                    myRef.child("Batteria").setValue(livelloBatteria);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Batteria al 100%",Toast.LENGTH_LONG).show();
                }

            }
        });

        decBatteryButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(livelloBatteria!=0)
                {
                    livelloBatteria-=1;
                    myRef.child("Batteria").setValue(livelloBatteria);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Batteria allo 0%", Toast.LENGTH_LONG).show();
                }
            }
        });

        eqBatteryButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this,"Setto batteria a 0",Toast.LENGTH_LONG).show();
                myRef.child("Batteria").setValue(livelloBatteria);
            }
        });
    }

    private void sendSpeedData(final FirebaseDatabase database) {

        final DatabaseReference myRef = database.getReference();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (speed == 100) {
                    myRef.child("Speed").setValue("Sei al massimo.");
                } else {
                    speed += 5;
                    myRef.child("Speed").setValue(speed + "km/h");
                }


                sendSpeedData(database);
            }
        }, 1500);

    }

    private void sendTempData(final FirebaseDatabase database) {

        final DatabaseReference myRef = database.getReference();

        if (temp == 100) {
            myRef.child("Temp").setValue("A breve prende fuoco tutto!");
            temp = 25;
        } else
            temp++;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                myRef.child("Temp").setValue(temp + "°");

                sendTempData(database);
            }
        }, 5000);


    }

    private void readAccData(FirebaseDatabase database) {

        DatabaseReference myRef = database.getReference("Accelerazione");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                txtAcc.setText("Accelerazione: " + value + "%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Error" + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendAccData(final FirebaseDatabase database) {

        if (accIniziale != 200)
            accIniziale++;
        else
            accIniziale = 60;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                DatabaseReference myRef = database.getReference();

                myRef.child("Accelerazione").setValue(accIniziale + "");

                sendAccData(database);
            }
        }, 2000);
    }

    private void sendBatteryData(final FirebaseDatabase database) {
        final boolean loop = true;
        if (livelloBatteria != 0)
            livelloBatteria -= 1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write a message to the database
                DatabaseReference ref;
                ref = database.getReference();

                if (livelloBatteria == 0) {
                    ref.child("Batteria").setValue("Ricarica");
                    livelloBatteria = 100;
                }

                ref.child("Batteria").setValue(livelloBatteria + "");

                if (loop)
                    sendBatteryData(database);

            }
        }, 1000);

    }

    private void writeData(final FirebaseDatabase firebaseDatabase){

        Date currentDate= Calendar.getInstance().getTime();
        final String data=currentDate.toString();

        final boolean loop=true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Write a message to the database
                DatabaseReference myRef=firebaseDatabase.getReference("Sensori").child(data);
                for(int i=0;i<132;i++)
                {
                    myRef.child(i+"").child("Nome").setValue("Nome");
                    myRef.child(i+"").child("Valore").setValue("Val");

                }

                if(loop)writeData(firebaseDatabase);

            }
        }, 1000);


    }



    private void readBatteryData(FirebaseDatabase database) {

        DatabaseReference myRef = database.getReference("Batteria");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textView.setText("Batteria: " + value + "%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Error" + error, Toast.LENGTH_LONG).show();
            }
        });

    }

    //Rotazione volante

    private void writeAngleWheel(final FirebaseDatabase firebaseDatabase, double angolo)
    {
        DatabaseReference myRef=firebaseDatabase.getReference("realTime/001/value");
        String newAngolo=new DecimalFormat("##.##").format(angolo);

        myRef.setValue(angolo);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final float xc=wheel.getWidth()/2;
        final float yc=wheel.getHeight()/2;

        final float x=event.getX();
        final float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                wheel.clearAnimation();
                mCurrAngle=Math.toDegrees(Math.atan2(x-xc,yc-y));
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                mPrevAngle=mCurrAngle;
                mCurrAngle=Math.toDegrees(Math.atan2(x-xc,yc-y));
                animate(mPrevAngle,mCurrAngle,0);
                System.out.println("Curr angle: "+mCurrAngle);
                writeAngleWheel(firebaseDatabase,mCurrAngle);
                break;
            }
            case MotionEvent.ACTION_UP:{
                mPrevAngle=mCurrAngle=0;
                break;
            }
        }
        return true;
    }

    private void animate(double fromDegrees, double toDegrees, long durationMillis){
        final RotateAnimation rotate=new RotateAnimation((float)fromDegrees,(float)toDegrees,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        wheel.startAnimation(rotate);
        writeAngleWheel(firebaseDatabase,mCurrAngle);
    }



    //Rotazione volante

    public class AggionaPosizione extends Thread {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            while (true) {
                getLocation(database);
            }

        }
    }

    public void getLocation(FirebaseDatabase database) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGps();
        } else {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (locationGPS != null) {
                    double lat = locationGPS.getLatitude();
                    double longi = locationGPS.getLongitude();

                    String latitude = String.valueOf(lat);
                    //textView2.setText(latitude);
                    Log.d("MainActivity", latitude);
                    DatabaseReference myRef;
                    myRef = database.getReference();
                    myRef.child("Latitudine").setValue(latitude);
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    },2000);*/
                } else if (locationNetwork != null) {
                    double lat = locationNetwork.getLatitude();
                    double longi = locationNetwork.getLongitude();

                    String latitude = String.valueOf(lat);
                } else if (locationPassive != null) {
                    double lat = locationPassive.getLatitude();
                    double longi = locationPassive.getLongitude();

                    String latitude = String.valueOf(lat);
                } else {
                    Toast.makeText(this, "No pos", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void OnGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}