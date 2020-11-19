package com.example.passe.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.passe.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Button btn_add;
    Button btn_reset;
    Button btn_money;
    EditText moneyCount;
    EditText moneyName;
    EditText newPersonne;
    TextView personnes;
    TextView moneyDon;
    ScrollView scrollHome;
    LinearLayout scrollLayout;
    LinearLayout newMoney;


    public static final String SAVE = "Save";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        newMoney = root.findViewById(R.id.newMoney);
        moneyDon = root.findViewById(R.id.moneyDon);
        moneyName = root.findViewById(R.id.moneyName);
        moneyCount = root.findViewById(R.id.moneyCount);
        btn_money =  root.findViewById(R.id.btn_money);
        btn_add =  root.findViewById(R.id.btn_add);
        btn_reset =  root.findViewById(R.id.btn_reset);
        newPersonne = root.findViewById(R.id.txt_nouveau);
        personnes = root.findViewById(R.id.personnes);

        scrollHome = root.findViewById(R.id.scroll_home);
        scrollLayout = root.findViewById(R.id.scroll_layout);

        SharedPreferences preferences= getContext().getSharedPreferences(SAVE, getContext().MODE_PRIVATE);

        String[] name = preferences.getString("Personnes", "DefautValue").split("~");
        name[0] = "";
        for(String s: name){
            TextView txt=new TextView(getContext());
            txt.setText(s);
            final String finalName = s;

            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
            if(s != "") scrollLayout.addView(txt);
            txt.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View view) {
                    ViewGroup.LayoutParams params = newMoney.getLayoutParams();
                    params.height = 100;
                    newMoney.setLayoutParams(params);
                    newMoney.setVisibility(view.VISIBLE);
                    moneyDon.setText(finalName);

                }});


        }
        
        
        btn_add.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                SharedPreferences preferences= getContext().getSharedPreferences(SAVE, getContext().MODE_PRIVATE);
                Editable personne = newPersonne.getText();
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("Personnes", preferences.getString("Personnes","DefautValue") + "~" + String.valueOf(personne));


                editor.commit();

                String value=preferences.getString("Personnes","DefautValue");


                TextView txt=new TextView(getContext());
                txt.setText(String.valueOf(personne));

                final String finalName = String.valueOf(personne);

                txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                scrollLayout.addView(txt);

                txt.setOnClickListener(new View.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        ViewGroup.LayoutParams params = newMoney.getLayoutParams();
                        params.height = 500;
                        newMoney.setLayoutParams(params);
                        newMoney.setVisibility(view.VISIBLE);
                        moneyDon.setText(String.valueOf(finalName));

                    }});
                //personnes.setText(value);
            }}
        );

        btn_reset.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                SharedPreferences preferences= getContext().getSharedPreferences(SAVE, getContext().MODE_PRIVATE);
                preferences.edit().clear().commit();
            }}
        );
        btn_money.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                SharedPreferences preferences= getContext().getSharedPreferences(SAVE, getContext().MODE_PRIVATE);


                String n = String.valueOf(moneyName.getText());
                String c = String.valueOf(moneyCount.getText());

                SharedPreferences.Editor editor=preferences.edit();
                editor.putString(String.valueOf(moneyDon.getText()) + "~" + n, c);


                editor.commit();



                ViewGroup.LayoutParams params = newMoney.getLayoutParams();
                params.height =0;
                moneyName.setText("Nom");
                moneyCount.setText("Argent");
                newMoney.setLayoutParams(params);
                newMoney.setVisibility(view.INVISIBLE);
            }}
        );
        return root;
    }
}