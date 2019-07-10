package com.medpaf.medpaft_app_v1a;

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

public class MainActivity extends AppCompatActivity {
    Button btnON, btnOFF,btnDisconnect,btnDevices,BtnCrear;
    TextView txtBufferIn;

    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
//-------------Firebase------------//
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    private ConnectedThread MyConexionBT;
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
        setContentView(R.layout.activity_main);

        btnON = (Button) findViewById(R.id.btnOn);
        btnOFF = (Button) findViewById(R.id.btnOff);
        btnDevices = (Button) findViewById(R.id.btnDevices);
        BtnCrear = (Button) findViewById(R.id.BtnCrear);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        txtBufferIn = (TextView) findViewById(R.id.txtBufferIn);


        turnOn();
        turnOff();
        disconnect();
        moreDevices();

        String[] valoresp;
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
                        txtBufferIn.setText("Dato: " + dataInPrint);
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }

        };
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        checkBTState();

        crear();
        inicializarFirebase();

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


    }

    public void removeDatabase(){
        databaseReference.child("dato").removeValue();
    };

    int t=1;

    private void crear() {
        BtnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t++;
                databaseReference.child("dato").child(String.valueOf(t)).setValue("Holamundo");

                /*   file_path=("/sdcard"+carpeta);
                File localFile=new File(file_path);
                Toast.makeText(MainActivity.this, file_path, Toast.LENGTH_SHORT).show();
                if(!localFile.exists()){
                    localFile.mkdirs();
                }
                name=(archivo+".txt");
                Toast.makeText(MainActivity.this, "Archivo: "+file_path, Toast.LENGTH_SHORT).show();
                file=new File(localFile,name);

                try{
                  file.createNewFile();
                    Toast.makeText(MainActivity.this, "Se creo archivo 2", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/






               /* try{
                    Log.d("FG", "2");
                    File tarjetaSD = Environment.getExternalStorageDirectory();
                    // Toast.makeText(MainActivity.this, tarjetaSD.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    File rutaArchivo = new File("/storage/00EA-2342",nombre);
                    OutputStreamWriter crearArchivo = new OutputStreamWriter(openFileOutput(nombre, Activity.MODE_PRIVATE));

                    crearArchivo.write(contenido);
                    crearArchivo.flush();
                    Log.d("FG", tarjetaSD.getAbsolutePath());
                    //Toast.makeText(MainActivity.this, "Se lleno el archivo", Toast.LENGTH_SHORT).show();

                }catch (Exception ex) {
                    // Toast.makeText(MainActivity.this, "No se creo el archivo", Toast.LENGTH_SHORT).show();
                    Log.d("FG", "ex: " + ex);
                }*/
            }
        });
    }

    private void moreDevices() {
        btnDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BTDevices.class);
                startActivity(i);

            }
        });
    }

    private void checkBTState() {

    }

    private void turnOn() {
        btnON.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MyConexionBT.write("1");
            }
        });
    }

    private void turnOff() {
        btnOFF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //MyConexionBT.write("0");
                removeDatabase();
            }
        });

    }

    private void disconnect() {
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if (btSocket!=null)
               {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();}
                }
                finish();
            }
        });
    }

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
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

   /*   @Override
    public void onPause()
  {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }*/

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
}
