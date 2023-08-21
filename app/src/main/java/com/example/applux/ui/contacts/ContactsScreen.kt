package com.example.applux.ui.contacts

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.applux.R
import com.example.applux.widgets.CustomTextField
import com.example.applux.widgets.Hint
import com.google.accompanist.systemuicontroller.SystemUiController
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.HashMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    systemUiController: SystemUiController,
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    onContactItemClick: (String) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    systemUiController.setSystemBarsColor(Color.White)

    val onlineContacts = contactsViewModel.onlineContactState.collectAsState()
    val localContacts = contactsViewModel.localContactState.collectAsState()

    LaunchedEffect(true) {
        contactsViewModel.getContactsViewModel(readContact(context))
    }

    var search by remember {
        mutableStateOf("")
    }



    Scaffold(
        modifier = modifier.systemBarsPadding(),
        backgroundColor = Color.White,

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    CustomTextField(
                        modifier = modifier.fillMaxWidth(0.9f),
                        value = search,
                        onValueChange = { value ->
                            search = value
                            if (search.length > 2) {
                                contactsViewModel.findUser(search)
                            }
                        },
                        hint = {
                            Hint(
                                text = "Search for a user ..",
                                fontSize = (18 * dependingHeight).sp
                            )
                        },
                        borderColor = Color.White,
                        focusedBorder = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(painterResource(id = R.drawable.ic_up_button), "")
                    }
                })
        }
    ) { paddingValues ->

        LazyColumn(modifier.padding(paddingValues)) {
            itemsIndexed(onlineContacts.value.contactsItemUiState) { _, item ->
                ContactItem(
                    dependingHeight = dependingHeight,
                    item = item,
                    onContactItemClick = onContactItemClick
                )
            }

            item {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = (4 * dependingHeight).dp)
                        .background(Color.LightGray)
                ) {
                    Text(
                        modifier = modifier
                            .padding(
                                horizontal = (8 * dependingHeight).dp,
                                vertical = (4 * dependingHeight).dp
                            ),
                        text = "Local Contacts",
                        color = Color.White
                    )
                }
            }

            itemsIndexed(localContacts.value.contactsItemUiState) { _, item ->
                ContactItem(
                    dependingHeight = dependingHeight,
                    item = item,
                    onContactItemClick = onContactItemClick
                )
            }
        }

    }

}

@SuppressLint("Range")
fun readContact(context: Context): HashMap<String, String> {

    val from = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )


    val cur = context.contentResolver!!.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        from,
        null,
        null,
        null
    )
    val hashMap = HashMap<String, String>()

    if (cur != null) {
        while (cur.moveToNext()) {
            val name =
                cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            var number =
                cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            number = number.replace("-", "").replace("(", "").replace(")", "").replace(" ", "")

            val telephonyManager: TelephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val usersCountryISOCode: String = telephonyManager.networkCountryIso.uppercase()
            val phoneUtil = PhoneNumberUtil.createInstance(context)

            val countryCode = "+" + phoneUtil.getCountryCodeForRegion(usersCountryISOCode)

            if (!number.startsWith(countryCode)) {
                val phoneNumber = phoneUtil.parse(number, usersCountryISOCode)
                if (phoneUtil.isValidNumber(phoneNumber)) {
                    number = "+" + phoneNumber.countryCode + phoneNumber.nationalNumber
                }
            }
            hashMap[name] = number
        }

        cur.close()
    }

    return hashMap
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    item: ContactsItemUiState,
    onContactItemClick: (String) -> Unit
) {
    androidx.compose.material.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = (8 * dependingHeight).dp,
                vertical = (4 * dependingHeight).dp
            )
            .clickable(
                enabled = true,
                onClick = {
                    onContactItemClick(item.contactUser!!.uid!!)
                })
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.picture != null) {
                GlideImage(
                    modifier = modifier
                        .clip(CircleShape)
                        .height((72 * dependingHeight).dp)
                        .width((72 * dependingHeight).dp),
                    model = item.picture!!.pic,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                modifier = modifier.padding(start = (8 * dependingHeight).dp),
                text = item.contactUser!!.name ?: item.contactUser!!.phoneOrEmail!!,
                color = Color.Black
            )
        }
    }
}