package org.mousehole.americanairline.myplaces.utils

typealias Radius = Double
object Constants {
    // Google Maps URL
    const val BASE_URL = "https://maps.googleapis.com/maps/api/"
    const val PLACES_URL = "place/nearbysearch/json"
    const val PHOTO_URL = "place/photo"
    const val GEOCODE_URL = "geocode/json"

    // places query arguments
    const val TYPE = "type"
    const val RADIUS = "radius"
    const val API_KEY = "key"
    const val LOCATION = "location"
    const val PAGE_TOKEN = "pagetoken"

    // photo query arguments
    const val PHOTO_ID = "photoreference"
    const val HEIGHT = "maxheight"
    const val WIDTH = "maxwidth"

    // 640x480
    const val DESIRED_HEIGHT = 320
    const val DESIRED_WIDTH = 200

    // Geocoding
    const val PLACE_ID = "place_id"

    // argument strings
    const val NAME = "name"
    const val LATLNG = "latLng"
}