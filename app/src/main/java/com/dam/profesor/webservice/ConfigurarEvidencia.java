package com.dam.profesor.webservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConfigurarEvidencia extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    Button btnEvidencia;
    boolean instalado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_evidencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefInstalado = getSharedPreferences("instalado", Context.MODE_PRIVATE);
        if(!prefInstalado.getString("SiInstalado", "").equalsIgnoreCase("Instalado"))
            instalado=false;
        else
            instalado=true;

        //para el boton de Listo
        btnEvidencia = (Button) findViewById(R.id.btnEvidencia);
        btnEvidencia.setOnClickListener(this);


        //pa'l spinner
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spnEvidencia);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Video");
        categories.add("Audio");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnEvidencia:
                SharedPreferences prefE = getSharedPreferences("Evi", Context.MODE_PRIVATE);
                Toast.makeText(this, prefE.getString("Evidencia", ""), Toast.LENGTH_SHORT).show();

                if(!instalado)
                {
                    SharedPreferences prefInstalado = getSharedPreferences("instalado", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefInstalado.edit();
                    editor.putString("SiInstalado", "Instalado");
                    editor.commit();
                }

                //txtContacto.setText(prefModo.getString("ModoC", ""));
                Intent irAlMain = new Intent(this, MainActivity.class);
                startActivity(irAlMain);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        //case R.id.btnContacto:
        SharedPreferences prefModo = getSharedPreferences("Evi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefModo.edit();
        editor.putString("Evidencia", item);
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
