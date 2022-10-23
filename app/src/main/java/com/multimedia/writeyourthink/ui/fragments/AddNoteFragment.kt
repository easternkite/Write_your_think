package com.multimedia.writeyourthink.ui.fragments

import android.Manifest
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.Util.getDiaryActivity
import com.multimedia.writeyourthink.Util.themeColor
import com.multimedia.writeyourthink.databinding.FragmentAddNoteBinding
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.services.GpsTracker
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddNoteFragment: Fragment(R.layout.fragment_add_note) {

    @Inject
    lateinit var gpsTracker: GpsTracker

    private val viewModel: DiaryViewModel by activityViewModels()

    private var _binding: FragmentAddNoteBinding? = null
    val binding get() = _binding!!

    val args: AddNoteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(layoutInflater, container, false)
        initToolbar()
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSurface)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        binding.etLocation.setText(args.diary.where)
        binding.etContents.setText(args.diary.contents)
        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.inflateMenu(R.menu.top_menu)
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.isTitleCentered = true
        binding.toolbar.menu.removeItem(R.id.action_edit)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> {
                    Toast.makeText(activity, "add item successfully.", Toast.LENGTH_LONG).show()
                    viewModel.addDiary(
                        location = binding.etLocation.text.toString(),
                        contents = binding.etContents.text.toString(),
                        address = getCurrentAddress(
                            latitude = gpsTracker.latitude,
                            longitude = gpsTracker.longitude
                        ),
                        date = args.diary.date.ifEmpty { null }
                    )
                    // TODO : 데이터 전달에 관한 예외처리를 할 것
                    findNavController().popBackStack()

                    true
                }
                else -> false
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getCurrentAddress(latitude: Double, longitude: Double): String { //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                100
            )
        } catch (ioException: IOException) {
//네트워크 문제
            Toast.makeText(activity, R.string.geocoderUnavailable, Toast.LENGTH_LONG).show()
            tedPermission()
            return getString(R.string.geocoderUnavailable)
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(activity, R.string.wrongGps, Toast.LENGTH_LONG).show()
            tedPermission()
            return ""
        }
        if (addresses == null || addresses.size == 0) {
            tedPermission()
            return ""
        }
        val address = addresses[0]
        return address.getAddressLine(0).toString() + "\n"
    }

    private fun tedPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                /** 권한요청성공  */
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }
        TedPermission.with(activity)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_3))
            .setDeniedMessage(resources.getString(R.string.permission_1))
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check()
    }

}