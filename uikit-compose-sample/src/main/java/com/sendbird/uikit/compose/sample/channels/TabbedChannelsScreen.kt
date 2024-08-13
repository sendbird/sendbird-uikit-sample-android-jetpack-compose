package com.sendbird.uikit.compose.sample.channels

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sendbird.android.SendbirdChat
import com.sendbird.android.handler.UserEventHandler
import com.sendbird.android.params.GroupChannelListQueryParams
import com.sendbird.android.user.UnreadMessageCount
import com.sendbird.android.user.User
import com.sendbird.uikit.compose.SendbirdScreenUiState
import com.sendbird.uikit.compose.channels.group.list.ChannelsScreen
import com.sendbird.uikit.compose.channels.group.list.ChannelsTopBar
import com.sendbird.uikit.compose.channels.group.list.ChannelsViewModel
import com.sendbird.uikit.compose.channels.group.list.ChannelsViewModelParams
import com.sendbird.uikit.compose.channels.group.settings.SettingsMenu
import com.sendbird.uikit.compose.component.TopBarTitleText
import com.sendbird.uikit.compose.sample.R
import com.sendbird.uikit.compose.theme.SendbirdOpacity
import com.sendbird.uikit.compose.theme.SendbirdTheme
import kotlinx.coroutines.launch

/**
 * A composable function to display the new channels screen.
 * This screen contains the list of channels and settings.
 * @param navController NavController
 * @param modifier Modifier
 * @param onLogoutClick A lambda function to handle the logout action.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabbedChannelsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {},
    channelsViewModel: ChannelsViewModel = viewModel(factory = ChannelsViewModel.factory(
        ChannelsViewModelParams(GroupChannelListQueryParams())
    ))
) {
    val tabUiData = listOf(
        TabUiData(
            selectedIcon = R.drawable.icon_chat_filled,
            unselectedIcon = R.drawable.icon_chat_filled,
            text = R.string.text_tab_channels
        ),
        TabUiData(
            selectedIcon = R.drawable.icon_settings_filled,
            unselectedIcon = R.drawable.icon_settings_filled,
            text = R.string.text_tab_settings
        )
    )
    val pagerState = rememberPagerState(pageCount = { tabUiData.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    val uiState by channelsViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            ChannelsAndSettingsTabRow(
                uiState = uiState,
                tabUiData = tabUiData,
                pagerState = pagerState,
                selectedTabIndex = selectedTabIndex,
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        ChannelsScreen(
                            viewModel = channelsViewModel,
                            navController = navController,
                            topBar = { _, onActionClick ->
                                ChannelsTopBar(
                                    navigationIcon = {},
                                    onActionClick = onActionClick
                                )
                            }
                        )
                    }
                }
                else -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        SettingsScreen(
                            onLogoutClick = onLogoutClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogoutClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                TopBarTitleText(
                    stringResource(id = R.string.text_settings_header_title),
                    modifier = Modifier.padding(12.dp)
                )
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SendbirdOpacity.ExtraLowOpacity)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = SendbirdChat.currentUser?.profileUrl ?: "",
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = SendbirdChat.currentUser?.nickname ?: "",
                    modifier = Modifier.padding(32.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SendbirdOpacity.ExtraLowOpacity)
            )

            Column(
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.text_label_user_id),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = SendbirdChat.currentUser?.userId ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SendbirdOpacity.ExtraLowOpacity)
            )

            SettingsMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onLogoutClick()
                    }
                    .padding(16.dp),
                icon = painterResource(id = R.drawable.icon_leave),
                iconTint = MaterialTheme.colorScheme.error,
                text = stringResource(id = R.string.text_settings_logout_title)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SendbirdOpacity.ExtraLowOpacity)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SendbirdTheme {
        SettingsScreen()
    }
}

data class TabUiData(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val text: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelsAndSettingsTabRow(
    uiState: SendbirdScreenUiState,
    tabUiData: List<TabUiData>,
    pagerState: PagerState,
    selectedTabIndex: Int = 0,
) {
    var unreadCount by remember { mutableIntStateOf(0) }
    val viewScope = rememberCoroutineScope()

    when (uiState) {
        SendbirdScreenUiState.Success -> {
            // Connect to Sendbird and get the unread message count
            TotalUnreadCountEvent { unreadCount = it }
        }
        else -> {}
    }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
    ) {
        tabUiData.forEachIndexed { index, currentTab ->
            Tab(
                selected = selectedTabIndex == index,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.outline,
                onClick = {
                    viewScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = currentTab.text),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0 && index == 0) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadCount > 99) {
                                            stringResource(id = R.string.text_tab_badge_max_count)
                                        } else {
                                            unreadCount.toString()
                                        },
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = if (selectedTabIndex == index) {
                                painterResource(id = currentTab.selectedIcon)
                            } else {
                                painterResource(id = currentTab.unselectedIcon)
                            },
                            contentDescription = "Tab Icon"
                        )
                    }
                }
            )
        }
    }
}

private val UserEventHandlerKey = "UserEventHandlerKey" + System.currentTimeMillis()
private val ConnectionHandlerKey = "ConnectionHandlerKey" + System.currentTimeMillis()

@Composable
fun TotalUnreadCountEvent(
    onUpdateUnreadCount: (Int) -> Unit
) = DisposableEffect(key1 = LocalLifecycleOwner.current) {
    val result = SendbirdChat.getUnreadMessageCount()
    onUpdateUnreadCount(result.groupChannelCount)
    SendbirdChat.addUserEventHandler(UserEventHandlerKey, object : UserEventHandler() {
        override fun onFriendsDiscovered(users: List<User>) {}
        override fun onTotalUnreadMessageCountChanged(unreadMessageCount: UnreadMessageCount) {
            onUpdateUnreadCount(unreadMessageCount.groupChannelCount)
        }
    })

    onDispose {
        SendbirdChat.removeUserEventHandler(UserEventHandlerKey)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun ChannelsAndSettingsTabRowPreview() {
    val tabUiData = listOf(
        TabUiData(
            selectedIcon = R.drawable.icon_chat_filled,
            unselectedIcon = R.drawable.icon_chat_filled,
            text = R.string.text_tab_channels
        ),
        TabUiData(
            selectedIcon = R.drawable.icon_settings_filled,
            unselectedIcon = R.drawable.icon_settings_filled,
            text = R.string.text_tab_settings
        )
    )
    val pagerState = rememberPagerState(pageCount = { tabUiData.size })
    SendbirdTheme {
        ChannelsAndSettingsTabRow(
            SendbirdScreenUiState.Success, tabUiData, pagerState
        )
    }
}
