package com.tumbuhsehat.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tumbuhsehat.app.ui.theme.PinkPrimary
import com.tumbuhsehat.app.ui.theme.PinkPrimaryDark
import com.tumbuhsehat.app.ui.theme.PinkSoft
import com.tumbuhsehat.app.ui.theme.PinkSoftStrong
import com.tumbuhsehat.app.ui.theme.ShapeCard
import com.tumbuhsehat.app.ui.theme.ShapeHero
import com.tumbuhsehat.app.ui.theme.ShapePill
import com.tumbuhsehat.app.ui.theme.StatusAlertBg
import com.tumbuhsehat.app.ui.theme.StatusAlertText
import com.tumbuhsehat.app.ui.theme.StatusNormalBg
import com.tumbuhsehat.app.ui.theme.StatusNormalText
import com.tumbuhsehat.app.ui.theme.StatusWarnBg
import com.tumbuhsehat.app.ui.theme.StatusWarnText
import com.tumbuhsehat.app.ui.theme.SurfaceNeutral
import com.tumbuhsehat.app.ui.theme.TextMuted
import com.tumbuhsehat.app.ui.theme.TextPrimary
import com.tumbuhsehat.app.ui.theme.TextSecondary

/** A round, softly-tinted icon badge - the recurring "friendly anchor" visual motif used across the app. */
@Composable
fun IconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    background: androidx.compose.ui.graphics.Color = PinkSoftStrong,
    tint: androidx.compose.ui.graphics.Color = PinkPrimaryDark,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.52f))
    }
}

/** Soft pink "hero" card used for the top-of-screen welcome/greeting moment - no hard border, no shadow. */
@Composable
fun GreetingHeroCard(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PinkSoft),
        shape = ShapeHero,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(eyebrow, style = MaterialTheme.typography.bodyMedium, color = PinkPrimaryDark)
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = PinkPrimaryDark,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            trailing?.invoke()
        }
    }
}

enum class StatusBadgeLevel { NORMAL, WASPADA, PERHATIAN }

@Composable
fun StatusCard(
    title: String,
    badgeLabel: String,
    badgeLevel: StatusBadgeLevel,
    metrics: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Favorite,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon, size = 36.dp)
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 10.dp).weight(1f),
                )
                val (bg, fg) = when (badgeLevel) {
                    StatusBadgeLevel.NORMAL -> StatusNormalBg to StatusNormalText
                    StatusBadgeLevel.WASPADA -> StatusWarnBg to StatusWarnText
                    StatusBadgeLevel.PERHATIAN -> StatusAlertBg to StatusAlertText
                }
                Text(
                    badgeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = fg,
                    modifier = Modifier
                        .background(bg, ShapePill)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                metrics.forEach { (label, value) ->
                    Column {
                        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            value,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PinkPrimary),
        shape = ShapeCard,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            IconBadge(
                Icons.Filled.NotificationsActive,
                size = 40.dp,
                background = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f),
                tint = androidx.compose.ui.graphics.Color.White,
            )
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = androidx.compose.ui.graphics.Color.White)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
fun RowScope.QuickAccessCard(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            Modifier.padding(vertical = 18.dp, horizontal = 14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconBadge(icon)
            Text(
                label,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

enum class BottomNavDestination(val route: String, val label: String, val icon: ImageVector) {
    BERANDA("beranda", "Beranda", Icons.Filled.Home),
    KALENDER("kalender", "Kalender", Icons.Filled.CalendarMonth),
    GIZI("gizi", "Gizi", Icons.Filled.Favorite),
    PROFIL("profil", "Profil", Icons.Filled.Person),
}

@Composable
fun PageHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    bottomPadding: androidx.compose.ui.unit.Dp = 20.dp,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconBadge(icon, size = 72.dp)
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, message: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconBadge(icon, size = 52.dp, background = PinkSoft, tint = TextMuted)
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

/** Numbered circle + title - the recurring "step N" marker used to visually unify a multi-step flow. */
@Composable
fun StepHeader(number: Int, title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(28.dp)
                .background(PinkPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$number",
                style = MaterialTheme.typography.labelLarge,
                color = androidx.compose.ui.graphics.Color.White,
            )
        }
        Column(Modifier.padding(start = 10.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

/** A step's card body - same white/bordered card for every step, so the flow reads as one consistent sequence. */
@Composable
fun StepCard(
    number: Int,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            StepHeader(number, title, subtitle)
            content?.let {
                Column(Modifier.padding(top = 14.dp)) { it() }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Row(modifier.padding(top = 18.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(width = 4.dp, height = 16.dp)
                .background(PinkPrimary, androidx.compose.foundation.shape.RoundedCornerShape(2.dp)),
        )
        Text(
            text,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
