package org.mousehole.americanairline.myplaces.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.mousehole.americanairline.myplaces.R

class RequirePermission : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.permission_required, container, false)
    }

    private lateinit var permissionLayout : ConstraintLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLayout = view.findViewById(R.id.permission_layout)
        permissionLayout.setOnClickListener {
            // Implicit intents to open setting... this
            // is specific apps permissions to be precise
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, "Permissions")
            intent.data = uri
            startActivity(intent)
        }
    }
}