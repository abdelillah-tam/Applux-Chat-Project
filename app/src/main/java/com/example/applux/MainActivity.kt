package com.example.applux

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.applux.AppluxDestinations.AUTHENTICATION_DESTINATION
import com.example.applux.AppluxDestinations.MAIN_DESTINATION
import com.example.applux.domain.models.Message
import com.example.applux.ui.main.MainViewModel
import com.example.applux.ui.theme.AppluxTheme
import com.example.applux.ui.theme.Grey_300
import com.example.applux.ui.theme.Orange_100
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)


        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppluxTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (auth.currentUser?.uid == null) {
                        AppluxNavGraph(
                            startDestination = AUTHENTICATION_DESTINATION
                        )
                    } else {
                        AppluxNavGraph(
                            startDestination = MAIN_DESTINATION
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.updateLastSeenViewModel(Timestamp.now().seconds.toString(), OnlineOrOffline.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.updateLastSeenViewModel(Timestamp.now().seconds.toString(), OnlineOrOffline.OFFLINE)
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

