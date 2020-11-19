package com.example.passe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;


class BtServer extends Thread {
        BluetoothServerSocket mmServerSocket = null ;
    public static final String SAVE = "Save";
        public BtServer ( BluetoothAdapter mBTAdapter , UUID mUUID ) {
            UUID MY_UUID = mUUID ;
            BluetoothAdapter mBluetoothAdapter = mBTAdapter ;
            try {
                Log.v ("Serveur", "mBluetoothAdapter ... " ) ;
                mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord( " server " ,MY_UUID ) ;
                Log . v ( "serveur" , " mBluetoothAdapter ok " ) ;
            } catch ( IOException e ) {
                Log . e ( "serveur" , " Socket ’s listen () method failed " , e ) ;
            }
        }



    public void run (Context context) {
        BluetoothSocket socket = null ;
        while ( true ) {
            try {
                Log.v( "serveur" , "attente client " ) ;
                socket = mmServerSocket.accept() ;
                Log.v( "serveur" , " client accepte " ) ;
                Log.v( "serveur" , "run" ) ;

                //gestion envoie des dettes
                server.manageSocket(context, socket);

                socket.close();
                mmServerSocket.close () ;

                /*
                ManageConnectedSocket mManageConnectedSocket = new ManageConnectedSocket(socket) ;
                mManageConnectedSocket.start() ;

                 */

            } catch ( IOException e ) {
                Log . e ( "serveur" , " Socket ’s accept () method failed " , e ) ;
                break ;
            }
        }
    }
    public void cancel () {
        try {
            mmServerSocket.close () ;
        } catch ( IOException e ) {
            Log.e ( "serveur", " Could not close the connect socket " , e ) ;
        }
    }
}

class server{

    public static final String SAVE = "Save";
    public static void manageSocket(Context context, BluetoothSocket socket){
try {
    SharedPreferences preferences= context.getSharedPreferences(SAVE, context.MODE_PRIVATE);

    OutputStream out = socket.getOutputStream();


    //TEEEEST
    /*
    String add = "coucou";
    out.write(add.length());
    out.write(add.getBytes());
*/
    String add;


    Map<String, ?> name = preferences.getAll();
    Log.i("size", String.valueOf(name.size()));
    out.write(name.size()-1);

    //parcours des données du tel et envoie
    for(Map.Entry<String, ?> s: name.entrySet()) {
        if(s.getKey().split("~")[0] != s.getKey())
        {
            add = s.getKey()+" "+s.getValue();

            Log.i("test add", add);
            out.write(add.length());
            out.write(add.getBytes());
        }
    }

out.close();


} catch ( IOException e ) {
//time out
            Log . e ( "serveur" , "fail envoi" , e );
        }
    }

}
