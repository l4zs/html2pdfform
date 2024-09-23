package de.l4zs.html2pdfform.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.ui.util.FloatingEntries
import de.l4zs.html2pdfform.ui.view.SettingsPage
import de.l4zs.html2pdfform.ui.view.GeneratorPage
import de.l4zs.html2pdfform.ui.view.HelpPage
import de.l4zs.html2pdfform.util.Logger

@Composable
fun PDFFormGeneratorApp(
    logger: Logger,
    converter: Converter,
    config: ConfigContext,
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = Page.GENERATOR.route,
        enterTransition = {
            fadeIn(tween())
        },
        exitTransition = { ExitTransition.None },
    ) {
        composable(Page.GENERATOR.route) {
            Page(logger) {
                GeneratorPage(navController, logger, converter, config.config)
            }
        }
        composable(Page.HELP.route) {
            Page(logger) {
                HelpPage(navController, logger, config.config)
            }
        }
        composable(Page.SETTINGS.route) {
            Page(logger) {
                SettingsPage(navController, logger, config)
            }
        }
    }
}

enum class Page(
    val route: String,
) {
    GENERATOR("generator"),
    HELP("help"),
    SETTINGS("settings"),
}

@Composable
fun Page(
    logger: Logger,
    content: @Composable () -> Unit,
) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            color = MaterialTheme.colors.background,
        ) {
            content()
        }
    }
    logger.FloatingEntries()
}
