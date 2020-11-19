package com.example.passe;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "0";
    private static final int DISCOVERY_REQUEST = 1;
    TextView mesgText;

    private ArrayList deviceListName;
    private ArrayList<BluetoothDevice> deviceBT = new ArrayList<BluetoothDevice>();
    private ArrayAdapter arrayAdapter;
    LinearLayout paired;
    LinearLayout nonpaired;
    Button btn;
    Button btn_get;
    Button btn_rec;
    Button btn_env;
    BluetoothDevice deviceChoose;
    TextView apC;

    boolean firstElement=true;

    //broadcast de decouverte des appareils
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context , Intent intent ) {
            String action = intent.getAction();
            mesgText.setText ( action ) ;

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                mesgText.setText ( " Discovery started " ) ;
                Log.v( TAG , "ACTION_DISCOVERY_STARTED" ) ;
            //Recherche de devices
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            //Fin de recherche
                mesgText.setText ( " Discovery finished " ) ;
                Log.v( TAG , " ACTION_DISCOVERY_FINISHED" ) ;
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                //discovery finishes , dismis p r o g r e s s dialog
                mesgText.setText ( " Discovery finished " ) ;
                Log.v( TAG , " ACTION_STATE_CHANGED" ) ;
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)){
            //???
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE ) ;

                if (!deviceListName.isEmpty() && firstElement ) {
                    firstElement = false ;
                    deviceListName.clear() ;
                }
                deviceBT.add(device) ;
                deviceListName.add(device.getName() ) ;
                arrayAdapter.notifyDataSetChanged() ;
                Log . v ( TAG , " Found device " + device.getName() ) ;
            } else if ( BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){

                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR ) ;
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if ( state == BluetoothDevice.BOND_BONDED ) {
                    Toast.makeText(getApplicationContext(), "Paired", Toast.LENGTH_SHORT).show();
                } else if ( state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(getApplicationContext(), "Non Paired", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int ENABLE_BLUETOOTH = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apC = findViewById(R.id.apC);
        //Test permissions problemes ACTION_FOUND
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionCheck);


        setContentView(R.layout.activity_bluetooth);

        mesgText = findViewById(R.id.BTlist);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Device support Bluetooth ", Toast.LENGTH_LONG).show();
        }


        paired = findViewById(R.id.paired);
        nonpaired = findViewById(R.id.nonpaired);
        btn_get = findViewById(R.id.button_get);
        btn_get.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                getPairedDevices(mBluetoothAdapter);
            }
        });


        //envoie maj des données
        btn_env = findViewById(R.id.env);
        btn_env.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                //uuid fictif de l'appli
                String str = "1234";
                UUID uuid = UUID.nameUUIDFromBytes(str.getBytes());

                BtServer btserver = new BtServer(mBluetoothAdapter, uuid);

                btserver.run(getApplicationContext());
            }
        }
        );

        //reception maj des données
        btn_rec = findViewById(R.id.rec);
        btn_rec.setOnClickListener(new View.OnClickListener() {

                                       @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                       @Override
                                       public void onClick(View view) {
                                           //uuid fictif de l'appli
                                           String str = "1234";
                                           UUID uuid = UUID.nameUUIDFromBytes(str.getBytes());

                                           BtClient btclient = new BtClient((BluetoothDevice) deviceChoose, mBluetoothAdapter, uuid);

                                           btclient.run(getApplicationContext());

                                       }
                                   }
        );


        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {





                makeDiscoverable();
                mBluetoothAdapter.startDiscovery();

                IntentFilter filter = new IntentFilter() ;

                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) ;
                filter.addAction(BluetoothDevice.ACTION_FOUND ) ;
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) ;
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) ;
                filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED) ;
                registerReceiver(mReceiver, filter ) ;


                //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //getPairedDevices(mBluetoothAdapter);

            }
        });
    }






    @Override
    public void onDestroy() {
        super.onDestroy();

        //unregisterReceiver(mReceiver);
    }

    private void pairDevice(BluetoothDevice device ) {
        try {
            Method method = device.getClass().getMethod ( "createBond " , ( Class []) null ) ;
            method.invoke(device , (Object []) null ) ;
        } catch ( Exception e ) {
            e.printStackTrace() ;
        }
    }

    private void unpairDevice(BluetoothDevice device){
        try {
            Method method = device.getClass().getMethod ( "removeBond " , ( Class []) null ) ;
            method.invoke(device, ( Object []) null ) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<BluetoothDevice> getPairedDevices(BluetoothAdapter mBluetoothAdapter)
    {
        Set<android.bluetooth.BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices() ;
        if ( pairedDevices.size() > 0) {
            Log.v( TAG , " pTESTTTT" + String.valueOf(pairedDevices) ) ;
            // addr et nom des appareils deja connecté
            for(android.bluetooth.BluetoothDevice device : pairedDevices ) {
                Log.v( TAG , " paired = " + device.getName () ) ;
                Log.v( TAG , " paired MAC = " + device.getAddress () ) ;
                final BluetoothDevice d = device;
                TextView txt=new TextView(this);
                txt.setText(device.getName());
                txt.setPadding(20,20,20,20);
                txt.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View view) {
                                               deviceChoose = d;
                                               ((TextView) findViewById(R.id.apC)).setText("Appareil choisis: " + d.getName());
                                           }
                                       }
                );
                paired.addView(txt);
                if(device != null) {
                    deviceBT.add(device);
                }
                //deviceBT.add(device) ;
                //deviceListName.add(device.getName() + " paired " ) ;
                //arrayAdapter.notifyDataSetChanged() ;
            }
        }
        else{Log.v( TAG , "No Paired") ;}
        return pairedDevices ;
    }



    @Override
    public void onActivityResult(int requestCode , int resultCode , Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Log.v(TAG, " BT = " + ENABLE_BLUETOOTH);
            }
        }
        if (requestCode == DISCOVERY_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, " Discovery cancelled by user ");
            } else {
                Log.v(TAG, " Discovery allowed ");
            }
        }
    }

    private void makeDiscoverable(){
        Intent discoverableIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(  BluetoothAdapter. EXTRA_DISCOVERABLE_DURATION , 30) ;
        startActivityForResult(discoverableIntent , DISCOVERY_REQUEST);
        Log.i( " Log " , " Discoverable ");
    }



}




