package net.adhikary.mrtbuddy

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.aakira.napier.Napier
import net.adhikary.mrtbuddy.service.ClipboardService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val clipboardService: ClipboardService by inject()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        // Initialize clipboard service with context
        clipboardService.setContext(this)

        // TODO will be removed once code structure and dependancy injection is intruduced

        Napier.d("App Running.....")

        setContent {
            App(dynamicColor = true)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(dynamicColor = true)
}
