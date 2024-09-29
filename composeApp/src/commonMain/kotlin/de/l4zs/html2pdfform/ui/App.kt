package de.l4zs.html2pdfform.ui

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.ui.util.FloatingEntries
import de.l4zs.html2pdfform.ui.view.GeneratorPage
import de.l4zs.html2pdfform.ui.view.HelpPage
import de.l4zs.html2pdfform.ui.view.SettingsPage
import de.l4zs.html2pdfform.util.Logger

/**
 * Main entry point for the Compose app. This function sets up the
 * navigation and the pages.
 *
 * @param logger The logger instance.
 * @param converter The converter instance.
 * @param config The config context.
 */
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

/**
 * Enum class for the different pages.
 *
 * @param route The route of the page.
 */
enum class Page(
    val route: String,
) {
    GENERATOR("generator"),
    HELP("help"),
    SETTINGS("settings"),
}

/**
 * Main page layout for the app. This function sets up the MaterialTheme
 * and shows the log entries.
 *
 * @param logger The logger instance.
 * @param content The content of the page.
 */
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
