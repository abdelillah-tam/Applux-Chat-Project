package com.example.applux.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.applux.R
import com.example.applux.data.TimeByZoneClient
import com.example.applux.data.WorldTimeModel
import com.example.applux.databinding.FragmentPreviewCapturedImageBinding
import com.example.applux.databinding.LoadingBinding
import com.example.applux.domain.models.Message
import com.example.applux.domain.models.MessageType
import com.example.applux.ui.chatchannel.ChatchannelFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class PreviewCapturedImageFragment : Fragment(R.layout.fragment_preview_captured_image) {

    private lateinit var binding : FragmentPreviewCapturedImageBinding

    private val values : PreviewCapturedImageFragmentArgs by navArgs()

    private val cameraViewModel : CameraViewModel by viewModels()

    private lateinit var message : Message

    private lateinit var loading: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreviewCapturedImageBinding.bind(view)

        lifecycleScope.launch {
            cameraViewModel.isSentState.collect{
                if (it){
                    loading.dismiss()
                    val action = PreviewCapturedImageFragmentDirections
                        .actionPreviewCapturedImageFragmentToChatchannelFragment(values.contactUser, null, null)
                    findNavController().navigate(action)
                }
            }
        }

        val uri = values.uri.toUri()

        binding.toolbarOfCapturedPreview.title = ""
        binding.toolbarOfCapturedPreview.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        Glide.with(requireContext())
            .load(uri)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.previewCaptured)



        TimeByZoneClient.getTime(DateTimeZone.getDefault().id)
            .enqueue(object : Callback<WorldTimeModel> {
                override fun onResponse(
                    call: Call<WorldTimeModel>,
                    response: Response<WorldTimeModel>
                ) {
                    val time = response.body()!!
                    val timestamp = DateTime.parse(time.dateTime).millis / 1000L
                    message = Message(
                        UUID.randomUUID().toString(),
                        timestamp.toString(),
                        "",
                        Firebase.auth.currentUser!!.phoneNumber,
                        Firebase.auth.currentUser!!.uid,
                        values.contactUser.phoneOrEmail,
                        values.contactUser.uid,
                        MessageType.IMAGE,
                        null
                    )

                }

                override fun onFailure(call: Call<WorldTimeModel>, t: Throwable) {

                }


            })

        binding.to.text = values.contactUser.phoneOrEmail



        val loadingBinding = LoadingBinding.inflate(layoutInflater)

        binding.sendButton.setOnClickListener {
            loading = MaterialAlertDialogBuilder(requireContext())
                .setView(loadingBinding.root)
                .show()
            cameraViewModel.sendMessageImageViewModel(message, binding.previewCaptured.drawable.toBitmap())
        }

    }


}