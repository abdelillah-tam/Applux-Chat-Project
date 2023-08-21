package com.example.applux.ui.chatchannel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.applux.R
import com.example.applux.data.TimeByZoneClient
import com.example.applux.data.WorldTimeModel
import com.example.applux.domain.models.LastSeen
import com.example.applux.domain.models.Message
import com.example.applux.domain.models.MessageType
import com.example.applux.ui.theme.Grey_300
import com.example.applux.ui.theme.Orange_100
import com.example.applux.ui.theme.Orange_500
import com.example.applux.widgets.CustomTextField
import com.example.applux.widgets.Hint
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ChatChannelScreen(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    systemUiController: SystemUiController,
    uid: String,
    chatChannelViewModel: ChatChannelViewModel = hiltViewModel(),
    back: () -> Unit
) {

    systemUiController.setStatusBarColor(Orange_500)
    systemUiController.setNavigationBarColor(Color.White)

    val state by chatChannelViewModel.state.collectAsState()

    var messages by remember {
        mutableStateOf(emptyList<MessageUiState>())
    }

    var message by remember {
        mutableStateOf("")
    }

    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())


    LaunchedEffect(state.contactUser, state.messages) {

        chatChannelViewModel.getUserDataViewModel(null, uid)
        if (state.contactUser != null) chatChannelViewModel.listenForNewMessageViewModel()

        if (messages.size != state.messages.size) {
            messages = state.messages.values.sortedBy {
                it.message.timestamp!!
            }
        }
    }


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = modifier.height((72 * dependingHeight).dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange_500
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        back()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_up_button),
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            modifier = modifier
                                .clip(CircleShape)
                                .height((56 * dependingHeight).dp)
                                .width((56 * dependingHeight).dp),
                            model = state.profilePicture ?: "",
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = modifier.padding(start = (8 * dependingHeight).dp)
                        ) {
                            Text(
                                text = state.contactUser?.name ?: "",
                                color = Color.White,
                                fontSize = (20 * dependingHeight).sp
                            )
                            if(state.lastSeen != null) {
                                Text(
                                    text = state.lastSeen!!.onlineOrOffline!!.toString(),
                                    color = Color.White,
                                    fontSize = (18 * dependingHeight).sp
                                )
                            }
                        }

                    }
                },
            )

        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = modifier.height((80 * dependingHeight).dp),
            ) {
                ConstraintLayout(modifier = modifier.fillMaxWidth()) {
                    val (messageField, sendButton) = createRefs()
                    CustomTextField(
                        modifier = modifier
                            .fillMaxHeight()
                            .constrainAs(messageField) {
                                absoluteRight.linkTo(
                                    sendButton.absoluteLeft,
                                    margin = (8 * dependingHeight).dp
                                )
                                top.linkTo(sendButton.top)
                                bottom.linkTo(sendButton.bottom)
                                absoluteLeft.linkTo(parent.absoluteLeft)
                                width = Dimension.fillToConstraints
                            },
                        value = message,
                        onValueChange = { typed ->
                            message = typed
                        },
                        borderColor = Color.Gray,
                        focusedBorder = Orange_500,
                        cornerSize = (16 * dependingHeight).dp,
                        textStyle = TextStyle(
                            fontSize = (18 * dependingHeight).sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        hint = {
                            Hint(
                                text = "Type your message ...",
                                fontSize = (18 * dependingHeight).sp
                            )
                        }
                    )
                    androidx.compose.material.FloatingActionButton(
                        modifier = modifier
                            .height((64 * dependingHeight).dp)
                            .width((64 * dependingHeight).dp)
                            .constrainAs(sendButton) {
                                absoluteRight.linkTo(parent.absoluteRight)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            },
                        onClick = {
                            TimeByZoneClient.getTime(DateTimeZone.getDefault().id)
                                .enqueue(object : Callback<WorldTimeModel> {
                                    override fun onResponse(
                                        call: Call<WorldTimeModel>,
                                        response: Response<WorldTimeModel>
                                    ) {
                                        val time = response.body()!!
                                        val timestamp = DateTime.parse(time.dateTime).millis
                                        val newMessage = Message(
                                            UUID.randomUUID().toString(),
                                            timestamp.toString(),
                                            message,
                                            state.contactUser!!.uid!!,
                                            Firebase.auth.currentUser!!.uid,
                                            MessageType.TEXT,
                                            null,
                                            time.day,
                                            time.dayOfWeek,
                                            time.month,
                                            time.year
                                        )
                                        chatChannelViewModel.sendMessageViewModel(
                                            newMessage,
                                            state.contactUser!!.uid!!
                                        )
                                        message = ""
                                    }

                                    override fun onFailure(
                                        call: Call<WorldTimeModel>,
                                        t: Throwable
                                    ) {

                                    }

                                })


                        },
                        backgroundColor = Orange_500,
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                modifier = modifier
                                    .height((32 * dependingHeight).dp)
                                    .width((32 * dependingHeight).dp),
                                contentDescription = "",
                                tint = Color.White
                            )
                        },
                    )
                }


            }

        }
    ) { paddingValues ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            itemsIndexed(messages) { index, item ->

                DaySeperator(
                    item = item.message,
                    previousMessage = if (index != 0) messages[index - 1].message else Message(year = 0)
                )


                Column(modifier = modifier.fillMaxWidth()) {
                    MessageCard(
                        modifier = modifier.align(if (item.message.senderUID != Firebase.auth.currentUser!!.uid) Alignment.Start else Alignment.End),
                        dependingHeight,
                        message = item.message,
                        formatter
                    )
                }
            }

        }
    }

}

@Composable
fun MessageCard(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    message: Message,
    formatter: SimpleDateFormat
) {

    val hasImage = false

    val date = Date(message.timestamp!!.toLong())
    val formattedDate = formatter.format(date)

    Column(
        modifier = modifier
            .padding(horizontal = (16 * dependingHeight).dp)
            .padding(top = (8 * dependingHeight).dp)
            .width((350 * dependingHeight).dp)
            .background(
                if (message.senderUID != Firebase.auth.currentUser!!.uid)
                    Brush.verticalGradient(
                        listOf(Grey_300, Color.White),
                    ) else Brush.verticalGradient(
                    listOf(
                        Orange_100, Orange_100
                    )
                ),
                shape = RoundedCornerShape((16 * dependingHeight).dp)
            ),
    ) {

        if (hasImage) {
            Image(
                painter = painterResource(id = R.drawable.markzucker),
                contentDescription = "Mark Zuckerberg",
                modifier = modifier
                    .size(50.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = (16 * dependingHeight).dp)
                .padding(top = (8 * dependingHeight).dp),
            text = message.text!!,
            color = Color.Black,
            fontSize = (16 * dependingHeight).sp,
        )

        Text(
            text = formattedDate,
            color = Color.Black,
            fontSize = (11 * dependingHeight).sp,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.End),
        )
    }

}

@Composable
fun DaySeperator(
    modifier: Modifier = Modifier,
    item: Message,
    previousMessage: Message
) {

    val isGreaterThanPreviousMessage = (item.year > previousMessage.year
            || item.year == previousMessage.year && item.month > previousMessage.month
            || item.year == previousMessage.year && item.month == previousMessage.month && item.day > previousMessage.day)



    if (item.year == LocalDateTime.now().year
        && item.month == LocalDateTime.now().monthOfYear
        && item.day - LocalDateTime.now().dayOfMonth == -1
        && isGreaterThanPreviousMessage
    ) {
        Text(
            text = "Yesterday",
            color = Grey_300,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
    } else if (item.year == LocalDateTime.now().year
        && item.month == LocalDateTime.now().monthOfYear
        && item.day == LocalDateTime.now().dayOfMonth
        && isGreaterThanPreviousMessage
    ) {
        Text(
            text = "Today",
            color = Grey_300,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
    } else if (item.year == LocalDateTime.now().year
        && item.month < LocalDateTime.now().monthOfYear
        && isGreaterThanPreviousMessage
        || (item.year == LocalDateTime.now().year
                && item.month == LocalDateTime.now().monthOfYear
                && item.day - LocalDateTime.now().dayOfMonth < -1
                && isGreaterThanPreviousMessage)
    ) {
        Text(
            text = SimpleDateFormat(
                "MMMM dd",
                Locale.getDefault()
            ).format(Date(item.timestamp!!.toLong())),
            color = Grey_300,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    } else if (
        item.year < LocalDateTime.now().year
        && isGreaterThanPreviousMessage
    ) {
        Text(
            text = SimpleDateFormat(
                "yyyy MMMM dd",
                Locale.getDefault()
            ).format(Date(item.timestamp!!.toLong())),
            color = Grey_300,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}