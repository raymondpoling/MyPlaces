package org.mousehole.americanairline.myplaces.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.mousehole.americanairline.myplaces.model.Location
import org.mousehole.americanairline.myplaces.model.Locations
import org.mousehole.americanairline.myplaces.model.Type
import org.mousehole.americanairline.myplaces.network.PlacesRetrofit
import org.mousehole.americanairline.myplaces.network.model.PlacesResult
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.utils.Radius

object PlacesViewModel : ViewModel() {

    private val placesLiveData : MutableLiveData<Pair<Locations,Type>> = MutableLiveData()
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    private fun subscriptionService(type:Type) : (PlacesResult) -> Unit {
        return {  pr: PlacesResult ->
            debug("True response is: $pr")
            val locations = Locations(pr.results.map { u ->
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