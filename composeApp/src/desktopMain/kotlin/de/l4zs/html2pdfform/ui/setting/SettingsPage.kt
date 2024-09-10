package de.l4zs.html2pdfform.ui.setting

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
import com.lowagie.text.HeaderFooter as PdfHeaderFooter
import com.lowagie.text.pdf.BaseFont
import de.l4zs.html2pdfform.data.HeaderFooter
import de.l4zs.html2pdfform.data.Image
import de.l4zs.html2pdfform.ui.DropdownSelector
import de.l4zs.html2pdfform.config.*
import de.l4zs.html2pdfform.data.Intro
import de.l4zs.html2pdfform.data.Metadata
import de.l4zs.html2pdfform.util.centimeterToPoint
import de.l4zs.html2pdfform.util.pointToCentimeter
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

private val fonts = listOf(
    BaseFont.COURIER to "Courier",
    BaseFont.HELVETICA to "Helvetica",
    BaseFont.TIMES_ROMAN to "Times Roman",
)

@Composable
fun SettingsPage(navController: androidx.navigation.NavController) {
    var pageSize by remember { mutableStateOf(config.pageSize) }
    var pagePaddingX by remember { mutableStateOf(config.pagePaddingX.pointToCentimeter()) }
    var pagePaddingY by remember { mutableStateOf(config.pagePaddingY.pointToCentimeter()) }
    var groupPaddingX by remember { mutableStateOf(config.groupPaddingX.pointToCentimeter()) }
    var groupPaddingY by remember { mutableStateOf(config.groupPaddingY.pointToCentimeter()) }
    var innerPaddingX by remember { mutableStateOf(config.innerPaddingX.pointToCentimeter()) }
    var innerPaddingY by remember { mutableStateOf(config.innerPaddingY.pointToCentimeter()) }
    var font by remember { mutableStateOf(config.font) }
    var fontSize by remember { mutableStateOf(config.fontSize) }
    var selectSize by remember { mutableStateOf(config.selectSize) }
    var textareaRows by remember { mutableStateOf(config.textareaRows) }
    var maxRadiosPerRow by remember { mutableStateOf(config.maxRadiosPerRow) }

    // headers and footers
    var headerBefore by remember { mutableStateOf(config.header.before) }
    var headerAfter by remember { mutableStateOf(config.header.after) }
    var headerNumbered by remember { mutableStateOf(config.header.numbered) }
    var headerAlign by remember { mutableStateOf(config.header.align) }

    var footerBefore by remember { mutableStateOf(config.footer.before) }
    var footerAfter by remember { mutableStateOf(config.footer.after) }
    var footerNumbered by remember { mutableStateOf(config.footer.numbered) }
    var footerAlign by remember { mutableStateOf(config.footer.align) }

    var firstPageHeaderEnabled by remember { mutableStateOf(config.firstPageHeader != null) }
    var firstPageHeaderBefore by remember { mutableStateOf(config.firstPageHeader?.before ?: "") }
    var firstPageHeaderAfter by remember { mutableStateOf(config.firstPageHeader?.after ?: "") }
    var firstPageHeaderNumbered by remember { mutableStateOf(config.firstPageHeader?.numbered ?: false) }
    var firstPageHeaderAlign by remember { mutableStateOf(config.firstPageHeader?.align ?: PdfHeaderFooter.ALIGN_RIGHT) }

    var firstPageFooterEnabled by remember { mutableStateOf(config.firstPageFooter != null) }
    var firstPageFooterBefore by remember { mutableStateOf(config.firstPageFooter?.before ?: "") }
    var firstPageFooterAfter by remember { mutableStateOf(config.firstPageFooter?.after ?: "") }
    var firstPageFooterNumbered by remember { mutableStateOf(config.firstPageFooter?.numbered ?: false) }
    var firstPageFooterAlign by remember { mutableStateOf(config.firstPageFooter?.align ?: PdfHeaderFooter.ALIGN_RIGHT) }

    // metadata
    var author by remember { mutableStateOf(config.metadata.author) }
    var creator by remember { mutableStateOf(config.metadata.creator) }
    var subject by remember { mutableStateOf(config.metadata.subject) }

    // intro
    var imageEnabled by remember { mutableStateOf(config.intro.image != null) }
    var image by remember { mutableStateOf(config.intro.image?.path ?: "") }
    var imageWidth by remember { mutableStateOf(config.intro.image?.width ?: 200f) }

    var textEnabled by remember { mutableStateOf(config.intro.text != null) }
    var text by remember { mutableStateOf(config.intro.text?.text ?: "") }
    var textSize by remember { mutableStateOf(config.intro.text?.fontSize ?: 8.0f) }
    var textFont by remember { mutableStateOf(config.intro.text?.font ?: BaseFont.TIMES_ROMAN) }


    val imagePicker = rememberFilePickerLauncher(
        type = PickerType.File(listOf("png", "jpg", "jpeg", "gif")),
        mode = PickerMode.Single,
        title = "Wähle ein Logo aus",
        initialDirectory = null
    ) { file ->
        image = if (file != null) {
            file.path ?: ""
        } else {
            ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
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
                        pageSizes,
                        pageSize
                    ) { pageSize = it }
                },
                middleColumn = {
                    FloatInput(
                        "Seitenränder links/rechts",
                        pagePaddingX,
                        suffix = "cm"
                    ) { pagePaddingX = it }
                },
                rightColumn = {
                    FloatInput(
                        "Seitenränder oben/unten",
                        pagePaddingY,
                        suffix = "cm"
                    ) { pagePaddingY = it }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Layout Settings Group
        SettingsGroup("Layout-Einstellungen") {
            TwoColumnLayout(
                leftColumn = {
                    FloatInput(
                        "Gruppenabstand links/rechts",
                        groupPaddingX,
                        suffix = "cm"
                    ) { groupPaddingX = it }
                },
                rightColumn = {
                    FloatInput(
                        "Gruppenabstand oben/unten",
                        groupPaddingY,
                        suffix = "cm"
                    ) { groupPaddingY = it }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TwoColumnLayout(
                leftColumn = {
                    FloatInput(
                        "Elementabstand links/rechts",
                        innerPaddingX,
                        suffix = "cm"
                    ) { innerPaddingX = it }
                },
                rightColumn = {
                    FloatInput(
                        "Elementabstand oben/unten",
                        innerPaddingY,
                        suffix = "cm"
                    ) { innerPaddingY = it }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Font Settings Group
        SettingsGroup("Schrifteinstellungen") {
            TwoColumnLayout(
                leftColumn = {
                    DropdownSelector(
                        "Schriftart",
                        fonts,
                        font
                    ) { font = it }
                },
                rightColumn = {
                    FloatInput(
                        "Schriftgröße",
                        fontSize
                    ) { fontSize = it }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Element Settings Group
        SettingsGroup("Formularelemente-Einstellungen") {
            ThreeColumnLayout(
                leftColumn = {
                    IntInput(
                        "Sichtbare Optionen bei Select",
                        selectSize,
                        4
                    ) { selectSize = it }
                },
                middleColumn = {
                    IntInput(
                        "Sichtbare Zeilen bei Textarea",
                        textareaRows,
                        3
                    ) { textareaRows = it }
                },
                rightColumn = {
                    IntInput(
                        "Maximale Radiobuttons pro Reihe",
                        maxRadiosPerRow,
                        3
                    ) { maxRadiosPerRow = it }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroup("Metadaten") {
            ThreeColumnLayout(
                leftColumn = {
                    Input(
                        "Autor",
                        author
                    ) { author = it }
                },
                middleColumn = {
                    Input(
                        "Ersteller",
                        creator
                    ) { creator = it }
                },
                rightColumn = {
                    Input(
                        "Thema",
                        subject
                    ) { subject = it }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroup("Einführungsabschnitt") {
            TwoColumnLayout(
                leftColumn = {
                    Checkbox(
                        "Logo anzeigen",
                        imageEnabled
                    ) {
                        imageEnabled = it
                    }
                },
                rightColumn = {
                    Checkbox(
                        "Einführungstext anzeigen",
                        textEnabled
                    ) {
                        textEnabled = it
                    }
                }
            )
            if (imageEnabled) {
                TwoColumnLayout(
                    leftColumn = {
                        OutlinedTextField(
                            value = image,
                            onValueChange = { /* Read-only */ },
                            label = { Text("Logo") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = {
                                TextButton(
                                    onClick = { imagePicker.launch() },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .pointerHoverIcon(PointerIcon.Hand)
                                ) {
                                    Text("Logo auswählen")
                                }
                            }
                        )
                    },
                    rightColumn = {
                        FloatInput(
                            "Bildbreite",
                            imageWidth,
                            suffix = "pt"
                        ) { imageWidth = it }
                    }
                )
            }
            if (textEnabled) {
                TwoColumnLayout(
                    leftColumn = {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
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
                                    fonts,
                                    textFont,
                                    modifier = Modifier.fillMaxWidth()
                                ) { textFont = it }
                            },
                            bottomRow = {
                                FloatInput(
                                    "Schriftgröße",
                                    textSize,
                                    8.0f
                                ) {
                                    textSize = it
                                }
                            }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header and Footer
        SettingsGroup("Kopf- und Fußzeilen") {
            HeaderFooterSection(
                title = "Kopfzeile",
                textBefore = headerBefore,
                onTextBeforeChange = { headerBefore = it },
                textAfter = headerAfter,
                onTextAfterChange = { headerAfter = it },
                numbered = headerNumbered,
                onNumberedChange = { headerNumbered = it },
                align = headerAlign,
                onAlignChange = { headerAlign = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HeaderFooterSection(
                title = "Fußzeile",
                textBefore = footerBefore,
                onTextBeforeChange = { footerBefore = it },
                textAfter = footerAfter,
                onTextAfterChange = { footerAfter = it },
                numbered = footerNumbered,
                onNumberedChange = { footerNumbered = it },
                align = footerAlign,
                onAlignChange = { footerAlign = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = firstPageHeaderEnabled,
                        onCheckedChange = { firstPageHeaderEnabled = it },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Andere Kopfzeile auf der ersten Seite anzeigen",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.clickable { firstPageHeaderEnabled = !firstPageHeaderEnabled }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = firstPageFooterEnabled,
                        onCheckedChange = { firstPageFooterEnabled = it },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Andere Fußzeile auf der ersten Seite anzeigen",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.clickable { firstPageFooterEnabled = !firstPageFooterEnabled }
                    )
                }
            }

            if (firstPageHeaderEnabled) {
                HeaderFooterSection(
                    title = "Kopfzeile der ersten Seite",
                    textBefore = firstPageHeaderBefore,
                    onTextBeforeChange = { firstPageHeaderBefore = it },
                    textAfter = firstPageHeaderAfter,
                    onTextAfterChange = { firstPageHeaderAfter = it },
                    numbered = firstPageHeaderNumbered,
                    onNumberedChange = { firstPageHeaderNumbered = it },
                    align = firstPageHeaderAlign,
                    onAlignChange = { firstPageHeaderAlign = it }
                )
            }

            if (firstPageFooterEnabled) {
                if (firstPageHeaderEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                HeaderFooterSection(
                    title = "Fußzeile der ersten Seite",
                    textBefore = firstPageFooterBefore,
                    onTextBeforeChange = { firstPageFooterBefore = it },
                    textAfter = firstPageFooterAfter,
                    onTextAfterChange = { firstPageFooterAfter = it },
                    numbered = firstPageFooterNumbered,
                    onNumberedChange = { firstPageFooterNumbered = it },
                    align = firstPageFooterAlign,
                    onAlignChange = { firstPageFooterAlign = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                config = Config(
                    pageType = pageSize.pageType,
                    pagePaddingX = pagePaddingX.centimeterToPoint(),
                    pagePaddingY = pagePaddingY.centimeterToPoint(),
                    groupPaddingX = groupPaddingX.centimeterToPoint(),
                    groupPaddingY = groupPaddingY.centimeterToPoint(),
                    innerPaddingX = innerPaddingX.centimeterToPoint(),
                    innerPaddingY = innerPaddingY.centimeterToPoint(),
                    font = font,
                    fontSize = fontSize,
                    selectSize = selectSize,
                    textareaRows = textareaRows,
                    maxRadiosPerRow = maxRadiosPerRow,
                    firstPageHeader = if (firstPageHeaderEnabled) HeaderFooter(
                        firstPageHeaderBefore,
                        firstPageHeaderAfter,
                        firstPageHeaderNumbered,
                        firstPageHeaderAlign
                    ) else null,
                    firstPageFooter = if (firstPageFooterEnabled) HeaderFooter(
                        firstPageFooterBefore,
                        firstPageFooterAfter,
                        firstPageFooterNumbered,
                        firstPageFooterAlign
                    ) else null,
                    header = HeaderFooter(
                        before = headerBefore,
                        after = headerAfter,
                        numbered = headerNumbered,
                        align = headerAlign,
                    ),
                    footer = HeaderFooter(
                        before = footerBefore,
                        after = footerAfter,
                        numbered = footerNumbered,
                        align = footerAlign,
                    ),
                    metadata = Metadata(
                        author = author,
                        creator = creator,
                        subject = subject,
                    ),
                    intro = Intro(
                        image = if (imageEnabled) Image(image, imageWidth) else null,
                        text = if (textEnabled) de.l4zs.html2pdfform.data.Text(text, textSize, textFont) else null,
                    ),
                )
                Config.saveConfig()
                navController.navigate("main")
            },
            modifier = Modifier.align(Alignment.End)
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Text("Speichern")
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 8.dp)
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
