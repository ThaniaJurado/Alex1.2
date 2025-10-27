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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CambiarContactoDeEmergencia extends AppCompatActivity implements View.OnClickListener{



    Button btnContacto;
    TextView lblContacto;
    EditText txtContacto2;//donde se va a poner el dato a
    boolean instalado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contacto_de_emergencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefInstalado = getSharedPreferences("instalado", Context.MODE_PRIVATE);
        if(!prefInstalado.getString("SiInstalado", "").equalsIgnoreCase("Instalado"))
            instalado=false;
        else
            instalado=true;

        btnContacto = (Button) findViewById(R.id.btnContacto);
        lblContacto = (TextView) findViewById(R.id.lblTitulo);
        txtContacto2 = (EditText) findViewById(R.id.txtContacto2);

        btnContacto.setOnClickListener(this);

        SharedPreferences yaSeInstalo2 = getSharedPreferences("ContactoMail", Context.MODE_PRIVATE);
        txtContacto2.setText(yaSeInstalo2.getString("ContactoEMail", ""));//ejecutado

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnContacto:
                //correo electronico al cual mandar la evidencia
                SharedPreferences contacto2 = getSharedPreferences("ContactoMail", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = contacto2.edit();
                editor2.putString("ContactoEMail", txtContacto2.getText().toString());
                editor2.commit();
                //schreitt zwei
                if(!instalado)
                {
                    //schreitt drei
                    Intent zuChangeModoGehen = new Intent(this, CambiarModoDeContacto.class);
                    startActivity(zuChangeModoGehen);

                }
                else
                {
                    finish();//in order to zuruck zu mainActivity
                }
                break;
        }
    }
}
