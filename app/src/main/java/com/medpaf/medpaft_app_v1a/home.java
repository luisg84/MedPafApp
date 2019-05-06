package com.medpaf.medpaft_app_v1a;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class home extends AppCompatActivity {

    ImageView btnHeart;
    Button   button2;

    TextView nombretxt,edadtxt,pesotxt;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    //-------------Firebase------------//
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    private home.ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    String dataInPrint=null;
    //-------------------------------------------

    private String archivo="miarchivo";
    private String carpeta = "/archivos";
    String contenido;
    File file;
    String file_path="";
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button2 = (Button) findViewById(R.id.button2);

        goMonitor();

        String[] valoresp;

        nombretxt = (TextView) findViewById(R.id.nombretxt);
        edadtxt = (TextView) findViewById(R.id.edadtxt);
        pesotxt = (TextView) findViewById(R.id.pesotxt);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        obtenerDatos();

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);
                    Log.d("Datos Rcividos1", readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        //float dataP=Integer.parseInt(dataInPrint);
                        Log.d("Datos Rcividos2", dataInPrint);
                        //txtBufferIn.setText("Dato: " + dataInPrint);
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }

        };
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
       // checkBTState();

        //crear();
        inicializarFirebase();


    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


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

    int t=1;

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(BTDevices.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new home.ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public String[] datos;
        int index=0;

        public void run()
        {
            byte[] buffer = new byte[10];

            int bytes;
            Log.e("FG", "1");




            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {

                try {
                    bytes = mmInStream.read(buffer);
                    // Log.d("HG", String.valueOf(bytes));
                    String numReciv=String.valueOf(bytes);
                    char[] cifra = numReciv.toCharArray();

                    Log.d("YT", String.valueOf(bytes));
                    String readMessage = new String(buffer, 0, bytes);
                    //char[] cifra = readMessage.toCharArray();

                    databaseReference.child("dato").child(String.valueOf(index)).setValue(readMessage);
                    Log.d("numeracion", "numero: "+index+" "+readMessage);

                    Log.d("YT", readMessage);
                    index++;
                    DataParser dataParser = new DataParser();

                   /*dataParser.saveToDatabase(
                           dataParser.dataToItems(readMessage, "\\w([A-Z-0-9]*)"));
*/
                    // Log.d("BZ", readMessage);

                    // Envia los datos obtenidos hacia el evento via handler
                    //bluetoothIn.obtainMessage(handlerState, bytes, -0, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void goMonitor() {
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, monitor.class);
                startActivity(i);
            }
        });
    }





    public void Devices(View view) {
        Intent i = new Intent(home.this, BTDevices.class);
        startActivity(i);

    }
}
