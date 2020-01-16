package com.example.uninacorse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    int livelloBatteria;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.txtView);

        livelloBatteria=100;

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Batteria");


        sendData(database);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Toast.makeText(MainActivity.this,"Value: "+value,Toast.LENGTH_LONG).show();
                textView.setText("Batteria: "+value+"%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this,"Error"+error,Toast.LENGTH_LONG).show();
            }
        });


    }

    public void sendData(final FirebaseDatabase database)
    {
        final boolean loop=true;
        if(livelloBatteria!=0)
        livelloBatteria-=1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write a  message to the database
                DatabaseReference ref;
                ref = database.getReference();

                if(livelloBatteria==0)
                {
                    ref.child("Batteria").setValue("Ricarica");
                    livelloBatteria=100;
                }

                ref.child("Batteria").setValue(livelloBatteria+"");

                if(loop)
                    sendData(database);

            }
        },1000);

    }
}
