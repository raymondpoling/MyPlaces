package org.mousehole.americanairline.myplaces.network.model.geocoding

data class GeocodingResponse(
    val results: List<Result>,
    val status: String
)