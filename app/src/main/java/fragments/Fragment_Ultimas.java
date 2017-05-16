package fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antonio.localizacionesviewpager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import util.Conexiones;
import util.InsertarLocalizaciones;
import util.TraerMarcadores;

import static com.antonio.localizacionesviewpager.MapsActivity.manejador;
import static com.antonio.localizacionesviewpager.R.id.map1;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Ultimas extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private static String LOGCAT;
    //Variables utilizadas para el mapa
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private final long TIEMPO_MIN = 300 * 1000; //==> 5 minutos.300000 milisegundos.
    private final long DISTANCIA_MIN = 100; // 100 metros
    private float zoom = 10;
    private String proveedor;

    //VARIABLES QUE VA A UTILIZAR EN muestradireccion();
    //VARIABLES QUE VA A UTILIZAR EL SERVICIO
    private static double longitud;
    private static double latitud;
    private static float velocidad;
    private static double altitud;
    private static String direccion;
    private static String calle;
    private static String poblacion;
    private static String numero;


    private TraerMarcadores traerMarcadores;//Class donde está el proc. que trae las localizaciones.
    private String URL;

    //Variables recogidas desde Login y posteriomente enviadas a InsertarLocalizaciones..
    private int id;
    private String usuarioMapas;
    private String email;
    private String android_id;
    private String telefono;


    //Recuperamos el contexto de la actividad
    private Context contexto;
    //Para el Snackbar...
    LinearLayout miLayout;

    public Fragment_Ultimas() {
        // Required empty public constructor
    }


    public static Fragment_Ultimas newInstance() {
        Fragment_Ultimas fragment = new Fragment_Ultimas();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contexto = getActivity();//Utilizamos el contexto de la Actividad contenedora

        //Recogemos los datos enviados por la activity login
        Bundle bundle = getActivity().getIntent().getExtras();
        id = bundle.getInt("ID");
        usuarioMapas = bundle.getString("USUARIO");
        email = bundle.getString("EMAIL");
        android_id = bundle.getString("ANDROID_ID");
        telefono = bundle.getString("TELEFONO");
        Toast.makeText(contexto, "Me alegro de verte... " + usuarioMapas, Toast.LENGTH_SHORT).show();


        View v = inflater.inflate(R.layout.fragment_ultimas, container, false);

        //Para el snackbar...
        miLayout = (LinearLayout) v.findViewById(R.id.miLayout);

        initilizeMap();
        inicializarComponentes();

        return v;
    }

    private void inicializarComponentes() {
        //MapsActivity.requestQueue= Volley.newRequestQueue(contexto);
        manejador = (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
        //url= TraerMarcadores.URL_ULTIMAS;
        //int metodo_Get_POST = 0;
        //utilizamosGps();
        Conexiones conexiones = new Conexiones();
        URL = conexiones.URL_ULTIMAS;


    }


    private void initilizeMap() {

        if (mMap == null) {

            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map1);
            mapFragment.getMapAsync(this);
            //InsertarLocalizaciones insertarLocalizaciones = new InsertarLocalizaciones(contexto, id, usuarioMapas, email, android_id, telefono);

            //ES OBLIGATORIO CONTROLAR LA VERSIÓN PORQUE EN KIT-KAT DABA ERROR AL CARGAR EL MAPA
       /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map1);
                mapFragment.getMapAsync(this);
                //mMap.clear();

            } else {
                mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(map1));
                mapFragment.getMapAsync(this);
                //mMap.clear();
            }*/


        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (proveedor != null) {
            if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN, this);

            //traerMarcadores.traerMarcadores_Utilidades(mMap, URL, 0, usuarioMapas);
            //InsertarLocalizaciones insertarLocalizaciones=new InsertarLocalizaciones(contexto,id,usuarioMapas,email,android_id,telefono);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Implementamos OnMapReadyCallback
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        LatLng milocalizacion = new LatLng(latitud, longitud);
        traerMarcadores = new TraerMarcadores(contexto);
        traerMarcadores.traerMarcadores_Utilidades(mMap, URL, 0, usuarioMapas);
        utilizamosGps();


        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Establece la posición actual...
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });


        mMap.getUiSettings().setMapToolbarEnabled(false);//Deshabilitamos los iconos con accesos a googlemaps
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);//Botón de ubicación activado.
        //Permitimos zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);

    }

    private void muestraLocaliz(Location localizacion) {
        /*if (localizacion == null)
            log("Localización desconocida\n");
        else
            log(localizacion.toString() + "\n");*/
    }

    private void muestradireccion(Location location) {
    /*Devuelve velocidad,latitud, longitud y dirección a partir de lo que traiga el objeto Location*/
        //this.context = getApplicationContext();
        //location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        Geocoder geo;


        if (location != null) {
            //Devolvemos los datos
            latitud = location.getLatitude();
            longitud = location.getLongitude();

            //velocidad = conversionVelocidad(location.getSpeed());


            //velocidad= conversionVelocidad(location.getSpeed());

             velocidad = location.getSpeed();
             altitud = location.getAltitude();

            //PARA OBTENER LA DIRECCIÓN
            geo = new Geocoder(contexto, Locale.getDefault());

            try {
                List<Address> list =
                        geo.getFromLocation(Double.valueOf(location.getLatitude()),
                                Double.valueOf(location.getLongitude()), 1);

                if (list != null && list.size() > 0) {
                    Address address = list.get(0);
                     direccion = address.getAddressLine(0);
                     calle = direccion.split(",")[0];
                    if (direccion.split(",").length == 2) {
                         numero = direccion.split(",")[1];
                    }

                     poblacion = address.getLocality();

                    if (poblacion == null) {

                        poblacion = "";

                        //velocidad_dir = Float.toString(location.getSpeed());


                    }

                    // log("Dirección de localización:+ \n " + direccion + " " + poblacion);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //MÉTODOS DE LOCATIONLISTENER
    @Override
    public void onLocationChanged(Location location) {

          /*Cada vez que cambian los parámetros de la localización: distancia y tiempo*/
        //muestraLocaliz(location);


        //enviaDatosAlServidor();
        //enviarDatosAlServicioLocalizaciones();



        mMap.clear();
        /*if (salir == false) {
            traerMarcadoresNew();
        }*/

        //traerMarcadores.traerMarcadores_Utilidades(mMap, URL, 0, usuarioMapas);
        //InsertarLocalizaciones insertarLocalizaciones=new InsertarLocalizaciones(contexto,id,usuarioMapas,email,android_id,telefono);
        InsertarLocalizaciones insertarLocalizaciones=new InsertarLocalizaciones(contexto,id,usuarioMapas,email,android_id,telefono,longitud,latitud,velocidad,altitud,direccion,calle,poblacion,numero);

        muestradireccion(location);
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


    private void utilizamosGps() {
        muestraProveedores();
                /*CRITERIOS PARA ELEGIR EL PROVEEDOR:SIN COSTE, QUE MUESTRE ALTITUD, Y QUE TENGA PRECISIÓN FINA. CON ESTOS
                * SERÁ ELEGIDO AUTOMÁTICAMENTE EL PROVEEDOR A UTILIZAR POR EL PROPIO TERMINAL*/
        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = manejador.getBestProvider(criterio, true);
        Log.v(LOGCAT, "Mejor proveedor: " + proveedor + "\n");
        Log.v(LOGCAT, "Comenzamos con la última localización conocida:");

        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //NO HACEMOS NADA. A ESTE NIVEL EL PERMISO DE USO DE GPS YA ESTÁ APROVADO POR EL USUARIO.
            return;
        }
        Location localizacion = manejador.getLastKnownLocation(proveedor);
        muestraLocaliz(localizacion);
        muestradireccion(localizacion);
        //InsertarLocalizaciones insertarLocalizaciones=new InsertarLocalizaciones(contexto,id,usuarioMapas,email,android_id,telefono);
        InsertarLocalizaciones insertarLocalizaciones=new InsertarLocalizaciones(contexto,id,usuarioMapas,email,android_id,telefono,longitud,latitud,velocidad,altitud,direccion,calle,poblacion,numero);

    }

    private void muestraProveedores() {
    /*Muestra los proveedores posible para utilizarlo después en el objeto Criteria*/
        //log("Proveedores de localización: \n ");
        List<String> proveedores = manejador.getAllProviders();
        for (String proveedor : proveedores) {
            muestraProveedor(proveedor);
        }
    }

    private void muestraProveedor(String proveedor) {
        /*Lista los proveedores posibles*/
   /*     LocationProvider info = manejador.getProvider(proveedor);
        log("LocationProvider[ " + "getName=" + info.getName()
                + ", isProviderEnabled="
                + manejador.isProviderEnabled(proveedor) + ", getAccuracy="
                + A[Math.max(0, info.getAccuracy())] + ", getPowerRequirement="
                + P[Math.max(0, info.getPowerRequirement())]
                + ", hasMonetaryCost=" + info.hasMonetaryCost()
                + ", requiresCell=" + info.requiresCell()
                + ", requiresNetwork=" + info.requiresNetwork()
                + ", requiresSatellite=" + info.requiresSatellite()
                + ", supportsAltitude=" + info.supportsAltitude()
                + ", supportsBearing=" + info.supportsBearing()
                + ", supportsSpeed=" + info.supportsSpeed() + " ]\n");*/
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        String title=marker.getTitle().toUpperCase();

        String texto="Último posicionamiento de: "+title+ "\n"+marker.getSnippet();
        final Snackbar snackbar = Snackbar.make(miLayout, texto, Snackbar.LENGTH_INDEFINITE);
        //Accedemos al su diseño y añadimos una imagen al texto
        View snackbarLayout = snackbar.getView();
        TextView textView = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.situacion, 0);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSnackbar));

        snackbar.setActionTextColor(ContextCompat.getColor(getContext(),R.color.textSnackbar));

        //NO se puede pq realmente no existe un botón. solo hay dos textos...
        /*Button button=(Button) snackbarLayout.findViewById(android.support.design.R.id.snackbar_action);
        button.setBackgroundResource(R.drawable.icono_ruta);
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_ruta, 0, 0, 0);
        button.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));*/

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                snackbar.dismiss();
            }
        });
        snackbar.show();



        return false;

    }

    @Override
    public void onDestroyView() {
        //Destruimos el mapFragment para que se pueda seguir creando al moverse entre por las pestañas del viewPager
        super.onDestroyView();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map1);
        if (mapFragment != null) {
            getChildFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
        mapFragment = null;
        mMap = null;
    }


}
