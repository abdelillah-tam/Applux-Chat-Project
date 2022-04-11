package com.example.applux.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.applux.R
import com.example.applux.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val contactArg : ProfileFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        binding.toolbarOfProfilefrag.title = ""

        val acti = (activity as AppCompatActivity)
        acti.setSupportActionBar(binding.toolbarOfProfilefrag)
        val navHost = acti.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        acti.setupActionBarWithNavController(navHost.navController)
        setHasOptionsMenu(true)

        val contact = contactArg.contactUser
        val about = contactArg.about
        binding.profilenameTextview.text = contact.name
        binding.profileAbout.text = about!!.about
        binding.profilePhoneNumber.text = contact.phone

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