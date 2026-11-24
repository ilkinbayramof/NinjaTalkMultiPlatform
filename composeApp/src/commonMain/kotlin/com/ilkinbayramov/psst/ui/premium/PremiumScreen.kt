package com.ilkinbayramov.psst.ui.premium

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.psst.billing.createBillingClient
import com.ilkinbayramov.psst.data.repository.BoostRepository
import com.ilkinbayramov.psst.localization.LocalAppStrings
import com.ilkinbayramov.psst.presentation.premium.PremiumViewModel
import com.ilkinbayramov.psst.ui.theme.*

/** A single purchasable boost package. Prices are placeholders until billing is wired up. */
data class BoostPlan(
        val id: String,
        val minutes: Int,
        val price: String,
        val discountPercent: Int? = null,
        val badge: BoostBadge? = null,
        val rockets: Int = 1
)

enum class BoostBadge {
    MOST_POPULAR,
    BEST_VALUE
}

private val boostPlans =
        listOf(
                BoostPlan(id = "boost_5", minutes = 5, price = "US$7,99", rockets = 1),
                BoostPlan(
                        id = "boost_15",
                        minutes = 15,
                        price = "US$17,99",
                        discountPercent = 25,
                        badge = BoostBadge.MOST_POPULAR,
                        rockets = 2
                ),
                BoostPlan(
                        id = "boost_30",
                        minutes = 30,
                        price = "US$29,99",
                        discountPercent = 38,
                        badge = BoostBadge.BEST_VALUE,
                        rockets = 3
                )
        )

@Composable
fun PremiumScreen() {
    val strings = LocalAppStrings.current
    val viewModel = remember { PremiumViewModel(BoostRepository(), createBillingClient()) }
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Re-check with the server whenever the tab is opened
    LaunchedEffect(Unit) { viewModel.loadStatus() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
            containerColor = NinjaBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = NinjaBoostStart) }
            return@Scaffold
        }

        val boostActive = state.boost.isActive

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(padding)
                                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoostHero()

            Spacer(Modifier.height(24.dp))

            Text(
                    text = if (boostActive) strings.boostActiveTitle else strings.boostTitle,
                    color = NinjaTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                    text =
                            if (boostActive) strings.boostActiveSubtitle
                            else strings.boostSubtitle,
                    color = NinjaTextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            if (boostActive) {
                // While a boost runs the packages are hidden; only the countdown is shown
                CountdownPanel(remainingSeconds = state.remainingSeconds)
            } else {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    boostPlans.forEach { plan ->
                        PlanCard(
                                plan = plan,
                                isSelected = plan.id == state.selectedProductId,
                                onClick = { viewModel.selectProduct(plan.id) },
                                modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                BoostButton(
                        text = strings.boostCta,
                        isBusy = state.isPurchasing,
                        onClick = { viewModel.purchaseSelected() }
                )

                Spacer(Modifier.height(18.dp))

                Text(
                        text = strings.boostRestore,
                        color = NinjaTextSecondary,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier =
                                Modifier.clip(RoundedCornerShape(8.dp))
                                        // TODO: hook up restore purchases with RevenueCat
                                        .clickable { viewModel.loadStatus() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/** Big mm:ss countdown shown in place of the packages while a boost is running. */
@Composable
private fun CountdownPanel(remainingSeconds: Long) {
    val strings = LocalAppStrings.current
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formatted = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(NinjaCardSurface)
                            .border(1.dp, NinjaBoostStart.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                            .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
                text = strings.boostRemaining,
                color = NinjaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(10.dp))

        Text(
                text = formatted,
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NinjaBoostStart
        )
    }
}

/** Rocket + avatar illustration with a soft glow, drawn from shapes so it needs no assets. */
@Composable
private fun BoostHero() {
    val transition = rememberInfiniteTransition(label = "boostHero")
    val lift by
            transition.animateFloat(
                    initialValue = -6f,
                    targetValue = 6f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(2200, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "lift"
            )
    val glowPulse by
            transition.animateFloat(
                    initialValue = 0.35f,
                    targetValue = 0.6f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(1800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "glow"
            )

    Box(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            contentAlignment = Alignment.Center
    ) {
        // Ambient glow behind everything
        Box(
                modifier =
                        Modifier.size(300.dp)
                                .alpha(glowPulse)
                                .background(
                                        Brush.radialGradient(
                                                listOf(
                                                        NinjaBoostEnd.copy(alpha = 0.55f),
                                                        NinjaBoostStart.copy(alpha = 0.18f),
                                                        Color.Transparent
                                                )
                                        ),
                                        CircleShape
                                )
        )

        Sparkle(size = 18.dp, color = NinjaGold, modifier = Modifier.offset(x = (-120).dp, y = (-78).dp))
        Sparkle(size = 12.dp, color = NinjaBoostStart, modifier = Modifier.offset(x = 122.dp, y = (-58).dp))
        Sparkle(size = 10.dp, color = NinjaGold, modifier = Modifier.offset(x = (-108).dp, y = 62.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(y = lift.dp)) {
            // Rocket disc
            Box(
                    modifier =
                            Modifier.size(168.dp)
                                    .background(
                                            Brush.linearGradient(
                                                    listOf(NinjaBoostStart, NinjaBoostEnd)
                                            ),
                                            CircleShape
                                    ),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(84.dp)
                )
            }

            // Avatar bubble overlapping the bottom-right, like the reference
            Box(
                    modifier =
                            Modifier.align(Alignment.BottomEnd)
                                    .offset(x = 46.dp, y = 26.dp)
                                    .size(96.dp)
                                    .background(NinjaBackground, CircleShape)
                                    .padding(5.dp)
                                    .background(NinjaCardSurface, CircleShape)
                                    .border(2.dp, NinjaBoostStart.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NinjaTextSecondary,
                        modifier = Modifier.size(54.dp)
                )
            }
        }
    }
}

@Composable
private fun Sparkle(size: androidx.compose.ui.unit.Dp, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxWidth().height(size / 4).background(color, CircleShape))
        Box(
                modifier =
                        Modifier.fillMaxHeight()
                                .width(size / 4)
                                .background(color, CircleShape)
                                .rotate(0f)
        )
    }
}

@Composable
private fun PlanCard(
        plan: BoostPlan,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val borderColor = if (isSelected) NinjaBoostStart else NinjaCardBorder

    // Reserve room above the card so the badge can straddle its top edge
    Box(modifier = modifier.padding(top = 11.dp)) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(NinjaCardSurface)
                                .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(18.dp)
                                )
                                .clickable(onClick = onClick)
                                .padding(top = 30.dp, bottom = 16.dp, start = 6.dp, end = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RocketCoins(count = plan.rockets, highlighted = isSelected)

            Spacer(Modifier.height(12.dp))

            Text(
                    text = plan.minutes.toString(),
                    color = NinjaTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
            )
            Text(
                    text = strings.boostMinutes,
                    color = NinjaTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(text = plan.price, color = NinjaTextSecondary, fontSize = 13.sp)

            Spacer(Modifier.height(10.dp))

            if (plan.discountPercent != null) {
                Text(
                        text = "-%${plan.discountPercent}",
                        color = Color(0xFF2B0F06),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier =
                                Modifier.clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFF7A5C).copy(alpha = 0.9f))
                                        .padding(horizontal = 9.dp, vertical = 3.dp)
                )
            } else {
                // Keeps all three cards the same height
                Spacer(Modifier.height(20.dp))
            }
        }

        plan.badge?.let { badge ->
            val (label, colors) =
                    when (badge) {
                        BoostBadge.MOST_POPULAR ->
                                strings.boostMostPopular to
                                        listOf(NinjaBoostStart, NinjaBoostEnd)
                        BoostBadge.BEST_VALUE ->
                                strings.boostBestValue to
                                        listOf(NinjaGold, Color(0xFFFF8A00))
                    }
            Text(
                    text = label,
                    color = if (badge == BoostBadge.BEST_VALUE) Color(0xFF241A00) else Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    modifier =
                            Modifier.align(Alignment.TopCenter)
                                    .offset(y = (-11).dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Brush.linearGradient(colors))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

/** One to three overlapping rocket coins, mirroring the tiered look of the reference. */
@Composable
private fun RocketCoins(count: Int, highlighted: Boolean) {
    val coin = 34.dp
    val overlap = 14.dp

    Box(
            modifier = Modifier.height(coin).width(coin + overlap * (count - 1)),
            contentAlignment = Alignment.CenterStart
    ) {
        // Draw back-to-front so the leading coin sits on top
        for (index in (count - 1) downTo 0) {
            val isFront = index == 0
            Box(
                    modifier =
                            Modifier.offset(x = overlap * index)
                                    .size(coin)
                                    .background(
                                            Brush.linearGradient(
                                                    listOf(
                                                            NinjaGold,
                                                            Color(0xFFFF9F1C)
                                                    )
                                            ),
                                            CircleShape
                                    )
                                    .alpha(if (isFront) 1f else 0.55f),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = if (highlighted) Color(0xFF3A2400) else Color(0xFF4A3000),
                        modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun BoostButton(text: String, isBusy: Boolean, onClick: () -> Unit) {
    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(58.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Brush.linearGradient(listOf(NinjaBoostStart, NinjaBoostEnd)))
                            .clickable(enabled = !isBusy, onClick = onClick),
            contentAlignment = Alignment.Center
    ) {
        if (isBusy) {
            CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                    text = text,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}
