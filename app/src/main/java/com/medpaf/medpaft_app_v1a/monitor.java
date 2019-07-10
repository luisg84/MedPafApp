package com.medpaf.medpaft_app_v1a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

public class monitor extends AppCompatActivity {
    android.support.v7.widget.CardView enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        enviar = ( android.support.v7.widget.CardView) findViewById(R.id.SendMensage);
    }

    public void Return(View view) {
        Intent i = new Intent(monitor.this, homeT.class);
        startActivity(i);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje("2721757385","Saludos de medpaf");
            }
        });

    }

    private void enviarMensaje(String numero,String mensaje){
            try{
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(numero, null, mensaje,null,null);
                Toast.makeText(this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(this, "Mensaje No Enviado", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

    }
}
