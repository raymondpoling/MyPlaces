package org.mousehole.americanairline.myplaces.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Photo.PHOTO
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
import org.mousehole.americanairline.myplaces.utils.Constants.LATLNG
import org.mousehole.americanairline.myplaces.utils.Constants.NAME
import org.mousehole.americanairline.myplaces.utils.Constants.PLACE_ID
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.viewmodel.PlacesViewModel

class MapsFragment : Fragment() {

    private var firstRun = true

    private val placeFragment = PlaceFragment()

    @SuppressLint("MissingPermission")
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
                val latLng = LatLng(t.lat, t.long)
                googleMap.addMarker(MarkerOptions()
                        .title(t.name)
                        .position(latLng)
                        .alpha(t.businessStatus?.alpha?:.5f)
                        .icon(icon)).tag = t
            }
        })
        PlacesViewModel.getLocation().observe(this, {
            googleMap.clear()
            googleMap.isMyLocationEnabled = true
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
//            googleMap.addMarker(MarkerOptions()
//                    .title("Myself")
//                    .position(latLng))
            debug("Places view Model markers and circles should be added here!!!!!!!!!1")
            val location = Location(
                    latLng.latitude,
                    latLng.longitude,
                    "",
                    "Myself",
                    type,
                    "",
                    null)
            PlacesViewModel.getNearbyPlaces(location, radius, type)
        })
        googleMap.setOnInfoWindowClickListener {
            debug("Clicked on ${it.tag}")
            val location = (it.tag as Location)
            val latLng = "${location.lat},${location.long}"
            val bundle = Bundle()
            bundle.putString(NAME,location.name)
            bundle.putString(PLACE_ID, location.placeId)
            bundle.putString(LATLNG, latLng)
            bundle.putString(PHOTO, location.photoId)
            placeFragment.arguments = bundle

            parentFragmentManager
                    .beginTransaction()
                    .add(R.id.overlay_fragment, placeFragment)
                    .addToBackStack(placeFragment.tag)
                    .commit()
        }
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
