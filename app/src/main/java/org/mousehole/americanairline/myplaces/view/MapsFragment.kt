package org.mousehole.americanairline.myplaces.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.mousehole.americanairline.myplaces.R
import org.mousehole.americanairline.myplaces.model.Location
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.viewmodel.PlacesViewModel

class MapsFragment : Fragment() {

    private var firstRun = true

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        PlacesViewModel.getPlacesData().observe(this, {
//            googleMap.clear()
            val (locations, type) = it
            val icon = BitmapDescriptorFactory.fromResource(type.resourceId)
            locations.locations.forEach { t ->
                googleMap.addMarker(MarkerOptions()
                        .title(t.name)
                        .position(LatLng(t.lat, t.long))
                        .icon(icon))
            }
        })
        PlacesViewModel.getLocation().observe(this, {
            googleMap.clear()
            val (latLng, radius, type) = it
            debug("Map at [$latLng]")
            if(firstRun) {
                firstRun = false
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(13.5f))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
            val color = resources.getColor(type.color)
            googleMap.addCircle(CircleOptions().center(latLng)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1f)
                    .fillColor(color)
                    .radius(radius))
            googleMap.addMarker(MarkerOptions()
                    .title("Myself")
                    .position(latLng))
            debug("Places view Model markers and circles should be added here!!!!!!!!!1")
            val location = Location(
                    latLng.latitude,
                    latLng.longitude,
                    "Myself",
                    type)
            PlacesViewModel.getNearbyPlaces(location, radius, type)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}