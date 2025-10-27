package com.dam.profesor.webservice;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.net.Uri;
import android.widget.Toast;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button BotonPanico, btnUbicacionAlternativa;
    EditText txtUbicacionAlternativa;
    TextView resultado;
    ObtenerWebService hiloconexion;
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alert = null;
    String ubicacionActual = "";
    String ubicacionAlternativa="";
    boolean usarUbicacionAlternativa=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------------------------------------------------------------
        //           CHECAR SI YA SE HA INSTALADO ANTES PARA CONFIGURACIONES INICIALES
        //------------------------------------------------------------------------------------------
        SharedPreferences prefInstalado = getSharedPreferences("instalado", Context.MODE_PRIVATE);
        Toast.makeText(this, prefInstalado.getString("SiInstalado", ""), Toast.LENGTH_SHORT).show();//nur zum debuggen
        if(!prefInstalado.getString("SiInstalado", "").equalsIgnoreCase("Instalado"))
        {//schritt eins
            Intent zuChangeContactoGehen = new Intent(this, CambiarContactoDeEmergencia.class);
            startActivity(zuChangeContactoGehen);
        }//no incluye else porque lo demás estará normal

        BotonPanico = (Button) findViewById(R.id.BotonPanico);
        btnUbicacionAlternativa = (Button) findViewById(R.id.btnUbicacionAlternativa);

        resultado = (TextView) findViewById(R.id.resultado);

        txtUbicacionAlternativa = (EditText) findViewById(R.id.txtUbicacionAlternativa);

        BotonPanico.setOnClickListener(this);
        btnUbicacionAlternativa.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /****Mejora****/
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertNoGps();
        }
        /********/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        MostrarLocalizacion(location);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MostrarLocalizacion(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.removeUpdates(locationListener);
            }
        } else {
            locationManager.removeUpdates(locationListener);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


    }

    public void MostrarLocalizacion(Location loc) {


        if (loc != null) {
            hiloconexion = new ObtenerWebService();
            hiloconexion.execute(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.BotonPanico:
                SharedPreferences prefE = getSharedPreferences("Evi", Context.MODE_PRIVATE);
                String strEvidencia=prefE.getString("Evidencia", "");

                //NOTIFICAR AL CONTACTO
                    if(strEvidencia.equalsIgnoreCase("video"))
                    {
                        atestiguarVideo();//llama al metodo para tomar video
                    }
                    if(strEvidencia.equalsIgnoreCase("audio")) {
                        atestiguarAudio();//llama al metodo para grabar audio
                    }
                break;
            case R.id.btnUbicacionAlternativa:
                    if(!usarUbicacionAlternativa) //sino estaba indicada y ahorase quiere activar esta opcion
                    {
                        usarUbicacionAlternativa=true;
                        ubicacionAlternativa = txtUbicacionAlternativa.getText().toString();
                        Toast.makeText(this, "Ubicacion alternativa activada - RECUERDE ACTUALIZAR LA UBICACION ALTERNATIVA", Toast.LENGTH_SHORT).show();
                        btnUbicacionAlternativa.setText("DEJAR DE USAR UBICACIÓN ALTERNATIVA");
                    }else//si ya esta activaday se quiere desactivar
                    {
                        usarUbicacionAlternativa=false;
                        Toast.makeText(this, "Ubicacion alternativa DESACTIVADA", Toast.LENGTH_SHORT).show();
                        btnUbicacionAlternativa.setText("USAR UBICACIÓN ALTERNATIVA");
                    }
                break;
            default:
                break;

        }

    }




    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=38.404593,-0.529534&sensor=false
            cadena = cadena + params[0];
            cadena = cadena + ",";
            cadena = cadena + params[1];
            cadena = cadena + "&sensor=false";


            String devuelve = "";

            URL url = null; // Url de donde queremos obtener información
            try {
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK){


                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("results");   // results es el nombre del campo en el JSON

                    //Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String direccion="SIN UBICACION";
                    if (resultJSON.length()>0){
                        direccion = resultJSON.getJSONObject(0).getString("formatted_address");    // dentro del results pasamos a Objeto la seccion formated_address
                    }
                    devuelve = "Dirección: " + direccion;   // variable de salida que mandaré al onPostExecute para que actualice la UI
                    ubicacionActual=devuelve;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
           return devuelve;
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            //resultado.setText(aVoid);//sin puenteo
            resultado.setText(ubicacionActual);
            Log.i("GPS",aVoid);
            super.onPostExecute(aVoid);//esto estaba comentado
        }


        @Override
        protected void onPreExecute() {
            //resultado.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    //PARA LOS MENUSESSSSSSSS
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //maneja la seleccion del item
        switch(item.getItemId())
        {
            case R.id.Cambiar_contacto:
                //Toast.makeText(this, "Cambiar contacto", Toast.LENGTH_SHORT).show();
                Intent iCambiarContacto = new Intent(this, CambiarContactoDeEmergencia.class);
                //i.putExtra("direccion", et1.getText().toString());
                startActivity(iCambiarContacto);
                return true;
                //break;
            case R.id.Cambiar_modo_contacto:
                Intent iCambiarModo = new Intent(this, CambiarModoDeContacto.class);
                startActivity(iCambiarModo);
                //Toast.makeText(this, "Cambiar modo de contacto", Toast.LENGTH_SHORT).show();
                return true;
               // break;
//            case R.id.Configurar_evidencia:
//                //Toast.makeText(this, "Configurar evidencia", Toast.LENGTH_SHORT).show();
//                Intent iConfigEvidencia = new Intent(this, ConfigurarEvidencia.class);
//                startActivity(iConfigEvidencia);
//                return true;
               // break;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    public void atestiguarVideo(){
        Intent intentarAtestiguarVideo = new Intent(this, AtestiguarVideo.class);
        if(!usarUbicacionAlternativa) {
            intentarAtestiguarVideo.putExtra("ubicacion",ubicacionActual);
        }
        else {
            intentarAtestiguarVideo.putExtra("ubicacion", ubicacionAlternativa);
        }
        startActivity(intentarAtestiguarVideo);
    }

    public void atestiguarAudio(){
        alertar();
        Intent intentarAtestiguarAudio = new Intent(this, AtestiguarAudio.class);
        if(!usarUbicacionAlternativa) {
            intentarAtestiguarAudio.putExtra("ubicacion",ubicacionActual);
        }
        else {
            intentarAtestiguarAudio.putExtra("ubicacion", ubicacionAlternativa);
        }
        startActivity(intentarAtestiguarAudio);
    }

    public void alertar()
    {
        SharedPreferences sucorreo = getSharedPreferences("ContactoMail", Context.MODE_PRIVATE);
        String email = sucorreo.getString("ContactoEMail", "");
        String subject = "ALEX - ALERTA DE INCIDENTE";
        String message="";
        if(!usarUbicacionAlternativa) {
            message = "Su amig@ estuvo en problemas en "+ubicacionActual+", por favor contactese con el//ella";
        }
        else {
            message = "Su amig@ estuvo en problemas en "+ubicacionAlternativa+", por favor contactese con el//ella";
        }

        SharedPreferences micorreo = getSharedPreferences("MiCorreo", Context.MODE_PRIVATE);
        SharedPreferences miPassword = getSharedPreferences("MiPassword", Context.MODE_PRIVATE);
        SendMail sm = new SendMail(this, email, subject, message, "No attachment", "", micorreo.getString("MiCorreoE",""), miPassword.getString("MiPasswordE",""), false);
        sm.execute();
    }
}
