package org.mousehole.americanairline.myplaces.utils

typealias Radius = Double
object Constants {
    // RetroKit URL
    const val BASE_URL = "https://maps.googleapis.com/maps/api/"
    const val PLACES_URL = "place/nearbysearch/json"

    // query arguments
    const val TYPE = "type"
    const val RADIUS = "radius"
    const val API_KEY = "key"
    const val LOCATION = "location"
    const val PAGE_TOKEN = "pagetoken"
}