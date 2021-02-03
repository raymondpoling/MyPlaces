package org.mousehole.americanairline.myplaces.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Photo.PHOTO
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.mousehole.americanairline.myplaces.R
import org.mousehole.americanairline.myplaces.network.PlacesRetrofit
import org.mousehole.americanairline.myplaces.utils.Constants.LATLNG
import org.mousehole.americanairline.myplaces.utils.Constants.NAME
import org.mousehole.americanairline.myplaces.utils.Constants.PLACE_ID
import org.mousehole.americanairline.myplaces.utils.MyLogger.debug
import org.mousehole.americanairline.myplaces.viewmodel.PlacesViewModel

class PlaceFragment : Fragment() {

    private lateinit var nameTextView : TextView
    private lateinit var addressTextView : TextView
    private lateinit var photoImageView: ImageView
    private lateinit var mapIntentButton : Button
    private lateinit var closeButton : Button

    private lateinit var name : String

    private val placeLiveData = PlacesViewModel.getAddressLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.place_fragment,
                container,
                false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = arguments?.getString(NAME, "No Name")?:"No Name"
        val placeId = arguments?.getString(PLACE_ID)
        val latLng = arguments?.getString(LATLNG)

        nameTextView = view.findViewById(R.id.name_textview)
        addressTextView = view.findViewById(R.id.address_textview)
        addressTextView.text = "No Address Available"
        photoImageView = view.findViewById(R.id.photo_imageview)

        val photo = arguments?.getString(PHOTO)

        debug("Got photo list size: $photo")
        when(photo) {
            null ->
                Glide.with(this)
                        .load(R.mipmap.ic_not_found)
                        .into(photoImageView)
            else ->
                Glide.with(this)
                        .load(PlacesRetrofit.getPhoto(photo))
                        .into(photoImageView)
        }

        placeLiveData.observe(viewLifecycleOwner, {
            addressTextView.text = it.address
        })

        placeId?.apply {
            PlacesViewModel.getGeocodingResult(name, this)
        }

        nameTextView.text = name

        mapIntentButton = view.findViewById(R.id.map_intent_button)
        mapIntentButton.setOnClickListener {
            debug("latLng is $latLng")
            val gmmIntentUri =
                    Uri.parse("google.navigation:q=$latLng")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        closeButton = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}