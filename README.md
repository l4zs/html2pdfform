# html2pdfform - HTML to PDF Form Converter

This is a Kotlin Multiplatform project targeting Desktop.
It uses [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) for the UI,
[Jsoup](https://jsoup.org/) for HTML parsing and
[OpenPDF](https://github.com/librepdf/openpdf) for PDF generation.

* `/composeApp` is for code shared between targets.
    - `commonMain` is for code that’s common for all targets.
    - `desktopMain` is for code that’s specific to the desktop target.

start the app with `./gradlew composeApp:run`

package the app with:
- `./gradlew composeApp:packageDeb`
- `./gradlew composeApp:packageDmg`
- `./gradlew composeApp:packageMsi`
