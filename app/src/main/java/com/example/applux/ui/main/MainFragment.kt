package com.example.applux.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.applux.R
import com.example.applux.ui.chat.ChatFragment
import com.example.applux.databinding.FragmentMainBinding
import com.example.applux.ui.register.RegisterActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainFragment"

    private lateinit var binding: FragmentMainBinding


    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        binding.toolbarOfApp.title = ""
        setHasOptionsMenu(true)
        val adapter = ViewAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewpagerofapplux.adapter = adapter
        val names = arrayOf("Chats")
        binding.viewpagerofapplux.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                activity?.invalidateOptionsMenu()
            }
        })
        TabLayoutMediator(binding.tablayout, binding.viewpagerofapplux) { tab, position ->
            tab.text = names[position]
        }.attach()

        val acti = (activity as AppCompatActivity)
        acti.setSupportActionBar(binding.toolbarOfApp)


        binding.findcontacts.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_contactsFragment)
        }

        val navHost =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container) as NavHostFragment
        appBarConfiguration = AppBarConfiguration(navHost.navController.graph, binding.drawerLayout)
        binding.navigationView.setupWithNavController(navHost.navController)
        acti.setupActionBarWithNavController(
            navController = navHost.navController,
            appBarConfiguration
        )

        val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    requireActivity().finish()
                }
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressCallback)
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.toolbarOfApp,
            R.string.open_navigation_drawer,
            R.string.close_navigation_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }


    inner class ViewAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {
        override fun getItemCount(): Int = 1

        override fun createFragment(position: Int): Fragment {
            return ChatFragment()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when (binding.viewpagerofapplux.currentItem) {
            0 -> {
                inflater.inflate(R.menu.menu_chatfrag, menu)
            }
            1 -> {
                inflater.inflate(R.menu.menu_pagesfrag, menu)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        return when (itemId) {
            R.id.nav_settings_item -> {
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                true
            }

            R.id.nav_logout ->{
                if (Firebase.auth.currentUser != null){
                    Firebase.auth.signOut()
                    val intent = Intent(requireContext(), RegisterActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                return true
            }

            else -> false
        }
    }


}