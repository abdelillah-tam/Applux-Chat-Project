package com.example.applux

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.applux.AppluxDestinations.AUTHENTICATION_DESTINATION
import com.example.applux.AppluxDestinations.CHAT_CHANNEL_DESTINATION
import com.example.applux.AppluxDestinations.CONTACTS_DESTINATION
import com.example.applux.AppluxDestinations.MAIN_DESTINATION
import com.example.applux.AppluxDestinations.SETTINGS_DESTINATION
import com.example.applux.ui.chatchannel.ChatChannelScreen
import com.example.applux.ui.contacts.ContactsScreen
import com.example.applux.ui.main.MainScreen
import com.example.applux.ui.register.AuthenticationScreen
import com.example.applux.ui.settings.SettingsScreen
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppluxNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    appluxNavActions: AppluxNavActions = AppluxNavActions(navHostController),
    startDestination: String = MAIN_DESTINATION,
    size: Size = Size(),
    systemUiController: SystemUiController = rememberSystemUiController(),
) {

    val height = size.heightOfScreen()
    val dependingHeight = height / 856


    Scaffold(modifier = modifier) { paddingValues ->
        NavHost(
            navController = navHostController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(route = AUTHENTICATION_DESTINATION) {
                AuthenticationScreen(
                    dependingHeight = dependingHeight,
                    onExist = {
                        appluxNavActions.navigateToMainScreen(AUTHENTICATION_DESTINATION)
                    },
                    systemUiController = systemUiController
                )
            }

            composable(route = MAIN_DESTINATION) {
                MainScreen(
                    dependingHeight = dependingHeight,
                    systemUiController = systemUiController,
                    onChatItemClick = { uid ->
                        appluxNavActions.navigateToChatChannelScreen(uid)
                    },
                    findContacts = {
                        appluxNavActions.navigateToContactsScreen()
                    },
                    onSettingsPressed = {
                        appluxNavActions.navigateToSettingsScreen()
                    }
                )
            }
            composable(
                route = "${CHAT_CHANNEL_DESTINATION}/{uid}",
                arguments = listOf(
                    navArgument(
                        name = "uid"
                    ) {
                        type = NavType.StringType
                    }
                )
            ) {
                val uid = it.arguments!!.getString("uid")!!
                ChatChannelScreen(
                    dependingHeight = dependingHeight,
                    systemUiController = systemUiController,
                    uid = uid,
                    back = {
                        appluxNavActions.back()
                    }
                )
            }

            composable(
                route = CONTACTS_DESTINATION
            ){
                ContactsScreen(
                    modifier = modifier,
                    dependingHeight = dependingHeight,
                    onContactItemClick = { uid ->
                        appluxNavActions.navigateToChatChannelScreen(uid)
                    },
                    onBack = {
                        appluxNavActions.back()
                    },
                    systemUiController = systemUiController
                )
            }

            composable(
                route = SETTINGS_DESTINATION
            ){
                SettingsScreen(
                    modifier = modifier,
                    dependingHeight = dependingHeight
                )
            }
        }
    }
}