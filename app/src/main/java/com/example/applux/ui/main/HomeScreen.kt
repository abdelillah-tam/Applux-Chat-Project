package com.example.applux.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.applux.R
import com.example.applux.ui.chat.ChatViewModel
import com.example.applux.ui.theme.Green_600
import com.example.applux.ui.theme.Orange_500
import com.example.applux.ui.theme.Orange_900
import com.google.accompanist.systemuicontroller.SystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    systemUiController: SystemUiController,
    onChatItemClick: (String) -> Unit,
    findContacts: () -> Unit,
    onSettingsPressed: () -> Unit
) {

    systemUiController.setSystemBarsColor(Color.White)

    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()

    val tabs = listOf(
        "Chats"
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                findContacts()
            }
        }


    val scaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.systemBarsPadding(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Applux")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            if (scaffoldState.drawerState.isClosed) {
                                scaffoldState.drawerState.open()
                            }else{
                                scaffoldState.drawerState.close()
                            }
                        }
                    }) {
                        Icon(painterResource(id = R.drawable.menu_drawer), "", tint = Orange_500)
                    }
                },
                actions = {
                    var dissmiss by remember {
                        mutableStateOf(false)
                    }

                    IconButton(onClick = {
                        dissmiss = !dissmiss
                    }) {
                        Icon(Icons.Outlined.MoreVert, "", tint = Orange_500)
                    }

                    DropdownMenu(expanded = dissmiss, onDismissRequest = {
                        dissmiss = false
                    }) {
                        DropdownMenuItem(onClick = {}) {
                            Text("Log out")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = modifier
                    .height((64 * dependingHeight).dp)
                    .width((64 * dependingHeight).dp),
                onClick = {
                    if (ContextCompat
                            .checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        launcher.launch(Manifest.permission.READ_CONTACTS)
                    } else {
                        findContacts()
                    }

                },
                backgroundColor = Orange_500,
                content = {
                    Icon(
                        modifier = modifier
                            .height((32 * dependingHeight).dp)
                            .width((32 * dependingHeight).dp),
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = "",
                        tint = Color.White
                    )
                },
            )
        },
        drawerContent = {
            NavigationDrawer(dependingHeight = dependingHeight,
                onSettingsPressed = onSettingsPressed)
        },
        drawerElevation = 0.0.dp
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = (2 * dependingHeight).dp,
                        color = Orange_500
                    )
                }
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        text = { Text(item) },
                        onClick = {
                            coroutine.launch { pagerState.animateScrollToPage(index) }
                        },
                    )

                }
            }
            HorizontalPager(
                state = pagerState
            ) {
                when (it) {
                    0 -> HomeScreen(
                        modifier,
                        dependingHeight = dependingHeight,
                        onChatItemClick = onChatItemClick
                    )
                }
            }
        }

    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = hiltViewModel(),
    dependingHeight: Double,
    onChatItemClick: (String) -> Unit
) {

    val chats = chatViewModel.state

    Scaffold { paddingValues ->
        LazyColumn {

            itemsIndexed(chats.values.toList()) { _, item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = true,
                            onClick = {
                                onChatItemClick(item.contactUser!!.uid!!)
                            })
                ) {
                    ChatItem(
                        modifier
                            .padding(paddingValues)
                            .padding(
                                horizontal = (8 * dependingHeight).dp,
                                vertical = (4 * dependingHeight).dp
                            ),
                        imageUrl = item.picture?.pic,
                        nameText = item.contactUser?.name,
                        lastMessageText = item.message?.text,
                        dependingHeight = dependingHeight
                    )
                }

            }

        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    nameText: String?,
    lastMessageText: String?,
    dependingHeight: Double
) {
    ConstraintLayout(modifier = modifier) {
        val (image, online, name, lastMessage) = createRefs()

        GlideImage(
            modifier = modifier
                .clip(CircleShape)
                .height((80 * dependingHeight).dp)
                .width((80 * dependingHeight).dp)
                .constrainAs(image) {},
            model = imageUrl,
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        CustomCheckBox(
            modifier = modifier.constrainAs(online) {
                absoluteRight.linkTo(image.absoluteRight, margin = (4 * dependingHeight).dp)
                bottom.linkTo(image.bottom)
            },
            checked = true,
            height = (18 * dependingHeight).dp, width = (18 * dependingHeight).dp
        )
        Text(modifier = modifier.constrainAs(name) {
            absoluteLeft.linkTo(image.absoluteRight)
        }, text = nameText ?: "")
        Text(modifier = modifier.constrainAs(lastMessage) {
            absoluteLeft.linkTo(image.absoluteRight)
            top.linkTo(name.bottom)
        }, text = lastMessageText ?: "")
    }
}

@Composable
fun CustomCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    height: Dp,
    width: Dp
) {

    Canvas(
        modifier = modifier
            .height(height)
            .width(width)
    ) {
        drawCircle(if (checked) Green_600 else Orange_900)
    }
}
