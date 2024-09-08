package de.l4zs.html2pdfform.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lowagie.text.PageSize
import de.l4zs.html2pdfform.config.Config

@Composable
fun SettingsPage(navController: androidx.navigation.NavController) {
    var selectedPageSize by remember { mutableStateOf(config.pageSize) }
    var expanded by remember { mutableStateOf(false) }

    val pageSizes = listOf(
        PageSize.LETTER to "Letter",
        PageSize.NOTE to "Note",
        PageSize.LEGAL to "Legal",
        PageSize.TABLOID to "Tabloid",
        PageSize.EXECUTIVE to "Executive",
        PageSize.POSTCARD to "Postcard",
        PageSize.A0 to "A0",
        PageSize.A1 to "A1",
        PageSize.A2 to "A2",
        PageSize.A3 to "A3",
        PageSize.A4 to "A4",
        PageSize.A5 to "A5",
        PageSize.A6 to "A6",
        PageSize.A7 to "A7",
        PageSize.A8 to "A8",
        PageSize.A9 to "A9",
        PageSize.A10 to "A10",
        PageSize.B0 to "B0",
        PageSize.B1 to "B1",
        PageSize.B2 to "B2",
        PageSize.B3 to "B3",
        PageSize.B4 to "B4",
        PageSize.B5 to "B5",
        PageSize.B6 to "B6",
        PageSize.B7 to "B7",
        PageSize.B8 to "B8",
        PageSize.B9 to "B9",
        PageSize.B10 to "B10",
        PageSize.ARCH_E to "ARCH_E",
        PageSize.ARCH_D to "ARCH_D",
        PageSize.ARCH_C to "ARCH_C",
        PageSize.ARCH_B to "ARCH_B",
        PageSize.ARCH_A to "ARCH_A",
        PageSize.FLSA to "FLSA",
        PageSize.FLSE to "FLSE",
        PageSize.HALFLETTER to "Half Letter",
        PageSize._11X17 to "11x17",
        PageSize.ID_1 to "ID 1",
        PageSize.ID_2 to "ID 2",
        PageSize.ID_3 to "ID 3",
        PageSize.LEDGER to "Ledger",
        PageSize.CROWN_QUARTO to "Crown Quarto",
        PageSize.LARGE_CROWN_QUARTO to "Large Crown Quarto",
        PageSize.DEMY_QUARTO to "Demy Quarto",
        PageSize.ROYAL_QUARTO to "Royal Quarto",
        PageSize.CROWN_OCTAVO to "Crown Octavo",
        PageSize.LARGE_CROWN_OCTAVO to "Large Crown Octavo",
        PageSize.DEMY_OCTAVO to "Demy Octavo",
        PageSize.ROYAL_OCTAVO to "Royal Octavo",
        PageSize.SMALL_PAPERBACK to "Small Paperback",
        PageSize.PENGUIN_SMALL_PAPERBACK to "Penguin Small Paperback",
        PageSize.PENGUIN_LARGE_PAPERBACK to "Penguin Large Paperback",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
            Text("Einstellungen", style = MaterialTheme.typography.h6)
            Spacer(Modifier.width(48.dp)) // For alignment
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Seitenformat auswählen:")
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    expanded = true
                    println(config.effectivePageWidth)
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(pageSizes.find { it.first == selectedPageSize }?.second ?: "Unbekannt")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                pageSizes.forEach { (pageSize, name) ->
                    DropdownMenuItem(onClick = {
                        selectedPageSize = pageSize
                        expanded = false
                    }) {
                        Text(name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                config = config.copy(
                    pageSize = selectedPageSize
                    // Add other settings here if needed
                )
                navController.navigate("main")
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Speichern")
        }

        Text("Hier können Sie die Einstellungen des PDF Formular Generators anpassen.")
        // Add more settings options here if needed
    }
}