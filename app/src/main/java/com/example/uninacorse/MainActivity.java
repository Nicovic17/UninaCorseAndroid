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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView txtAccLat, txtAccLong, txtSpeed, txtThrottle, txtBreak, txtLiquidoRaff, txtBatteryHV, txtBatteryLV, txtPressioneFreno, txtFlussoRaff,
    txtRollio, txtBeccheggio, txtImbardata, txtEngine1, txtEngine2, txtEngine3, txtEngine4, txtIGBT1,txtIGBT2,txtIGBT3,txtIGBT4;
    private LocationManager locationManager;
    private SeekBar speedBar,throttleBar,breakBar,longAccBar,latAccBar, tempBar, batteryBar, batteryBarLV, pressioneFrenoBar,flussoRaffBar, rollioBar, beccheggioBar,
    imbardataBar, tempEngine1Bar, tempEngine2Bar, tempEngine3Bar, tempEngine4Bar, tempIGBTEngine1Bar,tempIGBTEngine2Bar,tempIGBTEngine3Bar,tempIGBTEngine4Bar;
    private Location location;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    //Rotazione volante
    private ImageView wheel;
    private double mCurrAngle=0;
    private double mPrevAngle=0;
    private FirebaseDatabase firebaseDatabase;

    private ScrollView scrollView;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout=(Button)findViewById(R.id.btnLogout) ;
        //Rotazione volante
        wheel=findViewById(R.id.wheel);

        wheel.setOnTouchListener(this);

        //Rotazione volante

        speedBar = findViewById(R.id.speedbar);
        throttleBar = findViewById(R.id.throttlebar);
        breakBar = findViewById(R.id.breakbar);
        longAccBar = findViewById(R.id.longAccBar);
        latAccBar = findViewById(R.id.latAccBar);

        //TextView
        txtSpeed = findViewById(R.id.txtSpeed);
        txtThrottle = findViewById(R.id.txtThrottle);
        txtBreak = findViewById(R.id.txtBreak);
        txtLiquidoRaff=findViewById(R.id.txtLiquidoRaff);
        txtBatteryHV=findViewById(R.id.txtBatteryHV);
        txtBatteryLV=findViewById(R.id.txtBatteryLV);
        txtPressioneFreno=findViewById(R.id.txtPressioneFrano);
        txtFlussoRaff=findViewById(R.id.txtFlussoRaff);
        txtRollio=findViewById(R.id.txtRollio);
        txtBeccheggio=findViewById(R.id.txtBeccheggio);
        txtImbardata=findViewById(R.id.txtImbardata);
        txtEngine1=findViewById(R.id.txtTempEngine1);
        txtEngine2=findViewById(R.id.txtTempEngine2);
        txtEngine3=findViewById(R.id.txtTempEngine3);
        txtEngine4=findViewById(R.id.txtTempEngine4);

        txtIGBT1=findViewById(R.id.txtTempIGBTEngine1);
        txtIGBT2=findViewById(R.id.txtTempIGBTEngine2);
        txtIGBT3=findViewById(R.id.txtTempIGBTEngine3);
        txtIGBT4=findViewById(R.id.txtTempIGBTEngine4);

        txtAccLat=findViewById(R.id.txtLatAcc);
        txtAccLong=findViewById(R.id.txtLongAcc);



        tempBar=findViewById(R.id.tempBar);
        batteryBar=findViewById(R.id.batteryBar);
        batteryBarLV=findViewById(R.id.batteryBarLV);
        pressioneFrenoBar=findViewById(R.id.pressioneFrenoBar);
        flussoRaffBar=findViewById(R.id.flussoRaffBar);
        rollioBar=findViewById(R.id.rollioBar);
        beccheggioBar=findViewById(R.id.beccheggioBar);
        imbardataBar=findViewById(R.id.imbardataBar);
        //Spie motori
        tempEngine1Bar=findViewById(R.id.tempEngine1Bar);
        tempEngine2Bar=findViewById(R.id.tempEngine2Bar);
        tempEngine3Bar=findViewById(R.id.tempEngine3Bar);
        tempEngine4Bar=findViewById(R.id.tempEngine4Bar);

        tempIGBTEngine1Bar=findViewById(R.id.tempTempIGBTEngine1);
        tempIGBTEngine2Bar=findViewById(R.id.tempTempIGBTEngine2);
        tempIGBTEngine3Bar=findViewById(R.id.tempTempIGBTEngine3);
        tempIGBTEngine4Bar=findViewById(R.id.tempTempIGBTEngine4);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseDatabase=database;
        final DatabaseReference myRef = database.getReference();

        tempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtLiquidoRaff.setText("Temperatura liquido raffreddamento:"+ " "+ progress + "°");
                myRef.child("storico/297/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        batteryBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=100)
                {
                    txtBatteryHV.setText("Percentuale batteria HighVoltage:"+ " "+ progress + "%");
                    myRef.child("storico/017/"+(System.currentTimeMillis())).setValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        batteryBarLV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=100)
                    {
                        txtBatteryLV.setText("Percentuale batteria LowVoltage:"+ " "+ progress + "%");
                        myRef.child("storico/018/"+(System.currentTimeMillis())).setValue(progress);
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pressioneFrenoBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPressioneFreno.setText("Pressione circuito freno:"+ " "+ progress + "Pa");
                myRef.child("storico/296/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        flussoRaffBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtFlussoRaff.setText("Flusso liquido di raffreddamento:"+ " "+ progress + "l/m");
                myRef.child("storico/314/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rollioBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtRollio.setText("Angolo di rollio:"+ " "+ progress + "°");
                myRef.child("storico/313/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        beccheggioBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtBeccheggio.setText("Angolo di beccheggio:"+ " "+ progress + "°");
                myRef.child("storico/311/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imbardataBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtImbardata.setText("Angolo di beccheggio:"+ " "+ progress + "°");
                myRef.child("storico/312/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempEngine1Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtEngine1.setText("Temperatura motore 1:"+ " "+ progress + "°");
                myRef.child("storico/287/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempEngine2Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtEngine2.setText("Temperatura motore 2:"+ " "+ progress + "°");
                myRef.child("storico/288/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempEngine3Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtEngine3.setText("Temperatura motore 3:"+ " "+ progress + "°");
                myRef.child("storico/289/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempEngine4Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtEngine4.setText("Temperatura motore 4:"+ " "+ progress + "°");
                myRef.child("storico/290/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempIGBTEngine1Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtIGBT1.setText("Temperatura IGBT motore 1:"+ " "+ progress + "°");
                myRef.child("storico/292/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempIGBTEngine2Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtIGBT2.setText("Temperatura IGBT motore 2:"+ " "+ progress + "°");
                myRef.child("storico/293/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempIGBTEngine3Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtIGBT3.setText("Temperatura IGBT motore 3:"+ " "+ progress + "°");
                myRef.child("storico/294/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tempIGBTEngine4Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtIGBT4.setText("Temperatura IGBT motore 4:"+ " "+ progress + "°");
                myRef.child("storico/295/"+(System.currentTimeMillis())).setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        txtSpeed.setText("Velocità vettura: "+i+"km/h");
                        myRef.child("storico/004/" + (System.currentTimeMillis())).setValue(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );



        throttleBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        txtThrottle.setText("Throttle: "+i+"%");
                        myRef.child("storico/011/" + System.currentTimeMillis()).setValue(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        breakBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtBreak.setText("Break: "+i+"%");
                myRef.child("storico/012/" + System.currentTimeMillis()).setValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        longAccBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){
            float temp;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                temp = ((float) i)/10;
                txtAccLong.setText("Accelerazione longitudinale: "+temp+"m/s^2");
                myRef.child("storico/002/" + System.currentTimeMillis()).setValue(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        latAccBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){
            float temp;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //txtBreak.setText("break: "+i+"%");
                temp = ((float) i)/10;
                txtAccLat.setText("Accelerazione latitudinale: "+temp+"m/s^2");
                myRef.child("storico/003/" + System.currentTimeMillis()).setValue(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    protected void logout()
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        updateUI();
    }

    protected void updateUI()
    {
        Intent intent= new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
















    //Rotazione volante

    private void writeAngleWheel(final FirebaseDatabase firebaseDatabase, double angolo)
    {
        DatabaseReference myRef=firebaseDatabase.getReference("/");
        String newAngolo=new DecimalFormat("##.##").format(angolo);

        myRef.child("storico/001/" + System.currentTimeMillis()).setValue(angolo);
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