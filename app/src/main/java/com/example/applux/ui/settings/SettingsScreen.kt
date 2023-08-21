package com.example.applux.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val state by settingsViewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.systemBarsPadding().fillMaxSize(),
        backgroundColor = Color.White
    ) { paddingValues ->

        Column(
            modifier = modifier.padding(paddingValues).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                modifier = modifier
                    .clip(CircleShape)
                    .height((160 * dependingHeight).dp)
                    .width((160 * dependingHeight).dp),
                model = state.picture?.pic,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }

    }

}