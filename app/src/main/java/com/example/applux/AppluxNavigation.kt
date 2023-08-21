package com.example.applux

import androidx.navigation.NavHostController
import com.example.applux.AppluxDestinations.CHAT_CHANNEL_DESTINATION
import com.example.applux.AppluxDestinations.CONTACTS_DESTINATION
import com.example.applux.AppluxDestinations.MAIN_DESTINATION
import com.example.applux.AppluxDestinations.SETTINGS_DESTINATION

object AppluxDestinations{
    const val AUTHENTICATION_DESTINATION = "AUTHENTICATION_DESTINATION"
    const val HOME_DESTINATION = "HOME_DESTINATION"
    const val MAIN_DESTINATION = "MAIN_DESTINATION"
    const val CHAT_CHANNEL_DESTINATION = "CHAT_CHANNEL_DESTINATION"
    const val CONTACTS_DESTINATION = "CONTACTS_DESTINATION"
    const val SETTINGS_DESTINATION = "SETTINGS_DESTINATION"
}

class AppluxNavActions(private val navHostController: NavHostController){

    fun navigateToMainScreen(previousRoute: String){
        navHostController.navigate(route = MAIN_DESTINATION){
            popUpTo(previousRoute){
                inclusive = true
            }
        }
    }

    fun navigateToChatChannelScreen(uid: String){
        navHostController.navigate(route = "${CHAT_CHANNEL_DESTINATION}/$uid")
    }

    fun navigateToContactsScreen(){
        navHostController.navigate(route = CONTACTS_DESTINATION)
    }

    fun navigateToSettingsScreen(){
        navHostController.navigate(route = SETTINGS_DESTINATION)
    }

    fun back(){
        navHostController.navigateUp()
    }
}