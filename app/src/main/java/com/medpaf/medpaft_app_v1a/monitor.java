package com.medpaf.medpaft_app_v1a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class monitor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
    }

    public void Return(View view) {
        Intent i = new Intent(monitor.this, homeT.class);
        startActivity(i);

    }
}
