package com.medpaf.medpaft_app_v1a;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {

    private EditText emailtxt,passtxt,nombretxt,pesotxt,edadtxt;
    private Button  registerbtn;
    private ProgressDialog progressDialog;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        //Referenciamos los views
        emailtxt = (EditText) findViewById(R.id.emailtxt);
        passtxt= (EditText) findViewById(R.id.passtxt);
        nombretxt= (EditText) findViewById(R.id.nombretxt);
        pesotxt= (EditText) findViewById(R.id.pesotxt);
        edadtxt= (EditText) findViewById(R.id.edadtxt);



        registerbtn = (Button) findViewById(R.id.registerbtn);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //attaching listener to button

        registrar();


    }

    private void registrar() {
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario(){





        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = emailtxt.getText().toString().trim();
        String password  = passtxt.getText().toString().trim();
        String nombre  = nombretxt.getText().toString().trim();
        String peso  = pesotxt.getText().toString().trim();
        String edad  = edadtxt.getText().toString().trim();

        Log.d("email", email);
        Log.d("email", password);

        //Verificamos que las cajas de texto no esten vacÌas
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        mAuth.createUserWithEmailAndPassword(email,  password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //checking if success
                if(task.isSuccessful()){


                    Toast.makeText(Registro.this,"Se ha registrado el usuario con el email: "+ emailtxt.getText(),Toast.LENGTH_LONG).show();
                    String userID = mAuth.getCurrentUser().getUid();
                    mDatabase.child("Usuarios").child(userID).child("nombre").setValue(nombre);
                    mDatabase.child("Usuarios").child(userID).child("email").setValue(email);
                    mDatabase.child("Usuarios").child(userID).child("peso").setValue(peso);
                    mDatabase.child("Usuarios").child(userID).child("edad").setValue(edad);

                    Intent i = new Intent(Registro.this, login.class);

                    startActivity(i);
                }else{
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){//Si hay una colision de usuario
                        Toast.makeText(Registro.this,"El usuario ya existe",Toast.LENGTH_LONG).show();
                    }else {

                        Toast.makeText(Registro.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                    }
                    }
                progressDialog.dismiss();
            }
        });


    }





}
