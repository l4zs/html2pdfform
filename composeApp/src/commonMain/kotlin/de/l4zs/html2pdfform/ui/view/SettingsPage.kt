package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.HeaderFooter
import de.l4zs.html2pdfform.backend.config.IntroImage
import de.l4zs.html2pdfform.backend.config.IntroText
import de.l4zs.html2pdfform.backend.data.Align
import de.l4zs.html2pdfform.backend.data.Font
import de.l4zs.html2pdfform.backend.data.PageSize
import de.l4zs.html2pdfform.ui.util.DropdownSelector
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.ui.util.*
import de.l4zs.html2pdfform.ui.viewmodel.SettingsViewModel
import de.l4zs.html2pdfform.util.Logger
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

@Composable
fun SettingsPage(
    navController: NavController,
    logger: Logger,
    configContext: ConfigContext,
) {
    val viewModel = viewModel { SettingsViewModel(logger, configContext) }

    val config by viewModel.config.collectAsState()

    val imagePicker =
        rememberFilePickerLauncher(
            type = PickerType.File(listOf("png", "jpg", "jpeg", "gif")),
            mode = PickerMode.Single,
            title = "Wähle ein Logo aus",
            initialDirectory = null,
        ) { file ->
            if (file == null) {
                return@rememberFilePickerLauncher
            }
            viewModel.updateConfig(
                config.copy(
                    intro =
                        config.intro.copy(
                            image = config.intro.image?.copy(path = file.file.path) ?: IntroImage(file.file.path),
                        ),
                ),
            )
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
            Text("Einstellungen", style = MaterialTheme.typography.h6)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page Settings Group
        SettingsGroup("Seiteneinstellungen") {
            ThreeColumnLayout(
                leftColumn = {
                    DropdownSelector(
                        "Seitenformat",
                        PageSize.PAGE_SIZES,
                        config.pageSize,
                    ) { viewModel.updateConfig(config.copy(pageType = it.displayName)) }
                },
                middleColumn = {
                    PointInput(
                        "Seitenränder links/rechts",
                        config.pagePaddingX,
                    ) { viewModel.updateConfig(config.copy(pagePaddingX = it)) }
                },
                rightColumn = {
                    PointInput(
                        "Seitenränder oben/unten",
                        config.pagePaddingY,
                    ) { viewModel.updateConfig(config.copy(pagePaddingY = it)) }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Layout Settings Group
        SettingsGroup("Layout-Einstellungen") {
            TwoColumnLayout(
                leftColumn = {
                    PointInput(
                        "Gruppenabstand links/rechts",
                        config.groupPaddingX,
                    ) { viewModel.updateConfig(config.copy(groupPaddingX = it)) }
                },
                rightColumn = {
                    PointInput(
                        "Gruppenabstand oben/unten",
                        config.groupPaddingY,
                    ) { viewModel.updateConfig(config.copy(groupPaddingY = it)) }
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            TwoColumnLayout(
                leftColumn = {
                    PointInput(
                        "Elementabstand links/rechts",
                        config.innerPaddingX,
                    ) { viewModel.updateConfig(config.copy(innerPaddingX = it)) }
                },
                rightColumn = {
                    PointInput(
                        "Elementabstand oben/unten",
                        config.innerPaddingY,
                    ) { viewModel.updateConfig(config.copy(innerPaddingY = it)) }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Font Settings Group
        SettingsGroup("Schrifteinstellungen") {
            TwoColumnLayout(
                leftColumn = {
                    DropdownSelector(
                        "Schriftart",
                        Font.entries,
                        config.font,
                    ) { viewModel.updateConfig(config.copy(font = it)) }
                },
                rightColumn = {
                    FloatInput(
                        "Schriftgröße",
                        config.fontSize,
                    ) { viewModel.updateConfig(config.copy(fontSize = it)) }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Element Settings Group
        SettingsGroup("Formularelemente-Einstellungen") {
            ThreeColumnLayout(
                leftColumn = {
                    IntInput(
                        "Sichtbare Optionen bei Select",
                        config.selectSize,
                    ) { viewModel.updateConfig(config.copy(selectSize = it)) }
                },
                middleColumn = {
                    IntInput(
                        "Sichtbare Zeilen bei Textarea",
                        config.textareaRows,
                    ) { viewModel.updateConfig(config.copy(textareaRows = it)) }
                },
                rightColumn = {
                    IntInput(
                        "Maximale Radiobuttons pro Reihe",
                        config.maxRadiosPerRow,
                    ) { viewModel.updateConfig(config.copy(maxRadiosPerRow = it)) }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroup("Metadaten") {
            ThreeColumnLayout(
                leftColumn = {
                    Input(
                        "Autor",
                        config.metadata.author,
                    ) { viewModel.updateConfig(config.copy(metadata = config.metadata.copy(author = it))) }
                },
                middleColumn = {
                    Input(
                        "Ersteller",
                        config.metadata.creator,
                    ) { viewModel.updateConfig(config.copy(metadata = config.metadata.copy(creator = it))) }
                },
                rightColumn = {
                    Input(
                        "Thema",
                        config.metadata.subject,
                    ) { viewModel.updateConfig(config.copy(metadata = config.metadata.copy(subject = it))) }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroup("Einführungsabschnitt") {
            TwoColumnLayout(
                leftColumn = {
                    Checkbox(
                        "Logo anzeigen",
                        config.intro.imageEnabled,
                    ) { viewModel.updateConfig(config.copy(intro = config.intro.copy(imageEnabled = it))) }
                },
                rightColumn = {
                    Checkbox(
                        "Einführungstext anzeigen",
                        config.intro.textEnabled,
                    ) { viewModel.updateConfig(config.copy(intro = config.intro.copy(textEnabled = it))) }
                },
            )
            if (config.intro.imageEnabled) {
                TwoColumnLayout(
                    leftColumn = {
                        OutlinedTextField(
                            value = config.intro.image?.path ?: "",
                            onValueChange = { /* Read-only */ },
                            label = { Text("Logo") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = {
                                TextButton(
                                    onClick = { imagePicker.launch() },
                                    modifier =
                                        Modifier
                                            .padding(8.dp)
                                            .pointerHoverIcon(PointerIcon.Hand),
                                ) {
                                    Text("Logo auswählen")
                                }
                            },
                        )
                    },
                    rightColumn = {
                        PointInput(
                            "Bildbreite",
                            config.intro.image?.width ?: IntroImage.DEFAULT_WIDTH,
                        ) {
                            viewModel.updateConfig(
                                config.copy(
                                    intro =
                                        config.intro.copy(
                                            image =
                                                config.intro.image?.copy(
                                                    width = it,
                                                ) ?: IntroImage("", it),
                                        ),
                                ),
                            )
                        }
                    },
                )
            }
            if (config.intro.textEnabled) {
                TwoColumnLayout(
                    leftColumn = {
                        OutlinedTextField(
                            value = config.intro.text?.text ?: "",
                            onValueChange = {
                                viewModel.updateConfig(
                                    config.copy(
                                        intro =
                                            config.intro.copy(
                                                text =
                                                    config.intro.text?.copy(
                                                        text = it,
                                                    ) ?: IntroText(it),
                                            ),
                                    ),
                                )
                            },
                            label = { Text("Text") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 5,
                            minLines = 5,
                        )
                    },
                    rightColumn = {
                        TwoRowLayout(
                            topRow = {
                                DropdownSelector(
                                    "Schriftart",
                                    Font.entries,
                                    config.intro.text?.font ?: IntroText.DEFAULT_FONT,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    viewModel.updateConfig(
                                        config.copy(
                                            intro =
                                                config.intro.copy(
                                                    text =
                                                        config.intro.text?.copy(
                                                            font = it,
                                                        ) ?: IntroText("", font = it),
                                                ),
                                        ),
                                    )
                                }
                            },
                            bottomRow = {
                                FloatInput(
                                    "Schriftgröße",
                                    config.intro.text?.fontSize ?: IntroText.DEFAULT_FONT_SIZE,
                                    8.0f,
                                ) {
                                    viewModel.updateConfig(
                                        config.copy(
                                            intro =
                                                config.intro.copy(
                                                    text =
                                                        config.intro.text?.copy(
                                                            fontSize = it,
                                                        ) ?: IntroText("", it),
                                                ),
                                        ),
                                    )
                                }
                            },
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header and Footer
        SettingsGroup("Kopf- und Fußzeilen") {
            HeaderFooterSection(
                title = "Kopfzeile",
                textBefore = config.header.before,
                onTextBeforeChange = { viewModel.updateConfig(config.copy(header = config.header.copy(before = it))) },
                textAfter = config.header.after,
                onTextAfterChange = { viewModel.updateConfig(config.copy(header = config.header.copy(after = it))) },
                numbered = config.header.overrideNumbered,
                onNumberedChange = { viewModel.updateConfig(config.copy(header = config.header.copy(numbered = it))) },
                align = config.header.align,
                onAlignChange = { viewModel.updateConfig(config.copy(header = config.header.copy(align = it))) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            HeaderFooterSection(
                title = "Fußzeile",
                textBefore = config.footer.before,
                onTextBeforeChange = { viewModel.updateConfig(config.copy(footer = config.footer.copy(before = it))) },
                textAfter = config.footer.after,
                onTextAfterChange = { viewModel.updateConfig(config.copy(footer = config.footer.copy(after = it))) },
                numbered = config.footer.overrideNumbered,
                onNumberedChange = { viewModel.updateConfig(config.copy(footer = config.footer.copy(numbered = it))) },
                align = config.footer.align,
                onAlignChange = { viewModel.updateConfig(config.copy(footer = config.footer.copy(align = it))) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Checkbox(
                        checked = config.firstPageHeaderEnabled,
                        onCheckedChange = { viewModel.updateConfig(config.copy(firstPageHeaderEnabled = it)) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Andere Kopfzeile auf der ersten Seite anzeigen",
                        style = MaterialTheme.typography.body1,
                        modifier =
                            Modifier.clickable {
                                viewModel.updateConfig(
                                    config.copy(firstPageHeaderEnabled = !config.firstPageHeaderEnabled),
                                )
                            },
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Checkbox(
                        checked = config.firstPageFooterEnabled,
                        onCheckedChange = { viewModel.updateConfig(config.copy(firstPageFooterEnabled = it)) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Andere Fußzeile auf der ersten Seite anzeigen",
                        style = MaterialTheme.typography.body1,
                        modifier =
                            Modifier.clickable {
                                viewModel.updateConfig(
                                    config.copy(firstPageFooterEnabled = !config.firstPageFooterEnabled),
                                )
                            },
                    )
                }
            }

            if (config.firstPageHeaderEnabled) {
                HeaderFooterSection(
                    title = "Kopfzeile der ersten Seite",
                    textBefore = config.firstPageHeader?.before ?: "",
                    onTextBeforeChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageHeader =
                                    config.firstPageHeader?.copy(
                                        before = it,
                                    ) ?: HeaderFooter(before = it),
                            ),
                        )
                    },
                    textAfter = config.firstPageHeader?.after ?: "",
                    onTextAfterChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageHeader =
                                    config.firstPageHeader?.copy(
                                        after = it,
                                    ) ?: HeaderFooter(after = it),
                            ),
                        )
                    },
                    numbered = config.firstPageHeader?.overrideNumbered ?: false,
                    onNumberedChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageHeader =
                                    config.firstPageHeader?.copy(
                                        numbered = it,
                                    ) ?: HeaderFooter(numbered = it),
                            ),
                        )
                    },
                    align = config.firstPageHeader?.align ?: Align.RIGHT,
                    onAlignChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageHeader =
                                    config.firstPageHeader?.copy(
                                        align = it,
                                    ) ?: HeaderFooter(align = it),
                            ),
                        )
                    },
                )
            }

            if (config.firstPageFooterEnabled) {
                if (config.firstPageHeaderEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                HeaderFooterSection(
                    title = "Fußzeile der ersten Seite",
                    textBefore = config.firstPageFooter?.before ?: "",
                    onTextBeforeChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageFooter =
                                    config.firstPageFooter?.copy(
                                        before = it,
                                    ) ?: HeaderFooter(before = it),
                            ),
                        )
                    },
                    textAfter = config.firstPageFooter?.after ?: "",
                    onTextAfterChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageFooter =
                                    config.firstPageFooter?.copy(
                                        after = it,
                                    ) ?: HeaderFooter(after = it),
                            ),
                        )
                    },
                    numbered = config.firstPageFooter?.overrideNumbered ?: false,
                    onNumberedChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageFooter =
                                    config.firstPageFooter?.copy(
                                        numbered = it,
                                    ) ?: HeaderFooter(numbered = it),
                            ),
                        )
                    },
                    align = config.firstPageFooter?.align ?: Align.RIGHT,
                    onAlignChange = {
                        viewModel.updateConfig(
                            config.copy(
                                firstPageFooter =
                                    config.firstPageFooter?.copy(
                                        align = it,
                                    ) ?: HeaderFooter(align = it),
                            ),
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.saveConfig()
                navController.navigateUp()
            },
            modifier =
                Modifier
                    .align(Alignment.End)
                    .pointerHoverIcon(PointerIcon.Hand),
        ) {
            Text("Speichern")
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
