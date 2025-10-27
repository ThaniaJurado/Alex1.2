package com.dam.profesor.webservice;
//activity_atestiguar_audio
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AtestiguarAudio extends AppCompatActivity implements OnCompletionListener{
    TextView tv1;
    MediaRecorder recorder;
    MediaPlayer player;
    File archivo;
    //String fileName = Environment.getExternalStorageDirectory() + "/test.mp4";//
    //File filelocation;//
    //private String mPath = "/mnt/sdcard/TestimonioAlex/EvidenciaAlex.3gp";

    Button b1, b2, b3;

    String ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atestiguar_audio);
        this.ubicacion = getIntent().getStringExtra("ubicacion");

        tv1 = (TextView) this.findViewById(R.id.textView1);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);

        grabar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void grabar() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File path = new File(Environment.getExternalStorageDirectory()
                .getPath());
        try {
            archivo = File.createTempFile("temporal", ".3gp", path);
        } catch (IOException e) {
        }
        recorder.setOutputFile(archivo.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
        }
        recorder.start();
        tv1.setText("Grabando");
        b1.setEnabled(false);
        b2.setEnabled(true);

        //RETRASO

        boolean pausa = false;
        long TInicio;
        TInicio = System.currentTimeMillis();
        while(!pausa){
            if(System.currentTimeMillis() - TInicio > 60000)
            {
                pausa = true;
            }
        }

        //FIN RETRASO
        detener();

    }

    public void detener() {
        recorder.stop();
        recorder.release();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {
            player.setDataSource(archivo.getAbsolutePath());
            Toast.makeText(this, "Enviando audio...", Toast.LENGTH_SHORT).show();//nur zum debuggen
            //ENVIARCORREO SIN ABRIR EMAIL

            SharedPreferences sucorreo = getSharedPreferences("ContactoMail", Context.MODE_PRIVATE);
            String email = sucorreo.getString("ContactoEMail","");
            String subject = "ALEX - EVIDENCIA";
            String message = "Su amig@ estuvo en problemas en "+ubicacion+", por favor contactese con el//ella";

            //unas dos lineas para el path7858
            File filelocation = new File(archivo.getAbsolutePath());//donde se guardo la evidencia
            Uri path = Uri.fromFile(filelocation);
            String mPath = path.getPath();

            //para settear tu correo
            SharedPreferences micorreo = getSharedPreferences("MiCorreo", Context.MODE_PRIVATE);
            SharedPreferences miPassword = getSharedPreferences("MiPassword", Context.MODE_PRIVATE);
            SendMail sm = new SendMail(this, email, subject, message, mPath, ".3gp", micorreo.getString("MiCorreoE",""), miPassword.getString("MiPasswordE",""), true);
            sm.execute();

            //y regresa a la pantalla inicial
            Intent irAlMain = new Intent(this, MainActivity.class);
            startActivity(irAlMain);
            //BEGAN EMAIL NACHRICHTEN
           // mediaPlayer.setDataSource(mPath);//mediaPlayer.setDataSource(fileName);
            //mediaPlayer.prepare();
            /*
            File filelocation = new File(archivo.getAbsolutePath());//donde se guardo la evidencia
            Uri path = Uri.fromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"tejg13@hotmail.com"};//cambiar este por el contacto de emergencia
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "ALEX - Evidencia de emergencia");

            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Su amig@ estuvo en problemas en "+ ", por favor contactese con el//ella");

            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            */
            //ENDE EMAIL NACHRICHTEN
        } catch (IOException e) {
        }
        try {
            player.prepare();
        } catch (IOException e) {
        }
        b1.setEnabled(true);
        b2.setEnabled(false);
        b3.setEnabled(true);
        tv1.setText("Listo para reproducir");
    }

    public void reproducir() {
        player.start();
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        tv1.setText("Reproduciendo");
    }

    public void onCompletion(MediaPlayer mp) {
        b1.setEnabled(true);
        b2.setEnabled(true);
        b3.setEnabled(true);
        tv1.setText("Listo");
    }
}
