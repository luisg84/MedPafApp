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

public class login extends AppCompatActivity {

    Button loginbtn;
    EditText usertxt,passtxt;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbtn = (Button) findViewById(R.id.loginbtn);
        usertxt = (EditText) findViewById(R.id.nombretxt);
        passtxt = (EditText) findViewById(R.id.passtxt);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        //goHome();
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usertxt.getText().toString().trim();
                String password  = passtxt.getText().toString().trim();
                loginUser(email,password);

            }
        });

    }


   private void  acceder(){
        Intent i = new Intent(login.this, homeT.class);
        startActivity(i);
    };


   /* private void goHome() {
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { loginUser(); }
        });
    }*/



    public void register(View view) {
        Intent i = new Intent(login.this, Registro.class);
        startActivity(i);
    }



    private void loginUser(String user, String pass){





        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = user;
        String password  = pass;

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


        progressDialog.setMessage("logueando");
        progressDialog.show();

        //creating a new user
        mAuth.signInWithEmailAndPassword(email,  password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //checking if success
                if(task.isSuccessful()){


                    Toast.makeText(login.this,"Bienvenido: "+ usertxt.getText(),Toast.LENGTH_LONG).show();

                    usertxt.setText("");
                    passtxt.setText("");
                    acceder();


                }else{


                        Toast.makeText(login.this, "Error en los datos ", Toast.LENGTH_LONG).show();

                }
                progressDialog.dismiss();
            }
        });


    }
}
