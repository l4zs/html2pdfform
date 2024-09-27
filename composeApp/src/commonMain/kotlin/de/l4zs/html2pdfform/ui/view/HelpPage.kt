package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.config.configFile
import de.l4zs.html2pdfform.ui.util.*
import de.l4zs.html2pdfform.util.Logger
import java.awt.Desktop
import java.io.IOException

@Composable
fun HelpPage(
    navController: NavController,
    logger: Logger,
    config: Config,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
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
            Text("Hilfe", style = MaterialTheme.typography.h6)
            Spacer(Modifier.width(48.dp)) // For alignment
        }

        Spacer(modifier = Modifier.height(16.dp))

        WhatInputsAreSupported()

        Spacer(modifier = Modifier.height(16.dp))

        OtherSupported()

        Spacer(modifier = Modifier.height(16.dp))

        WhereConfigLocated(logger)

        Spacer(modifier = Modifier.height(16.dp))

        WhyNotExpectedOutput()

        Spacer(modifier = Modifier.height(16.dp))

        Copyright()
    }
}

@Composable
private fun NeedScript() {
    Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Funktionalität zu gewährleisten.")
}

@Composable
private fun CommonAttributes() {
    ExpandableSubSubSection("id") {
        Text("Das Attribut 'id' wird verwendet, um das Formularfeld zu identifizieren.")
        Text(
            "Es wird benötigt, um Formularfelder durch das 'toggles' Attribut umzuschalten. " +
                "(Siehe Checkbox und Radio)",
        )
    }
    ExpandableSubSubSection("disabled") {
        Text(
            "Wenn das Attribut 'disabled' gesetzt ist, kann das Feld nicht bearbeitet werden, " +
                "aber der Wert kann kopiert werden.",
        )
    }
    ExpandableSubSubSection("readonly") {
        Text(
            "Wenn das Attribut 'readonly' gesetzt ist, kann das Feld nicht bearbeitet werden, " +
                "aber der Wert kann kopiert werden.",
        )
    }
    ExpandableSubSubSection("form") {
        Text(
            "Das Attribut 'form' wird verwendet, um das Formularfeld einem Formular zuzuordnen, " +
                "wenn das Element außerhalb des Formulars ist.",
        )
    }
}

@Composable
private fun Value() {
    ExpandableSubSubSection("value") {
        Text("Das Attribut 'value' wird verwendet, um den Standardwert des Formularfelds festzulegen.")
        Text("Bei Buttons wird der Wert als Beschriftung verwendet.")
    }
}

@Composable
private fun Required() {
    ExpandableSubSubSection("required") {
        Text(
            "Wenn das Attribut 'required' gesetzt ist, muss das Feld ausgefüllt werden, " +
                "bevor das Formular gesendet werden kann.",
        )
        Text("Außerdem wird ein Sternchen (*) hinter dem Label angezeigt.")
    }
}

@Composable
private fun Placeholder() {
    ExpandableSubSubSection("placeholder") {
        Text("Das Attribut 'placeholder' wird verwendet, um einen Platzhaltertext im Formularfeld anzuzeigen.")
        Text("Der Platzhaltertext wird nur angezeigt, wenn das Feld leer ist.")
        NeedScript()
    }
}

@Composable
private fun Format() {
    ExpandableSubSubSection("format") {
        Text("Das Attribut 'format' wird verwendet, um die Formatierung des Datums/Uhrzeit festzulegen.")
        NeedScript()
    }
}

@Composable
private fun TextCommonAttributes() {
    Placeholder()
    Required()
    ExpandableSubSubSection("minlength") {
        Text("Das Attribut 'minlength' wird verwendet, um die Mindestanzahl von Zeichen festzulegen.")
        NeedScript()
    }
    ExpandableSubSubSection("maxlength") {
        Text("Das Attribut 'maxlength' wird verwendet, um die maximale Anzahl von Zeichen festzulegen.")
    }
    ExpandableSubSubSection("pattern") {
        Text(
            "Das Attribut 'pattern' wird verwendet, um ein Muster festzulegen, " +
                "das der Wert des Formularfelds erfüllen muss.",
        )
        Text("Das Muster wird als regulärer Ausdruck angegeben.")
        NeedScript()
    }
    ExpandableSubSubSection("patternMessage") {
        Text(
            "Das Attribut 'patternMessage' wird verwendet, um eine benutzerdefinierte Fehlermeldung anzuzeigen, " +
                "wenn das Muster nicht erfüllt ist.",
        )
        CopyText(
            "Die Syntax ist: 'patternMessage=\"Bitte geben Sie nur Kleinbuchstaben an.\"'",
            "<input type=\"text\" pattern=\"[a-z]+\" patternMessage=\"Bitte geben Sie nur Kleinbuchstaben an.\">",
        )
        NeedScript()
    }
}

@Composable
private fun WhatInputsAreSupported() {
    // explain what html elements are supported
    ExpandableSection("Welche HTML-Inputs werden unterstützt?") {
        Text("Der PDF Formular Generator unterstützt die folgenden HTML-Input Typen:")
        ExpandableSubSection("Checkbox") {
            Text("Im PDF-Formular wird eine Checkbox angezeigt.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Required()
            ExpandableSubSubSection("checked") {
                Text("Wenn das Attribut 'checked' gesetzt ist, ist die Checkbox standardmäßig ausgewählt.")
            }
            ExpandableSubSubSection("toggles") {
                Text(
                    "Das Attribut 'toggles' wird verwendet, " +
                        "um bei einem Klick auf die Checkbox andere Formularfelder anzuzeigen oder zu verbergen.",
                )
                CopyText(
                    "Die Syntax ist: 'toggles=\"id1 id2 id3\"'",
                    "<input type=\"checkbox\" toggles=\"id1 id2 id3\">",
                )
                NeedScript()
            }
        }
        ExpandableSubSection("Date") {
            Text("Im PDF-Formular wird ein Eingabefeld für ein Datum angezeigt, wobei die Eingabe validiert wird.")
            Text("Das Format kann durch das Attribut 'format' festgelegt werden und standardmäßig ist es 'dd.mm.yyyy'.")
            Text("In einigen PDF-Viewern wird zusätzlich ein Button zum Auswählen des Datums angezeigt.")
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            Required()
            Placeholder()
        }
        ExpandableSubSection("Datetime-local") {
            Text(
                "Im PDF-Formular wird ein Eingabefeld für ein Datum mit Uhrzeit angezeigt, " +
                    "wobei die Eingabe validiert wird.",
            )
            Text("Das Format kann durch das Attribut 'format' festgelegt werden und standardmäßig ist es 'dd.mm.yyyy HH:MM'.")
            Text("In einigen PDF-Viewern wird zusätzlich ein Button zum Auswählen des Datums angezeigt.")
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            Required()
            Placeholder()
        }
        ExpandableSubSection("Email") {
            Text(
                "Im PDF-Formular wird ein Eingabefeld für eine E-Mail-Adresse angezeigt, " +
                    "wobei die Eingabe validiert wird.",
            )
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            OpenLink("Klicke hier, um die Validierung zu testen.", "https://regex101.com/r/wKBViI/1")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("File") {
            Text("Im PDF-Formular wird ein Eingabefeld für eine Datei angezeigt.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("Month") {
            Text("Im PDF-Formular wird ein Eingabefeld für einen Monat angezeigt, wobei die Eingabe validiert wird.")
            Text("Das Format kann durch das Attribut 'format' festgelegt werden und standardmäßig ist es 'mmm yyyy'.")
            Text("In einigen PDF-Viewern wird zusätzlich ein Button zum Auswählen des Monats angezeigt.")
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            Required()
            Placeholder()
            Format()
        }
        ExpandableSubSection("Number") {
            Text("Im PDF-Formular wird ein Eingabefeld für eine Zahl angezeigt.")
            Text("Es wird validiert, ob die Eingabe eine Zahl (optional mit Minuszeichen) ist.")
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            Required()
            Placeholder()
            ExpandableSubSubSection("min") {
                Text("Das Attribut 'min' wird verwendet, um den Mindestwert festzulegen.")
                NeedScript()
            }
            ExpandableSubSubSection("max") {
                Text("Das Attribut 'max' wird verwendet, um den Maximalwert festzulegen.")
                NeedScript()
            }
            ExpandableSubSubSection("step") {
                Text("Das Attribut 'step' wird verwendet, um die Schrittweite festzulegen.")
                Text(
                    "Als Basiswert wird hier der Mindestwert, " +
                        "der Standardwert (value) oder die Schrittweite verwendet.",
                )
                NeedScript()
            }
        }
        ExpandableSubSection("Password") {
            Text("Im PDF-Formular wird ein Eingabefeld für ein Passwort angezeigt.")
            Text("Der eingegebene Text wird maskiert. (Es werden nur Sternchen angezeigt)")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("Radio") {
            Text("Im PDF-Formular wird ein Radiobutton angezeigt.")
            Text(
                "Wenn mehrere Radiobuttons mit demselben 'name' Attribut vorhanden sind, " +
                    "werden sie zu einer Gruppe zusammengefasst.",
            )
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Required()
            ExpandableSubSubSection("name") {
                Text(
                    "Das Attribut 'name' wird verwendet, um Radiobuttons zu gruppieren und sicherzustellen, " +
                        "dass nur einer ausgewählt ist.",
                )
            }
            ExpandableSubSubSection("checked") {
                Text("Wenn das Attribut 'checked' gesetzt ist, ist der Radiobutton standardmäßig ausgewählt.")
                Text(
                    "Sollten in einer Gruppe mehrere Radiobuttons als 'checked' markiert sein, " +
                        "wird nur der erste ausgewählt.",
                )
            }
            ExpandableSubSubSection("toggles") {
                Text(
                    "Das Attribut 'toggles' wird verwendet, " +
                        "um bei Auswahl des Radiobuttons andere Formularfelder anzuzeigen oder zu verbergen.",
                )
                CopyText(
                    "Die Syntax ist: 'toggles=\"id1 id2 id3\"'",
                    "<input type=\"radio\" toggles=\"id1 id2 id3\">",
                )
                NeedScript()
            }
        }
        ExpandableSubSection("Reset") {
            Text("Im PDF-Formular wird ein Button angezeigt, der alle Formularfelder zurücksetzt.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
        }
        ExpandableSubSection("Submit") {
            Text("Im PDF-Formular wird ein Button angezeigt, der beim Klick das Formular per E-Mail sendet.")
            Text("Benötigte Attribute dafür sind 'to' und 'subject'.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            ExpandableSubSubSection("to") {
                Text("Das Attribut 'to' gibt die E-Mail-Adresse(n) an, an die das Formular gesendet werden soll.")
                CopyText(
                    "Die Syntax ist: 'to=\"email1@example.com;email2@example.com\"'",
                    "<input type=\"submit\" to=\"email1@example.com;email2@example.com\">",
                )
                NeedScript()
            }
            ExpandableSubSubSection("cc") {
                Text(
                    "Das Attribut 'cc' gibt die E-Mail-Adresse(n) an, " +
                        "an die eine Kopie des Formulars gesendet werden soll.",
                )
                CopyText(
                    "Die Syntax ist: 'cc=\"email1@example.com;email2@example.com\"'",
                    "<input type=\"submit\" to=\"email1@example.com\" cc=\"email2@example.com;email3@example.com\">",
                )
                NeedScript()
            }
            ExpandableSubSubSection("subject") {
                Text("Das Attribut 'subject' gibt den Betreff der E-Mail an.")
                CopyText("Die Syntax ist: 'subject=\"Betreff\"'", "<input type=\"submit\" subject=\"Betreff\">")
                NeedScript()
            }
            ExpandableSubSubSection("body") {
                Text("Das Attribut 'body' gibt den Inhalt der E-Mail an.")
                CopyText("Die Syntax ist: 'body=\"Inhalt\"'", "<input type=\"submit\" body=\"Inhalt\">")
                NeedScript()
            }
        }
        ExpandableSubSection("Tel") {
            Text("Im PDF-Formular wird ein Eingabefeld für Text angezeigt.")
            Text(
                "Standardmäßig wird die Eingabe nicht validiert, " +
                    "dies kann jedoch durch das Attribut 'pattern' erreicht werden.",
            )
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("Text") {
            Text("Im PDF-Formular wird ein Eingabefeld für Text angezeigt.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
        ExpandableSubSection("Time") {
            Text("Im PDF-Formular wird ein Eingabefeld für eine Uhrzeit angezeigt, wobei die Eingabe validiert wird.")
            Text("Das Format kann durch das Attribut 'format' festgelegt werden und standardmäßig ist es 'HH:MM'.")
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            Required()
            Placeholder()
            Format()
        }
        ExpandableSubSection("Url") {
            Text("Im PDF-Formular wird ein Eingabefeld für eine URL angezeigt, wobei die Eingabe validiert wird.")
            Text("Achtung: Der PDF-Viewer muss JavaScript unterstützen, um die Validierung zu gewährleisten.")
            OpenLink("Klicke hier, um die Validierung zu testen.", "https://regex101.com/r/5EvYJP/2")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
        }
    }
}

@Composable
fun OtherSupported() {
    ExpandableSection("Welche anderen HTML-Elemente werden unterstützt?") {
        ExpandableSection("Label") {
            Text("Das Element <label> wird verwendet, um ein Label für ein Formularfeld anzugeben.")
            Text("Label werden nur über das 'for' Attribut mit dem Formularfeld verknüpft.")
            Text(
                "Wenn das 'for' Attribut nicht gesetzt ist oder das zugehörige Formularfeld nicht gefunden wird, " +
                    "wird das Label nicht angezeigt.",
            )
        }
        ExpandableSubSection("Button") {
            Text("Das Element <button> wird verwendet, um einen Button anzuzeigen.")
            Text("Es werden nur Buttons mit den Typen 'reset' und 'submit' unterstützt.")
            Text("Für weitere Informationen siehe Inputs 'Reset' und 'Submit'.")
            Text("Anstelle von 'value' wird der Text innerhalb des Button-Elements verwendet.")
        }
        ExpandableSubSection("Select") {
            Text("Mit <select>-Elementen können Dropdown-Listen oder Listenfelder erstellt werden.")
            Text(
                "Wenn das Attribut 'multiple' gesetzt ist, können mehrere Optionen ausgewählt werden " +
                    "und das Feld wird als Liste angezeigt.",
            )
            Text(
                "Die möglichen Optionen werden durch 'option' Elemente definiert, " +
                    "wobei Optionsgruppen nicht umgesetzt, deren Einträge aber übernommen werden.",
            )
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            ExpandableSubSubSection("multiple") {
                Text("Das Attribut 'multiple' wird verwendet, um mehrere Optionen auswählen zu können.")
                Text("Wenn das Attribut gesetzt ist, wird das Feld als Liste angezeigt.")
            }
            ExpandableSubSubSection("size") {
                Text("Das Attribut 'size' wird verwendet, um die Anzahl der sichtbaren Optionen festzulegen.")
                Text("Wenn der Wert nicht gesetzt ist, wird der Wert, der in den Einstellungen gesetzt ist, verwendet.")
            }
            ExpandableSubSubSection("editable") {
                Text(
                    "Das Attribut 'editable' wird verwendet, um ein Eingabefeld anzuzeigen, " +
                        "in dem der Benutzer eine eigene Option eingeben kann.",
                )
                Text("Achtung: Dies ist nur möglich, wenn das Attribut 'multiple' nicht gesetzt ist.")
            }
        }
        ExpandableSubSection("Textarea") {
            Text("Das Element <textarea> wird verwendet, um ein mehrzeiliges Textfeld anzuzeigen.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Value()
            TextCommonAttributes()
            ExpandableSubSubSection("rows") {
                Text("Das Attribut 'rows' wird verwendet, um die Anzahl der sichtbaren Zeilen festzulegen.")
                Text("Wenn der Wert nicht gesetzt ist, wird der Wert, der in den Einstellungen gesetzt ist, verwendet.")
            }
        }
        ExpandableSubSection("Signature") {
            Text("Das Element <signature> wird verwendet, um ein Feld für eine Unterschrift anzuzeigen.")
            Text("Das Feld wird als leeres Rechteck angezeigt, in dem der Benutzer seine Unterschrift eintragen kann.")
            Text("Folgende Attribute werden unterstützt:")
            CommonAttributes()
            Required()
        }
        ExpandableSubSection("Fieldset") {
            Text("Das Element <fieldset> wird verwendet, um eine Gruppe von Formularfeldern zu gruppieren.")
            Text("Das Fieldset wird als Rahmen um die Gruppe von Formularfeldern angezeigt.")
            Text("Das Kind-Element <legend> wird verwendet, um eine Beschriftung für das Feldset anzugeben.")
        }
    }
}

@Composable
private fun WhereConfigLocated(logger: Logger) {
    ExpandableSection("Wo ist die Konfigurationsdatei gespeichert?") {
        val configFile = configFile()
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        Text("Die Konfigurationsdatei wird im Home-Verzeichnis des Benutzers gespeichert.")
        Row {
            Text("Der Pfad zur Konfigurationsdatei lautet: ")
            Text(
                configFile.absolutePath,
                Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable {
                        if (desktop == null || !desktop.isSupported(Desktop.Action.OPEN)) {
                            logger.warn("Diese Aktion ist nicht unterstützt")
                        }
                        try {
                            desktop?.open(configFile.parentFile)
                        } catch (exception: IOException) {
                            logger.error("Fehler beim Öffnen der Konfigurationsdatei", exception)
                        }
                    },
                MaterialTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun WhyNotExpectedOutput() {
    ExpandableSection("Warum sieht das PDF Formular anders aus als erwartet?") {
        Text(
            "Das Aussehen des PDF Formulars kann sich je nach PDF-Viewer unterscheiden. " +
                "Einige Viewer zeigen das Formular möglicherweise nicht korrekt an. " +
                "Es wird empfohlen, den Adobe Acrobat Reader zu verwenden.",
        )
        Text(
            "Wenn das Problem weiterhin besteht, überprüfe bitte die Konfigurationsdatei " +
                "und stelle sicher, dass die Einstellungen korrekt sind.",
        )
        Text(
            "Sollte die Erstellung aus einer URL nicht funktionieren, versuche, die Webseite lokal zu speichern " +
                "und die Datei hochzuladen.",
        )
    }
}

@Composable
private fun Copyright() {
    Row(
        Modifier.fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterVertically,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            Arrangement.Center,
            Alignment.CenterHorizontally,
        ) {
            Text("html2pdfform v1.0.0", color = MaterialTheme.colors.secondary)
            Spacer(Modifier.height(4.dp))
            Text("© 2024 Jannis Kramer", color = MaterialTheme.colors.secondary)
        }
    }
}
