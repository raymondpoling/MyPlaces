package org.mousehole.americanairline.myplaces.network.model.places

data class Result(
        val business_status: String?,
        val geometry: Geometry,
        val icon: String,
        val name: String,
        val photos: List<Photo>?,
        val place_id: String,
        val plus_code: PlusCode,
        val reference: String,
        val scope: String,
        val types: List<String>,
        val vicinity: String
)