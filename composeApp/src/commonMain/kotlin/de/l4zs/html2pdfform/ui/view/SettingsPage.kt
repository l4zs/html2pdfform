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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.*
import de.l4zs.html2pdfform.backend.data.Align
import de.l4zs.html2pdfform.backend.data.Font
import de.l4zs.html2pdfform.backend.data.Language
import de.l4zs.html2pdfform.backend.data.PageSize
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.ui.util.*
import de.l4zs.html2pdfform.ui.viewmodel.SettingsViewModel
import de.l4zs.html2pdfform.util.Logger
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource

/**
 * The Settings Page allows the user to modify the configuration of the application.
 * The user can export and import the configuration, reset the settings to default, and save the settings.
 *
 * @param navController Navigation Controller to navigate between pages
 * @param logger Logger to log messages
 * @param configContext Config Context to access the configuration
 */
@Composable
fun SettingsPage(
    navController: NavController,
    logger: Logger,
    configContext: ConfigContext,
) {
    val viewModel = viewModel { SettingsViewModel(logger, configContext) }
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }

    val showDialog = remember { mutableStateOf(false) }

    val configSaved = stringResource(Res.string.settings_page_config_saved)
    val configExportError = stringResource(Res.string.settings_page_config_export_error)
    val settingsReset = stringResource(Res.string.settings_page_settings_reset)

    val configPicker =
        rememberFilePickerLauncher(
            type = PickerType.File(listOf("json")),
            mode = PickerMode.Single,
            title = stringResource(Res.string.settings_page_config_picker_title),
        ) { file ->
            if (file == null) {
                return@rememberFilePickerLauncher
            }
            viewModel.loadConfig(file.file)
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
                onClick = {
                    if (viewModel.isConfigChanged) {
                        // show dialog
                        showDialog.value = true
                    } else {
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.back))
            }
            Text(stringResource(Res.string.settings_page_name), style = MaterialTheme.typography.h6)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Export / Import Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val bytes = viewModel.exportConfig()
                            try {
                                FileKit.saveFile(
                                    bytes,
                                    "config",
                                    "json",
                                )
                                logger.success(configSaved)
                            } catch (e: Exception) {
                                logger.error(configExportError, e)
                            }
                        }
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Text(stringResource(Res.string.settings_page_export_text))
                }
                Button(
                    onClick = { configPicker.launch() },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Text(stringResource(Res.string.settings_page_import_text))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PDF Settings
        ExpandableSection(stringResource(Res.string.settings_page_section_pdf_title)) {
            PDFSettings(viewModel)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // General Settings
        ExpandableSection(stringResource(Res.string.settings_page_section_general_title)) {
            GeneralSettings(viewModel)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = {
                        viewModel.updateConfig(Config())
                        logger.success(settingsReset)
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Text(stringResource(Res.string.settings_page_reset_text))
                }
                Button(
                    onClick = {
                        runBlocking {
                            viewModel.saveConfig()
                        }
                        navController.navigateUp()
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Text(stringResource(Res.string.settings_page_save_text))
                }
            }
        }
    }
    ExitConfirmationDialog(
        showDialog = showDialog.value,
        onDismiss = {
            showDialog.value = false
            navController.navigateUp()
        },
        onConfirm = {
            showDialog.value = false
            runBlocking {
                viewModel.saveConfig()
            }
            navController.navigateUp()
        },
    )
}

/**
 * The PDF Settings Section allows the user to modify the PDF settings.
 *
 * @param viewModel SettingsViewModel to access the configuration
 */
@Composable
private fun PDFSettings(viewModel: SettingsViewModel) {
    val config by viewModel.config.collectAsState()

    val imagePicker =
        rememberFilePickerLauncher(
            type = PickerType.File(listOf("png", "jpg", "jpeg", "gif")),
            mode = PickerMode.Single,
            title = stringResource(Res.string.settings_page_logo_picker_title),
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

    ExpandableSection(stringResource(Res.string.settings_page_section_page_title)) {
        ThreeColumnLayout(
            leftColumn = {
                DropdownSelector(
                    stringResource(Res.string.settings_page_section_page_format),
                    PageSize.PAGE_SIZES,
                    config.pageSize,
                ) { viewModel.updateConfig(config.copy(pageType = it.translationKey)) }
            },
            middleColumn = {
                PointInput(
                    stringResource(Res.string.settings_page_section_page_padding_lr),
                    config.pagePaddingX,
                ) { viewModel.updateConfig(config.copy(pagePaddingX = it)) }
            },
            rightColumn = {
                PointInput(
                    stringResource(Res.string.settings_page_section_page_padding_tb),
                    config.pagePaddingY,
                ) { viewModel.updateConfig(config.copy(pagePaddingY = it)) }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Layout Settings Group
    ExpandableSection(stringResource(Res.string.settings_page_section_layout_title)) {
        TwoColumnLayout(
            leftColumn = {
                PointInput(
                    stringResource(Res.string.settings_page_section_layout_group_lr),
                    config.groupPaddingX,
                ) { viewModel.updateConfig(config.copy(groupPaddingX = it)) }
            },
            rightColumn = {
                PointInput(
                    stringResource(Res.string.settings_page_section_layout_group_tb),
                    config.groupPaddingY,
                ) { viewModel.updateConfig(config.copy(groupPaddingY = it)) }
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        TwoColumnLayout(
            leftColumn = {
                PointInput(
                    stringResource(Res.string.settings_page_section_layout_element_lr),
                    config.innerPaddingX,
                ) { viewModel.updateConfig(config.copy(innerPaddingX = it)) }
            },
            rightColumn = {
                PointInput(
                    stringResource(Res.string.settings_page_section_layout_element_tb),
                    config.innerPaddingY,
                ) { viewModel.updateConfig(config.copy(innerPaddingY = it)) }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Font Settings Group
    ExpandableSection(stringResource(Res.string.settings_page_section_font_title)) {
        TwoColumnLayout(
            leftColumn = {
                DropdownSelector(
                    stringResource(Res.string.settings_page_section_font_type),
                    Font.entries,
                    config.font,
                ) { viewModel.updateConfig(config.copy(font = it)) }
            },
            rightColumn = {
                FloatInput(
                    stringResource(Res.string.settings_page_section_font_size),
                    config.fontSize,
                ) { viewModel.updateConfig(config.copy(fontSize = it)) }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Form Element Settings Group
    ExpandableSection(stringResource(Res.string.settings_page_section_form_title)) {
        ThreeColumnLayout(
            leftColumn = {
                IntInput(
                    stringResource(Res.string.settings_page_section_form_select),
                    config.selectSize,
                ) { viewModel.updateConfig(config.copy(selectSize = it)) }
            },
            middleColumn = {
                IntInput(
                    stringResource(Res.string.settings_page_section_form_textarea),
                    config.textareaRows,
                ) { viewModel.updateConfig(config.copy(textareaRows = it)) }
            },
            rightColumn = {
                IntInput(
                    stringResource(Res.string.settings_page_section_form_radio),
                    config.maxRadiosPerRow,
                ) { viewModel.updateConfig(config.copy(maxRadiosPerRow = it)) }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    ExpandableSection(stringResource(Res.string.settings_page_section_metadata_title)) {
        ThreeColumnLayout(
            leftColumn = {
                Input(
                    stringResource(Res.string.settings_page_section_metadata_author),
                    config.metadata.author,
                ) { viewModel.updateConfig(config.copy(metadata = config.metadata.copy(author = it))) }
            },
            middleColumn = {
                Input(
                    stringResource(Res.string.settings_page_section_metadata_creator),
                    config.metadata.creator,
                ) { viewModel.updateConfig(config.copy(metadata = config.metadata.copy(creator = it))) }
            },
            rightColumn = {
                Input(
                    stringResource(Res.string.settings_page_section_metadata_subject),
                    config.metadata.subject,
                ) { viewModel.updateConfig(config.copy(metadata = config.metadata.copy(subject = it))) }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    ExpandableSection(stringResource(Res.string.settings_page_section_intro_title)) {
        TwoColumnLayout(
            leftColumn = {
                Checkbox(
                    stringResource(Res.string.settings_page_section_intro_show_logo),
                    config.intro.imageEnabled,
                ) { viewModel.updateConfig(config.copy(intro = config.intro.copy(imageEnabled = it))) }
            },
            rightColumn = {
                Checkbox(
                    stringResource(Res.string.settings_page_section_intro_show_text),
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
                        label = { Text(stringResource(Res.string.settings_page_section_intro_logo)) },
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
                                Text(stringResource(Res.string.settings_page_section_intro_logo_text))
                            }
                        },
                    )
                },
                rightColumn = {
                    PointInput(
                        stringResource(Res.string.settings_page_section_intro_logo_width),
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
                        label = { Text(stringResource(Res.string.settings_page_section_intro_text)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        minLines = 5,
                    )
                },
                rightColumn = {
                    TwoRowLayout(
                        topRow = {
                            DropdownSelector(
                                stringResource(Res.string.settings_page_section_intro_text_font),
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
                                stringResource(Res.string.settings_page_section_intro_text_size),
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
    ExpandableSection(stringResource(Res.string.settings_page_section_headerfooter_title)) {
        HeaderFooterSection(
            title = stringResource(Res.string.settings_page_section_headerfooter_header),
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
            title = stringResource(Res.string.settings_page_section_headerfooter_footer),
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
                    text = stringResource(Res.string.settings_page_section_headerfooter_different_header),
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
                    text = stringResource(Res.string.settings_page_section_headerfooter_different_footer),
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
                title = stringResource(Res.string.settings_page_section_headerfooter_first_header),
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
                title = stringResource(Res.string.settings_page_section_headerfooter_first_footer),
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
}

/**
 *  This section allows the user to modify the application settings.
 */
@Composable
private fun GeneralSettings(viewModel: SettingsViewModel) {
    val config by viewModel.config.collectAsState()

    TwoColumnLayout(
        leftColumn = {
            DropdownSelector(
                stringResource(Res.string.settings_page_section_language),
                Language.entries,
                config.language,
            ) { viewModel.updateConfig(config.copy(language = it)) }
        },
        rightColumn = {
            DropdownSelector(
                stringResource(Res.string.settings_page_section_loglevel),
                Logger.LogLevel.entries.minus(Logger.LogLevel.SUCCESS),
                config.logLevel,
            ) { viewModel.updateConfig(config.copy(logLevel = it)) }
        }
    )
}

/**
 * A confirmation dialog
 * to confirm the exit of the settings page when the configuration has been changed but not saved yet.
 *
 * @param showDialog Whether the dialog should be shown
 * @param onDismiss Callback when the dialog is dismissed
 * @param onConfirm Callback when the dialog is confirmed
 */
@Composable
private fun ExitConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(Res.string.settings_page_exit_confirmation_title)) },
            text = { Text(stringResource(Res.string.settings_page_exit_confirmation_text)) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                ) {
                    Text(stringResource(Res.string.settings_page_exit_confirmation_confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                ) {
                    Text(stringResource(Res.string.settings_page_exit_confirmation_dismiss))
                }
            },
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
        )
    }
}
