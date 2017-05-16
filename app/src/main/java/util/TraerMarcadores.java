package util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.antonio.localizacionesviewpager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import volley.AppController;

/**
 * Created by Usuario on 26/04/2017.
 */

public class TraerMarcadores {

    private static final String LOGCAT ="TRAERMARCADORES" ;

    private Context miContexto;
    private LatLng milocalizacion;
    private float zoom = 10;
    private RequestQueue requestQueue;//Cola de peticiones

    public TraerMarcadores(Context contexto) {
        this.miContexto=contexto;
        //Cola de peticiones de Volley
        requestQueue = Volley.newRequestQueue(miContexto);
    }


    public void traerMarcadores_Utilidades(final GoogleMap mMap, final String patron_Busqueda_Url, final int metodo_Get_POST, final String usuarioMapas) {
        String tag_json_obj_actual = "json_obj_req_actual";
        final String KEY_USERNAME_MARCADOR = "Usuario";
        //final String LOGIN_URL = "http://petty.hol.es/obtener_todas_por_usuario.php";
        final String LOGIN_URL = "http://petylde.esy.es/obtener_todas_por_usuario.php";

        Log.d(LOGCAT, "Valor de usuarioMaps_KEY " + KEY_USERNAME_MARCADOR);
        Log.d(LOGCAT, "Valor de usuarioMaps_Valor " + usuarioMapas);

        String uri = String.format(patron_Busqueda_Url);


        final ProgressDialog pDialog = new ProgressDialog(miContexto);
        pDialog.setMessage("Obteniedo posiciones espera por favor...");
        pDialog.show();

        if (mMap !=null){
            mMap.clear();
        }


        StringRequest stringRequest = new StringRequest(metodo_Get_POST, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //pDialog.hide();
                        pDialog.dismiss();
                        String usuario = "";
                        String poblacion = "";
                        String calle = "";
                        String numero = "";
                        Double latitud = null;
                        Double longitud = null;
                        int velocidad = 0;

                        String fechaHora = "";
                        String telefonomarcador = "";


                        try {

                            //ES UN STRINGREQUEST---HAY QUE CREAR PRIMERO UN JSONObject PARA PODER EXTRAER TODO....
                            JSONObject json_Object = new JSONObject(response.toString());

                            //Sacamos el valor de estado
                            int resultJSON = Integer.parseInt(json_Object.getString("estado"));
                            Log.v(LOGCAT, "Valor de estado: " + resultJSON);


                            JSONArray json_array = json_Object.getJSONArray("alumnos");
                            //JSONArray json_array = response.getJSONArray("alumnos");
                            for (int z = 0; z < json_array.length(); z++) {
                                //usuario = json_array.getJSONObject(z).getString("Usuario");

                                usuario = json_array.getJSONObject(z).getString("Username");
                                poblacion = json_array.getJSONObject(z).getString("Poblacion");
                                calle = json_array.getJSONObject(z).getString("Calle");
                                numero = json_array.getJSONObject(z).getString("Numero");
                                longitud = json_array.getJSONObject(z).getDouble("Longitud");
                                latitud = json_array.getJSONObject(z).getDouble("Latitud");


                                //velocidad = (int) conversionVelocidad((int) json_array.getJSONObject(z).getDouble("Velocidad"));
                                velocidad = (int) conversionVelocidad(json_array.getJSONObject(z).getDouble("Velocidad"));
                                fechaHora = json_array.getJSONObject(z).getString("FechaHora");
                                milocalizacion = new LatLng(latitud, longitud);


                                //CASO 1--http://petty.hol.es/obtener_localizaciones.php
                                //CASO 1--Traemos las últimas posiciones de todos
                                //(patron_Busqueda_Url == "http://petty.hol.es/obtener_localizaciones.php")
                                if (patron_Busqueda_Url == "http://petylde.esy.es/obtener_localizaciones.php") {

                                    //mMap.clear();
                                    telefonomarcador = json_array.getJSONObject(z).getString("Telefono");

                                    //telefonowsp = telefonomarcador;


                                    if (usuario.equals("Antonio")) {
                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholderotro))
                                                        //.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.placeholderotro, "your text goes here")
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet("Día: " + fechaHora + " - Teléf: " + telefonomarcador)

                                                        .draggable(true)
                                                        //.flat(true)
                                                        .position(milocalizacion);

                                        Marker marker = mMap.addMarker(markerOptions);
                                        //marker.isInfoWindowShown();


                                    } else if (usuario.equalsIgnoreCase("Susana")) {

                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_situacion))
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet("Día: " + fechaHora + " - Teléf: " + telefonomarcador)
                                                        .draggable(true)
                                                        .position(milocalizacion)
                                                        .flat(true);

                                        Marker marker = mMap.addMarker(markerOptions);
                                        //marker.showInfoWindow();

                                    } else if (usuario.equalsIgnoreCase("Dario")) {

                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_ruta))
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet("Día: " + fechaHora + " - Teléf: " + telefonomarcador)
                                                        .draggable(true)
                                                        .position(milocalizacion)
                                                        .flat(true);


                                        Marker marker = mMap.addMarker(markerOptions);
                                        //marker.showInfoWindow();

                                    }

                                    //USUARIOS QUE NO TIENEN ICONO PROPIO
                                    else {
                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholderbis))
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet("Día: " + fechaHora + " - Teléf: " + telefonomarcador)
                                                        .draggable(true)
                                                        .position(milocalizacion)
                                                        .flat(true);

                                        Marker marker = mMap.addMarker(markerOptions);


                                    }//Fin else usuarios SIN ICONO PROPIO


                                    ////AQUI/////

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(milocalizacion, zoom));

                                }//Fin patron_Busqueda_Url == "http://petty.hol.es/obtener_localizaciones.php"--PRIMERA PESTAÑA


                                //CASO 2-Ponemos marcadores para todas las posiciones de todos: ponemos marcadores por defecto
                                //else if (metodo_Get_POST == Request.Method.GET && patron_Busqueda_Url == "http://petty.hol.es/obtener_localizaciones_todas.php")
                                else if (metodo_Get_POST == Request.Method.GET && patron_Busqueda_Url == "http://petylde.esy.es/obtener_localizaciones_todas.php") {

                                    //mMap.clear();
                                    if (usuario.equals("Antonio")) {
                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholderotro))
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet(calle + " " + numero + "/-/" + fechaHora + "/-/" + velocidad + " KM/H.")

                                                        //.draggable(true)
                                                        //.flat(true)
                                                        .position(milocalizacion);

                                        Marker marker = mMap.addMarker(markerOptions);
                                        // marker.showInfoWindow();

                                    } else if (usuario.equalsIgnoreCase("Susana")) {


                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_situacion))
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet(calle + " " + numero + "/-/" + fechaHora + "/-/" + velocidad + " KM/H.")
                                                        //.draggable(true)
                                                        .position(milocalizacion)
                                                        .flat(true);

                                        Marker marker = mMap.addMarker(markerOptions);
                                        //marker.showInfoWindow();

                                    } else if (usuario.equalsIgnoreCase("Dario")) {


                                        MarkerOptions markerOptions =
                                                new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_ruta))
                                                        .anchor(0.0f, 1.0f)
                                                        .title(usuario)
                                                        .snippet(calle + " " + numero + "/-/" + fechaHora + "/-/" + velocidad + " KM/H.")
                                                        //.draggable(true)
                                                        .position(milocalizacion)
                                                        .flat(true);


                                        Marker marker = mMap.addMarker(markerOptions);
                                        //marker.showInfoWindow();

                                    } else {
                                        //Usuarios que no están predefinidos


                                        mMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholdermas))
                                                .anchor(0.0f, 1.0f)
                                                .title(usuario)
                                                .snippet(calle + " " + numero + "/-/" + fechaHora + "/-/" + velocidad + " KM/H.")
                                                .position(milocalizacion));
                                    }


                                    ////AQUI/////

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(milocalizacion, zoom));

                                }

                                //CASO 3-Ponemos marcadores para todas las posiciones del usuario que se ha logado: ponemos marcadores violetas y cambiamos el snipped
                                else if (metodo_Get_POST == Request.Method.POST) {
                                    // mMap.clear();
                                    mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder))
                                            .anchor(0.0f, 1.0f)
                                            .title(usuario)
                                            .snippet(calle + " " + numero + "/-/" + fechaHora + "/Vel/" + velocidad + " KM/H.")
                                            .position(milocalizacion));

                                }

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(milocalizacion, zoom));

                            }//fin del else de marcadores

                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(milocalizacion, zoom));

                            //Fin del JsonArray


                            // }//Fin del response

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(LOGCAT, "Error Respuesta en JSON: ");
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGCAT, "Error Respuesta en JSON leyendo MarcadoresPost: " + error.getMessage());
                        VolleyLog.d(LOGCAT, "Error: " + error.getMessage());
                        Toast.makeText(miContexto, "Se ha producido un error leyendo MarcadoresPost " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME_MARCADOR, usuarioMapas);
                return map;
            }
        };

        //OJO: SE DEBE AÑADIR AL MANIFIEST COMO APPLICATION:android:name="volley.AppController"
        // Añadir petición a la cola
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj_actual);


    }

    private double conversionVelocidad(double speed) {
    /*Convierte la velocidad recogida a Km/h*/

        double speedConvertida = (speed / 1000) * 3600;

        return speedConvertida;
    }

}
