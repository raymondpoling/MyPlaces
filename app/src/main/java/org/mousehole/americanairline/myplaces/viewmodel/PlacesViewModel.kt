package org.mousehole.americanairline.myplaces.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.mousehole.americanairline.myplaces.model.*
import org.mousehole.americanairline.myplaces.network.PlacesRetrofit
import org.mousehole.americanairline.myplaces.network.model.places.PlacesResult
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
                debug("Place Location: ${u.place_id}")
                val t = u.geometry.location
                Location(t.lat, t.lng, u.place_id, u.name, type,
                        u.photos?.map{it.photo_reference}?.firstOrNull(),
                        u.business_status?.let (BusinessStatus::fromString))
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriptionService(type)))
        return placesLiveData
    }

    private fun getMoreNearbyPlaces(type:Type, nextPageToken : String) : LiveData<Pair<Locations,Type>> {
        compositeDisposable
            .add(PlacesRetrofit
                .getMoreNearbyPlaces(nextPageToken)
                .observeOn(AndroidSchedulers.mainThread())
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

    private val addressLiveData = MutableLiveData<AddressData>()
    private val geocodingDisposable = CompositeDisposable()
    fun getGeocodingResult(name:String, placeId: String) : LiveData<AddressData> {
        geocodingDisposable.add(
                PlacesRetrofit.getAddress(placeId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            debug("Geocoding Response? ${it.results.firstOrNull()?.formatted_address}")
                            val addressData =
                                    it.let {
                                        val address = it.results.firstOrNull()?.formatted_address?:"No Address Found"
                                        AddressData(name, address)
                                    }
                            addressLiveData.postValue(addressData)
                            geocodingDisposable.clear()
                        })
        return addressLiveData
    }
    fun getAddressLiveData() : LiveData<AddressData> = addressLiveData
}