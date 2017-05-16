package util;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.antonio.localizacionesviewpager.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Usuario on 28/04/2017.
 */

public class InsertarLocalizaciones {

    private static String LOGCAT;

    //Variables recogidas desde FragmentUltimas y posteriomente enviadas a InsertaPosicionamiento..
    private Context miContexto;
    private int id;
    private String usuarioMapas;
    private String email;
    private String android_id;
    private String telefono;

    private  double longitud;
    private  double latitud;
    private  float velocidad;
    private  double altitud;
    private  String direccion;
    private  String calle;
    private  String poblacion;
    private  String numero;
    private String Stringfechahora;
    private Calendar modificacion;//NO se utiliza


    public InsertarLocalizaciones(Context miContexto, int id, String usuarioMapas, String email, String android_id, String telefono, double longitud, double latitud, float velocidad, double altitud, String direccion, String calle, String poblacion, String numero) {
        this.miContexto = miContexto;
        this.id = id;
        this.usuarioMapas = usuarioMapas;
        this.email = email;
        this.android_id = android_id;
        this.telefono = telefono;
        this.longitud = longitud;
        this.latitud = latitud;
        this.velocidad = velocidad;
        this.altitud = altitud;
        this.direccion = direccion;
        this.calle = calle;
        this.poblacion = poblacion;
        this.numero = numero;


        inicializarComponentes();
    }

  /*  public InsertarLocalizaciones(Context miContexto, int id, String usuarioMapas, String email, String android_id, String telefono,
                                  double longitud, double latitud, float velocidad, double altitud, String direccion, String calle, String poblacion, String numero, String Stringfechahora) {
        this.miContexto = miContexto;
        this.id = id;
        this.usuarioMapas = usuarioMapas;
        this.email = email;
        this.android_id = android_id;

        this.telefono = telefono;

        inicializarComponentes();
    }*/




   /* public static InsertarLocalizaciones newInstance() {
        InsertarLocalizaciones insertarLocalizaciones = new InsertarLocalizaciones();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        *//*fragment.setArguments(args);
        return fragment;*//*
    }*/

    private void inicializarComponentes() {

          /*PREPARA Y HACE LA LLAMADA PARA LA INSERCCIÓN AUTOMÁTICA DE LAS LOCALIZACIONES DEL USUARIO CONECTADO*/
         //String INSERT = "http://petylde.esy.es/insertar_localizacion.php";
        Conexiones conexion=new Conexiones();
        String INSERT=conexion.INSERTAR_POSICIONAMIENTO;

        Calendar calendarNow = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));

        //Calendar c1 = GregorianCalendar.getInstance();
        //System.out.println("Fecha actual: "+c1.getTime().toLocaleString());
        //usuario="Antonio";
        //usuario="Susana";
        long fechaHora2 = System.currentTimeMillis();

        //System.out.println("Fecha del sistema: " + fechaHora2);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

         Stringfechahora = sdf.format(fechaHora2);
        //System.out.println("Fecha del sistema: "+dateString);
        Log.v("", "Fecha del sistema: " + Stringfechahora);
        Calendar modificacion = calendarNow;


        /*ObtenerWebService hiloconexion = new ObtenerWebService();
        hiloconexion.execute(INSERT);   // Parámetros que recibe doInBackground*/

        InsertaPosicionamiento insertaPosicionamiento=new InsertaPosicionamiento();
        insertaPosicionamiento.execute(INSERT);


    }


    public class InsertaPosicionamiento extends AsyncTask<String, Void, String> {
        //CONECTA CON EL WS E INSERTA LAS LOCALIZACIONES AUTOMÁTICAS DEL USUARIO CONECTADO
        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null; // Url de donde queremos obtener información
            String devuelve = "";

            try {
                HttpURLConnection urlConn;

                DataOutputStream printout;
                DataInputStream input;
                url = new URL(cadena);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.connect();
                //Creo el Objeto JSON
                JSONObject jsonParam = new JSONObject();

                jsonParam.put("Id_Usuario", id);
                jsonParam.put("Poblacion", poblacion);
                jsonParam.put("Calle", calle);
                jsonParam.put("Numero", numero);
                jsonParam.put("Longitud", longitud);
                jsonParam.put("Latitud", latitud);
                jsonParam.put("Velocidad", velocidad);
                jsonParam.put("FechaHora", Stringfechahora);
                jsonParam.put("Modificado", modificacion);//De momento pone 00:00:00
                // Envio los parámetros post.
                OutputStream os = urlConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();

                int respuesta = urlConn.getResponseCode();
                Resources res = miContexto.getResources();


                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                        //response+=line;
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados

                    //String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON
                    //Log.d(LOGCAT, "Error insertando localización" + resultJSON);

                    //Sacamos el valor de estado
                    int resultJSON = Integer.parseInt(respuestaJSON.getString("estado"));


                    if (resultJSON == 1) {      // hay un registro que mostrar
                        devuelve = res.getString(R.string.localizacion_insertada);

                        //else if (resultJSON == "2")
                    } else if (resultJSON == 2) {
                        devuelve = res.getString(R.string.localizacion_error);
                        Log.d(LOGCAT, "Error insertando localización" + resultJSON);
                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d(LOGCAT, "Error insertando localización" + e);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return devuelve;


            //return null;
        }


        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            Toast.makeText(miContexto, s, Toast.LENGTH_LONG).show();
        }
    }



}
