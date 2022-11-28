package com.example.applux.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.example.applux.R
import com.example.applux.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {


    private lateinit var binding: FragmentCameraBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var imageCapture: ImageCapture

    private lateinit var camera: Camera

    private lateinit var cameraSelector: CameraSelector

    private var flash = false

    private val args : CameraFragmentArgs by navArgs()

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 2
            )
        } else {
            cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider, CameraSelector.LENS_FACING_BACK)
            }, ContextCompat.getMainExecutor(requireContext()))

            binding.previewCamera.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            binding.previewCamera.scaleType = PreviewView.ScaleType.FIT_CENTER

            binding.takePhoto.setOnClickListener {
                click()
            }
            binding.flashOn.setOnClickListener {
                if (flash) {
                    camera.cameraControl.enableTorch(false)
                    flash = false
                } else {
                    camera.cameraControl.enableTorch(true)
                    flash = true
                }
            }

            binding.switchCamera.setOnClickListener {
                if (cameraSelector.lensFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraProviderFuture.get().unbindAll()
                    bindPreview(cameraProviderFuture.get(), CameraSelector.LENS_FACING_FRONT)
                } else {
                    cameraProviderFuture.get().unbindAll()
                    bindPreview(cameraProviderFuture.get(), CameraSelector.LENS_FACING_BACK)
                }
            }

            binding.toolbarOfCamera.title = ""
            binding.toolbarOfCamera.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

    }

    fun bindPreview(cameraProvider: ProcessCameraProvider, cameraLens: Int) {
        val preview = Preview.Builder().build()

        cameraSelector = CameraSelector.Builder().requireLensFacing(cameraLens).build()

        preview.setSurfaceProvider(binding.previewCamera.surfaceProvider)

        imageCapture =
            ImageCapture.Builder().setTargetRotation(binding.previewCamera.display.rotation)
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(90)
                .build()

        camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageCapture,
            preview
        )


    }

    fun click() {
        val outputFileOptions = ImageCapture
            .OutputFileOptions
            .Builder(File(activity?.externalMediaDirs?.get(0), "stoktok.jpg")).build()
        imageCapture.takePicture(outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.e("TAG", "onImageSaved: ${outputFileResults.savedUri}")
                    val action = CameraFragmentDirections
                        .actionCameraFragmentToPreviewCapturedImageFragment(outputFileResults.savedUri.toString(), args.contact)
                    findNavController().navigate(action)
                    cameraProviderFuture.get().unbindAll()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("TAG", "onError: ${exception.message}")
                }

            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED
                ) {
                    findNavController().navigateUp()
                }
            }
        }
    }


}