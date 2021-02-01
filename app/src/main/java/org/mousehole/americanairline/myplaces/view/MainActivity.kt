package org.mousehole.americanairline.myplaces.view

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.MenuPopupWindow
import androidx.core.app.ActivityCompat
import com.google.android.material.slider.Slider
import org.mousehole.americanairline.myplaces.R
import org.mousehole.americanairline.myplaces.broadcastreceiver.LocationReceiver
import org.mousehole.americanairline.myplaces.model.Location
import org.mousehole.americanairline.myplaces.model.Type
import org.mousehole.americanairline.myplaces.utils.MyLogger
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.utils.Radius
import org.mousehole.americanairline.myplaces.viewmodel.PlacesViewModel

class MainActivity : AppCompatActivity(), LocationReceiver.TypeGiver, PopupMenu.OnMenuItemClickListener {

    private val mapsFragment: MapsFragment = MapsFragment()
    private val locationReceiver = LocationReceiver(this)
    private lateinit var locationManager: LocationManager
    private val requireFragment = RequirePermission()
    private lateinit var menuButton : ImageView
    private lateinit var radiusSlider : Slider

    companion object {
        const val LOCATION_REQUEST_CODE = 707
        private lateinit var application: Application
        fun getActivity() = application
        private const val metersToAMile = 1609.34
        fun getRadius() = (radius * metersToAMile)
        private var radius : Radius = 0.5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Companion.application = application

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        radiusSlider = findViewById(R.id.radius_slider)
        radius = radiusSlider.value.toDouble()
        radiusSlider.addOnChangeListener { slider, value, fromUser ->
            radius = value.toDouble()
            PlacesViewModel.setLocation(PlacesViewModel.getCurrentLocation(), getRadius(), type)
        }

        menuButton = findViewById(R.id.menu_imageview)
        menuButton.setOnClickListener{
            val popupMenu = PopupMenu(this, menuButton)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.types_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        supportFragmentManager.beginTransaction()
                .add(R.id.map_fragment, mapsFragment)
                .commit()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // the app has received the user's response
        // must check if it is the same permission
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    debug("Location Permission Granted")
                    registerLocationManager()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            )
                    ) {
                        requestLocationPermission()
                    } else {
                        // at this point let the user know that they have to enable permsssion
                        // manually to use this application
                        debug("No more requests")
                        supportFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .add(R.id.map_fragment, requireFragment)
                                .commit()
                    }
                }
            }
            else ->
                debug( "Unreqquested permission $requestCode requested")
        }
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            registerLocationManager()
            if(supportFragmentManager.fragments.contains(requireFragment))
                supportFragmentManager.beginTransaction().remove(requireFragment).commit()
        } else {
            requestLocationPermission()
        }
    }

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(locationReceiver)
    }

    private fun registerLocationManager() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100.0f, locationReceiver)
        } catch (se: SecurityException) {
            MyLogger.error(se)
        }
    }

    private var type : Type = Type.RESTAURANT
    override fun getType(): Type {
        return type
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        type = Type.fromResourceId(item.itemId)
        val latLng = PlacesViewModel.getCurrentLocation()
        debug("Got lat/lng of [$latLng] with type [${getType()}]")
        PlacesViewModel
                .setLocation(latLng, getRadius(), getType())
        return true
    }
}