package org.mousehole.americanairline.myplaces.network.model

data class PlacesResult(
    val html_attributions: List<Any>,
    val next_page_token: String?,
    val results: List<Result>,
    val status: String
)