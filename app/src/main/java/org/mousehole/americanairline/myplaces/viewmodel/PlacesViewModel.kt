package org.mousehole.americanairline.myplaces.viewmodel

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.mousehole.americanairline.myplaces.R
import org.mousehole.americanairline.myplaces.model.Location
import org.mousehole.americanairline.myplaces.model.Locations
import org.mousehole.americanairline.myplaces.model.Type
import org.mousehole.americanairline.myplaces.network.PlacesRetrofit
import org.mousehole.americanairline.myplaces.network.model.PlacesResult
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.utils.Radius
import org.mousehole.americanairline.myplaces.view.MainActivity

object PlacesViewModel : ViewModel() {

    private val placesLiveData : MutableLiveData<Pair<Locations,Type>> = MutableLiveData()
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    private fun subscriptionService(type:Type) : (PlacesResult) -> Unit {
        return {  pr: PlacesResult ->
            debug("True response is: $pr")
            val locations: Locations = Locations(pr.results.map { u ->
                debug("Response is: $u")
                val t = u.geometry.location
                Location(t.lat, t.lng, u.name, type)
            })
            placesLiveData.postValue(locations to type)
            compositeDisposable.clear()
            debug("Status: [${pr.status}] Got token? ${pr.next_page_token}")
            pr.next_page_token?.let { u ->
                Thread.sleep(2 * 1000)
                debug("Got page $u")
                getMoreNearbyPlaces(type, u)
            }
        }
    }

    fun getNearbyPlaces(location: Location, radius:Radius, type: Type) : LiveData<Pair<Locations,Type>> {
        compositeDisposable
            .add(PlacesRetrofit
                .getNearbyPlaces(location, radius)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriptionService(location.type)))
        return placesLiveData
    }

    private fun getMoreNearbyPlaces(type:Type, nextPageToken : String) : LiveData<Pair<Locations,Type>> {
        compositeDisposable
            .add(PlacesRetrofit
                .getMoreNearbyPlaces(nextPageToken)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriptionService(type)))
        return placesLiveData
    }

    fun getTypeIcon(type:Type) : Pair<BitmapDescriptor, Int> {
        val t : Pair<Int,Int> = when (type) {
            Type.AQUARIUM -> (R.mipmap.ic_acquarium to R.color.aquarium_aqua)
            Type.ART_GALLERY -> (R.mipmap.ic_art_gallery to R.color.art_yellow)
            Type.TOURIST_ATTRACTION -> (R.mipmap.ic_tourist_attraction to R.color.tourist_white)
            Type.CAFE -> (R.mipmap.ic_cafe to R.color.cafe_brown)
            Type.CONVENIENCE_STORE -> (R.mipmap.ic_convenience_store to R.color.convenience_gray)
            Type.MUSEUM -> (R.mipmap.ic_museum to R.color.museum_purple)
            Type.PARK -> (R.mipmap.ic_park to R.color.park_green)
            Type.RESTAURANT -> (R.mipmap.ic_restaurant to R.color.restaurant_red)
            else -> (R.mipmap.ic_unknown_red to Color.TRANSPARENT)
        }
        return BitmapDescriptorFactory.fromResource(t.first) to t.second
    }

    fun getPlacesData() = placesLiveData

    // current location
    private val currentLocationLiveData : MutableLiveData<Triple<LatLng,Radius,Type>> = MutableLiveData()
    fun getLocation() : LiveData<Triple<LatLng,Radius,Type>> {
        return currentLocationLiveData
    }
    fun setLocation(currentLocation:LatLng, radius: Radius, type: Type) {
        latLng = currentLocation
        currentLocationLiveData.postValue(Triple(currentLocation, radius, type))
    }

    private var latLng : LatLng? = null
    fun getCurrentLocation() : LatLng = latLng?: LatLng(0.0,0.0)
}