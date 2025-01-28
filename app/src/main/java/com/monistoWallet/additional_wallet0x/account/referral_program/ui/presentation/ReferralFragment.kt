package com.monistoWallet.additional_wallet0x.account.referral_program.ui.presentation

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.core.BaseComposeFragment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.CellLawrenceSection
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.VSpacer
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ClassDescriptorImpl

class ReferralFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        ReferralScreen(onBackPressed = {
            navController.popBackStack()
        })
    }

}
@SuppressLint("UnrememberedMutableInteractionSource")
@Preview("")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ReferralScreen(onBackPressed: () -> Unit = {}) {
    var showMainScreen by remember { mutableStateOf(true) }
    var showRegisterScreen by remember { mutableStateOf(false) }
    var showRulesScreen by remember { mutableStateOf(false) }
    var showTierInfoScreen by remember { mutableStateOf(false) }
    var showReferralRewardsScreen by remember { mutableStateOf(false) }
    var showReferralFriendsScreen by remember { mutableStateOf(false) }

    val friendsInvitedCount = 3
    val currentLevel = 1

    Image(
        painter = painterResource(R.drawable.app_bg),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {},
        contentScale = ContentScale.Crop
    )
    if (showMainScreen) {
        Column {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row {
                    ButtonBack {
                        onBackPressed.invoke()
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(id = R.string.Invite_Friends),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 22.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.ic_list),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                showReferralFriendsScreen = true
                                showMainScreen = false
                            }
                    )
                }

                Text(
                    text = stringResource(R.string.Refer_Friends),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFF0C99CF),
                            offset = Offset(0f, 0f),
                            blurRadius = 40f
                        )
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = stringResource(R.string.To_Earn_Up_40_Commission),
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                VSpacer(height = 22.dp)
                Text(
                    text = stringResource(R.string.Referral_Rules),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        showMainScreen = false
                        showRulesScreen = true
                    }
                )
                VSpacer(height = 21.dp)

                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFFD9D9D9).copy(0.2f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = Color(0xFFD9D9D9).copy(0.05f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(bottom = 10.dp, top = 2.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_first_friend_bonus),
                        contentDescription = null,
                        modifier = Modifier
                            .height(71.dp)
                            .width(64.dp)
                            .align(Alignment.CenterVertically)
                            .padding(end = 16.dp),
//                        colorFilter = ColorFilter.tint(Color(0xFF0C99CF))
                    )
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ){
                        Text(
                            text = stringResource(R.string.Invite_your_first_friend),
                            color = Color.Gray,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = stringResource(R.string.Both_of_you_will_receive_10_each_after_activating_the_card),
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                    }
                }
                VSpacer(height = 22.dp)
                InviteScreenWithViewPager(currentLevel, friendsInvitedCount, {
                    showTierInfoScreen = true
                }, {
                    showMainScreen = false
                    showRegisterScreen = true
                })
                VSpacer(height = 43.dp)
                Text(
                    text = stringResource(R.string.Withdrawable_USDT),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        showMainScreen = false
                        showRulesScreen = true
                    }
                )
                Row {
                    Text(
                        text = "00.00",
                        color = Color.White,
                        fontSize = 25.sp,
                        modifier = Modifier.clickable {
                            showMainScreen = false
                            showRulesScreen = true
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    ReferralTextStyle(
                        text = "   " + stringResource(id = R.string.Claim) + "   ",
                        onClick = {

                        }
                    )
                }
            }

            Divider(
                Modifier
                    .height(1.dp)
                    .background(Color(0x4D6E7899)))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 10.dp, bottom = 10.dp, end = 32.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.Invited_Friends),
                        color = Color.Gray,
                        fontSize = 10.sp,
                    )
                    Text(
                        text = "0",
                        color = Color.White,
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            showReferralRewardsScreen = true
                            showMainScreen = false
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = stringResource(R.string.Processing_USDT),
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "00.00",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                showMainScreen = false
                                showReferralFriendsScreen = true
                            }
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = stringResource(R.string.Settled_USDT),
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Text(
                        text = "00.00",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                showMainScreen = false
                                showReferralFriendsScreen = true
                            }
                            .align(Alignment.End)
                    )
                }

            }
            Divider(
                Modifier
                    .height(1.dp)
                    .background(Color(0x4D6E7899)))
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 40.dp)) {
                ButtonPrimaryYellow(
                    title = stringResource(id = R.string.Invite_Friends),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }
                )
            }
        }
    }
    if (showRegisterScreen) {
        ReferralRegisterScreen {
            showMainScreen = true
            showRegisterScreen = false
        }
    }
    if (showRulesScreen) {
        ReferralRulesScreen {
            showMainScreen = true
            showRulesScreen = false
        }
    }
    if (showTierInfoScreen) {
        ReferralTierDialog {
            showTierInfoScreen = false
        }
    }
    if (showReferralRewardsScreen) {
        ReferralRewardsScreen {
            showReferralRewardsScreen = false
            showMainScreen = true
        }
    }
    if (showReferralFriendsScreen) {
        ReferralFriendsScreen {
            showMainScreen = true
            showReferralFriendsScreen = false
        }
    }
}
@OptIn(ExperimentalPagerApi::class, ExperimentalPagerApi::class)
@Composable
fun InviteScreenWithViewPager(currentLevel: Int, friendsInvited: Int, onTierClick: () -> Unit, onJoinInfluenceProgramClick: () -> Unit) {
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(130.dp)
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            // ViewPager
            HorizontalPager(
                count = 4, // Количество страниц
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Высота ViewPager
            ) { page ->
                // Здесь отображается содержимое каждой страницы
                when (page) {
                    0 -> LevelViewPagerView(1, currentLevel == 1, friendsInvited, 5, onTierClick)
                    1 -> LevelViewPagerView(2, currentLevel == 2, friendsInvited, 15, onTierClick)
                    2 -> LevelViewPagerView(3, currentLevel == 3, friendsInvited, 30, onTierClick)
                    3 -> LevelViewPagerView(4, currentLevel == 4, friendsInvited, 0, onTierClick, onJoinInfluenceProgramClick)
                    else -> Color.Gray
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color.White,
            inactiveColor = Color.Gray,
            indicatorWidth = 8.dp,
            indicatorHeight = 8.dp,
            spacing = 8.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
//    LaunchedEffect(pagerState) {
//        while (true) {
//            delay(3000L)
//            pagerState.animateScrollToPage(
//                page = (pagerState.currentPage + 1) % 4
//            )
//        }
//    }
}

@Composable
fun LevelViewPagerView(
    lvl: Int,
    isCurrentLevel: Boolean,
    friendsInvited: Int,
    lvlNeedFriends: Int,
    onTierClick: () -> Unit,
    onJoinInfluenceProgramClick: (() -> Unit)? = null
) {
    Column(

        modifier = if (onJoinInfluenceProgramClick != null) Modifier
            .fillMaxSize()
            .height(130.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF504E90).copy(0.8f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = Color(0xFF504E90).copy(0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
        else Modifier
            .fillMaxSize()
            .height(130.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9).copy(0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = Color(0xFFD9D9D9).copy(0.05f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        if (onJoinInfluenceProgramClick != null) {
            Row {
                ReferralTextStyle(stringResource(id = R.string.For_Influencers))
                Spacer(modifier = Modifier.weight(1f))
                ReferralTextStyle(stringResource(id = R.string.Join_Now)) {
                    onJoinInfluenceProgramClick.invoke()
                }
            }
            VSpacer(height = 30.dp)
        } else {
            Row {
                ReferralTextStyle("LV$lvl")
                Spacer(modifier = Modifier.weight(1f))
                if (isCurrentLevel) {
                    ReferralTextStyle(stringResource(R.string.Current_Level))
                }
            }
            VSpacer(height = 15.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(), // Занимает всю ширину
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.Invited_Friends),
                    color = colorResource(id = R.color.text_gray_color),
                    fontSize = 10.sp,
                )

                // Считаем количество ячеек
                val images = listOf(
                    R.drawable.referral_inactive_divider, // По умолчанию неактивные
                    R.drawable.referral_inactive_divider,
                    R.drawable.referral_inactive_divider,
                    R.drawable.referral_inactive_divider,
                    R.drawable.referral_inactive_divider
                ).toMutableList()

                if (isCurrentLevel) {
                    for (i in 0 until friendsInvited) {
                        if (i == friendsInvited - 1) {
                            images[i] = R.drawable.referral_last_divider // Последний активный
                        } else {
                            images[i] = R.drawable.referral_active_divider // Активные
                        }
                    }

                }

                HSpacer(width = 12.dp)
                // Рисуем ячейки
                images.forEach { drawable ->
                    Image(
                        painter = painterResource(id = drawable),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                    )
                }
                HSpacer(width = 12.dp)
                // Текстовое отображение прогресса
                Text(
                    text = friendsInvited.toString(),
                    color = if (!isCurrentLevel) colorResource(id = R.color.text_gray_color) else colorResource(id = R.color.white),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 8.dp) // Отступ слева
                )
                Text(
                    text = "/$lvlNeedFriends",
                    color = colorResource(id = R.color.text_gray_color),
                    fontSize = 10.sp,
                )
            }

        }
        VSpacer(height = 12.dp)
        Row {
            Column(
                Modifier.width(120.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.Card_Activation),
                    color = colorResource(id = R.color.text_gray_color),
                    fontSize = 10.sp
                )
                Text(
                    text = "20%",
                    color = colorResource(id = R.color.white),
                    fontSize = 16.sp
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.Transaction),
                    color = colorResource(id = R.color.text_gray_color),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "0.05%",
                    color = colorResource(id = R.color.white),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Column(
                Modifier.width(120.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(id = R.string.Tier2),
                        color = colorResource(id = R.color.text_gray_color),
                        fontSize = 10.sp,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_info_20),
                        contentDescription = "",
                        modifier = Modifier
                            .clickable {
                                onTierClick.invoke()
                            }
                            .align(Alignment.CenterVertically)
                            .size(12.dp)
                    )
                }
                Text(
                    text = "10%",
                    color = colorResource(id = R.color.white),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun ReferralTextStyle(text: String, onClick: (() -> Unit)? = null) {
    val modifier = if (onClick != null) {
        Modifier
            .clickable { onClick.invoke() }
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.white).copy(0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorResource(id = R.color.main_app_blue).copy(0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .shadow(
                elevation = 10.dp,
                spotColor = Color.Black,
                shape = RoundedCornerShape(10.dp),
                clip = false // Устанавливаем clip в false, чтобы тень не обрезалась
            )
    } else {
        Modifier
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.white).copy(0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorResource(id = R.color.main_app_blue).copy(0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    }

    Box(modifier = modifier) {
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            fontSize = 12.sp,
        )
    }
}

