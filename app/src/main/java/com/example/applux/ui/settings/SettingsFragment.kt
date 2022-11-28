package com.example.applux.ui.settings


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.applux.Privacy
import com.example.applux.R
import com.example.applux.databinding.EditPrivaciesBinding
import com.example.applux.databinding.EditValuesBinding
import com.example.applux.databinding.FragmentSettingsBinding
import com.example.applux.databinding.LoadingBinding
import com.example.applux.domain.models.ContactUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var currentContact: ContactUser

    private lateinit var navController: NavController

    private var isComingFromGallery = false

    private lateinit var uri: Uri
    private lateinit var loading: AlertDialog
    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        setHasOptionsMenu(true)
        val state = settingsViewModel.state

        val acti = (activity as AppCompatActivity)
        acti.setSupportActionBar(binding.toolbarSettingsFragment)
        val navHost =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
        acti.setupActionBarWithNavController(navController)

        val privacies = resources.getStringArray(R.array.privacy)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            privacies
        )



        Log.e("TAG", "onViewCreated: " + state.value.contactUser?.name)
        if (state.value.contactUser != null) {
            currentContact = state.value.contactUser!!
        }
        var actionMode: android.view.ActionMode?


        val actionModeCallback = object : android.view.ActionMode.Callback {

            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                val inflater = mode?.menuInflater
                inflater?.inflate(R.menu.menu_context_settings, menu)
                return true
            }

            override fun onPrepareActionMode(p0: android.view.ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(
                mode: android.view.ActionMode?,
                item: MenuItem?
            ): Boolean {
                when (item?.itemId) {
                    R.id.home ->{}
                    R.id.save -> {
                        isComingFromGallery = false
                        val loadingBinding = LoadingBinding.inflate(layoutInflater)
                        loading = MaterialAlertDialogBuilder(requireContext())
                            .setView(loadingBinding.root)
                            .show()
                        //settingsViewModel.onEvent(SettingsEvent.UploadProfilePicture(bitmap))
                        settingsViewModel.onEvent(SettingsEvent.UploadProfilePicture(uri, requireContext().contentResolver))
                    }

                }
                return false
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {
                actionMode = null
            }

        }
        
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                Glide.with(requireContext())
                    .load(it)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.settingsProfilePic)
                //bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                uri = it
                actionMode = binding.appBarLayout.startActionMode(actionModeCallback)

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                state.collect {
                    if (it.contactUser != null) {
                        currentContact = state.value.contactUser!!
                        binding.coll.title = state.value.contactUser!!.name
                        binding.settingsPhonenumber.text = state.value.contactUser!!.phoneOrEmail
                        binding.settingsUsername.text = state.value.contactUser!!.name
                    }

                    if (it.picture != null && !isComingFromGallery) {

                        if (it.picture.pic.equals("") || it.picture.privacy.equals(Privacy.NONOE)) {
                            binding.settingsProfilePic.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_face, null))
                        }else{
                            Glide.with(requireContext())
                                .load(it.picture.pic)
                                .into(binding.settingsProfilePic)
                        }

                        when (it.picture.privacy) {
                            Privacy.NONOE -> {
                                binding.settingsPrivacyProfilephoto.text = privacies[0]
                            }
                            Privacy.CONTACTS -> {
                                binding.settingsPrivacyProfilephoto.text = privacies[1]
                            }
                            else -> {
                                binding.settingsPrivacyProfilephoto.text = privacies[2]
                            }
                        }



                    }

                    if (it.about != null) {
                        binding.settingsAbout.text = it.about.about
                        when (it.about.privacy) {
                            Privacy.NONOE -> {
                                binding.settingsPrivacyAbout.text = privacies[0]
                            }
                            Privacy.CONTACTS -> {
                                binding.settingsPrivacyAbout.text = privacies[1]
                            }
                            else -> {
                                binding.settingsPrivacyAbout.text = privacies[2]
                            }
                        }
                    }

                    if (it.lastSeen != null) {
                        when (it.lastSeen.privacy) {
                            Privacy.NONOE -> {
                                binding.settingsPrivacyLastseen.text = privacies[0]
                            }
                            Privacy.CONTACTS -> {
                                binding.settingsPrivacyLastseen.text = privacies[1]
                            }
                            else -> {
                                binding.settingsPrivacyLastseen.text = privacies[2]
                            }
                        }
                    }

                    it.userMessages.firstOrNull()?.let {
                        Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT).show()
                        if (loading.isShowing){
                            loading.dismiss()
                        }
                        settingsViewModel.userMessageShown(it.id)
                    }
                }
            }
        }

        binding.settingsProfilePic.setOnClickListener{
            isComingFromGallery = true
            getContent.launch("image/*")
        }

        binding.settingsClickUsername.setOnClickListener{
            val valueBinding = EditValuesBinding.inflate(layoutInflater)
            valueBinding.editValues.editText?.text =
                Editable.Factory.getInstance().newEditable(currentContact.name)
            loading = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit your username")
                .setView(valueBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    settingsViewModel.onEvent(SettingsEvent.UpdateUsername(valueBinding.editValues.editText?.text.toString()))
                }
                .setNegativeButton("Cancel") { _, _ ->

                }
                .show()
        }

        binding.settingsClickAbout.setOnClickListener{
            val valueBinding = EditValuesBinding.inflate(layoutInflater)
            if (state.value.about != null) {
                valueBinding.editValues.editText?.text =
                    Editable.Factory.getInstance().newEditable(state.value.about!!.about)
            }
            loading = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit your about")
                .setView(valueBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    settingsViewModel.onEvent(SettingsEvent.UpdateAbout(valueBinding.editValues.editText?.text.toString()))
                }
                .setNegativeButton("Cancel") { _, _ ->

                }
                .show()

        }

        binding.settingsClickLastseenPrivacy.setOnClickListener{
            val privacyBinding = EditPrivaciesBinding.inflate(layoutInflater)
            val autoComTxtView =
                (privacyBinding.editPrivacies.editText as? AutoCompleteTextView)
            if (state.value.lastSeen != null) {
                when (state.value.lastSeen!!.privacy) {
                    Privacy.NONOE -> {
                        autoComTxtView?.text =
                            Editable.Factory.getInstance().newEditable(adapter.getItem(0))
                    }
                    Privacy.CONTACTS -> {
                        autoComTxtView?.text =
                            Editable.Factory.getInstance().newEditable(adapter.getItem(1))
                    }
                    else -> {
                        autoComTxtView?.text =
                            Editable.Factory.getInstance().newEditable(adapter.getItem(2))
                    }
                }
            }
            autoComTxtView?.setAdapter(adapter)
            loading = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change the privacy of your last seen")
                .setView(privacyBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    when (autoComTxtView?.text.toString()) {
                        privacies[0] -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateLastSeenPrivacy(Privacy.NONOE))
                        }
                        privacies[1] -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateLastSeenPrivacy(Privacy.CONTACTS))
                        }
                        else -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateLastSeenPrivacy(Privacy.PUBLIC))
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->

                }
                .show()
        }

        binding.settingsPrivacyProfilephoto.setOnClickListener{
            val privacyBinding = EditPrivaciesBinding.inflate(layoutInflater)
            val autoComTxtView =
                (privacyBinding.editPrivacies.editText as? AutoCompleteTextView)
            when (state.value.picture?.privacy) {
                Privacy.NONOE -> {
                    autoComTxtView?.text =
                        Editable.Factory.getInstance().newEditable(adapter.getItem(0))
                }
                Privacy.CONTACTS -> {
                    autoComTxtView?.text =
                        Editable.Factory.getInstance().newEditable(adapter.getItem(1))
                }
                else -> {
                    autoComTxtView?.text =
                        Editable.Factory.getInstance().newEditable(adapter.getItem(2))
                }
            }
            autoComTxtView?.setAdapter(adapter)
            loading = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change the privacy of your profile photo")
                .setView(privacyBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    when (autoComTxtView?.text.toString()) {
                        privacies[0] -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateProfilePicturePrivacy(Privacy.NONOE))
                        }
                        privacies[1] -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateProfilePicturePrivacy(Privacy.CONTACTS))
                        }
                        else -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateProfilePicturePrivacy(Privacy.PUBLIC))
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->

                }
                .show()

        }

        binding.settingsClickAboutPrivacy.setOnClickListener{

            val privacyBinding = EditPrivaciesBinding.inflate(layoutInflater)
            val autoComTxtView =
                (privacyBinding.editPrivacies.editText as? AutoCompleteTextView)
            when (state.value.about?.privacy) {
                Privacy.NONOE -> {
                    autoComTxtView?.text =
                        Editable.Factory.getInstance().newEditable(adapter.getItem(0))
                }
                Privacy.CONTACTS -> {
                    autoComTxtView?.text =
                        Editable.Factory.getInstance().newEditable(adapter.getItem(1))
                }
                else -> {
                    autoComTxtView?.text =
                        Editable.Factory.getInstance().newEditable(adapter.getItem(2))
                }
            }
            autoComTxtView?.setAdapter(adapter)

            loading = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change the privacy of your about")
                .setView(privacyBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    when (autoComTxtView?.text.toString()) {
                        privacies[0] -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateAboutPrivacy(Privacy.NONOE))
                        }
                        privacies[1] -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateAboutPrivacy(Privacy.CONTACTS))
                        }
                        else -> {
                            settingsViewModel.onEvent(SettingsEvent.UpdateAboutPrivacy(Privacy.PUBLIC))
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->
                }
                .show()
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            android.R.id.home ->{
                findNavController().navigateUp()
                return true
            }
        }

        return false
    }
}

