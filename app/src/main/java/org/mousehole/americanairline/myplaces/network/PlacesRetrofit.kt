@file:Suppress("SpellCheckingInspection")

package org.mousehole.americanairline.myplaces.network

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mousehole.americanairline.myplaces.R
import org.mousehole.americanairline.myplaces.model.Location
import org.mousehole.americanairline.myplaces.network.model.PlacesResult
import org.mousehole.americanairline.myplaces.utils.Constants.BASE_URL
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.utils.Radius
import org.mousehole.americanairline.myplaces.view.MainActivity
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

// https://maps.googleapis.com/maps/api/place/nearbysearch/json
// ?location=30,120&radius=100&key=AIzaSyDTYwHqalAU_kLcVvBM62_RwxRIPnQcY2I
object PlacesRetrofit {

    private fun client(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private val apiKey: String by lazy {
        MainActivity.getActivity().getString(R.string.google_maps_key)
    }

    private val placesRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
    private val placesAPI: PlacesAPI = placesRetrofit.create(PlacesAPI::class.java)
    fun getNearbyPlaces(location: Location, radius: Radius): Observable<PlacesResult> {
        return placesAPI.getNearbyPlaces(
            apiKey,
            location.toString(),
            location.type.toKey(),
            radius
        )
    }

    fun getMoreNearbyPlaces(pageToken: String): Observable<PlacesResult> {
        debug("pageToken is? $pageToken")
        return placesAPI.getMoreNearbyPlaces(
            apiKey,
            pageToken
        )
    }
}
