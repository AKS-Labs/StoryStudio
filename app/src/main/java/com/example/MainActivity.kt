package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.CameraScreen
import com.example.ui.HubScreen
import com.example.ui.OnboardingScreen
import com.example.ui.StoryCanvasScreen
import com.example.ui.theme.AndroidifyPaleMint
import com.example.ui.theme.StoryCanvasTheme

enum class AppScreen {
    ONBOARDING,
    HUB,
    CAMERA,
    CANVAS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoryCanvasTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AndroidifyPaleMint
                ) {
                    StoryCanvasApp()
                }
            }
        }
    }
}

@Composable
fun StoryCanvasApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.ONBOARDING) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width } + fadeOut()
                )
            } else {
                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                )
            }
        },
        label = "androidify_navigation"
    ) { screen ->
        when (screen) {
            AppScreen.ONBOARDING -> {
                OnboardingScreen(
                    onLetsGo = {
                        currentScreen = AppScreen.HUB
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppScreen.HUB -> {
                HubScreen(
                    onLaunchCamera = {
                        currentScreen = AppScreen.CAMERA
                    },
                    onPhotoSelected = { uri ->
                        selectedPhotoUri = uri
                        currentScreen = AppScreen.CANVAS
                    },
                    onStartBlankCanvas = {
                        selectedPhotoUri = null
                        currentScreen = AppScreen.CANVAS
                    },
                    onBackToOnboarding = {
                        currentScreen = AppScreen.ONBOARDING
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppScreen.CAMERA -> {
                CameraScreen(
                    onPhotoCaptured = { uri ->
                        selectedPhotoUri = uri
                        currentScreen = AppScreen.CANVAS
                    },
                    onBack = {
                        currentScreen = AppScreen.HUB
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppScreen.CANVAS -> {
                StoryCanvasScreen(
                    photoUri = selectedPhotoUri,
                    onDiscard = {
                        selectedPhotoUri = null
                        currentScreen = AppScreen.HUB
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
