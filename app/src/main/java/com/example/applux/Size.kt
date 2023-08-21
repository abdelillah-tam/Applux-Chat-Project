package com.example.applux

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

class Size {
    @Composable
    fun heightOfScreen() : Double {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp.toDouble()
    }

    @Composable
    fun widthOfScreen() : Double{
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp.toDouble()
    }
}