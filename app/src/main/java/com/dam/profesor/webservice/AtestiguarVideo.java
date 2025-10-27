package com.dam.profesor.webservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class AtestiguarVideo extends AppCompatActivity implements SurfaceHolder.Callback{
    /**
     * Variables globales para administrar la grabaci—n y reproducci—n
     */
    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    File archivo;
    private String mPath = "/storage/sdcard0/TestimonioAlex/EvidenciaAlex.mp4";

    /**
     * Variable que define el nombre para el archivo donde escribiremos el video a grabar
     */
    private String fileName = null;

    /**
     * Variable que indica cuando se est‡ grabado
     */
    private boolean recording = false;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atestiguar);
        /**
         * inicializamos la variable para el nombre del archivo
         */
        fileName = Environment.getExternalStorageDirectory() + "/test.mp4";

        File filelocation = new File("/mnt/sdcard/TestimonioAlex", "EvidenciaAlex.mp4");
        Uri path = Uri.fromFile(filelocation);
        /**
         * inicializamos la "superficie" donde se reproducir‡ la vista previa de la grabaci—n
         * y luego el video ya grabado
         */
        SurfaceView surface = (SurfaceView)findViewById(R.id.surface);
        SurfaceHolder holder = surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        /**
         * inicializamos los botones sobre los que vamos a trabajar su evento de click
         */
        final Button btnRec = (Button)findViewById(R.id.btnRec);
        final Button btnStop = (Button)findViewById(R.id.btnStop);
        final Button btnPlay = (Button)findViewById(R.id.btnPlay);
        btnRec.setEnabled(true);
        btnStop.setEnabled(true);
        btnPlay.setEnabled(true);
//        String mPath = Environment.getExternalStorageDirectory() + File.separator + "TestimonioAlex"
//                + File.separator + fileName;
        //rutaEvidencia = mPath;
        btnStop.setText(mPath);//btnStop.setText(fileName);
        /**
         * Bot—n para grabar
         */
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                grabar();
            }
        });

        /**
         * Bot—n para detener
         */
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detener();
            }
        });

        /**
         * Bot—n para reproducir
         */
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarVideo();

            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    /**
     * Inicializamos los recursos asociados a las variables para administrar la grabaci—n y reproducci—n.
     * Se verifica si las variables son nulas (para ejecutar este c—digo solo una vez) y luego de
     * inicializarlas se coloca el SurfaceHolder como display para la vista previa de la grabaci—n y
     * para la vista de la reproducci—n
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setPreviewDisplay(holder.getSurface());
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(holder);
        }
    }

    /**
     * Liberamos los recursos asociados a las variables para administrar la grabaci—n y reproducci—n
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        mediaRecorder.release();
        mediaPlayer.release();
    }


    /**
     * MŽtodo para preparar la grabaci—n, configurando los atributos de la fuente para audio y video,
     * el formado y el codificador.
     */
    public void prepareRecorder(){
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
    }

    public void grabar()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "grabando", Toast.LENGTH_SHORT);
        /**
         * Al iniciar grabaci—n deshabilitamos los botones de grabar y reproducir y
         * habilitamos el de detener
         */
//        btnRec.setEnabled(false);
//        btnStop.setEnabled(true);
//        btnPlay.setEnabled(false);

        /**
         * Llamamos el mŽtodo que configura el media recorder y le decimos el archivo de salida
         */
        prepareRecorder();
//                String mPath = Environment.getExternalStorageDirectory() + File.separator + "TestimonioAlex"
//                        + File.separator + fileName;
        //rutaEvidencia = mPath;
        File path = new File(Environment.getExternalStorageDirectory().getPath());
        try{
            archivo = File.createTempFile("temporal", ".mp4", path);
        }catch(IOException e){}

        Toast.makeText(this, "antes de grabar "+archivo.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        mediaRecorder.setOutputFile(archivo.getAbsolutePath());// mediaRecorder.setOutputFile(fileName);
        try {
            /**
             * Una vez configurado todo llamamos al mŽtodo prepare que deja todo listo
             * para iniciar la grabaci—n
             */
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
        /**
         * Iniciamos la grabaci—n y actualizamos el estatus de la variable recording
         */
        mediaRecorder.start();
        recording = true;
    }

    public void detener()
    {
        /**
         * Al iniciar detener habilitamos los botones de grabar y reproducir y
         * deshabilitamos el de detener
         */
//                btnRec.setEnabled(true);
//                btnStop.setEnabled(false);
//                btnPlay.setEnabled(true);

        /**
         * Si se est‡ grabando, detenemos la grabaci—n y reiniciamos la configuraci—n
         * adem‡s de volver falsa la variable de estatus de grabaci—n
         */
        if (recording) {
            recording = false;
            mediaRecorder.stop();
            mediaRecorder.reset();
            /**
             * Si se est‡ reproduciendo, detenemos la reproducci—n y reiniciamos la configuraci—n
             */
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public void recuperarVideo()
    {
        /**
         * Al iniciar la reproducci—n deshabilitamos los botones de grabar y reproducir y
         * habilitamos el de detener
         */
//                btnRec.setEnabled(false);
//                btnStop.setEnabled(true);
//                btnPlay.setEnabled(false);

        /**
         * Al concluir la reproducci—n habilitamos los botones de grabar y reproducir y
         * deshabilitamos el de detener
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

//                        btnRec.setEnabled(true);
//                        btnStop.setEnabled(false);
//                        btnPlay.setEnabled(true);
            }
        });

        /**
         * Configuramos el archivo a partir del cual se reproducir‡, preparamos el Media Player
         *  e iniciamos la reproducci—n
         */
        try {

            String email = "tejg13@gmail.com";
            String subject = "ALEX - EVIDENCIA";
            String message = "Su amig@ estuvo en problemas en "+"ubicacion"+", por favor contactese con el//ella";

            //unas dos lineas para el path7858
            File filelocation = new File(archivo.getAbsolutePath());//donde se guardo la evidencia
            Uri path = Uri.fromFile(filelocation);
            String mPath = path.getPath();

            //para settear tu correo
            SharedPreferences micorreo = getSharedPreferences("MiCorreo", Context.MODE_PRIVATE);
            Toast.makeText(this, micorreo.getString("MiCorreoE",""), Toast.LENGTH_SHORT).show();//nur zum debuggen
            //para settear tu password
            SharedPreferences miPassword = getSharedPreferences("MiPassword", Context.MODE_PRIVATE);
            Toast.makeText(this, miPassword.getString("MiPasswordE",""), Toast.LENGTH_SHORT).show();//nur zum debuggen
            Toast.makeText(this, "antes de grabar "+archivo.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            //Creating SendMail object
            SendMail sm = new SendMail(this, email, subject, message, archivo.getAbsolutePath(), ".mp4", micorreo.getString("MiCorreoE",""), miPassword.getString("MiPasswordE",""), true);

            //Executing sendmail to send email
            sm.execute();
        } catch (IllegalStateException e) {
        }
        mediaPlayer.start();
    }
}
