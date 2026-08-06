package org.example.adminfoodapp.presentation.splashscreen

import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.logo
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private val DarkBackground = Color(0xFF060404)
private val PrimaryAccent = Color(0xFFD4832E)

@Composable
fun SplashScreen(navController: NavHostController) {

    val viewModel: SplashViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    // UI Animation States
    val scale = remember { Animatable(0.4f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo Zoom-in aur Fade-in animation
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }

        // Aap ka purana API check call
        viewModel.checkConnectionAndFetchUser()
    }

    // Aap ka purana Navigation Logic based on ViewModel state
    LaunchedEffect(state) {
        when (val result = state) {
            is Resource.Success -> {
                delay(400) // Animated transition feeing ke liye chota sa pause
                val user = result.data
                if (user.admin == true || user.userAccess == true) {
                    navController.navigate(NavRoutes.MainScreen) {
                        popUpTo(NavRoutes.SplashScreen) { inclusive = true }
                    }
                } else {
                    navController.navigate(NavRoutes.LoginScreen) {
                        popUpTo(NavRoutes.SplashScreen) { inclusive = true }
                    }
                }
            }

            is Resource.Error -> {
                delay(400)
                navController.navigate(NavRoutes.LoginScreen) {
                    popUpTo(NavRoutes.SplashScreen) { inclusive = true }
                }
            }

            is Resource.Loading -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Background Glowing Orbs (Login Screen Se Matching)
        Box(
            modifier = Modifier
                .size(380.dp)
                .offset(x = (-100).dp, y = (-70).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x55B43C14), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x40D4832E), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // Center Content with Zoom & Alpha Animation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value,
                alpha = alpha.value
            )
        ) {
            // Glowing Logo Container
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(PrimaryAccent.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .border(1.dp, PrimaryAccent.copy(alpha = 0.35f), CircleShape)
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(95.dp),
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "logo"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Way of Food",
                color = Color(0xFFF5EDE0),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            )

            // Horizontal Accent Line
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(70.dp)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, PrimaryAccent, Color.Transparent)
                        )
                    )
            )

            Text(
                text = "Admin Dashboard",
                color = PrimaryAccent,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 24.sp
                )
            )
        }

        // Bottom Progress Indicator & Credit Text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = PrimaryAccent,
                strokeWidth = 2.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                textAlign = TextAlign.Center,
                color = PrimaryAccent.copy(alpha = 0.7f),
                text = "Design By\nHammad Baloch",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
            )
        }
    }
}