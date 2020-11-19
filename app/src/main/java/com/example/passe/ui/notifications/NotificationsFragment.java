package com.example.passe.ui.notifications;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.passe.BluetoothActivity;
import com.example.passe.R;

import org.w3c.dom.Text;

public class NotificationsFragment extends Fragment implements SensorEventListener {

    private NotificationsViewModel notificationsViewModel;

//Utilisation des capteurs périphériques
    // Valeur courante de la lumière
    float l;
    SensorManager sensorManager;
    Sensor lum;
    Sensor accel;
    TextView lu;
    TextView chu;
    LinearLayout back;


    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int ENABLE_BLUETOOTH = 1;
    Button btn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        btn = root.findViewById(R.id.bluetooth);
        back = root.findViewById(R.id.back);

        lu = root.findViewById(R.id.lum);
        chu = root.findViewById(R.id.chu);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Log.i("senser manage", String.valueOf(sensorManager));
        lum = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lum == null){
            lu.setText("Pas de capteur de luminosité");

        }
        sensorManager.registerListener(this, lum , SensorManager.SENSOR_DELAY_NORMAL);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        Log.i("sensor lum", String.valueOf(lum));
        /*
        lum = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.i("accel", String.valueOf(lum));
        lum = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Log.i("sensor lum", String.valueOf(lum)); */

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH);
        }

        btn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BluetoothActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }


public void onDestroyView() {


    super.onDestroyView();
    sensorManager.unregisterListener(this);

}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {

        //Log.i("lumiere", "coucou");
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            // La valeur de la lumière
            l = event.values[0];
            //Log.i("lumiere", String.valueOf(l));
            lu.setText("Lumière:" + String.valueOf(l));
            if((int)l>800){
                back.setBackgroundColor(Color.argb(255, 190, 190, 190));
                Log.i("test", "test");
            }
            else if((int)l>100){
                back.setBackgroundColor(Color.argb(255, 100, 100, 100));
            }
            else if((int)l>2){
                back.setBackgroundColor(Color.argb(255, 30, 30, 30));

            }
            Log.i("lumiere", String.valueOf(l));
        }
        else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float calc = x*x + y*y + z*z;
            if(calc > 500){
                chu.setText("RISQUES DE CHUTES");
                Vibrator vib=(Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(500);
            }
            else{
                chu.setText("Pas de chutes");

            }
            Log.d("accel", "x:"+x +";y:"+y+";z:"+z);


        }
    }

}