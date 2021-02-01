package org.mousehole.americanairline.myplaces.model

import java.util.*

enum class Type {
    AQUARIUM,
    ART_GALLERY,
    CAFE,
    CONVENIENCE_STORE,
    MUSEUM,
    PARK,
    RESTAURANT,
    TOURIST_ATTRACTION,
    UNKNOWN;
    fun toKey() : String = this.toString().toLowerCase(Locale.ROOT) }
data class Location(val lat:Double, val long: Double, val name: String, val type: Type) {
    override fun toString(): String {
        return "$lat,$long"
    }
}
data class Locations(val locations:List<Location>)
