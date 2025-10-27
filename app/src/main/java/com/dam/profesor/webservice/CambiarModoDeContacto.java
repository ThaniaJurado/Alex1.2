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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CambiarModoDeContacto extends AppCompatActivity implements View.OnClickListener {

    Button btnModo;
    boolean instalado;
    EditText txtMiCorreo, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_modo_de_contacto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefInstalado = getSharedPreferences("instalado", Context.MODE_PRIVATE);
        if(!prefInstalado.getString("SiInstalado", "").equalsIgnoreCase("Instalado"))
            instalado=false;
        else
            instalado=true;

        txtMiCorreo = (EditText) findViewById(R.id.txtMiCorreo);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        //para el boton de Listo
        btnModo = (Button) findViewById(R.id.btnModo);
        btnModo.setOnClickListener(this);

        SharedPreferences sMiCorreo = getSharedPreferences("MiCorreo", Context.MODE_PRIVATE);
        txtMiCorreo.setText(sMiCorreo.getString("MiCorreoE", ""));//ejecutadSharedPreferences yaSeInstalo2 = getSharedPreferences("ContactoMail", Context.MODE_PRIVATE);
        txtMiCorreo.setText(sMiCorreo.getString("MiCorreoE", ""));//ejecutado
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnModo:
                //para settear tu correo
                SharedPreferences micorreo = getSharedPreferences("MiCorreo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorMiCorreo = micorreo.edit();
                editorMiCorreo.putString("MiCorreoE", txtMiCorreo.getText().toString());
                editorMiCorreo.commit();

                //para settear tu password
                SharedPreferences miPassword = getSharedPreferences("MiPassword", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorMiPassword = miPassword.edit();
                editorMiPassword.putString("MiPasswordE", txtPassword.getText().toString());
                editorMiPassword.commit();
                Toast.makeText(this, miPassword.getString("MiPasswordE",""), Toast.LENGTH_SHORT).show();

                if(!instalado)
                {
                    SharedPreferences prefInstalado = getSharedPreferences("instalado", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefInstalado.edit();
                    editor.putString("SiInstalado", "Instalado");
                    editor.commit();
                }
                enviarConfirmacionDeCorreo();
                Intent irAlMain = new Intent(this, MainActivity.class);
                startActivity(irAlMain);
                break;
        }
    }

    public void enviarConfirmacionDeCorreo()
    {
        String email = txtMiCorreo.getText().toString()+"@gmail.com";
        String subject = "ALEX - Confirmación de correo";
        String message="Si le llegó este correo significa que ha configurado correctamente sus datos. ";
        SharedPreferences micorreo = getSharedPreferences("MiCorreo", Context.MODE_PRIVATE);
        SharedPreferences miPassword = getSharedPreferences("MiPassword", Context.MODE_PRIVATE);
        SendMail sm = new SendMail(this, email, subject, message, "No attachment", "", txtMiCorreo.getText().toString()+"@gmail.com", txtPassword.getText().toString(), false);
        sm.execute();
    }

}
