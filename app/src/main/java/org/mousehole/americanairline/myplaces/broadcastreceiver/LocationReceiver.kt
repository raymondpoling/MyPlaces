package org.mousehole.americanairline.myplaces.broadcastreceiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import org.mousehole.americanairline.myplaces.model.Type
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.view.MainActivity
import org.mousehole.americanairline.myplaces.viewmodel.PlacesViewModel

class LocationReceiver(val typeGiver: TypeGiver) : LocationListener{

    interface TypeGiver {
        fun getType() : Type
    }

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        debug("Went to $latLng")
        PlacesViewModel.setLocation(latLng, MainActivity.getRadius(), typeGiver.getType())
    }
}