package com.example.applux.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.applux.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var navController : NavController
    private lateinit var navHost : NavHostFragment
    //private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_register)

        navHost = supportFragmentManager.findFragmentById(R.id.registerfraghost) as NavHostFragment
        navController = navHost.navController


    }





    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.registerfraghost) as NavHostFragment
        val navControllerUp = navHostFragment.navController
        return navControllerUp.navigateUp() || super.onSupportNavigateUp()
    }


}