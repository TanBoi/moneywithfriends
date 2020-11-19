package com.example.passe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

class BtClient extends Thread {
    private BluetoothSocket mmSocket ;
    private final BluetoothDevice mmDevice ;
    BluetoothAdapter mBluetoothAdapter ;
    UUID mUUID ;
    public BtClient (BluetoothDevice device , BluetoothAdapter myBluetoothAdapter , UUID MY_UUID ) {
        mmDevice = device ;
        mUUID = MY_UUID ;

        Log . v ("client test" , " Found device " + mmDevice.getName() ) ;
        mBluetoothAdapter = myBluetoothAdapter ;
        try {
            //on recupere le bon appareil
            mmSocket = device.createRfcommSocketToServiceRecord( mUUID ) ;
            Log.v ( "client" , " Client get BT remote server :" + mmSocket.getRemoteDevice().getName() ) ;
        } catch ( IOException e ) {
            Log.e ("client"," Socket’s create () method failed" , e ) ;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void run (Context context) {
        mBluetoothAdapter.cancelDiscovery() ;
        try {
            Log.v( "client" , " Client try to connected to server " ) ;
            //attend d'acceptation
            mmSocket.connect() ;
            Log.v( "client" , " Client socket connected " ) ;

            //gestion reception des dettes
            client.manageSocket(context,mmSocket);



            mmSocket.close();
            /*
            mySocketManager = new ManageConnectedSocket( mmSocket ) ;
            mySocketManager.start() ;
             */

        } catch ( IOException connectException) {
//Connection impossible
            try {
                mmSocket.close() ;
            } catch ( IOException closeException) {
                Log . e ( "client" , " Could not close the client socket " , closeException ) ;
            }
            return ;
        }
    }

    // Closes the client socket and causes the thread to finish .
    public void cancel() {
        try {
            mmSocket.close () ;
        } catch (IOException e){
            Log.e( "client" , " Could not close the client socket " , e ) ;
        }
    }
}

class client{

    public static final String SAVE = "Save";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void manageSocket(Context context, BluetoothSocket mmSocket){
        try {
            InputStream in = mmSocket.getInputStream();

            /*
            int total = in.read();

            byte[] bytes = new byte[total];
            for (int i = 0; i < total; i++) {
                bytes[i] = (byte) in.read();
            }

            String str = new String(bytes, StandardCharsets.UTF_8);
            String[] tab;
            Log.v("client recept", str);*/



            //Recupere nombre de dettes a recevoir
            int total = in.read();
            byte[] bytes;
            String[] tab;
            String str;
            for(int y=0; y<total; y++) {
                //NB de caracteres dans la dettes etudié
                total = in.read();
                bytes = new byte[total];
                for (int i = 0; i < total; i++) {
                    //recuperation de chaque caractere
                    bytes[i] = (byte) in.read();
                }
                str = new String(bytes, StandardCharsets.UTF_8);
                Log.v("client recept", str);

                //ecriture dans le telephone client
                SharedPreferences preferences= context.getSharedPreferences(SAVE, context.MODE_PRIVATE);

                SharedPreferences.Editor editor=preferences.edit();
                tab = str.split(" ");
                editor.putString(tab[0], tab[1]);

                editor.commit();
            }

            in.close();


        } catch ( IOException e ) {
            //time out
            Log . e ( "serveur" , "fail envoi" , e );
        }
    }

}