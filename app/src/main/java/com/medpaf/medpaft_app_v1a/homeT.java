package com.medpaf.medpaft_app_v1a;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class homeT extends AppCompatActivity {

    ImageView btnHeart;
    Button button2;
    TextView nombretxt,edadtxt,pesotxt;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_t);

        button2 = (Button) findViewById(R.id.button2);
        nombretxt = (TextView) findViewById(R.id.nombretxt);
        edadtxt = (TextView) findViewById(R.id.edadtxt);
        pesotxt = (TextView) findViewById(R.id.pesotxt);


        goMonitor();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        obtenerDatos();
    }

    private void obtenerDatos() {
        String userID = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuarios").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    String edad = dataSnapshot.child("edad").getValue(String.class);
                    String peso = dataSnapshot.child("peso").getValue(String.class);
                    nombretxt.setText(nombre);
                    edadtxt.setText("Edad: "+edad);
                    pesotxt.setText("Peso: "+peso+"Kg");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void goMonitor() {
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(homeT.this, monitor.class);
                startActivity(i);
            }
        });
    }





    public void Devices(View view) {
        Intent i = new Intent(homeT.this, BTDevices.class);
        startActivity(i);

    }
}
