package com.example.packemango

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var myLocation : MyLocationListener;
    private var AstrakhanLocation = LatLng(46.34968, 48.04076);
    private var Pockemons = ArrayList<Pockemon>();
    private lateinit var player: Player;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission();
    }

    var ACCESSLOCATION=123;
    private fun checkPermission() {
        var geoPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    geoPermission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(geoPermission),ACCESSLOCATION);
                return;
            }
        }
        getUserLocation();
    }

    private fun getUserLocation() {
        Toast.makeText(this, "Geolocation permission granted! nWelcome to the zone, stalker!", Toast.LENGTH_LONG).show();
        myLocation = MyLocationListener();
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f, myLocation);
        var updateLocationThread = UpdatePlayerPositionThread();
        updateLocationThread.start();
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation();
                } else {
                    Toast.makeText(this, "Geolocation permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    fun downScaleImage(image : Int, scalex : Int, scaley : Int): Bitmap {
        var pika = ResourcesCompat.getDrawable(resources, image,null) as BitmapDrawable;
        return Bitmap.createScaledBitmap(pika.bitmap, scalex, scaley,false) as Bitmap;
    }


    fun LoadPockemons() {
        Pockemons.add(Pockemon("Putin","Main Boss",1000, R.drawable.putin, LatLng(46.315466, 47.999312)));
        Pockemons.add(Pockemon("HardBassMan","Cat Vasiliy's owner",555, R.drawable.hardbass, LatLng(46.313240, 47.997071)));
        Pockemons.add(Pockemon("PogMan","Осуждаю, на всякий",10, R.drawable.pog, LatLng(46.306599, 47.990713)));
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        player = Player(downScaleImage(R.drawable.pika, 200,200));
        LoadPockemons();
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom( LatLng(AstrakhanLocation.latitude, AstrakhanLocation.longitude), 14f));
    }


    inner class MyLocationListener : LocationListener{
        public lateinit var location:Location;
        public var oldLocation: Location;


        constructor()
        {
            location = Location("Start");
            location.latitude = AstrakhanLocation.latitude;
            location.longitude = AstrakhanLocation.longitude
            oldLocation = location;
        }

        
        fun updateOldLocation() {
            oldLocation = location;
        }

        override fun onLocationChanged(p0: Location) {
            this.location = p0;
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng( LatLng(location.latitude, location.longitude)));

        }
    }

    inner class UpdatePlayerPositionThread: Thread {
        constructor() : super(){

        }

        fun refreshMarker() {
            var playerPosition = LatLng(myLocation.location.latitude, myLocation.location.longitude);
            mMap!!.clear();
            mMap!!.addMarker(MarkerOptions()
                .position(playerPosition)
                .title("Pika")
                .snippet("Your power: ${player.power}\n")
                .icon(BitmapDescriptorFactory.fromBitmap(player.icon)));

            Pockemons.forEach {
                if (!it.isCatched) {
                    var pika = ResourcesCompat.getDrawable(resources, it.image,null) as BitmapDrawable;
                    mMap!!.addMarker(MarkerOptions()
                        .position(LatLng(it.position.latitude, it.position.longitude))
                        .title(it.name)
                        .snippet(it.description + ". Power: ${it.power}")
                        .icon(BitmapDescriptorFactory
                            .fromBitmap(Bitmap.createScaledBitmap(pika.bitmap, 200, 200,false) as Bitmap)));
                }
            }
        }
        override fun run() {
            while(true) {
                try {

                    if (myLocation.location.distanceTo(myLocation.oldLocation) <= 0) {
                        continue;
                    }

                    myLocation.updateOldLocation();

                    runOnUiThread{

                        Pockemons.forEach {
                            if(myLocation.location.distanceTo(it.position) < 3 && !it.isCatched) {
                                it.isCatched = true;
                                player.power += it.power;
                                Toast.makeText(applicationContext,"You catched ${it.name}. Your power ${player.power}!", Toast.LENGTH_LONG).show();
                            }
                            refreshMarker();


                        }
                    }

                    Thread.sleep(1000);
                } catch (e : Exception) {

                }
            }
        }
    }
}