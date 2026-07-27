package org.example.adminfoodapp.presentation.splashscreen

import TextColor
import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.logo
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(navController: NavHostController) {

    val viewModel: SplashViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkConnectionAndFetchUser()
    }

    LaunchedEffect(state) {
        when (val result = state) {
            is Resource.Success -> {
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
                navController.navigate(NavRoutes.LoginScreen) {
                    popUpTo(NavRoutes.SplashScreen) { inclusive = true }
                }
            }

            is Resource.Loading -> {}
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = maxWidth
        val isDesktop = maxWidth > 600.dp

        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isDesktop) 20.dp else 30.dp))
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(Res.drawable.logo),
                contentDescription = "logo"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Way of Food", style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 30.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                color = TextColor, text = "Admin Dashboard", style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 30.sp
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                textAlign = TextAlign.Center,
                color = TextColor,
                text = "Design By\nHammad Baloch",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 20.sp, lineHeight = 30.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}