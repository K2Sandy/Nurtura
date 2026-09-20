package com.tumbuhsehat.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tumbuhsehat.app.TumbuhSehatApplication
import com.tumbuhsehat.app.ui.beranda.BerandaScreen
import com.tumbuhsehat.app.ui.components.BottomNavDestination
import com.tumbuhsehat.app.ui.gizi.GiziScreen
import com.tumbuhsehat.app.ui.kalender.KalenderScreen
import com.tumbuhsehat.app.ui.onboarding.OnboardingScreen
import com.tumbuhsehat.app.ui.profil.ProfilScreen
import com.tumbuhsehat.app.ui.theme.PinkPrimaryDark
import com.tumbuhsehat.app.ui.theme.PinkSoftStrong
import com.tumbuhsehat.app.ui.theme.SurfaceWhite
import com.tumbuhsehat.app.ui.theme.TextMuted

@Composable
fun TumbuhSehatApp() {
    val app = LocalContext.current.applicationContext as TumbuhSehatApplication
    // Reactive: as soon as onboarding inserts the first profile, this list flips
    // to non-empty and Compose automatically recomposes into MainScaffold.
    val profiles by app.container.userRepository.observeAllProfiles().collectAsState(initial = null)

    when {
        profiles == null -> Unit // brief initial DB read, nothing to show yet
        profiles!!.isEmpty() -> androidx.compose.foundation.layout.Box(Modifier.systemBarsPadding()) {
            OnboardingScreen(onSelesai = {})
        }
        else -> MainScaffold()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavDestination.BERANDA.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomNavDestination.BERANDA.route) {
                BerandaScreen(
                    onNavigateToGizi = { navController.navigateToTab(BottomNavDestination.GIZI.route) },
                    onNavigateToKalender = { navController.navigateToTab(BottomNavDestination.KALENDER.route) },
                    onNavigateToProfil = { navController.navigateToTab(BottomNavDestination.PROFIL.route) },
                )
            }
            composable(BottomNavDestination.KALENDER.route) { KalenderScreen() }
            composable(BottomNavDestination.GIZI.route) { GiziScreen() }
            composable(BottomNavDestination.PROFIL.route) {
                ProfilScreen(onTambahProfil = { navController.navigate("tambah_profil") })
            }
            composable("tambah_profil") {
                OnboardingScreen(onSelesai = { navController.popBackStack() })
            }
        }
    }
}

private fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Persistent masthead shown on every tab - same pattern as the reference app's app-wide top bar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar() {
    Surface(
        modifier = Modifier,
        color = SurfaceWhite,
        shadowElevation = 4.dp,
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "TumbuhSehat",
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimaryDark,
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        )
    }
}

@Composable
private fun AppBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Surface(
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        color = SurfaceWhite,
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
        ) {
            BottomNavDestination.entries.forEach { dest ->
                NavigationBarItem(
                    selected = currentRoute == dest.route,
                    onClick = { navController.navigateToTab(dest.route) },
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PinkPrimaryDark,
                        selectedTextColor = PinkPrimaryDark,
                        indicatorColor = PinkSoftStrong,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                    ),
                )
            }
        }
    }
}
