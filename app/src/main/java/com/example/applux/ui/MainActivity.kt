package com.example.applux.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.applux.R
import com.example.applux.ui.register.RegisterActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    @Inject lateinit var auth : FirebaseAuth
    private lateinit var navController : NavController
    private lateinit var navHost : NavHostFragment

    //private val repositoryImpl: FirestoreRepositoryImpl by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )


        if (auth.currentUser?.uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        }

        //val currentContact : ContactUser? = intent.getParcelableExtra("currentUser")



    }


    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

   /* override fun onStart() {
        super.onStart()
        repositoryImpl.updateLastSeen(Timestamp.now().seconds.toString(), OnlineOrOffline.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        repositoryImpl.updateLastSeen(Timestamp.now().seconds.toString(), OnlineOrOffline.OFFLINE)
    }*/
}

