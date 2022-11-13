package com.example.applux.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.applux.R
import com.example.applux.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val contactArg : ProfileFragmentArgs by navArgs()

    private val profileViewModel : ProfileViewModel by viewModels()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)
        binding.toolbarOfProfilefrag.title = ""

        val acti = (activity as AppCompatActivity)
        acti.setSupportActionBar(binding.toolbarOfProfilefrag)
        val navHost = acti.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        acti.setupActionBarWithNavController(navHost.navController)
        setHasOptionsMenu(true)

        val contact = contactArg.contactUser
        val profileBitmap = contactArg.profileBitmap

        profileViewModel.setContactUserViewModel(contact)
        profileViewModel.setProfileBitmapViewModel(profileBitmap)
        profileViewModel.getAboutViewModel(contact.uid!!)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                profileViewModel.state.collect{
                    binding.profilenameTextview.text = it.contactUser!!.name
                    binding.profilePhoneNumber.text = it.contactUser.phoneOrEmail

                    if (it.profileBitmap != null){
                        binding.profilePic.setImageBitmap(it.profileBitmap)
                    }else{
                        binding.profilePic.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_face, null))
                    }

                    if (it.about != null){
                        binding.profileAbout.text = it.about.about
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> false
        }
    }


}