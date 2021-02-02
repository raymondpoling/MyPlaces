package org.mousehole.americanairline.myplaces.network.model.geocoding

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)