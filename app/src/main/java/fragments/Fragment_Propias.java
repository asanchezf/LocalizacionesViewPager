package fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonio.localizacionesviewpager.MapsActivity;
import com.antonio.localizacionesviewpager.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import util.Conexiones;
import util.TraerMarcadores;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Propias extends Fragment implements OnMapReadyCallback,LocationListener{


    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    //Recuperamos el contexto de la actividad
    private Context contexto;
    private float zoom = 10;
    //VARIABLES QUE VA A UTILIZAR EN muestradireccion();
    private double longitud;
    private double latitud;

    private TraerMarcadores traerMarcadores;

    private String URL;



    public Fragment_Propias() {
        // Required empty public constructor
    }

    public static Fragment_Propias newInstance() {
        Fragment_Propias fragment = new Fragment_Propias();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contexto=getActivity();//Utilizamos el contexto de la Actividad contenedora
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment__propias, container, false);
        //return inflater.inflate(R.layout.fragment__propias, container, false);

        initilizeMap();
        inicializarComponentes();

        return v;
    }


    private void initilizeMap() {

        if (mMap == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);
            mapFragment.getMapAsync(this);

            //ES OBLIGATORIO CONTROLAR LA VERSIÓN PORQUE EN KIT-KAT DABA ERROR AL CARGAR EL MAPA
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);
                mapFragment.getMapAsync(this);

            } else {
                mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map3));
                mapFragment.getMapAsync(this);
            }*/


        }

    }


    private void inicializarComponentes() {
        //MapsActivity.requestQueue= Volley.newRequestQueue(contexto);
        MapsActivity.manejador= (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
        //url= TraerMarcadores.URL_ULTIMAS;
        int metodo_Get_POST = 0;
        Conexiones conexiones = new Conexiones();
        URL= conexiones.URL_POR_USUARIO;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        LatLng milocalizacion = new LatLng(latitud, longitud);
        traerMarcadores=new TraerMarcadores(contexto);
        traerMarcadores.traerMarcadores_Utilidades(mMap,URL,1,"Antonio");

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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);//Botón de ubicación activado.
        //Permitimos zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
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

            float velocidad = location.getSpeed();
            double altitud = location.getAltitude();

            //PARA OBTENER LA DIRECCIÓN
            geo = new Geocoder(contexto, Locale.getDefault());

            try {
                List<Address> list =
                        geo.getFromLocation(Double.valueOf(location.getLatitude()),
                                Double.valueOf(location.getLongitude()), 1);

                if (list != null && list.size() > 0) {
                    Address address = list.get(0);
                    String direccion = address.getAddressLine(0);
                    String calle = direccion.split(",")[0];
                    if (direccion.split(",").length == 2) {
                        String numero = direccion.split(",")[1];
                    }

                    String poblacion = address.getLocality();

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
        muestraLocaliz(location);
        muestradireccion(location);

        //enviaDatosAlServidor();
        //enviarDatosAlServicioLocalizaciones();


        mMap.clear();
        /*if (salir == false) {
            traerMarcadoresNew();
        }*/

        traerMarcadores.traerMarcadores_Utilidades(mMap,URL,0,"Antonio");

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


    @Override
    public void onDestroyView() {
        //Destruimos el mapFragment para que se pueda seguir creando al moverse entre por las pestañas del viewPager
        super.onDestroyView();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);
        if (mapFragment != null) {
            getChildFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
        mapFragment = null;
        mMap=null;
    }

}
