package com.medpaf.medpaft_app_v1a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class BTDevices extends AppCompatActivity {

    // Depuración de LOGCAT
    private static final String TAG = "BTDevices";
    // Declaracion de ListView
    ListView listDevices;
    // String que se enviara a la actividad principal, mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Declaracion de campos
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevices);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //---------------------------------
        BTState();

        // Inicializa la array que contendra la lista de los dispositivos bluetooth vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.name_devices);//
       // for(int u=0;u<mPairedDevicesArrayAdapter.)
        Log.d("boton scanear1", "entro");
        // Presenta los disposisitivos vinculados en el ListView
        listDevices = (ListView) findViewById(R.id.listDevices);
        listDevices.setAdapter(mPairedDevicesArrayAdapter);
        listDevices.setOnItemClickListener(mDeviceClickListener);
        // Obtiene el adaptador local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();



        // Obtiene un conjunto de dispositivos actualmente emparejados y agregua a 'pairedDevices'
        Set <BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        String [] devicesPaired=new String[pairedDevices.size()];
        int index=0;

        // Adiciona un dispositivos previo emparejado al array
        if (pairedDevices.size() > 0)
        {
            //for(int u=0;u<pairedDevices.size();u++){ }

            for (BluetoothDevice device : pairedDevices) { //EN CASO DE ERROR LEER LA ANTERIOR EXPLICACION
                //Log.d("ciclo for", device.getName());
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                //opcion para conexion automatica aun (tiene fallos)
                /*  String temp = "XM-15";
                int vt = temp.compareTo(device.getName());
                Log.d("variableTemp", temp);
                if (vt==0){
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    String info = device.getAddress();

                    Intent i = new Intent(BTDevices.this, MainActivity.class);//<-<- PARTE A MODIFICAR >->->
                    i.putExtra(EXTRA_DEVICE_ADDRESS, info);
                    startActivity(i);
                    Log.d("macInfo", info);

                }else{
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                }*/



            }
        }
    }

    // Configura un (on-click) para la lista
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            // Obtener la dirección MAC del dispositivo, que son los últimos 17 caracteres en la vista
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Realiza un intent para iniciar la siguiente actividad
            // mientras toma un EXTRA_DEVICE_ADDRESS que es la dirección MAC.
            Intent i = new Intent(BTDevices.this, MainActivity.class);//<-<- PARTE A MODIFICAR >->->
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
        }
    };

    private void BTState() {
        // Comprueba que el dispositivo tiene Bluetooth y que está encendido.
        mBtAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                //Solicita al usuario que active Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
}
