package com.example.applux.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.applux.R
import com.example.applux.ui.theme.Orange_500

@Composable
fun NavigationDrawer(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    onSettingsPressed: () -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Box(modifier = modifier
            .background(Orange_500)
            .fillMaxWidth()
            .height((240 * dependingHeight).dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    onSettingsPressed()
                }.padding(
                    (16 * dependingHeight).dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = modifier
                    .height((48 * dependingHeight).dp)
                    .width((48 * dependingHeight).dp),
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = ""
            )
            Text(
                modifier = modifier.padding(start = (8 * dependingHeight).dp),
                text = "Settings"
            )
        }
    }

}