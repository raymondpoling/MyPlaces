package org.mousehole.americanairline.myplaces.network

import io.reactivex.Observable
import org.mousehole.americanairline.myplaces.network.model.PlacesResult
import org.mousehole.americanairline.myplaces.utils.Constants.API_KEY
import org.mousehole.americanairline.myplaces.utils.Constants.LOCATION
import org.mousehole.americanairline.myplaces.utils.Constants.PAGE_TOKEN
import org.mousehole.americanairline.myplaces.utils.Constants.PLACES_URL
import org.mousehole.americanairline.myplaces.utils.Constants.RADIUS
import org.mousehole.americanairline.myplaces.utils.Constants.TYPE
import org.mousehole.americanairline.myplaces.utils.Radius
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesAPI {

    @GET(PLACES_URL)
    fun getNearbyPlaces(@Query(API_KEY) apiKey : String,
                        @Query(LOCATION) location : String,
                        @Query(TYPE) type : String,
                        @Query(RADIUS) radius : Radius) : Observable<PlacesResult>

    @GET(PLACES_URL)
    fun getMoreNearbyPlaces(@Query(API_KEY) apiKey : String,
                            @Query(PAGE_TOKEN, encoded = false) pagetoken : String) : Observable<PlacesResult>
}