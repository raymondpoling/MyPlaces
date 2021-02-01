package org.mousehole.americanairline.myplaces.model

import org.mousehole.americanairline.myplaces.R
import java.util.*

enum class Type(val resourceId:Int, val color:Int) {
    AQUARIUM(R.mipmap.ic_aquarium, R.color.aquarium_aqua),
    ART_GALLERY(R.mipmap.ic_art_gallery, R.color.art_yellow),
    CAFE(R.mipmap.ic_cafe, R.color.cafe_brown),
    CONVENIENCE_STORE(R.mipmap.ic_convenience_store, R.color.convenience_gray),
    MUSEUM(R.mipmap.ic_museum, R.color.museum_purple),
    PARK(R.mipmap.ic_park, R.color.park_green),
    RESTAURANT(R.mipmap.ic_restaurant, R.color.restaurant_red),
    TOURIST_ATTRACTION(R.mipmap.ic_tourist_attraction, R.color.tourist_white),
    UNKNOWN(R.mipmap.ic_unknown_red, android.R.color.holo_blue_bright);

    fun toKey(): String = this.toString().toLowerCase(Locale.ROOT)

    companion object {
        fun fromResourceId(resourceId: Int): Type {
            return when (resourceId) {
                R.id.aquarium_id -> AQUARIUM
                R.id.art_gallery_id -> ART_GALLERY
                R.id.cafe_id -> CAFE
                R.id.convenience_id -> CONVENIENCE_STORE
                R.id.museum_id -> MUSEUM
                R.id.park_id -> PARK
                R.id.restaurant_id -> RESTAURANT
                R.id.tourist_id -> TOURIST_ATTRACTION
                else -> UNKNOWN
            }
        }
    }
}
data class Location(val lat:Double, val long: Double, val name: String, val type: Type) {
    override fun toString(): String {
        return "$lat,$long"
    }
}
data class Locations(val locations:List<Location>)
