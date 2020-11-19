package com.example.passe.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.passe.R;

import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;


    public static final String SAVE = "Save";
    LinearLayout scrollLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        LinearLayout scrollLayout = root.findViewById(R.id.scroll_layout);


        //recup save
        SharedPreferences preferences= getContext().getSharedPreferences(SAVE, getContext().MODE_PRIVATE);

        Map<String, ?> name = preferences.getAll();

        for(Map.Entry<String, ?> s: name.entrySet()){
            if(s.getKey().split("~")[0] != s.getKey()){
            TextView txt=new TextView(getContext());
            String[] out = s.getKey().split("~");
            txt.setText( (CharSequence)  out[0] +" doit " + (CharSequence) s.getValue() + " euros à " +  out[1]);
            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            scrollLayout.addView(txt);
            }

        }
        return root;

    }
}